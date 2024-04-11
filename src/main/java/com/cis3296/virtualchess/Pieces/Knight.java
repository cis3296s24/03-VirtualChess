package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;

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
