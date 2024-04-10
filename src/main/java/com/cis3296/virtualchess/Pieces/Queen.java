package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

public class Queen extends Piece {

    /**
     *
     * @param coordinates
     * @param color
     */
    public Queen(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "queen";
        setImage();
    }

}
