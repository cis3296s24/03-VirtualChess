package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Board;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class Piece extends ImageView {
    public int x, y; // Pixel coordinates for the pieces
    public int col, row, prevCol, prevRow; // We want to track previous location in addition to current location

    public String color;
    public String type;
    /**
     * Constructor method for pieces
     * @param col is the column number of the piece
     * @param row is the row number of the piece
     */
    public Piece(int col, int row, String color) {
        this.col = col;
        this.row = row;
        this.color = color;
        x = getX(col);
        y = getY(row);
        prevCol = col;
        prevRow = row;
    }

    /**
     * Getter method for the X value of a piece
     * @param col is the column number
     * @return col * SQUARE_SIZE. This is the X-coordinate pixel location of the piece
     */
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    /**
     * Getter method for the Y value of a piece
     * @param row is the row number
     * @return row * SQUARE_SIZE. This is the Y-coordinate pixel location of the piece
     */
    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public void setImage(){
        this.setPiece(new Image(getClass().getResourceAsStream("/assets/piecesNorm/" + this.color + "_" +  this.type + ".png"), 100, 100, false, false));
    }
    public void setPiece(Image image){
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