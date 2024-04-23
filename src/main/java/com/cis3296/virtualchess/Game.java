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

public class Game {

    private TurnSystem turnSystem;
    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardStyle.SANDCASTLE);

    private Stockfish stockfish = new Stockfish();
    public String FEN;




    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        getTheme();
        this.turnSystem = TurnSystem.getInstance();
        this.turnSystem.start();
        this.chessBoard = new Board(chessBoard, boardSettings, this);
        this.FEN = this.chessBoard.toString();
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))) setupStockfish();
    }

    private void setupStockfish() {
        stockfish.startEngine();

        // send commands manually
        stockfish.setUCINewGame();

        System.out.println(stockfish.getOutput(0));

        // draw board from a given position
        System.out.println("Board state :");
        stockfish.drawBoard(FEN);
    }

    public void getTheme() {
        String theme = BoardSettings.getConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING);
        boardSettings.currentBoardStyle = BoardSettings.getStyleFromString(theme);
    }


    public void handleTurn() {
        turnSystem.changeTurn();

        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))){
            Platform.runLater(() ->{
                String move = "";
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
            piece.currentMoveSet = piece.getMoveSet();
        }

        for (Piece piece : chessBoard.pieces) {
            if (piece instanceof King king && king.isInCheck(king.coordinates)) {
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

    public void handleCheck() {
        System.out.println("Check");

    }

    public void endGame(){
        Database.insert(turnSystem.getWhitePlayer(), turnSystem.getBlackPlayer(), "Lose", "Win");
        turnSystem.stop();
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))){
            stockfish.stopEngine();
        }
    }
}
