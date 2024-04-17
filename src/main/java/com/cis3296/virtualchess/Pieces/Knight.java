package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Data.Coordinates;

import java.util.ArrayList;
import com.cis3296.virtualchess.Board.Board;

public class Knight extends Piece {

    /**
     * Constructor for a Knight type piece
     * @param coordinates are the coordinates of the Knight on the board
     * @param color chooses the color of the piece
     */
    public Knight(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "knight";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();

        Coordinates targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 2);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() + 2);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 2, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 2, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() - 2, this.coordinates.getRow() - 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() - 2, this.coordinates.getRow() + 1);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() - 2);
        addCoordinates(moveSet, targetCoordinates);

        targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() - 2);
        addCoordinates(moveSet, targetCoordinates);

        return moveSet;
    }


}
