package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Pawn extends Piece {

    /**
     * Constructor for a Pawn type piece
     * @param coordinates are the coordinates of the Pawn on the board
     * @param color chooses the color of the piece
     */
    public Pawn(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "pawn";
        setImage();
    }

    public String type(){
        return "Pawn";
    }
}
