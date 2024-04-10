package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

public class King extends Piece {

    /**
     * Constructor for a piece in the board
     * @param coordinates
     * @param color
     */
    public King(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "king";
        setImage();
    }

}
