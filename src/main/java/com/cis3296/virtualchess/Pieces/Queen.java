package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

import java.util.ArrayList;

public class Queen extends Piece {

    /**
     * Constructor for a Queen type piece
     * @param coordinates are the coordinates of the Queen on the board
     * @param color chooses the color of the piece
     */
    public Queen(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "queen";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        return null;
    }


}
