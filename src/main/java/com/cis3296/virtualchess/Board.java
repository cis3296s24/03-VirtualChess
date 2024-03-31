package com.cis3296.virtualchess;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Board {

    private GridPane chessBoard;
    private ArrayList<BoardSquare> boardSquares = new ArrayList<>();

    //The border surround each of the board squares
    private final Border border = new Border(
                                new BorderStroke(
                                        Color.BLACK,
                                        BorderStrokeStyle.SOLID,
                                        CornerRadii.EMPTY,
                                        BorderWidths.DEFAULT
                                )
                        );

    /**
     *  Constructor for the Chess Board
     * @param chessBoard - A gridpane representing the chessboard
     */
    public Board(GridPane chessBoard){
        this.chessBoard = chessBoard;

        init(this.chessBoard);
    }

    /**
     * Goes through each of the tiles in the board and sets them up to be displayed
     * @param chessBoard - A gridpane representing the chessboard
     */
    private void init(GridPane chessBoard){
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                BoardSquare square = new BoardSquare(x, y);
                square.setPrefHeight(100);
                square.setPrefWidth(100);
                square.setBorder(border);
                setColor(square);
                chessBoard.add(square, x, y, 1, 1);
                boardSquares.add(square);
            }
        }
    }

    /**
     *  Sets the color of a given square
     * @param square - The square that's color will be changed
     */
    private void setColor(BoardSquare square){
        // Could be easy to change the colors later here. Or load them from a settings file
        Color black = Color.web("#000000");
        Color white = Color.web("#ffffff");

        if((square.getxPos()+square.getyPos())%2==0){
            square.setBackground(new Background(new BackgroundFill(black, CornerRadii.EMPTY, Insets.EMPTY)));
        }else{
            square.setBackground(new Background(new BackgroundFill(white, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

}
