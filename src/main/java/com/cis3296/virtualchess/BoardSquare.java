package com.cis3296.virtualchess;

import javafx.scene.layout.StackPane;

public class BoardSquare extends StackPane {

    // The X position of the square
    private int xPos;
    // The Y position of the square
    private int yPos;
    // Not used now but can later be used when pieces are added
    boolean containsPiece;

    /**
     *
     * @param x - The X position of the square in the board
     * @param y - The X position of the square in the board
     */
    public BoardSquare(int x, int y){
        this.xPos = x;
        this.yPos = y;
        this.containsPiece = false;
    }

    /**
     * Getter for the X position
     * @return the X position
     */
    public int getxPos(){
        return xPos;
    }

    /**
     * Getter for the Y position
     * @return the Y position
     */
    public int getyPos(){
        return yPos;
    }
}
