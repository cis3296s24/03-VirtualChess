package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

public class Knight extends Piece {

    /**
     *
     * @param coordinates
     * @param color
     */
    public Knight(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "knight";
        setImage();
    }

}
