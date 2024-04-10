package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

public class Rook extends Piece {

    /**
     *
     * @param coordinates
     * @param color
     */
    public Rook(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "rook";
        setImage();
    }

}
