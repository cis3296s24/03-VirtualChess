package com.cis3296.virtualchess;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Board {

    private GridPane chessBoard;
    private ArrayList<BoardSquare> boardSquares = new ArrayList<>();

    private final Border border = new Border(
                                new BorderStroke(
                                        Color.BLACK,
                                        BorderStrokeStyle.SOLID,
                                        CornerRadii.EMPTY,
                                        BorderWidths.DEFAULT
                                )
                        );

    public Board(GridPane chessBoard){
        this.chessBoard = chessBoard;

        init(this.chessBoard);
    }

    private void init(GridPane chessBoard){
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                BoardSquare square = new BoardSquare(x, y);
                square.setPrefHeight(100);
                square.setPrefWidth(100);
                square.setBorder(border);
                setColor(square, x, y);
                chessBoard.add(square, x, y, 1, 1);
                boardSquares.add(square);
            }
        }
    }

    private void setColor(BoardSquare square, int x, int y){
        Color black = Color.web("#00000000");
        Color white = Color.web("#ffffffff");

        if((x+y)%2==0){
            square.setBackground(new Background(new BackgroundFill(black, CornerRadii.EMPTY, Insets.EMPTY)));
        }else{
            square.setBackground(new Background(new BackgroundFill(white, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

}
