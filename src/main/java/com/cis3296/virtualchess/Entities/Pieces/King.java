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
            addCoordinates(moveSet, target);
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

}
