package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;
import com.cis3296.virtualchess.Board.Board;
import java.util.ArrayList;

public class Pawn extends Piece {

    private final int UP = -1;
    private final int DOWN = 1;

    private int direction = DOWN;

    /**
     * Constructor for a Pawn type piece
     * @param coordinates are the coordinates of the Pawn on the board
     * @param color chooses the color of the piece
     */
    public Pawn(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "pawn";
        setImage();
        if(color.equals("white")){
            direction = UP;
        } else {
            direction = DOWN;
        }
    }

    @Override
    public ArrayList<Coordinates> getMoveSet(){
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        Coordinates targetCoordinates;

            targetCoordinates = new Coordinates(this.coordinates.getCol() - direction, this.coordinates.getRow() + direction);
            addCoordinates(moveSet, targetCoordinates);
            targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + direction);
            addCoordinates(moveSet, targetCoordinates);
            targetCoordinates = new Coordinates(this.coordinates.getCol() + direction, this.coordinates.getRow() + direction);
            addCoordinates(moveSet, targetCoordinates);
        return moveSet;
    }


}
