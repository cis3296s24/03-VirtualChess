package com.cis3296.virtualchess.Pieces;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Pawn extends Piece {

    /**
     * Constructor for a piece in the board
     * @param x - The X position of the square in the board
     * @param y - The X position of the square in the board
     */
    public Pawn(int x, int y, String color){
        super(x, y, color);
        this.type = "pawn";
        setImage();
    }

}
