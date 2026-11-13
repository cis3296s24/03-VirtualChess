package com.cis3296.virtualchess;

import com.cis3296.virtualchess.engine.ChessGame;
import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.GameResult;
import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Square;
import com.cis3296.virtualchess.Systems.Database;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.application.Platform;

import java.util.List;

/**
 * Runs one game on the game screen: the rules (through {@link ChessGame}), the clocks, the computer
 * opponent, and recording the result on the leaderboard.
 * <p>
 * The screen calls {@link #play}, {@link #undo}, and {@link #resign} in response to the player, and gets
 * the {@code onChange} callback whenever it should redraw.
 */
public class Game {

    // The computer always plays Black; the player selection screen only names the human (White) player
    private static final Color COMPUTER_COLOR = Color.BLACK;
    private static final int COMPUTER_THINK_MILLIS = 1000;

    private final ChessGame chessGame = new ChessGame();
    private final TurnSystem turnSystem = TurnSystem.getInstance();
    private final Runnable onChange;
    private final boolean computerRequested;
    private Stockfish stockfish; // null unless playing the computer and the engine started
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
            }
        }
    }

    public ChessGame chessGame() {
        return chessGame;
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
     */
    public void play(Move move) {
        chessGame.play(move);
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
        chessGame.undo();
        if (isComputerTurn() && chessGame.canUndo()) {
            chessGame.undo();
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
        chessGame.resign(chessGame.sideToMove());
        afterChange();
    }

    /**
     * Cleans up when leaving the game screen. An unfinished game isn't recorded.
     */
    public void leave() {
        closed = true;
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
                System.err.println("Stockfish didn't return a move for " + chessGame.position().toFen());
                return;
            }
            play(Move.parse(bestMove));
        });
    }

    private void recordResult() {
        if (resultRecorded) {
            return;
        }
        resultRecorded = true;
        GameResult result = chessGame.result();
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
