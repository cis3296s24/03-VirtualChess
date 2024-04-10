package com.cis3296.virtualchess;

import javafx.scene.layout.StackPane;

public class BoardSquare extends StackPane {

    Coordinates coordinates;
    // Not used now but can later be used when pieces are added
    boolean containsPiece;

    /**
     *
     * @param col
     * @param row
     */
    public BoardSquare(int col, int row){
        this.coordinates = new Coordinates(col, row);
        this.containsPiece = false;
    }
}
