package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Coordinates;

public class Pawn extends Piece {

    /**
     * Constructor for a Pawn type piece
     * @param coordinates are the coordinates of the Pawn on the board
     * @param color chooses the color of the piece
     */
    public Pawn(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "pawn";
        setImage();
    }

    @Override
    public boolean canMoveBlack(Coordinates targetCoordinates) {
        return compareTopRight(targetCoordinates) || compareTopLeft(targetCoordinates) || compareTop(targetCoordinates);
    }

    @Override
    public boolean canMoveWhite(Coordinates targetCoordinates) {
        return compareBottomLeft(targetCoordinates) || compareBottomRight(targetCoordinates) || compareBottom(targetCoordinates);
    }

    @Override
    public void showMoves(Board board) {
        board.showMoves(this.coordinates);
    }

}
