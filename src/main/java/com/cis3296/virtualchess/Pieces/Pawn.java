package com.cis3296.virtualchess.Pieces;

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
    public boolean canMoveBlack(int targetCol, int targetRow) {
        if(targetRow == coordinates.getRow()-1){
            return true;
        } else if(targetCol == coordinates.getCol()+1 && targetRow == coordinates.getRow()+1){
            return true;
        } else if(targetCol == coordinates.getCol()+1 && targetRow == coordinates.getRow()-1){
            return true;
        }
        return false;
    }

    @Override
    public boolean canMoveWhite(int targetCol, int targetRow) {
        if(targetRow == coordinates.getRow()+1){
            return true;
        } else if(targetCol == coordinates.getCol()+1 && targetRow == coordinates.getRow()+1){
            return true;
        } else if(targetCol == coordinates.getCol()+1 && targetRow == coordinates.getRow()-1){
            return true;
        }
        return false;
    }

}
