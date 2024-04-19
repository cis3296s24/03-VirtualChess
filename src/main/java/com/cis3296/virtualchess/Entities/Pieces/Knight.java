package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;
import com.cis3296.virtualchess.Components.Board;

public class Knight extends Piece {

    /**
     * Constructor for a Knight type piece
     * @param coordinates are the coordinates of the Knight on the board
     * @param color chooses the color of the piece
     */
    public Knight(Coordinates coordinates, String color, Board board, boolean isTurn){
        super(coordinates, color, board, isTurn);
        this.type = "knight";
        setImage();
    }

    /**
     * This method determines the possible moves that a knight can move based on their current position
     * @return a move set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        // Possible moves a knight can make (row, col) from the current position.
        int[][] moves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : moves) {
            Coordinates targetCoordinates = new Coordinates(
                    this.coordinates.getCol() + move[1],
                    this.coordinates.getRow() + move[0]
            );
            addCoordinates(moveSet, targetCoordinates);
        }

        return moveSet;
    }


}
