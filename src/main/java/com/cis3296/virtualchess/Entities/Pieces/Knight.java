package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;
import com.cis3296.virtualchess.Components.Board;

/**
 * Models the moveset of the knight piece
 */
public class Knight extends Piece {

    /**
     * Constructor for a Knight type piece
     * @param coordinates are the {@link Coordinates} of the Knight on the board
     * @param color chooses the color of the piece
     */
    public Knight(Coordinates coordinates, String color, Board board, boolean isTurn){
        super(coordinates, color, board, isTurn);
        this.type = "knight";
        setImage();
    }

    /**
     * This method determines the possible moves that a knight can move based on their current position
     * @return an arraylist move set with possible {@link Coordinates} to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        // Set to return with all possible coordinates based on current position
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        // Coordinates to be added in the move set
        Coordinates targetCoordinates;

        // Move up 2 and right 1
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 2, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move up 2 and left 1
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 2, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move up 1 and right 2
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 2);
        addCoordinates(moveSet, targetCoordinates);
        // Move up 1 and left 2
        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() - 2);
        addCoordinates(moveSet, targetCoordinates);

        // Move down 2 and right 1
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 2, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move down 2 and left 1
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 2, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);
        // Move down 1 and right 2
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() + 2);
        addCoordinates(moveSet, targetCoordinates);
        // Move down 1 and left 2
        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() - 2);
        addCoordinates(moveSet, targetCoordinates);

        return moveSet;
    }

    /**
     * Generates a string for the FEN representation of this piece
     * @return A string containing the lowercase first letter if black or capital if white
     */
    @Override
    public String toString() {
        if(color.equals("white")){
            return "N";
        } else{
            return "n";
        }
    }


}
