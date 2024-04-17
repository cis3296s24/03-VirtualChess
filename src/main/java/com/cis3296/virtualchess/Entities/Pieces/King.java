package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;

public class King extends Piece {

    public boolean hasKingMoved;

    /**
     * Constructor for a King type piece
     * @param coordinates are the coordinates of the King on the board
     * @param color chooses the color of the piece
     * @param board current board the piece is on
     */
    public King(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        hasKingMoved = false;
        this.type = "king";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

        Coordinates targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow());
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow());
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);

        return moveSet;
    }


}
