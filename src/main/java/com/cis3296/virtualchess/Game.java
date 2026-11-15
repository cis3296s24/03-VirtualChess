package com.cis3296.virtualchess;

import com.cis3296.virtualchess.engine.ChessGame;
import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.GameResult;
import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Notation;
import com.cis3296.virtualchess.engine.Position;
import com.cis3296.virtualchess.engine.Square;
import com.cis3296.virtualchess.Systems.Database;
import com.cis3296.virtualchess.Systems.GameClock;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Runs one game on the game screen: the rules (through {@link ChessGame}), the clocks, the computer
 * opponent, and recording the result on the leaderboard.
 * <p>
 * The screen calls {@link #play}, {@link #undo}, and {@link #resign} in response to the player, and gets
 * the {@code onChange} callback whenever it should redraw.
 * <p>
 * If the computer opponent fails (Stockfish missing at the start, or crashing or misbehaving mid-game), the
 * game carries on as two players at one board rather than getting stuck on the computer's turn.
 */
public class Game {

    private static final Logger log = LoggerFactory.getLogger(Game.class);

    // The computer always plays Black; the player selection screen only names the human (White) player
    private static final Color COMPUTER_COLOR = Color.BLACK;
    // Full-strength Stockfish gets up to this many threads (half the machine's cores) and this much hash
    private static final int MAX_ENGINE_THREADS = 4;
    private static final int FULL_STRENGTH_HASH_MB = 128;

    private final ChessGame chessGame = new ChessGame();
    private final GameSession session;
    private final GameClock clock;
    private final Runnable onChange;
    private final boolean computerRequested;
    private Stockfish stockfish; // null unless playing the computer and the engine is working
    private Runnable onComputerFailure = () -> { };
    // Stockfish thinks on this thread so the window stays responsive
    private final ExecutorService engineThread = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "stockfish");
        thread.setDaemon(true);
        return thread;
    });
    private final Random random = new Random();
    private boolean resultRecorded;
    private boolean closed;

    /**
     * @param session  who's playing, against the computer or not, and the clock time
     * @param onChange called after every change to the game, so the screen can redraw
     */
    public Game(GameSession session, Runnable onChange) {
        this.session = session;
        this.onChange = onChange;
        this.computerRequested = session.vsComputer();
        this.clock = new GameClock(session.minutesPerSide());
        clock.switchTo(chessGame.sideToMove());
        if (computerRequested) {
            Stockfish engine = new Stockfish();
            if (engine.startEngine()) {
                engine.setUCINewGame();
                engine.setSkillLevel(session.difficulty().skillLevel());
                if (session.difficulty().isFullStrength()) {
                    int threads = Math.clamp(Runtime.getRuntime().availableProcessors() / 2, 1, MAX_ENGINE_THREADS);
                    engine.setResources(threads, FULL_STRENGTH_HASH_MB);
                }
                stockfish = engine;
            } else {
                log.warn("Playing the computer was chosen, but Stockfish couldn't be started; "
                        + "this game is two players at one board instead");
            }
        }
        log.info("Game started ({})", isVsComputer() ? "player vs computer" : "two players");
    }

    public ChessGame chessGame() {
        return chessGame;
    }

    public GameSession session() {
        return session;
    }

    public GameClock clock() {
        return clock;
    }

    /**
     * Called regularly by the screen: ends the game if a side has run out of time.
     */
    public void tick() {
        if (chessGame.isOver()) {
            return;
        }
        Color flagged = clock.flagged();
        if (flagged != null) {
            log.info("{} ran out of time", flagged);
            chessGame.timeout(flagged);
            afterChange();
        }
    }

    /**
     * @return true if a draw can be offered: a game between two people that's still going. (The computer
     * never accepts one.)
     */
    public boolean canOfferDraw() {
        return !chessGame.isOver() && !isVsComputer();
    }

    /**
     * The side to move offered a draw and the opponent accepted.
     */
    public void agreeDraw() {
        if (!canOfferDraw()) {
            return;
        }
        log.info("Draw agreed");
        chessGame.agreeDraw();
        afterChange();
    }

    /**
     * @param handler called if the computer opponent stops working mid-game (after which the game continues
     *                as two players at one board), so the screen can tell the player
     */
    public void setOnComputerFailure(Runnable handler) {
        this.onComputerFailure = handler;
    }

    /**
     * @return true if the player chose to play the computer but Stockfish couldn't be started, in which case
     * the game falls back to two players at one board
     */
    public boolean isComputerUnavailable() {
        return computerRequested && stockfish == null;
    }

    public boolean isVsComputer() {
        return stockfish != null;
    }

    /**
     * @return true if a person is allowed to move right now (the game is on and it isn't the computer's turn)
     */
    public boolean isHumanTurn() {
        return !chessGame.isOver() && !isComputerTurn();
    }

    /**
     * @return true while the computer is working out its move
     */
    public boolean isComputerThinking() {
        return !chessGame.isOver() && isComputerTurn();
    }

    private boolean isComputerTurn() {
        return isVsComputer() && chessGame.sideToMove() == COMPUTER_COLOR;
    }

    /**
     * @return the moves a person may make with the piece on this square right now (empty when it's not their
     * turn or the piece isn't theirs)
     */
    public List<Move> movesFor(Square square) {
        return isHumanTurn() ? chessGame.legalMovesFrom(square) : List.of();
    }

    /**
     * Plays a legal move, then lets the computer reply if it's its turn.
     *
     * @throws IllegalArgumentException if the move isn't legal
     */
    public void play(Move move) {
        String san = Notation.toSan(chessGame.position(), move); // also rejects illegal moves
        int moveNumber = chessGame.position().fullmoveNumber();
        boolean white = chessGame.sideToMove() == Color.WHITE;
        chessGame.play(move);
        log.debug("Move {}{} {}", moveNumber, white ? "." : "...", san);
        afterChange();
        if (isComputerTurn() && !chessGame.isOver()) {
            requestComputerMove();
        }
    }

    /**
     * @return true if the undo button should be enabled: there's a move to take back, the game isn't over,
     * and it's a person's turn. (Finished games can't be undone, so each result is recorded exactly once.)
     */
    public boolean canUndo() {
        return chessGame.canUndo() && isHumanTurn();
    }

    /**
     * Takes back the last move. Against the computer, also takes back its reply, so it's the player's turn
     * again.
     */
    public void undo() {
        if (!canUndo()) {
            return;
        }
        Move undone = chessGame.undo();
        if (isComputerTurn() && chessGame.canUndo()) {
            Move playerMove = chessGame.undo();
            log.debug("Took back {} and the computer's reply {}", playerMove, undone);
        } else {
            log.debug("Took back {}", undone);
        }
        afterChange();
    }

    /**
     * The side to move resigns.
     */
    public void resign() {
        if (chessGame.isOver()) {
            return;
        }
        log.info("{} resigned", chessGame.sideToMove() == Color.WHITE ? "White" : "Black");
        chessGame.resign(chessGame.sideToMove());
        afterChange();
    }

    /**
     * Cleans up when leaving the game screen. An unfinished game isn't recorded.
     */
    public void leave() {
        closed = true;
        if (!chessGame.isOver()) {
            log.info("Left an unfinished game after {} moves; it won't be recorded", chessGame.moves().size());
        }
        clock.stop();
        engineThread.shutdownNow(); // also cuts short the computer's pause, if it's in one
        if (stockfish != null) {
            stockfish.stopEngine();
        }
    }

    private void afterChange() {
        if (chessGame.isOver()) {
            clock.stop();
            recordResult();
        } else {
            clock.switchTo(chessGame.sideToMove());
        }
        onChange.run();
    }

    private void requestComputerMove() {
        Position position = chessGame.position();
        String fen = position.toFen();
        Stockfish engine = stockfish;
        Difficulty difficulty = session.difficulty();
        // Stockfish answers almost instantly at the easier levels; pause like a person would before moving
        long delayMillis = difficulty.moveDelayMillis(chessGame.legalMoves().size(), position.fullmoveNumber(),
                clock.remainingMillis(COMPUTER_COLOR), random);
        // Think on the engine thread; apply the answer back on the UI thread
        engineThread.execute(() -> {
            long started = System.nanoTime();
            String bestMove = engine.getBestMove(fen, difficulty.thinkMillis());
            long pauseLeft = delayMillis - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
            if (pauseLeft > 0) {
                try {
                    Thread.sleep(pauseLeft);
                } catch (InterruptedException e) {
                    return; // the game screen was left
                }
            }
            Platform.runLater(() -> applyComputerMove(fen, bestMove));
        });
    }

    private void applyComputerMove(String fenAskedAbout, String bestMove) {
        // The game may have moved on while the computer was thinking (time ran out, or the screen was left)
        if (closed || stockfish == null || chessGame.isOver() || !isComputerTurn()
                || !chessGame.position().toFen().equals(fenAskedAbout)) {
            return;
        }
        if (bestMove == null) {
            computerFailed("Stockfish didn't return a move");
            return;
        }
        try {
            play(Move.parse(bestMove));
        } catch (IllegalArgumentException e) {
            log.error("Stockfish's move \"{}\" isn't legal in {}", bestMove, fenAskedAbout, e);
            computerFailed("Stockfish sent an illegal move");
        }
    }

    /**
     * The computer stopped working mid-game: carry on as two players at one board, rather than leaving the
     * game stuck waiting for a move that will never come.
     */
    private void computerFailed(String reason) {
        log.error("{}; continuing as two players at one board", reason);
        stockfish.stopEngine();
        stockfish = null;
        afterChange();
        onComputerFailure.run();
    }

    private void recordResult() {
        if (resultRecorded) {
            return;
        }
        resultRecorded = true;
        GameResult result = chessGame.result();
        log.info("Game over after {} moves: {} ({})", chessGame.moves().size(), result.describe(), result.score());
        Database.insert(session.white(), session.black(),
                leaderboardResult(result, Color.WHITE), leaderboardResult(result, Color.BLACK));
    }

    private static String leaderboardResult(GameResult result, Color side) {
        if (result.isDraw()) {
            return "Tie";
        }
        return result.winner() == side ? "Win" : "Lose";
    }
}
