package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Coordinates;

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
