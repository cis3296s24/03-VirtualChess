package com.cis3296.virtualchess.Pieces;

import com.cis3296.virtualchess.Coordinates;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Pawn extends Piece {

    /**
     *
     * @param coordinates
     * @param color
     */
    public Pawn(Coordinates coordinates, String color){
        super(coordinates, color);
        this.type = "pawn";
        setImage();
    }

}
