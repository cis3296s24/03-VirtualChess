package com.cis3296.virtualchess;

import javafx.scene.layout.StackPane;

public class BoardSquare extends StackPane {

    Coordinates coordinates;
    // Not used now but can later be used when pieces are added
    boolean containsPiece;

    /**
     *
     * @param x
     * @param y
     */
    public BoardSquare(int x, int y){
        this.coordinates = new Coordinates(x, y);
        this.containsPiece = false;
    }
}
