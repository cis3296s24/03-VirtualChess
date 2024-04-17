package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Components.BoardStyle;
import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Entities.Pieces.Piece;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Game {

    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardStyle.SANDCASTLE);
    public TurnSystem turnSystem;

    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        getTheme();
        this.turnSystem = new TurnSystem(new Player("Player1"), new Player("Player2"), this);
        this.chessBoard = new Board(chessBoard, boardSettings, this);
    }

    public void getTheme() {
        File configFile;
        Properties props = new Properties();

        try {
            configFile = new File("config.xml");
            FileInputStream in = new FileInputStream(configFile);
            props.loadFromXML(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String theme = props.get(BoardSettings.CONFIG_ACCESS_STRING).toString();
        BoardStyle style = BoardSettings.getStyleFromString(theme);
        boardSettings.currentBoardStyle = style;
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
    }

    public void endGame(){
        turnSystem.stop();
    }
}
