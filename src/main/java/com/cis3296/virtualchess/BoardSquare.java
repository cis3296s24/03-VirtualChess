package com.cis3296.virtualchess;

import javafx.scene.layout.StackPane;

public class BoardSquare extends StackPane {

    // The column position of the square on the board
    private final int colPos;
    // The row position of the square on the board
    private final int rowPos;
    // Not used now but can later be used when pieces are added
    boolean containsPiece;

    /**
     * Constructor for a square in the board
     * @param col - The column position of the square in the board
     * @param row - The row position of the square in the board
     */
    public BoardSquare(int col, int row){
        this.colPos = col;
        this.rowPos = row;
        this.containsPiece = false;
    }

    /**
     * Getter for the column position
     * @return the column position
     */
    public int getColPos(){
        return colPos;
    }

    /**
     * Getter for the row position
     * @return the row position
     */
    public int getRowPos(){
        return rowPos;
    }
}
