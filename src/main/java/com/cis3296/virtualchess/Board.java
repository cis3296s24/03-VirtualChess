package com.cis3296.virtualchess;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;


import java.util.ArrayList;

public class Board {

    private GridPane chessBoard;
    private ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    private ArrayList<Pawn> pawns = new ArrayList<>();

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
                setSquareColor(square);
                chessBoard.add(square, x, y, 1, 1);
                boardSquares.add(square);

                if(y == 1 || y == 6){
                    // Only shows a pawn node in the 2nd row of each side

                    // radii of the corners of the circle
                    CornerRadii radii = new CornerRadii(100);
                    // border of the circle stuff
                    BorderWidths pawnBW = new BorderWidths(2);
                    BorderStroke pawnBS = new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID,
                           radii,pawnBW);
                    Border pawnBorder = new Border(pawnBS);

                    // make new "pawn" circle
                    Pawn pawn = new Pawn(x, y);

                    // set the height and width of the circle
                    pawn.setMaxHeight(50);
                    pawn.setMaxWidth(50);

                    // set the border using border code on lines 56-61
                    pawn.setBorder(pawnBorder);

                    // set pawn color
                    setPawnColor(pawn);
                    // add it to the board
                    chessBoard.add(pawn, x, y, 1, 1);
                    // add it to the list of pawns
                    pawns.add(pawn);
                }
            }
        }
    }

    /**
     *  Sets the color of a given square
     * @param square - The square that's color will be changed
     */
    private void setSquareColor(BoardSquare square){
        // Could be easy to change the colors later here. Or load them from a settings file
        Color black = Color.web("#000000");
        Color white = Color.web("#ffffff");

        if((square.getxPos()+square.getyPos())%2==0){
            square.setBackground(new Background(new BackgroundFill(black, CornerRadii.EMPTY, Insets.EMPTY)));
        }else{
            square.setBackground(new Background(new BackgroundFill(white, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    private void setPawnColor(Pawn pawn){
        Color red = Color.web("F5F5DC");
        CornerRadii radii = new CornerRadii(95);
        BackgroundFill bgf = new BackgroundFill(red,  radii, Insets.EMPTY);
        Background bg = new Background(bgf);
        pawn.setBackground(bg);
    }

}
