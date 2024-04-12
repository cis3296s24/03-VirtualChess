package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Piece extends ImageView {

    public Coordinates coordinates;
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
        Coordinates target = new Coordinates(targetCol, targetRow);
        if(color.equals("white")){
            return canMoveWhite(target);
        } else {
            return canMoveBlack(target);
        }
    }

    public abstract boolean canMoveBlack(Coordinates targetCoordinates);

    public abstract boolean canMoveWhite(Coordinates targetCoordinates);

    public boolean compareTopRight(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol() + 1,
                        targetCoordinates.getRow() + 1
                ));
    }

    public boolean compareTopLeft(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol() - 1,
                        targetCoordinates.getRow() + 1
                ));
    }

    public boolean compareTop(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol(),
                        targetCoordinates.getRow() + 1
                ));
    }

    public boolean compareBottomRight(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol() + 1,
                        targetCoordinates.getRow() - 1
                ));
    }

    public boolean compareBottomLeft(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol() - 1,
                        targetCoordinates.getRow() - 1
                ));
    }

    public boolean compareBottom(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol(),
                        targetCoordinates.getRow() - 1
                ));
    }

    public boolean compareLeft(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol() - 1,
                        targetCoordinates.getRow()
                ));
    }

    public boolean compareRight(Coordinates targetCoordinates){
        return this.coordinates.equals(
                new Coordinates(
                        targetCoordinates.getCol() + 1,
                        targetCoordinates.getRow()
                ));
    }

}