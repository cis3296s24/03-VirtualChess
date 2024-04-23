package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Coordinates;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Piece extends ImageView {

    public Coordinates coordinates;
    public String color;
    public String type;
    public Board board;
    public boolean isTurn;
    public boolean moved;
    public boolean twoStepped;
    public ArrayList<Coordinates> currentMoveSet = new ArrayList<>();
    public ArrayList<Coordinates> guardedSquares = new ArrayList<>();
    public boolean isChecking;
    public boolean inCheck;


    /**
     * Constructor for a standard Piece
     * @param coordinates are the coordinates of the Piece on the board
     * @param color chooses the color of the piece
     */
    public Piece(Coordinates coordinates, String color, Board board, boolean isTurn) {
        this.coordinates = coordinates;
        this.color = color;
        this.board = board;
        this.isTurn = isTurn;
        moved = false;
        isChecking = false;
        inCheck = false;
        setDragHandlers();
    }

    /**
     * This method allows the Piece objects to be dragged
     */
    private void setDragHandlers() {
        // Set event handlers for drag-and-drop operations
        setOnDragDetected(event -> {
            startFullDrag();
            event.consume();
        });


        setOnMouseDragged(Event::consume);
    }

    public void setImage() {
        this.setPiece(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + this.color + "_" + this.type + ".png")), 90, 90, true, true));
    }
    public void setPiece(Image image) {
        this.setImage(image);
    }

    /**
     * Determines if a piece can move to a desired space or not
     * No additional code is needed here as of now
     * Each child class will overwrite this method to distinguish different piece movement
     * @param targetCol is the targeted column for movement
     * @param targetRow is the targeted row for movement
     * @return false by default, preventing erratic movement
     */
    public boolean canMove(int targetCol, int targetRow){
        Coordinates targetCoordinates = new Coordinates(targetCol, targetRow);
        this.currentMoveSet = this.getMoveSet();
        for(Coordinates coordinates : currentMoveSet){
            if(coordinates.equals(targetCoordinates)){
                return true;
            }
        }
        return false;
    }

    public void showMoves(Board board){
        this.currentMoveSet = this.getMoveSet();
        for(Coordinates coordinates : currentMoveSet){
            board.showMoves(coordinates);
        }
    }
    public abstract ArrayList<Coordinates> getMoveSet();

    public boolean isGuarded() {
        for (Piece piece : board.pieces) {
            if (!piece.equals(this)) {
                for (Coordinates otherCoordinates : piece.guardedSquares) {
                    if (this.coordinates.equals(otherCoordinates)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method confirms adds the coordinates to the piece move set if the moves
     * do not conflict with other pieces
     * @param moveSet the ObservableList the coordinates get added to
     * @param targetCoordinates the coordinates of the move being made
     */
    public void addCoordinates(ArrayList<Coordinates> moveSet, Coordinates targetCoordinates){
        Piece targetPiece = board.getPieceAt(targetCoordinates);
        if(!board.isValidCoordinate(targetCoordinates.getCol(), targetCoordinates.getRow())){
            return;
        }
        // Check to see if the square has a piece on it
        if(targetPiece != null){
            // If this piece is a king
            if (this instanceof King) {
                if (!this.color.equals(targetPiece.color)) {
                    if (!targetPiece.isGuarded()) {
                        // Add the coordinate as a possible move
                        moveSet.add(targetCoordinates);
                    }
                }
                // These are the squares that a piece is guarding
                guardedSquares.add(targetCoordinates);
            }
            else {
                // Check to see if the piece is not of the same color
                if(!this.color.equals(targetPiece.color)){
                    // Add the coordinate as a possible move
                    moveSet.add(targetCoordinates);
                }
                // These are the squares that a piece is guarding
                guardedSquares.add(targetCoordinates);
            }

        } else {
            // Add the coordinate as a possible move
            moveSet.add(targetCoordinates);
        }
    }
}