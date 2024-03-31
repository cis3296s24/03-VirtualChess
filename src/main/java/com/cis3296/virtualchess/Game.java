package com.cis3296.virtualchess;

import javafx.scene.layout.GridPane;

public class Game {

    private Board chessBoard;

    public Game(GridPane chessBoard){
        this.chessBoard = new Board(chessBoard);
    }

}
