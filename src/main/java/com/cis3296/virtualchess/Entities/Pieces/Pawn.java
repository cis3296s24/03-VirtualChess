package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import java.util.ArrayList;

public class Pawn extends Piece {

    private final int UP = -1;
    private final int DOWN = 1;
    private final int direction;
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

    /**
     * This method determines the possible moves that a pawn can move based on their current position
     * @return a move set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet(){
        // Set to return with all possible coordinates based on current position
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        // Coordinates to be added in the move set
        Coordinates targetCoordinates;

        // Allows for pawns to move twice on their first move
        if(!moved){
            targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + (direction * 2));
            addCoordinates(moveSet, targetCoordinates);
        }
        // Handles the movement moving forward
        targetCoordinates = new Coordinates(this.coordinates.getCol(), this.coordinates.getRow() + direction);
        if(checkForForwardPieces(targetCoordinates)){
            addCoordinates(moveSet, targetCoordinates);
        }
        // Allow for diagonal movement if the pawn's forward path is blocked
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
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of pawn's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the pawn can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedForward = false;
                    return true;
                }
            }
        }
        // Pawn is blocked and cannot move forward
        blockedForward = true;
        return false;
    }

    /**
     * This checks whether a pawn can move diagonally.
     * If there is a piece in the diagonal path, the pawn can move
     * @param targetCoordinates the desired diagonal coordinates
     * @return true if the is not blocked
     */
    public boolean checkForDiagPiece(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of pawn's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the pawn can move there
                if(board.pieceToSquare.containsKey(square)){
                    return true;
                }
            }
        }
        // The pawn cannot move diagonally, there's no piece
        return false;
    }
}
