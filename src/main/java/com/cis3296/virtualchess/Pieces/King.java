package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

public class King extends Piece {

    /**
     * Constructor for a King type piece
     * @param coordinates are the coordinates of the King on the board
     * @param color chooses the color of the piece
     */
    public King(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "king";
        setImage();
    }

    public String type(){
        return "King";
    }

}
