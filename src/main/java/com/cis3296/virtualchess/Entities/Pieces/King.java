package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;

public class King extends Piece {

    /**
     * Constructor for a King type piece
     * @param coordinates are the coordinates of the King on the board
     * @param color chooses the color of the piece
     * @param board current board the piece is on
     */
    public King(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "king";
        setImage();
    }

    /**
     * This method determines the possible moves that a king can move based on their current position
     * @return a set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        // Set to return with all possible coordinates based on current position
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        // Coordinates to be added in the move set
        Coordinates targetCoordinates;


        // Move to the North
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow());
        addCoordinates(moveSet, targetCoordinates);
        // Move to the South
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow());
        addCoordinates(moveSet, targetCoordinates);
        // Move to the West
        targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move to the East
        targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        // Move to the North-East
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move to the North-West
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move to the South-East
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates); // Move to the South-West
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);

        return moveSet;
    }


}
