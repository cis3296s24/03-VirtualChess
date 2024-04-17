package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import java.util.ArrayList;

public class Pawn extends Piece {

    private final int UP = -1;
    private final int DOWN = 1;
    private int direction = DOWN;
    // directions are in respect to the pawn's location
    // i.e. black pawn's forward goes up, white pawn's "forward" goes down
    private boolean blockedForward;

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
        blockedForward = false;
    }

    @Override
    public ArrayList<Coordinates> getMoveSet(){
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        Coordinates targetCoordinates;

        // only allows pawns to move two squares on their first turn
        if(color.equals("white") && whitePawnFirstMove){
            targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + (direction-1));
            addCoordinates(moveSet, targetCoordinates);
        } else if (color.equals("black") && blackPawnFirstMove){
            targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + (direction+1));
            addCoordinates(moveSet, targetCoordinates);
        }

        // Handles the movement moving forward
        targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + direction);
        if(checkForForwardPieces(targetCoordinates)){
            addCoordinates(moveSet, targetCoordinates);
        }

        if(blockedForward){
            targetCoordinates = new Coordinates(this.coordinates.getCol() - direction, this.coordinates.getRow() + direction);
            if(checkForDiagPiece(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            targetCoordinates = new Coordinates(this.coordinates.getCol() + direction, this.coordinates.getRow() + direction);
            if(checkForDiagPiece(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        return moveSet;
    }

    /**
     * Checks to see whether a pawn's forward movement is blocked
     * @param targetCoordinates the desired forward coordinates
     * @return whether the path is blocked or not
     */
    public boolean checkForForwardPieces(Coordinates targetCoordinates){
        for(BoardSquare square: board.boardSquares){
            if(square.coordinates.equals(targetCoordinates)){
                if(!board.pieceToSquare.containsKey(square)){
                    blockedForward = false;
                    return true;
                }
            }
        }
        blockedForward = true;
        return false;
    }

    /**
     *
     * @param targetCoordinates
     * @return
     */
    public boolean checkForDiagPiece(Coordinates targetCoordinates){
        for(BoardSquare square: board.boardSquares){
            if(square.coordinates.equals(targetCoordinates)){
                if(board.pieceToSquare.containsKey(square)){
                    return true;
                }
            }
        }
        return false;
    }
}
