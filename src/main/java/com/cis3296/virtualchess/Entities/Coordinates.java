package com.cis3296.virtualchess.Entities;

import static com.cis3296.virtualchess.Components.Board.SQUARE_SIZE;

/**
 * Data class for storing row, column, x, and y of objects
 */
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
     * Constructor for creating coordinates from real chess coordinates
     * @param chessCoords String of actual chess coordinates ex. a2
     */
    public Coordinates(String chessCoords) {
        if(chessCoords.length() != 2){
            System.out.println("Invalid coordinates");
        } else{
            switch(chessCoords.charAt(0)){
                case 'a':
                    col = 0;
                    break;
                case 'b':
                    col = 1;
                    break;
                case 'c':
                    col = 2;
                    break;
                case 'd':
                    col = 3;
                    break;
                case 'e':
                    col = 4;
                    break;
                case 'f':
                    col = 5;
                    break;
                case 'g':
                    col = 6;
                    break;
                case 'h':
                    col = 7;
                    break;
            }
            row = (8 - Integer.parseInt(chessCoords.substring(1, 2)));
        }
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
     * @return The X position in pixels
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter for the y-coordinate
     * @return The Y position in pixels
     */
    public int getY() {
        return this.y;
    }

    /**
     * Formates the variables into a string
     * @return String containing the row, column, x, and y of this coordinate
     */
    @Override
    public String toString() {
        return "(Col:" + col + ", Row:" + row + ", X:" + x + ", Y:" + y + ")";
    }

    /**
     * Overrides the equals operator to compare the rows and columns
     * @param obj Should always be a {@link Coordinates} object
     * @return True if the rows and columns are the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            if(((Coordinates) obj).col == this.col && ((Coordinates) obj).row == this.row){
                return true;
            }
        }
        return super.equals(obj);
    }

    /**
     * Converts our coordinate system to actual chess notation
     * @param coordinates The coordinates to be converted
     * @return A String containing the proper chess notation
     */
    public static String toChessCoordinates(Coordinates coordinates) {
        String coordinatesString = "";
        switch(coordinates.getCol()) {
            case 0:
                coordinatesString += "a";
                break;
            case 1:
                coordinatesString += "b";
                break;
            case 2:
                coordinatesString += "c";
                break;
            case 3:
                coordinatesString += "d";
                break;
            case 4:
                coordinatesString += "e";
                break;
            case 5:
                coordinatesString += "f";
                break;
            case 6:
                coordinatesString += "g";
                break;
            case 7:
                coordinatesString += "h";
                break;
        }
        switch(coordinates.getRow()) {
            case 0:
                coordinatesString += "1";
                break;
            case 1:
                coordinatesString += "2";
                break;
            case 2:
                coordinatesString += "3";
                break;
            case 3:
                coordinatesString += "4";
                break;
            case 4:
                coordinatesString += "5";
                break;
            case 5:
                coordinatesString += "6";
                break;
            case 6:
                coordinatesString += "7";
                break;
            case 7:
                coordinatesString += "8";
                break;
        }
        return coordinatesString;
    }
}
