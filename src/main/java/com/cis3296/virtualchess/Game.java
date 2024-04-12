package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Board.BoardSettings;
import javafx.scene.layout.GridPane;


public class Game {

    // Not in use for now
    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardSettings.SANDCASTLE);

    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        this.chessBoard = new Board(chessBoard, boardSettings);
    }


}
