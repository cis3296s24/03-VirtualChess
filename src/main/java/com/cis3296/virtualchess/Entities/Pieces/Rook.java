package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;

public class Rook extends Piece {

    private boolean blockedNorth;
    private boolean opponentNorth;
    private boolean blockedSouth;
    private boolean opponentSouth;
    private boolean blockedWest;
    private boolean opponentWest;
    private boolean blockedEast;
    private boolean opponentEast;

    /**
     * Constructor for a Rook type piece
     * @param coordinates are the coordinates of the Rook on the board
     * @param color chooses the color of the piece
     */
    public Rook(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "rook";
        setImage();
    }

    /**
     * This method determines the possible moves that a Rook can move based on their current position
     * @return a set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        // Set to return with all possible coordinates based on current position
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        blockedNorth = false;
        opponentNorth = false;
        blockedSouth = false;
        opponentSouth = false;
        blockedWest = false;
        opponentWest = false;
        blockedEast = false;
        opponentEast = false;


        // Top direction
        for (int row = coordinates.getRow() - 1; row >= 0; row--) {
            // desired coordinates above the rook
            Coordinates targetCoordinates = new Coordinates(coordinates.getCol(), row);
            // As long as there are no pieces above the rook, it can move there
            if(!blockedNorth && checkForPiecesNorth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent above the rook, it can take that opponent
            if(!opponentNorth && checkForOpponentNorth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Bottom direction
        for (int row = coordinates.getRow() + 1; row < 8; row++) {
            // desired coordinates below the rook
            Coordinates targetCoordinates = new Coordinates(coordinates.getCol(), row);
            // As long as there are no pieces below the rook, it can move there
            if(!blockedSouth && checkForPiecesSouth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent below the rook, it can take that opponent
            if(!opponentSouth && checkForOpponentSouth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Right direction
        for (int col = coordinates.getCol() + 1; col < 8; col++) {
            // desired coordinates
            Coordinates targetCoordinates = new Coordinates(col, coordinates.getRow());
            // As long as there are no pieces to the right of the rook, it can move there
            if(!blockedEast && checkForPiecesEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent to the right of the rook, it can take that opponent
            if(!opponentEast && checkForOpponentEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Left direction
        for (int col = coordinates.getCol() - 1; col >= 0; col--) {
            // desired coordinates
            Coordinates targetCoordinates = new Coordinates(col, coordinates.getRow());
            // As long as there are no pieces to the left of the rook, it can move there
            if(!blockedWest && checkForPiecesWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent to the left of the rook, it can take that opponent
            if(!opponentWest && checkForOpponentWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        return moveSet;
    }

    /**
     * Checks to see whether a Rook's Northern movement is blocked
     * @param targetCoordinates the desired Northern coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesNorth(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the rook can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedNorth = false;
                    return true;
                }
            }
        }
        blockedNorth = true;
        return false;
    }

    /**
     * Checks to see whether a Rook's Southern movement is blocked
     * @param targetCoordinates the desired Southern coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesSouth(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the rook can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedSouth = false;
                    return true;
                }
            }
        }
        blockedSouth = true;
        return false;
    }

    /**
     * Checks to see whether a Rook's Western movement is blocked
     * @param targetCoordinates the desired Western coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesWest(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the rook can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedWest = false;
                    return true;
                }
            }
        }
        blockedWest = true;
        return false;
    }

    /**
     * Checks to see whether a Rook's Eastern movement is blocked
     * @param targetCoordinates the desired Eastern coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesEast(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the rook can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedEast = false;
                    return true;
                }
            }
        }
        blockedEast = true;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Rook's North
     * @param targetCoordinates the desired Northern coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentNorth(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the rook can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentNorth = true;
                    return true;
                }
            }
        }
        opponentNorth = false;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Rook's South
     * @param targetCoordinates the desired Southern coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentSouth(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the rook can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentSouth = true;
                    return true;
                }
            }
        }
        opponentSouth = false;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Rook's West
     * @param targetCoordinates the desired Western coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentWest(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the rook can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentWest = true;
                    return true;
                }
            }
        }
        opponentWest = false;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Rook's East
     * @param targetCoordinates the desired Eastern coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentEast(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of rook's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the rook can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentEast = true;
                    return true;
                }
            }
        }
        opponentEast = false;
        return false;
    }
}
