package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.util.ArrayList;
import java.util.Objects;

public class Board {
    // In case we need to change these column/row/size values for any reason later on...
    public final int MAX_COL = 8, MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    private GridPane chessBoard;
    private ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    private ArrayList<Pawn> pawns = new ArrayList<>();
    private BoardSettings settings;

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
    public Board(GridPane chessBoard, BoardSettings settings){

        this.chessBoard = chessBoard;
        this.settings = settings;
        init(this.chessBoard);
    }

    /**
     * Goes through each of the tiles in the board and sets them up to be displayed
     * @param chessBoard - A gridpane representing the chessboard
     */
    private void init(GridPane chessBoard){
        for(int x = 0; x < MAX_COL; x++){
            for(int y = 0; y < MAX_ROW; y++){
                BoardSquare square = new BoardSquare(x, y);
                square.setPrefHeight(SQUARE_SIZE);
                square.setPrefWidth(SQUARE_SIZE);
                square.setBorder(border);
                setSquareColor(square);
                chessBoard.add(square, x, y, 1, 1);
                boardSquares.add(square);
            }
        }
        addPieces();
    }

    /**
     *  Sets the color of a given square
     * @param square - The square that's color will be changed
     */
    private void setSquareColor(BoardSquare square){
        if((square.getxPos()+square.getyPos())%2==0){
            square.setBackground(
                    new Background(
                            new BackgroundFill(
                                    settings.currentBoardStyle.squareColor1,
                                    CornerRadii.EMPTY,
                                    Insets.EMPTY
                            )
                    )
            );
        }else{
            square.setBackground(
                    new Background(
                            new BackgroundFill(
                                    settings.currentBoardStyle.squareColor2,
                                    CornerRadii.EMPTY,
                                    Insets.EMPTY
                            )
                    )
            );
        }
    }
    private void addPieces(){
        for(BoardSquare square : boardSquares){
            if(square.getyPos() == 0){
                if(square.getxPos() == 0 || square.getxPos() == 7){
                    addPiece(square, new Rook(square.getxPos(), square.getyPos(), "white"));
                }
                if(square.getxPos() == 1 || square.getxPos() == 6){
                    addPiece(square, new Knight(square.getxPos(), square.getyPos(), "white"));
                }
                if(square.getxPos() == 2 || square.getxPos() == 5){
                    addPiece(square, new Bishop(square.getxPos(), square.getyPos(), "white"));
                }
                if(square.getxPos() == 3){
                    addPiece(square, new Queen(square.getxPos(), square.getyPos(), "white"));
                }
                if(square.getxPos() == 4){
                    addPiece(square, new King(square.getxPos(), square.getyPos(), "white"));
                }

            }
            if(square.getyPos() == 1){
                addPiece(square, new Pawn(square.getxPos(), square.getyPos(), "white"));
            }
            if(square.getyPos() == 6){
                addPiece(square, new Pawn(square.getxPos(), square.getyPos(), "black"));
            }

            if(square.getyPos() == 7){
                if(square.getxPos() == 0 || square.getxPos() == 7){
                    addPiece(square, new Rook(square.getxPos(), square.getyPos(), "black"));
                }
                if(square.getxPos() == 1 || square.getxPos() == 6){
                    addPiece(square, new Knight(square.getxPos(), square.getyPos(), "black"));
                }
                if(square.getxPos() == 2 || square.getxPos() == 5){
                    addPiece(square, new Bishop(square.getxPos(), square.getyPos(), "black"));
                }
                if(square.getxPos() == 3){
                    addPiece(square, new King(square.getxPos(), square.getyPos(), "black"));
                }
                if(square.getxPos() == 4){
                    addPiece(square, new Queen(square.getxPos(), square.getyPos(), "black"));
                }
            }
        }

    }

    private void addPiece(BoardSquare square, Piece piece){
        square.getChildren().add(piece);
        square.containsPiece = true;
    }

}
