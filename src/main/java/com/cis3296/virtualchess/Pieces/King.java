package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Board.BoardSettings;
import com.cis3296.virtualchess.Coordinates;

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

    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        moveSet.add(new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 1));
        moveSet.add(new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + 1));
        moveSet.add(new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() + 1));
        moveSet.add(new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow()));
        moveSet.add(new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow()));
        moveSet.add(new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() - 1));
        moveSet.add(new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() - 1));
        moveSet.add(new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() - 1));
        return moveSet;
    }


}
