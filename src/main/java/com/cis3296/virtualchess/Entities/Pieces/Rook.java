package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;

public class Rook extends Piece {

    public boolean hasRookMoved;

    /**
     * Constructor for a Rook type piece
     * @param coordinates are the coordinates of the Rook on the board
     * @param color chooses the color of the piece
     */
    public Rook(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        hasRookMoved = false;
        this.type = "rook";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

        // Top direction
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

        return moveSet;
    }

}
