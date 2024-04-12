package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Coordinates;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public abstract class Piece extends ImageView {

    public Coordinates coordinates;
    public String color;
    public String type;
    public Board board;

    /**
     * Constructor for a standard Piece
     * @param coordinates are the coordinates of the Piece on the board
     * @param color chooses the color of the piece
     */
    public Piece(Coordinates coordinates, String color, Board board) {
        this.coordinates = coordinates;
        this.color = color;
        this.board = board;
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
        this.setPiece(new Image(getClass().getResourceAsStream("/assets/piecesNorm/" + this.color + "_" +  this.type + ".png"), 90, 90, true, true));
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
        for(Coordinates coordinates : getMoveSet()){
            if(coordinates.equals(targetCoordinates)){
                return true;
            }
        }
        return false;
    }

    public void showMoves(Board board){
        for(Coordinates coordinates : getMoveSet()){
            board.showMoves(coordinates);
        }
    }
    public abstract ArrayList<Coordinates> getMoveSet();
}