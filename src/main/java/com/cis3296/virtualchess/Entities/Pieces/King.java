package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Coordinates;

import java.util.ArrayList;

public class King extends Piece {


    /**
     * Constructor for a King type piece
     * @param coordinates are the coordinates of the King on the board
     * @param color chooses the color of the piece
     * @param board current board the piece is on
     */
    public King(Coordinates coordinates, String color, Board board, boolean isTurn){
        super(coordinates, color, board, isTurn);
        this.type = "king";
        setImage();
    }

    /**
     * Determines the possible moves for a King based on its current position.
     *
     * @return An array list of possible coordinates the King can move to.
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        addKingMoves(moveSet);
        addCastlingMoves(moveSet);
        return moveSet;
    }

    /**
     * Adds possible moves in all directions around the King.
     *
     * @param moveSet The list to add the coordinates to.
     */
    private void addKingMoves(ArrayList<Coordinates> moveSet) {
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1} };
        for (int[] dir : directions) {
            Coordinates target = new Coordinates(this.coordinates.getCol() + dir[0], this.coordinates.getRow() + dir[1]);
            if(!movingIntoCheck(target)){
                addCoordinates(moveSet, target);
            }
        }
    }

    private void addCastlingMoves(ArrayList<Coordinates> moveSet) {
        // Check for left castling
        if (!moved && canCastleLeft()) {
            addCoordinates(moveSet, new Coordinates(this.coordinates.getCol() - 2, this.coordinates.getRow()));
        }
        // Check for right castling
        if (!moved && canCastleRight()) {
            addCoordinates(moveSet, new Coordinates(this.coordinates.getCol() + 2, this.coordinates.getRow()));
        }
    }

    private boolean canCastleLeft() {
        Coordinates rookPosition = new Coordinates(this.coordinates.getCol() - 4, this.coordinates.getRow());
        Piece piece = board.getPieceAt(rookPosition);

        if (!(piece instanceof Rook)) {
            return false;
        }

        Rook leftRook = (Rook) piece;

        if (leftRook.moved) {
            return false;
        }

        // Check for clear path
        for (int i = this.coordinates.getCol() - 1; i >= this.coordinates.getCol() - 3; i--) {
            if (board.getPieceAt(new Coordinates(i, this.coordinates.getRow())) != null) {
                return false;
            }
        }

        // Check to see if castle is moving through the King being checked
        Coordinates leftCastleTest1 = new Coordinates(this.coordinates.getCol() - 1, this.coordinates.getRow());
        Coordinates leftCastleTest2 = new Coordinates(this.coordinates.getCol() - 2, this.coordinates.getRow());
        if (movingIntoCheck(this.coordinates) || movingIntoCheck(leftCastleTest1) || movingIntoCheck(leftCastleTest2)) {
            return false;
        }

        return true;
    }

    private boolean canCastleRight() {
        Coordinates rookPosition = new Coordinates(this.coordinates.getCol() + 3, this.coordinates.getRow());
        Piece piece = board.getPieceAt(rookPosition);

        if (!(piece instanceof Rook)) {
            return false;
        }

        Rook rightRook = (Rook) piece;

        if (rightRook.moved) {
            return false;
        }

        // Check for clear path
        for (int i = this.coordinates.getCol() + 1; i <= this.coordinates.getCol() + 2; i++) {
            if (board.getPieceAt(new Coordinates(i, this.coordinates.getRow())) != null) {
                return false;
            }
        }

        // Check to see if castle is moving through the King being checked
        Coordinates rightCastleTest1 = new Coordinates(this.coordinates.getCol() + 1, this.coordinates.getRow());
        Coordinates rightCastleTest2 = new Coordinates(this.coordinates.getCol() + 2, this.coordinates.getRow());
        if (movingIntoCheck(this.coordinates) || movingIntoCheck(rightCastleTest1) || movingIntoCheck(rightCastleTest2)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        if(color.equals("white")){
            return "K";
        } else{
            return "k";
        }
    }

    public boolean movingIntoCheck(Coordinates targetCoordinates){
        // For all Pieces on the board
        for (Piece piece : board.pieces) {
            // If the other piece isn't this king (king to be moved)
            if (!piece.equals(this)) {
                // Case of a pawn
                if (piece instanceof Pawn)
                {
                    for (Coordinates guarded : piece.guardedSquares) {
                        if (guarded.equals(targetCoordinates) && !piece.color.equals(this.color)) {
                            return true;
                        }
                    }
                } else {
                    // For all of a piece's possible moves (other than pawn)
                    for (Coordinates opCoords : piece.currentMoveSet) {
                        // If the intended square for the king to move is in the other piece's move set
                        if (targetCoordinates.equals(opCoords) && !piece.color.equals(this.color)) {
                            return true;
                        }
                    }
                }
            } else {
                for (Coordinates guarded : piece.guardedSquares) {
                    if (guarded.equals(targetCoordinates) && !piece.color.equals(this.color)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck()
    {
        for (Piece otherPiece : board.pieces) {
            if (!otherPiece.equals(this)) {
                for (Coordinates opCoords : otherPiece.currentMoveSet) {
                    if (this.coordinates.equals(opCoords) && !otherPiece.color.equals(this.color)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
