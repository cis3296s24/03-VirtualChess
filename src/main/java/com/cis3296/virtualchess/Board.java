package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.*;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Node;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Board{
    // In case we need to change these column/row/size values for any reason later on...
    public final int MAX_COL = 8, MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;


    private GridPane chessBoard;
    private ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    private HashMap<BoardSquare, Piece> boardSquareToPiece = new HashMap<>();

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
     * This method recognizes when a user clicks on a square and calls the movePiece() method
     */
    public void mouseClick(){
        for(BoardSquare square: boardSquares) {
            square.setOnMouseClicked(event -> {movePiece(square);});
        }
    }

    private void movePiece(BoardSquare startSquare){
        Piece piece = boardSquareToPiece.get(startSquare);
        startSquare.getChildren().remove(piece);
        boardSquareToPiece.remove(startSquare);
        for(BoardSquare destinationSquare: boardSquares) {
            destinationSquare.setOnMouseClicked(event -> {pieceDestination(destinationSquare, piece);});
        }
    }

    private void pieceDestination(BoardSquare destinationSquare, Piece piece){
        if(boardSquareToPiece.containsKey(destinationSquare)){
            boardSquareToPiece.remove(destinationSquare, boardSquareToPiece.get(destinationSquare));
        }
        addPiece(destinationSquare, piece);
        boardSquareToPiece.put(destinationSquare, piece);
    }

    /**
     *  Sets the color of a given square
     * @param square - The square that's color will be changed
     */
    private void setSquareColor(BoardSquare square){
        // Could be easy to change the colors later here. Or load them from a settings file
        Color black = Color.web("#e4c16f");
        Color white = Color.web("#b88b4a");

        if((square.getxPos()+square.getyPos())%2==0){
            square.setBackground(new Background(new BackgroundFill(black, CornerRadii.EMPTY, Insets.EMPTY)));
        }else{
            square.setBackground(new Background(new BackgroundFill(white, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * This method instantiates objects that extend the Piece class,
     * and assigns/associates them with specific board squares
     */
    private void addPieces(){
        for(BoardSquare square : boardSquares) {
            if (square.getyPos() == 0) {
                if (square.getxPos() == 0 || square.getxPos() == 7) {
                    Piece rookW = new Rook(square.getxPos(), square.getyPos(), "white");
                    addPiece(square, rookW);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, rookW);
                }
                if (square.getxPos() == 1 || square.getxPos() == 6) {
                    Piece knightW = new Knight(square.getxPos(), square.getyPos(), "white");
                    addPiece(square, knightW);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, knightW);
                }
                if (square.getxPos() == 2 || square.getxPos() == 5) {
                    Piece bishopW = new Bishop(square.getxPos(), square.getyPos(), "white");
                    addPiece(square, bishopW);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, bishopW);
                }
                if (square.getxPos() == 3) {
                    Piece queenW = new Queen(square.getxPos(), square.getyPos(), "white");
                    addPiece(square, queenW);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, queenW);
                }
                if (square.getxPos() == 4) {
                    Piece kingW = new King(square.getxPos(), square.getyPos(), "white");
                    addPiece(square, kingW);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, kingW);
                }

            }
            if (square.getyPos() == 1) {
                Piece pawnW = new Pawn(square.getxPos(), square.getyPos(), "white");
                addPiece(square, pawnW);
                // associate the piece with the square
                boardSquareToPiece.put(square, pawnW);
            }
            if (square.getyPos() == 6) {
                Piece pawnB = new Pawn(square.getxPos(), square.getyPos(), "black");
                addPiece(square, pawnB);
                // associate the piece with the square
                boardSquareToPiece.put(square, pawnB);
            }

            if (square.getyPos() == 7) {
                if (square.getxPos() == 0 || square.getxPos() == 7) {
                    Piece rookB = new Rook(square.getxPos(), square.getyPos(), "black");
                    addPiece(square, rookB);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, rookB);
                }
                if (square.getxPos() == 1 || square.getxPos() == 6) {
                    Piece knightB = new Knight(square.getxPos(), square.getyPos(), "black");
                    addPiece(square, knightB);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, knightB);
                }
                if (square.getxPos() == 2 || square.getxPos() == 5) {
                    Piece bishopB = new Bishop(square.getxPos(), square.getyPos(), "black");
                    addPiece(square, bishopB);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, bishopB);
                }
                if (square.getxPos() == 3) {
                    Piece kingB = new King(square.getxPos(), square.getyPos(), "black");
                    addPiece(square, kingB);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, kingB);
                }
                if (square.getxPos() == 4) {
                    Piece queenB = new Queen(square.getxPos(), square.getyPos(), "black");
                    addPiece(square, queenB);
                    // associate the piece with the square
                    boardSquareToPiece.put(square, queenB);
                }
            }
        }
    }

    private void addPiece(BoardSquare square, Node piece){
        square.getChildren().add(piece);
        square.containsPiece = true;
    }
}
