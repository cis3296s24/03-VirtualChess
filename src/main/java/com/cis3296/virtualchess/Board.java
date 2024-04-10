package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    // In case we need to change these column/row/size values for any reason later on...
    public final int MAX_COL = 8, MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    private GridPane chessBoard;
    private ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    private HashMap<BoardSquare, Piece> pieceToSquare = new HashMap();
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
     * @param chessBoard - A GridPane representing the chessboard
     */
    private void init(GridPane chessBoard){
        for(int col = 0; col < MAX_COL; col++){
            for(int row = 0; row < MAX_ROW; row++){
                Coordinates coordinates = new Coordinates(col, row);
                BoardSquare square = new BoardSquare(coordinates);
                square.setPrefHeight(SQUARE_SIZE);
                square.setPrefWidth(SQUARE_SIZE);
                square.setBorder(border);
                setSquareColor(square);
                chessBoard.add(square, col, row, 1, 1);
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
        if((square.coordinates.getCol()+square.coordinates.getRow())%2==0){
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
            if(square.coordinates.getRow() == 0){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 7){
                    Piece rookW = new Rook(square.coordinates, "white");
                    addPiece(square, rookW);
                    // associate the piece with the square
                    pieceToSquare.put(square, rookW);
                }
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 6){
                    Piece knightW = new Knight(square.coordinates, "white");
                    addPiece(square, knightW);
                    // associate the piece with the square
                    pieceToSquare.put(square, knightW);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 5){
                    Piece bishopW = new Bishop(square.coordinates, "white");
                    addPiece(square, bishopW);
                    // associate the piece with the square
                    pieceToSquare.put(square, bishopW);
                }
                if(square.coordinates.getCol() == 3){
                    Piece queenW = new Queen(square.coordinates, "white");
                    addPiece(square, queenW);
                    // associate the piece with the square
                    pieceToSquare.put(square, queenW);
                }
                if(square.coordinates.getCol() == 4){
                    Piece kingW = new King(square.coordinates, "white");
                    addPiece(square, kingW);
                    // associate the piece with the square
                    pieceToSquare.put(square, kingW);
                }

            }
            if(square.coordinates.getRow() == 1){
                Piece pawnW = new Pawn(square.coordinates, "white");
                addPiece(square, pawnW);
                // associate the piece with the square
                pieceToSquare.put(square, pawnW);
            }
            if(square.coordinates.getRow() == 6){
                Piece pawnB = new Pawn(square.coordinates, "black");
                addPiece(square, pawnB);
                // associate the piece with the square
                pieceToSquare.put(square, pawnB);
            }

            if(square.coordinates.getRow() == 7){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 7){
                    Piece rookB = new Rook(square.coordinates, "black");
                    addPiece(square, rookB);
                    // associate the piece with the square
                    pieceToSquare.put(square, rookB);
                }
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 6){
                    Piece knightB = new Knight(square.coordinates, "black");
                    addPiece(square, knightB);
                    // associate the piece with the square
                    pieceToSquare.put(square, knightB);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 5){
                    Piece bishopB = new Bishop(square.coordinates, "black");
                    addPiece(square, bishopB);
                    // associate the piece with the square
                    pieceToSquare.put(square, bishopB);
                }
                if(square.coordinates.getCol() == 3){
                    Piece kingB = new King(square.coordinates, "black");
                    addPiece(square, kingB);
                    // associate the piece with the square
                    pieceToSquare.put(square, kingB);
                }
                if(square.coordinates.getCol() == 4){
                    Piece queenB = new Queen(square.coordinates, "black");
                    addPiece(square, queenB);
                    // associate the piece with the square
                    pieceToSquare.put(square, queenB);
                }
            }
        }

    }

    public void rerenderBoard() {
        for (BoardSquare square : boardSquares) {
            setSquareColor(square);
        }
    }

    private void addPiece(BoardSquare square, Piece piece){
        square.getChildren().add(piece);
        square.containsPiece = true;
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
        Piece piece = pieceToSquare.get(startSquare);
        startSquare.getChildren().remove(piece);
        pieceToSquare.remove(startSquare);
        for(BoardSquare destinationSquare: boardSquares) {
            destinationSquare.setOnMouseClicked(event -> {pieceDestination(destinationSquare, piece);});
        }
    }

    private void pieceDestination(BoardSquare destinationSquare, Piece piece){
        if(pieceToSquare.containsKey(destinationSquare)){
            pieceToSquare.remove(destinationSquare, pieceToSquare.get(destinationSquare));
        }
        addPiece(destinationSquare, piece);
        pieceToSquare.put(destinationSquare, piece);
    }
}
