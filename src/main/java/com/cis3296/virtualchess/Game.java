package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Components.BoardStyle;
import com.cis3296.virtualchess.Entities.Pieces.Piece;
import com.cis3296.virtualchess.Systems.Database;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.scene.layout.GridPane;

public class Game {

    private TurnSystem turnSystem;
    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardStyle.SANDCASTLE);

    private boolean isCheck = false;
    private boolean isCheckMate = false;

    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        getTheme();
        this.turnSystem = TurnSystem.getInstance();
        this.turnSystem.start();
        this.chessBoard = new Board(chessBoard, boardSettings, this);
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
        if(isCheckMate){
            endGame();
        }
        if(isCheck){
            isCheckMate = true;
        }

    }

    public void endGame(){
        Database.insert(turnSystem.getWhitePlayer(), turnSystem.getBlackPlayer(), "Lose", "Win");
        turnSystem.stop();
    }
}
