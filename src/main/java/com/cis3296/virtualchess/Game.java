package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Components.BoardStyle;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Pieces.King;
import com.cis3296.virtualchess.Entities.Pieces.Piece;
import com.cis3296.virtualchess.Systems.Database;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.scene.layout.GridPane;

public class Game {

    private TurnSystem turnSystem;
    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardStyle.SANDCASTLE);


    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        getTheme();
        this.turnSystem = TurnSystem.getInstance();
        this.turnSystem.start();
        this.chessBoard = new Board(chessBoard, boardSettings, this);
        setupStockfish(this.chessBoard.toString());
    }

    private void setupStockfish(String FEN) {
        Stockfish stockfish = new Stockfish();
        stockfish.startEngine();

        // send commands manually
        stockfish.commandBuffer(() -> {
            stockfish.setUCI();
            stockfish.startNewGame();
        });

        System.out.println(stockfish.getOutput(1000));

        // draw board from a given position
        System.out.println("Board state :");
        stockfish.drawBoard(FEN);
        stockfish.commandBuffer(() -> {

        });
        System.out.println(stockfish.getOutput(1000));


    }

    public void getTheme() {
        String theme = BoardSettings.getConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING);
        boardSettings.currentBoardStyle = BoardSettings.getStyleFromString(theme);
    }


    public void handleTurn() {
        turnSystem.changeTurn();

        for(Piece piece: this.chessBoard.pieces){
            if(piece.color.equals("white")){
                piece.isTurn = !piece.isTurn;
            }
            if(piece.color.equals("black")){
                piece.isTurn = !piece.isTurn;
            }
        }
        if(turnSystem.isCheckMate){
            endGame();
        }
        if(turnSystem.isCheck){
            turnSystem.isCheckMate = true;
        }

    }

    public void endGame(){
        Database.insert(turnSystem.getWhitePlayer(), turnSystem.getBlackPlayer(), "Lose", "Win");
        turnSystem.stop();
    }
}
