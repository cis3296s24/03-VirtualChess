package com.cis3296.virtualchess.Entities;

import com.cis3296.virtualchess.Entities.Pieces.Piece;

public class Move {
    private Piece piece;
    private Coordinates previousCoordinates;

    /**
     * Used to store in a stack to undo moves
     * @param piece the most recent piece moved
     * @param previousCoordinates the coordinates of the most recent piece
     */
    public Move(Piece piece, Coordinates previousCoordinates) {
        this.piece = piece;
        this.previousCoordinates = previousCoordinates;
    }

    /**
     * Getter
     * @return recent piece
     */
    public Piece getPiece(){
        return piece;
    }

    /**
     * Getter
     * @return recent piece previous coordinates
     */
    public Coordinates getPreviousCoordinates(){
        return previousCoordinates;
    }

}
