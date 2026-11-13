package com.cis3296.virtualchess;

import com.cis3296.virtualchess.engine.ChessGame;
import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.GameResult;
import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Notation;
import com.cis3296.virtualchess.engine.Square;
import com.cis3296.virtualchess.Systems.Database;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    private static final int COMPUTER_THINK_MILLIS = 1000;

    private final ChessGame chessGame = new ChessGame();
    private final TurnSystem turnSystem = TurnSystem.getInstance();
    private final Runnable onChange;
    private final boolean computerRequested;
    private Stockfish stockfish; // null unless playing the computer and the engine is working
    private Runnable onComputerFailure = () -> { };
    private boolean resultRecorded;
    private boolean closed;

    /**
     * @param vsComputer true for player vs computer, false for two players at one board
     * @param onChange   called after every change to the game, so the screen can redraw
     */
    public Game(boolean vsComputer, Runnable onChange) {
        this.onChange = onChange;
        this.computerRequested = vsComputer;
        turnSystem.start();
        turnSystem.setTurn(chessGame.sideToMove());
        if (vsComputer) {
            Stockfish engine = new Stockfish();
            if (engine.startEngine()) {
                engine.setUCINewGame();
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
        turnSystem.stop();
        if (stockfish != null) {
            stockfish.stopEngine();
        }
    }

    private void afterChange() {
        if (chessGame.isOver()) {
            turnSystem.pauseClocks();
            recordResult();
        } else {
            turnSystem.setTurn(chessGame.sideToMove());
        }
        onChange.run();
    }

    private void requestComputerMove() {
        // runLater lets the player's move finish processing first. Stockfish still runs on the UI thread,
        // so the window pauses while it thinks; moving it to a background thread is planned separately.
        Platform.runLater(() -> {
            if (closed || !isComputerTurn() || chessGame.isOver()) {
                return;
            }
            String bestMove = stockfish.getBestMove(chessGame.position().toFen(), COMPUTER_THINK_MILLIS);
            if (bestMove == null) {
                computerFailed("Stockfish didn't return a move");
                return;
            }
            try {
                play(Move.parse(bestMove));
            } catch (IllegalArgumentException e) {
                log.error("Stockfish's move \"{}\" isn't legal in {}", bestMove, chessGame.position().toFen(), e);
                computerFailed("Stockfish sent an illegal move");
            }
        });
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
        Database.insert(turnSystem.getWhitePlayer(), turnSystem.getBlackPlayer(),
                leaderboardResult(result, Color.WHITE), leaderboardResult(result, Color.BLACK));
    }

    private static String leaderboardResult(GameResult result, Color side) {
        if (result.isDraw()) {
            return "Tie";
        }
        return result.winner() == side ? "Win" : "Lose";
    }
}
