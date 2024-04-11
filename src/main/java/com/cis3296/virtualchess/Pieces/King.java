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
