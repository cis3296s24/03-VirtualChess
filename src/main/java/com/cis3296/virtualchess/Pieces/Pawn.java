package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;
import com.cis3296.virtualchess.Board.Board;
import java.util.ArrayList;

public class Pawn extends Piece {

    /**
     * Constructor for a Pawn type piece
     * @param coordinates are the coordinates of the Pawn on the board
     * @param color chooses the color of the piece
     */
    public Pawn(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "pawn";
        setImage();
    }

    @Override
    public ArrayList<Coordinates> getMoveSet(){
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        Coordinates targetCoordinates;

        if(this.color.equals("white")){
            targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() + 1);
            addCoordinates(moveSet, targetCoordinates);
            targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + 1);
            addCoordinates(moveSet, targetCoordinates);
            targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() + 1);
            addCoordinates(moveSet, targetCoordinates);
        } else{
            targetCoordinates = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow() - 1);
            addCoordinates(moveSet, targetCoordinates);
            targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() - 1);
            addCoordinates(moveSet, targetCoordinates);
            targetCoordinates = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow() - 1);
            addCoordinates(moveSet, targetCoordinates);
        }
        return moveSet;
    }


}
