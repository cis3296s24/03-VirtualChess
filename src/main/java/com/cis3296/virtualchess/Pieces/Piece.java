package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board;
import com.cis3296.virtualchess.Coordinates;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Piece extends ImageView {

    public Coordinates coordinates;
    public Coordinates previousCoordinates;
    public String color;
    public String type;

    /**
     *
     * @param coordinates
     * @param color
     */
    public Piece(Coordinates coordinates, String color) {
        this.coordinates = coordinates;
        this.color = color;
        this.previousCoordinates = new Coordinates(coordinates.getCol(), coordinates.getRow());
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
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}