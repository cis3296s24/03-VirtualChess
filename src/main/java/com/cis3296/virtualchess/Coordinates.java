package com.cis3296.virtualchess;

import static com.cis3296.virtualchess.Board.Board.SQUARE_SIZE;

public class Coordinates {
    private int col, row; // The column and row of each location on the chessboard
    private int x, y; // The specific pixel location of each col and row

    /**
     * Constructor for a Coordinates object
     * @param col represents the column-coordinate of each square on the chessboard
     * @param row represents the row-coordinate of each square on the chessboard
     */
    public Coordinates(int col, int row) {
        this.col = col;
        this.row = row;
        this.x = SQUARE_SIZE * col;
        this.y = SQUARE_SIZE * row;
    }

    /**
     * Sets all 4 coordinate values
     * @param col is the desired new row-coordinate to be set
     * @param row is the desired new row-coordinate to be set
//     * @param x is the desired new y-coordinate to be set
//     * @param y is the desired new y-coordinate to be set
     */
    public void setCoordinates(int col, int row) {
        this.col = col;
        this.row = row;
        this.x = SQUARE_SIZE * col;
        this.y = SQUARE_SIZE * row;
    }

    /**
     * Setter for column and x coordinates
     * @param col is the desired new row-coordinate to be set
     * @param x is the desired new y-coordinate to be set
     */
    public void setColX(int col, int x) {
        this.col = col;
        this.x = x;
    }

    /**
     * Setter for row and y coordinates
     * @param row is the desired new row-coordinate to be set
     * @param y is the desired new y-coordinate to be set
     */
    public void setRowY(int row, int y) {
        this.row = row;
        this.y = y;
    }

    /**
     * Getter for the column-coordinate
     * @return column
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Getter for the row-coordinate
     * @return row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Getter for the x-coordinate
     * @return x
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter for the y-coordinate
     * @return y
     */
    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "(Col:" + col + ", Row:" + row + ", X:" + x + ", Y:" + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            if(((Coordinates) obj).col == this.col && ((Coordinates) obj).row == this.row){
                return true;
            }
        }
        return super.equals(obj);
    }
}
