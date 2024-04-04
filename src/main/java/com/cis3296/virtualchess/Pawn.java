package com.cis3296.virtualchess;

import javafx.scene.layout.StackPane;

public class Pawn extends StackPane {
    // The X position of the square
    private int xPos;
    // The Y position of the square
    private int yPos;

    /**
     * Constructor for a piece in the board
     * @param x - The X position of the square in the board
     * @param y - The X position of the square in the board
     */
    public Pawn(int x, int y){
        this.xPos = x;
        this.yPos = y;
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
