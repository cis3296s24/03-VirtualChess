package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import java.util.ArrayList;

public class Queen extends Piece {
    /**
     * Constructor for a Queen type piece
     * @param coordinates are the coordinates of the Queen on the board
     * @param color chooses the color of the piece
     */
    public Queen(Coordinates coordinates, String color, Board board, boolean isTurn){
        super(coordinates, color, board, isTurn);
        this.type = "queen";
        setImage();
    }

    /**
    * Determines the possible moves that a bishop can make based on its current position.
    * @return a list of possible coordinates the bishop can move to.
    */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

        // Get diagonal moves in all four directions
        moveSet.addAll(getDiagonalMoves(-1, -1)); // Top-left
        moveSet.addAll(getDiagonalMoves(-1, 1)); // Top-right
        moveSet.addAll(getDiagonalMoves(1, -1)); // Bottom-left
        moveSet.addAll(getDiagonalMoves(1, 1)); // Bottom-right
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
     * Helper method to get all diagonal moves in a specific direction.
     * @param rowDirection the row direction (-1 for up, 1 for down).
     * @param colDirection the column direction (-1 for left, 1 for right).
     * @return a list of coordinates representing the moves.
     */
    private ArrayList<Coordinates> getDiagonalMoves(int rowDirection, int colDirection) {
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        int currentRow = coordinates.getRow() + rowDirection;
        int currentCol = coordinates.getCol() + colDirection;

        while (isValidMove(currentRow, currentCol)) {
            Coordinates targetCoordinates = new Coordinates(currentCol, currentRow);
            Piece piece = board.getPieceAt(targetCoordinates);

            if (piece == null) {
                moveSet.add(targetCoordinates);
            } else {
                if (!piece.color.equals(this.color)) {
                    moveSet.add(targetCoordinates);
                }
                break; // Stop moving further in this direction
            }

            currentRow += rowDirection;
            currentCol += colDirection;
        }

        return moveSet;
    }

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
     * Checks whether the target coordinates are within the bounds of the board.
     * @param row the row of the target coordinates.
     * @param col the column of the target coordinates.
     * @return true if the coordinates are within bounds, false otherwise.
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

}
