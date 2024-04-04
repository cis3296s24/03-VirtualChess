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

    private final String imageFolderPath = "";
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
     * @returns col * SQUARE_SIZE. This is the X-coordinate pixel location of the piece
     */
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    /**
     * Getter method for the Y value of a piece
     * @param row is the row number
     * @returns row * SQUARE_SIZE. This is the Y-coordinate pixel location of the piece
     */
    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }
    public void setImage(){
        this.setPiece(new Image(getClass().getResourceAsStream("/assets/piecesNorm/w_bishop_1x.png"), 100, 100, false, false));
    }
    public void setPiece(Image image){
        this.setImage(image);
    }

}
