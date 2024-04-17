package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Data.Coordinates;
import com.cis3296.virtualchess.Board.Board;
import java.util.ArrayList;

public class Queen extends Piece {

    /**
     * Constructor for a Queen type piece
     * @param coordinates are the coordinates of the Queen on the board
     * @param color chooses the color of the piece
     */
    public Queen(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "queen";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

        // Up direction
        for (int row = coordinates.getRow() + 1; row < 8; row++) {
            Coordinates targetCoordinates = new Coordinates(coordinates.getCol(), row);
            addCoordinates(moveSet, targetCoordinates);
        }

        // Down direction
        for (int row = coordinates.getRow() - 1; row >= 0; row--) {
            Coordinates targetCoordinates = new Coordinates(coordinates.getCol(), row);
            addCoordinates(moveSet, targetCoordinates);
        }

        // Right direction
        for (int col = coordinates.getCol() + 1; col < 8; col++) {
            Coordinates targetCoordinates = new Coordinates(col, coordinates.getRow());
            addCoordinates(moveSet, targetCoordinates);
        }

        // Left direction
        for (int col = coordinates.getCol() - 1; col >= 0; col--) {
            Coordinates targetCoordinates = new Coordinates(col, coordinates.getRow());
            addCoordinates(moveSet, targetCoordinates);
        }

        // Top-right direction
        for (int col = coordinates.getCol() + 1, row = coordinates.getRow() + 1;
             col < 8 && row < 8;
             col++, row++) {
            Coordinates targetCoordinates = new Coordinates(col, row);
            addCoordinates(moveSet, targetCoordinates);
        }

        // Top-left direction
        for (int col = coordinates.getCol() - 1, row = coordinates.getRow() + 1;
             col >= 0 && row < 8;
             col--, row++) {
            Coordinates targetCoordinates = new Coordinates(col, row);
            addCoordinates(moveSet, targetCoordinates);
        }

        // Bottom-right direction
        for (int col = coordinates.getCol() + 1, row = coordinates.getRow() - 1;
             col < 8 && row >= 0;
             col++, row--) {
            Coordinates targetCoordinates = new Coordinates(col, row);
            addCoordinates(moveSet, targetCoordinates);
        }

        // Bottom-left direction
        for (int col = coordinates.getCol() - 1, row = coordinates.getRow() - 1;
             col >= 0 && row >= 0;
             col--, row--) {
            Coordinates targetCoordinates = new Coordinates(col, row);
            addCoordinates(moveSet, targetCoordinates);
        }

        return moveSet;
    }


}
