package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

import java.util.ArrayList;

public class Knight extends Piece {

    /**
     * Constructor for a Knight type piece
     * @param coordinates are the coordinates of the Knight on the board
     * @param color chooses the color of the piece
     */
    public Knight(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "knight";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        return null;
    }


}
