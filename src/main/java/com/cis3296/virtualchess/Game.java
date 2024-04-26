package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Components.BoardStyle;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Pieces.King;
import com.cis3296.virtualchess.Entities.Pieces.Piece;
import com.cis3296.virtualchess.Systems.Database;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;

/**
 * The main game code that manages everything
 */
public class Game {

    // The turnsystem being used to track players, turns, and timers
    private TurnSystem turnSystem;
    // The ChessBoard itself
    public Board chessBoard;
    // Settings for the board
    public BoardSettings boardSettings = new BoardSettings(BoardStyle.SANDCASTLE);
    // The stockfish process
    private Stockfish stockfish = new Stockfish();
    // The Board represented in FEN
    public String FEN;




    /**
     * Constructor for the game
     * @param chessBoard A {@link GridPane} where the ChessBoard will be rendered
     */
    public Game(GridPane chessBoard) {
        getTheme();
        this.turnSystem = TurnSystem.getInstance();
        this.turnSystem.start();
        this.chessBoard = new Board(chessBoard, boardSettings, this);
        this.FEN = this.chessBoard.toString();
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))) setupStockfish();
    }

    /**
     * Starts Stockfish
     */
    private void setupStockfish() {
        // Tries to start the engine. Could fail. Might need to fix later
        stockfish.startEngine();
        // Makes sure UCI is enabled to send commands
        stockfish.setUCINewGame();
    }

    /**
     * Access the {@link BoardSettings} to get the theme and sets the current theme to that
     */
    public void getTheme() {
        String theme = BoardSettings.getConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING);
        boardSettings.currentBoardStyle = BoardSettings.getStyleFromString(theme);
    }

    /**
     * The method that is run after each piece is placed on the board
     */
    public void handleTurn() {
        turnSystem.changeTurn();

        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))){
            Platform.runLater(() ->{
                String move;
                FEN = this.chessBoard.toString();
                stockfish.drawBoard(FEN);
                if(turnSystem.currentColor.equals("black")){
                    move = stockfish.getBestMove(FEN, 100);

                    System.out.println(move);
                    this.chessBoard.moveFromTo(new Coordinates(move.substring(0, 2)), new Coordinates(move.substring(2, 4)));
                }
            });
        }

        for(Piece piece: this.chessBoard.pieces){
            if (turnSystem.currentColor.equals("white") && piece.color.equals("white")) {
                piece.twoStepped = false;
            } else if (turnSystem.currentColor.equals("black") && piece.color.equals("black")) {
                piece.twoStepped = false;
            }
            if(piece.color.equals("white")){
                piece.isTurn = !piece.isTurn;
            }
            if(piece.color.equals("black")){
                piece.isTurn = !piece.isTurn;
            }
        }

        for (Piece piece : chessBoard.pieces) {
            piece.guardedSquares.clear();
            piece.currentMoveSet.clear();
            piece.currentMoveSet = piece.getMoveSetSuper();
        }

        for (Piece piece : chessBoard.pieces) {
            if (piece instanceof King && ((King) piece).isInCheck()) {
                piece.inCheck = true;
                handleCheck();
            }
        }

        if(turnSystem.isCheckMate){
            endGame();
        }
        if(turnSystem.isCheck){
            turnSystem.isCheckMate = true;
        }

    }

    /**
     * Not in use
     */
    public void handleCheck() {
        System.out.println("Check");

    }

    /**
     * Ends the game and clean things up
     * Inserts into the DB, stops the turn system, and stops stockfish
     */
    public void endGame(){
        Database.insert(turnSystem.getWhitePlayer(), turnSystem.getBlackPlayer(), "Lose", "Win");
        turnSystem.stop();
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))){
            stockfish.stopEngine();
        }
    }
}
