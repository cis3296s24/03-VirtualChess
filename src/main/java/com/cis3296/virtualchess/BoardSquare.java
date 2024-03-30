package com.cis3296.virtualchess;

import javafx.scene.layout.StackPane;

public class BoardSquare extends StackPane {

    private int xPos;
    private int yPos;
    boolean containsPiece;

    public BoardSquare(int x, int y){
        this.xPos = x;
        this.yPos = y;
        this.containsPiece = false;
    }

    /**
     *
     * @return
     */
    public int getxPos(){
        return xPos;
    }

    /**
     *
     * @return
     */
    public int getyPos(){
        return yPos;
    }
}
