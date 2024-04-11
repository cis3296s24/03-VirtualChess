package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Piece extends ImageView {

    public Coordinates coordinates;
    public Coordinates previousCoordinates;
    public String color;
    public String type;

    /**
     * Constructor for a standard Piece
     * @param coordinates are the coordinates of the Piece on the board
     * @param color chooses the color of the piece
     */
    public Piece(Coordinates coordinates, String color) {
        this.coordinates = coordinates;
        this.color = color;
        this.previousCoordinates = new Coordinates(coordinates.getCol(), coordinates.getRow());
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
        this.setPiece(new Image(getClass().getResourceAsStream("/assets/piecesNorm/" + this.color + "_" +  this.type + ".png"), 100, 100, false, false));
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
        if(color.equals("white")){
            return canMoveWhite(targetCol, targetRow);
        } else {
            return canMoveBlack(targetCol, targetRow);
        }
    }

    public boolean canMoveBlack(int targetCol, int targetRow){
        return false;
    }

    public boolean canMoveWhite(int targetCol, int targetRow){
        return false;
    }
}