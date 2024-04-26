package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.Entities.Coordinates;
import javafx.scene.layout.StackPane;

/**
 * Data class for representing a square on the chess board
 */
public class BoardSquare extends StackPane {

    // Coordinates of this square
    public Coordinates coordinates;
    // Whether or not it contains a piece
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
