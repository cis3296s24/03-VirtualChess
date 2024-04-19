package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.Entities.Coordinates;
import javafx.scene.layout.StackPane;

public class BoardSquare extends StackPane {

    public Coordinates coordinates;
    // Not used now but can later be used when pieces are added
    public boolean containsPiece;

    /**
     * Constructor for the squares on the board
     * @param coordinates is a set of values that represent the column and row of each square in the GridPane
     */
    public BoardSquare(Coordinates coordinates){
        this.coordinates = coordinates;
        this.containsPiece = false;
    }
}
