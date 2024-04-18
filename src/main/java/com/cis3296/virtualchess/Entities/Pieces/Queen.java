package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import java.util.ArrayList;

public class Queen extends Piece {

    private boolean blockedNorth;
    private boolean opponentNorth;
    private boolean blockedSouth;
    private boolean opponentSouth;
    private boolean blockedWest;
    private boolean opponentWest;
    private boolean blockedEast;
    private boolean opponentEast;
    private boolean blockedNorthEast;
    private boolean opponentNorthEast;
    private boolean blockedNorthWest;
    private boolean opponentNorthWest;
    private boolean blockedSouthEast;
    private boolean opponentSouthEast;
    private boolean blockedSouthWest;
    private boolean opponentSouthWest;

    /**
     * Constructor for a Queen type piece
     * @param coordinates are the coordinates of the Queen on the board
     * @param color chooses the color of the piece
     */
    public Queen(Coordinates coordinates, String color, Board board){
        super(coordinates, color, board);
        this.type = "queen";
        setImage();
    }

    /**
     * This method determines the possible moves that a queen can move based on their current position
     * @return a set with possible coordinates to move
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        // Set to return with all possible coordinates based on current position
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        // Coordinates to be added in the move set
        Coordinates targetCoordinates;

        blockedNorth = false;
        opponentNorth = false;
        blockedSouth = false;
        opponentSouth = false;
        blockedWest = false;
        opponentWest = false;
        blockedEast = false;
        opponentEast = false;
        blockedNorthEast = false;
        opponentNorthEast = false;
        blockedNorthWest = false;
        opponentNorthWest = false;
        blockedSouthEast = false;
        opponentSouthEast = false;
        blockedSouthWest = false;
        opponentSouthWest = false;

        // Top direction
        for (int row = coordinates.getRow() - 1; row >= 0; row--) {
            // desired coordinates above the queen
            targetCoordinates = new Coordinates(coordinates.getCol(), row);
            // As long as there are no pieces above the queen, it can move there
            if(!blockedNorth && checkForPiecesNorth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent North of the queen, it can take that opponent
            if(!opponentNorth && checkForOpponentNorth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Bottom direction
        for (int row = coordinates.getRow() + 1; row < 8; row++) {
            // desired coordinates below the queen
            targetCoordinates = new Coordinates(coordinates.getCol(), row);
            // As long as there are no pieces below the queen, it can move there
            if(!blockedSouth && checkForPiecesSouth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent South of the queen, it can take that opponent
            if(!opponentSouth && checkForOpponentSouth(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Right direction
        for (int col = coordinates.getCol() + 1; col < 8; col++) {
            // desired coordinates
            targetCoordinates = new Coordinates(col, coordinates.getRow());
            // As long as there are no pieces to the right of the queen, it can move there
            if(!blockedEast && checkForPiecesEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent East of the queen, it can take that opponent
            if(!opponentEast && checkForOpponentEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Left direction
        for (int col = coordinates.getCol() - 1; col >= 0; col--) {
            // desired coordinates
            targetCoordinates = new Coordinates(col, coordinates.getRow());
            // As long as there are no pieces to the left of the queen, it can move there
            if(!blockedWest && checkForPiecesWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent West of the queen, it can take that opponent
            if(!opponentWest && checkForOpponentWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }


        // Top-Left direction
        for (int col = coordinates.getCol() - 1, row = coordinates.getRow() - 1;
             col >= 0 && row >= 0;
             col--, row--) {
            // desired coordinates North-West of the queen
            targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces North-West of the queen, it can move there
            if(!blockedNorthWest && checkForPiecesNorthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent North-West the queen, it can take that opponent
            if(!opponentNorthWest && checkForOpponentNorthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Top-Right direction
        for (int col = coordinates.getCol() - 1, row = coordinates.getRow() + 1;
             col >= 0 && row < 8;
             col--, row++) {
            // desired coordinates North-East of the queen
            targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces North-East of the queen, it can move there
            if(!blockedNorthEast && checkForPiecesNorthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent North-East the queen, it can take that opponent
            if(!opponentNorthEast && checkForOpponentNorthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Bottom-Left direction
        for (int col = coordinates.getCol() + 1, row = coordinates.getRow() - 1;
             col < 8 && row >= 0;
             col++, row--) {
            // desired coordinates South-West of the queen
            targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces South-West of the queen, it can move there
            if(!blockedSouthWest && checkForPiecesSouthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent South-West the queen, it can take that opponent
            if(!opponentSouthWest && checkForOpponentSouthWest(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        // Bottom-right direction
        for (int col = coordinates.getCol() + 1, row = coordinates.getRow() + 1;
             col < 8 && row < 8;
             col++, row++) {
            // desired coordinates South-East of the queen
            targetCoordinates = new Coordinates(col, row);
            // As long as there are no pieces South-East of the queen, it can move there
            if(!blockedSouthEast && checkForPiecesSouthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
            // If there is an opponent South-East the queen, it can take that opponent
            if(!opponentSouthEast && checkForOpponentSouthEast(targetCoordinates)){
                addCoordinates(moveSet, targetCoordinates);
            }
        }

        return moveSet;
    }

    /**
     * Checks to see whether a Queen's Northern movement is blocked
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
     * Checks to see whether a Queen's Southern movement is blocked
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
     * Checks to see whether a Queen's Western movement is blocked
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
     * Checks to see whether a Queen's Eastern movement is blocked
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
     * Checks to see whether an opponent is to the Queen's North
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
     * Checks to see whether an opponent is to the Queen's South
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
     * Checks to see whether an opponent is to the Queen's West
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
     * Checks to see whether an opponent is to the Queen's East
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

    /**
     * Checks to see whether a Queen's North-Eastern movement is blocked
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
     * Checks to see whether a Queen's North-Western movement is blocked
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
     * Checks to see whether a Queen's South-Eastern movement is blocked
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
     * Checks to see whether a Queen's South-Western movement is blocked
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
     * Checks to see whether an opponent is to the Queen's North-East
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
     * Checks to see whether an opponent is to the Queen's North-West
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
     * Checks to see whether an opponent is to the Queen's South-West
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
     * Checks to see whether an opponent is to the Queen's South-East
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
