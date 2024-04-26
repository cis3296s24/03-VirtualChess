package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Entities.Coordinates;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Models a basic piece and what it can do
 * Extends Image View so that it can have an image on it
 */
public abstract class Piece extends ImageView {

    // Coordinates of the piece
    public Coordinates coordinates;
    // Color of the piece
    public String color;
    // Type of the piece (i.e. pawn, queen...)
    public String type;
    // Board that the piece is on
    public Board board;
    // If it is this pieces turn or not
    public boolean isTurn;
    // Has this piece moved yet
    public boolean moved;
    // Has this piece done a double move(For pawn)
    public boolean twoStepped;
    // The current valid moves for the piece
    public ArrayList<Coordinates> currentMoveSet = new ArrayList<>();
    // The squares around the piece that are guarded
    public ArrayList<Coordinates> guardedSquares = new ArrayList<>();
    // Is this piece checking another piece
    public boolean isChecking;
    // Is this piece in check
    public boolean inCheck;
    private final int UP = -1;
    private final int DOWN = 1;
    public int direction;

    public boolean isSimple = false;


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
        if(color.equals("white")){
            direction = UP;
        } else {
            direction = DOWN;
        }
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.KV_CONFIG_ACCESS_STRING))){
            isSimple = true;
        } else {
            isSimple = false;
        }
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

    /**
     * Finds the images file by using the type and color
     */
    public void setImage() {
        this.setPiece(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + this.color + "_" + this.type + ".png")), 90, 90, true, true));
    }

    /**
     * Sets the ImageView image using an image from resources
     * @param image An image representing the piece
     */
    public void setPiece(Image image) {
        this.setImage(image);
    }

    /**
     * Determines if a piece can move to a desired space or not
     * Each child class will overwrite this method to distinguish different piece movement
     * @param targetCol is the targeted column for movement
     * @param targetRow is the targeted row for movement
     * @return false by default, true if it can move
     */
    public boolean canMove(int targetCol, int targetRow){
        Coordinates targetCoordinates = new Coordinates(targetCol, targetRow);
        this.currentMoveSet = this.getMoveSetSuper();

        for(Coordinates coordinates : currentMoveSet){
            if(coordinates.equals(targetCoordinates)){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Coordinates> getSimpleMoveSet() {
        ArrayList<Coordinates> simpleMoveSet = new ArrayList<>();
        Coordinates targetCoordinates;

        int[] diagonalOffsets = {-1, 1};
        for (int offset : diagonalOffsets) {
            targetCoordinates = new Coordinates(coordinates.getCol() + offset, coordinates.getRow() + direction);
            Piece diagPiece = board.getPieceAt(targetCoordinates);
            if (diagPiece != null && !diagPiece.color.equals(this.color)) {
                addCoordinates(simpleMoveSet, new Coordinates(targetCoordinates.getCol() + offset, targetCoordinates.getRow() + direction));
            } else{
                addCoordinates(simpleMoveSet, targetCoordinates);
            }
        }
        return simpleMoveSet;
    }

    /**
     * Shows hints on the board for where the piece can move
     * @param board The board the piece is on
     */
    public void showMoves(Board board){
        this.currentMoveSet = this.getMoveSetSuper();
        for(Coordinates coordinates : currentMoveSet){
            board.showMoves(coordinates);
        }
    }

    /**
     * Gets the moveset for a specific piece
     * @return An arraylist containing the valid move {@link Coordinates} for a piece
     */
    public abstract ArrayList<Coordinates> getMoveSet();

    public ArrayList<Coordinates> getMoveSetSuper(){
        if(!isSimple){
            return this.getMoveSet();
        } else {
            return this.getSimpleMoveSet();
        }
    }

    /**
     * Checks whether the piece is guarded or not
     * @return true if it is guarded, false otherwise
     */
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
     * @param moveSet the ArrayList the {@link Coordinates} get added to
     * @param targetCoordinates the {@link Coordinates} of the move being made
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