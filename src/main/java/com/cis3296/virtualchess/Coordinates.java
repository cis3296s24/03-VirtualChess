package com.cis3296.virtualchess;

public class Coordinates {
    private int col, row; // The column and row of each location on the chessboard
    private int x, y; // The specific pixel location of each col and row
    private static final int DEFAULT_PIXEL_FACTOR = 100;

    /**
     * Constructor for a Coordinates object
     * @param col represents the column-coordinate of each square on the chessboard
     * @param row represents the row-coordinate of each square on the chessboard
     */
    public Coordinates(int col, int row) {
        this.col = col;
        this.row = row;
        this.x = DEFAULT_PIXEL_FACTOR * col;
        this.y = DEFAULT_PIXEL_FACTOR * row;
    }

    /**
     * Setter for column coordinate
     * @param col is the desired new column-coordinate to be set
     */
    public void setCol(int col) {
        this.col = col;
        this.x = DEFAULT_PIXEL_FACTOR * col;
    }

    /**
     * Setter for row coordinate
     * @param row is the desired new row-coordinate to be set
     */
    public void setRow(int row) {
        this.row = row;
        this.x = DEFAULT_PIXEL_FACTOR * row;
    }


    /**
     * Setter for the x-coordinate
     * @param x is the desired pixel coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Setter for the y-coordinate
     * @param y is the desired pixel coordinate
     */
    public void setY(int y) {
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
}
