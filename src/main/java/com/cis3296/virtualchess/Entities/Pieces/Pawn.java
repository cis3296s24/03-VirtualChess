package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Pieces.PieceUtilities.PawnPromotionPopup;

import java.util.ArrayList;

public class Pawn extends Piece {

    private final int UP = -1;
    private final int DOWN = 1;
    private final int direction;
    private PawnPromotionPopup popup;
    public boolean wasPromoted;
    public Piece piecePromotedTo;
    // directions are in respect to the pawn's location
    // i.e. black pawn's forward goes up, white pawn's "forward" goes down

    /**
     * Constructor for a Pawn type piece
     * @param coordinates are the coordinates of the Pawn on the board
     * @param color chooses the color of the piece
     */
    public Pawn(Coordinates coordinates, String color, Board board, boolean isTurn){
        super(coordinates, color, board, isTurn);
        this.type = "pawn";
        setImage();
        if(color.equals("white")){
            direction = UP;
        } else {
            direction = DOWN;
        }
        this.popup = new PawnPromotionPopup(this);
    }

    /**
     * Determines the possible moves that a pawn can make based on its current position.
     * @return a list of possible coordinates the pawn can move to.
     */
    @Override
    public ArrayList<Coordinates> getMoveSet() {
        ArrayList<Coordinates> moveSet = new ArrayList<>();
        Coordinates targetCoordinates;



        // Handle single forward movement
        targetCoordinates = new Coordinates(coordinates.getCol(), coordinates.getRow() + direction);
        if (isForwardPathClear(targetCoordinates)) {
            addCoordinates(moveSet, targetCoordinates);
            // Handle initial double step move
            if (!moved) {
                targetCoordinates = new Coordinates(coordinates.getCol(), coordinates.getRow() + (direction * 2));
                if (isForwardPathClear(targetCoordinates)) {
                    addCoordinates(moveSet, targetCoordinates);
                }
            }
        }

        // Handle diagonal captures
        int[] diagonalOffsets = {-1, 1};
        for (int offset : diagonalOffsets) {
            targetCoordinates = new Coordinates(coordinates.getCol() + offset, coordinates.getRow() + direction);
            if (canMoveDiagonally(targetCoordinates)) {
                addCoordinates(moveSet, targetCoordinates);
            }
            // Regardless of whether on not they can move there, pawns guard these squares. Add them to the guarded list
            guardedSquares.add(targetCoordinates);
        }

        //Handle En Passant
        targetCoordinates = new Coordinates(this.coordinates.getCol() - direction, this.coordinates.getRow());
        if(canMoveDiagonally(targetCoordinates) &&
                !board.getPieceAt(targetCoordinates).color.equals(this.color) &&
                board.getPieceAt(targetCoordinates).type.equals("pawn") &&
        board.getPieceAt(targetCoordinates).twoStepped) {
            targetCoordinates = new Coordinates(this.coordinates.getCol() - direction, this.coordinates.getRow() + direction);
            addCoordinates(moveSet, targetCoordinates);
        }
        targetCoordinates = new Coordinates(this.coordinates.getCol() + direction, this.coordinates.getRow());
        if(canMoveDiagonally(targetCoordinates) &&
                !board.getPieceAt(targetCoordinates).color.equals(this.color) &&
                board.getPieceAt(targetCoordinates).type.equals("pawn") &&
                board.getPieceAt(targetCoordinates).twoStepped){
            targetCoordinates = new Coordinates(this.coordinates.getCol() + direction, this.coordinates.getRow() + direction);
            addCoordinates(moveSet, targetCoordinates);
        }
        return moveSet;
    }

    /**
     * Checks if the pawn's forward path is clear of other pieces.
     * @param targetCoordinates the desired forward coordinates.
     * @return true if the path is clear, false otherwise.
     */
    public boolean isForwardPathClear(Coordinates targetCoordinates) {
        Piece piece = board.getPieceAt(targetCoordinates);
        return piece == null;
    }

    /**
     * Checks if the pawn can move diagonally (for capturing pieces).
     * @param targetCoordinates the desired diagonal coordinates.
     * @return true if the pawn can move diagonally, false otherwise.
     */
    public boolean canMoveDiagonally(Coordinates targetCoordinates) {
        Piece piece = board.getPieceAt(targetCoordinates);
        return piece != null;
    }

    /**
     * Method to promote the pawn
     */
    public void promote(BoardSquare currentSquare, Board board) {
        // Display buttons from PawnPromotionPopup class
        popup.displayPromotionButtons(currentSquare, board);
    }

    @Override
    public String toString() {
        if(color.equals("white")){
            return "P";
        } else{
            return "p";
        }
    }
}
