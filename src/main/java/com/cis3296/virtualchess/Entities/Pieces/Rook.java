package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;

/**
 * Models the moveset of the rook piece
 */
public class Rook extends Piece {

    public Coordinates coordBeforeCastle;
    public boolean castled = false;
    /**
     * Constructor for a Rook type piece
     * @param coordinates are the coordinates of the Rook on the board
     * @param color chooses the color of the piece
     */
    public Rook(Coordinates coordinates, String color, Board board, boolean isTurn){
        super(coordinates, color, board, isTurn);
        this.type = "rook";
        setImage();
    }

    /**
     * Checks north, east, south, and west for valid position to add to the moveset
     * @param moveSet An arraylist containing {@link Coordinates} that represents valid moves for the piece
     * @param rowOffset Direction up or down
     * @param colOffset Direction side to side
     */
    public void checkDirection(ArrayList<Coordinates> moveSet, int rowOffset, int colOffset) {
        int row = coordinates.getRow() + rowOffset;
        int col = coordinates.getCol() + colOffset;

        while (row >= 0 && row < 8 && col >= 0 && col < 8) {
            Coordinates targetCoordinates = new Coordinates(col, row);
            Piece targetPiece = board.getPieceAt(targetCoordinates);

            if (targetPiece == null) {
                moveSet.add(targetCoordinates);
            } else {
                if (!targetPiece.color.equals(color)) {
                    moveSet.add(targetCoordinates);
                }
                break;
            }

            row += rowOffset;
            col += colOffset;
        }
    }

    /**
     * This method determines the possible moves that a Rook can move based on their current position
     * @return a move set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

        int[][] directions = {
                {-1, 0}, // North
                {1, 0}, // South
                {0, 1}, // East
                {0, -1}, // West
        };

        for (int[] direction : directions) {
            checkDirection(moveSet, direction[0], direction[1]);
        }

        return moveSet;
    }

    /**
     * Generates a string for the FEN representation of this piece
     * @return A string containing the lowercase first letter if black or capital if white
     */
    @Override
    public String toString() {
        if(color.equals("white")){
            return "R";
        } else{
            return "r";
        }
    }
}
