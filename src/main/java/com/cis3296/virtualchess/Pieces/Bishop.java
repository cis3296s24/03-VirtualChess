package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

public class Bishop extends Piece {


    /**
     *
     * @param coordinates
     * @param color
     */
    public Bishop(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "bishop";
        setImage();
    }

}
