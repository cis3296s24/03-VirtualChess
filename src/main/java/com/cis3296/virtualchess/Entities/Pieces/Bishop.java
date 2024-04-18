package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import java.util.ArrayList;

public class Bishop extends Piece {

    private boolean blockedNorthEast;
    private boolean opponentNorthEast;
    private boolean blockedNorthWest;
    private boolean opponentNorthWest;
    private boolean blockedSouthEast;
    private boolean opponentSouthEast;
    private boolean blockedSouthWest;
    private boolean opponentSouthWest;


    /**
     * Constructor for a Bishop type piece
     * @param coordinates are the coordinates of the Bishop on the board
     * @param color chooses the color of the piece
     * @param board current board the piece is on
     */
    public Bishop(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "bishop";
        setImage();
    }

    /**
     * This method determines the possible moves that a bishop can move based on their current position
     * @return a set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        // Set to return with all possible coordinates based on current position
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        blockedNorthEast = false;
        opponentNorthEast = false;
        blockedNorthWest = false;
        opponentNorthWest = false;
        blockedSouthEast = false;
        opponentSouthEast = false;
        blockedSouthWest = false;
        opponentSouthWest = false;

        // Top-Left direction
        for (int col = coordinates.getCol() - 1, row = coordinates.getRow() - 1;
             col >= 0 && row >= 0;
             col--, row--) {
            // desired coordinates North-West of the bishop
            Coordinates targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces North-West of the bishop, it can move there
            if(!blockedNorthWest && checkForPiecesNorthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent North-West the bishop, it can take that opponent
            if(!opponentNorthWest && checkForOpponentNorthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Top-Right direction
        for (int col = coordinates.getCol() - 1, row = coordinates.getRow() + 1;
             col >= 0 && row < 8;
             col--, row++) {
            // desired coordinates North-East of the bishop
            Coordinates targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces North-East of the bishop, it can move there
            if(!blockedNorthEast && checkForPiecesNorthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent North-East the bishop, it can take that opponent
            if(!opponentNorthEast && checkForOpponentNorthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Bottom-Left direction
        for (int col = coordinates.getCol() + 1, row = coordinates.getRow() - 1;
             col < 8 && row >= 0;
             col++, row--) {
            // desired coordinates South-West of the bishop
            Coordinates targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces South-West of the bishop, it can move there
            if(!blockedSouthWest && checkForPiecesSouthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent South-West the bishop, it can take that opponent
            if(!opponentSouthWest && checkForOpponentSouthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Bottom-right direction
        for (int col = coordinates.getCol() + 1, row = coordinates.getRow() + 1;
             col < 8 && row < 8;
             col++, row++) {
            // desired coordinates South-East of the bishop
            Coordinates targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces South-East of the bishop, it can move there
            if(!blockedSouthEast && checkForPiecesSouthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent South-East the bishop, it can take that opponent
            if(!opponentSouthEast && checkForOpponentSouthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        return moveSet;
    }

    /**
     * Checks to see whether a Bishop's North-Eastern movement is blocked
     * @param targetCoordinates the desired North-Eastern coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesNorthEast(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the bishop can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedNorthEast = false;
                    return true;
                }
            }
        }
        blockedNorthEast = true;
        return false;
    }

    /**
     * Checks to see whether a Bishop's North-Western movement is blocked
     * @param targetCoordinates the desired North-Western coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesNorthWest(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the bishop can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedNorthWest = false;
                    return true;
                }
            }
        }
        blockedNorthWest = true;
        return false;
    }

    /**
     * Checks to see whether a Bishop's South-Eastern movement is blocked
     * @param targetCoordinates the desired South-Eastern coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesSouthEast(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the bishop can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedSouthEast = false;
                    return true;
                }
            }
        }
        blockedSouthEast = true;
        return false;
    }

    /**
     * Checks to see whether a Bishop's South-Western movement is blocked
     * @param targetCoordinates the desired South-Western coordinates
     * @return true if the is not blocked
     */
    public boolean checkForPiecesSouthWest(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square does not have a piece then the bishop can move there
                if(!board.pieceToSquare.containsKey(square)){
                    // Ensure the piece is not blocked
                    blockedSouthWest = false;
                    return true;
                }
            }
        }
        blockedSouthWest = true;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Bishop's North-East
     * @param targetCoordinates the desired North-Eastern coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentNorthEast(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the bishop can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentNorthEast = true;
                    return true;
                }
            }
        }
        opponentNorthEast = false;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Bishop's North-West
     * @param targetCoordinates the desired North-Western coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentNorthWest(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the bishop can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentNorthWest = true;
                    return true;
                }
            }
        }
        opponentNorthWest = false;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Bishop's South-West
     * @param targetCoordinates the desired South-Western coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentSouthWest(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the bishop can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentSouthWest = true;
                    return true;
                }
            }
        }
        opponentSouthWest = false;
        return false;
    }

    /**
     * Checks to see whether an opponent is to the Bishop's South-East
     * @param targetCoordinates the desired South-Eastern coordinates
     * @return true if there is an opponent
     */
    public boolean checkForOpponentSouthEast(Coordinates targetCoordinates){
        // Go through all board squares
        for(BoardSquare square: board.boardSquares){
            // Find the square with the matching coordinates of bishop's pathing
            if(square.coordinates.equals(targetCoordinates)){
                // If the square has a piece then the bishop can move there
                if(board.pieceToSquare.containsKey(square)){
                    opponentSouthEast = true;
                    return true;
                }
            }
        }
        opponentSouthEast = false;
        return false;
    }
}
