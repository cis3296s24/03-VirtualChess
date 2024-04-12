package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board.Board;
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

    @Override
    public boolean canMoveBlack(Coordinates targetCoordinates) {
        return false;
    }

    @Override
    public boolean canMoveWhite(Coordinates targetCoordinates) {
        return false;
    }

    @Override
    public void showMoves(Board board) {

    }

}
