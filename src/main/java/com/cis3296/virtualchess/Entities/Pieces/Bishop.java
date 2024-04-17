package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import java.util.ArrayList;

public class Bishop extends Piece {

    /**
     * Constructor for a Bishop type piece
     * @param coordinates are the coordinates of the Bishop on the board
     * @param color chooses the color of the piece
     * @param board current board the piece is on
     */
    public Bishop(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "bishop";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

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
