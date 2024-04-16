package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Board.BoardSettings;
import javafx.scene.layout.GridPane;


public class Game {

    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardSettings.SANDCASTLE);
    public TurnSystem turnSystem;

    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        this.turnSystem = new TurnSystem(new Player("Test1"), new Player("Test2"));
        this.chessBoard = new Board(chessBoard, boardSettings);
        Database db = new Database();
        db.databaseTest();
    }


}
