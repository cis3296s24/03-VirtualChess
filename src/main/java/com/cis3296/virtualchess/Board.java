package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.*;
import javafx.geometry.Insets;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.Event;

public class Board {
    // In case we need to change these column/row/size values for any reason later on...
    public final int MAX_COL = 8, MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    private GridPane chessBoard;
    public ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    private HashMap<BoardSquare, Piece> pieceToSquare = new HashMap();
    private BoardSettings settings;
    private Piece draggingPiece;

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

    public ArrayList<BoardSquare> getBoardSquares() {
        return boardSquares;
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

    /**
     * This method associates the objects that extend the Piece class to the BoardSquare objects.
     * It also sets the pieces to the correct chess start setup.
     */
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
        // Add drag-and-drop event handlers for each Piece object
        for (BoardSquare square : boardSquares) {
            Piece piece = pieceToSquare.get(square);
            if (piece != null) {
                piece.setOnDragDetected(event -> {
                    draggingPiece = piece;
                    Dragboard db = piece.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(""); // You can put any content here if needed
                    db.setContent(content);
                    event.consume();
                });

                // Optionally, add other drag-and-drop event handlers (e.g., drag over, drag dropped)
                // if needed for additional functionality.
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
     * iterates through pieceToSquare key set to find the piece associated with the square
     * @param piece used to confirm the Piece is associated with the square
     * @return the square or null of not found
     */
    private BoardSquare getSquareContainingPiece(Piece piece) {
        for (BoardSquare square : pieceToSquare.keySet()) {
            if (pieceToSquare.get(square) == piece) {
                return square;
            }
        }
        return null;
    }

    /**
     * Getter for the piece currently being dragged
     * @return dragged piece
     */
    public Piece getDraggingPiece() {
        return draggingPiece;
    }

    /**
     * Allows for the movement of the Piece objects
     * @param draggingPiece the piece currently being dragged
     * @param col the destination column
     * @param row the destination row
     */
    public void movePiece(Piece draggingPiece, int col, int row) {
        // Check if the move is valid
        if (isValidMove(draggingPiece, col, row)) {
            // Update the coordinates of the dragging piece
            draggingPiece.coordinates.setCoordinates(col, row);

            // Update the piece's position on the board
            BoardSquare currentSquare = getSquareContainingPiece(draggingPiece);
            currentSquare.getChildren().remove(draggingPiece);

            BoardSquare targetSquare;
            for(BoardSquare square: boardSquares){
                if(square.coordinates.getCol() == col && square.coordinates.getRow() == row){
                    targetSquare = square;
                    targetSquare.getChildren().add(draggingPiece);

                    System.out.println("From: col,row: "+
                            currentSquare.coordinates.getCol()+", "+currentSquare.coordinates.getRow()+
                            "\nTo col,row:" +
                            col+", "+row);
                }
            }

            // Optionally, update any data structures or variables tracking piece positions
        }
    }

    /**
     * Checks to see if teh move being made is valid
     * @param draggingPiece the piece currently being dragged
     * @param col the destination column
     * @param row the destination row
     * @return
     */
    public boolean isValidMove(Piece draggingPiece, int col, int row) {
        // Check if the target coordinates are within the bounds of the board
        if (!isValidCoordinate(col, row)) {
            return false;
        }

        // Retrieve the current position of the dragging piece
        int currentCol = draggingPiece.coordinates.getCol();
        int currentRow = draggingPiece.coordinates.getRow();


        // Check if the move is valid based on the type of piece
        switch (draggingPiece.type) {
            case "knight":
            case "bishop":
            case "queen":
            case "king":
            case "pawn":
            case "rook":
                // Implement rook movement rules
                return isValidRookMove(draggingPiece, currentCol, currentRow, col, row);
            // Implement other piece movement rules (e.g., Knight, Bishop, Queen, King) as needed
            default:
                // Unsupported piece type
                System.out.println("Did not move");
                return false;
        }
    }

    /**
     * Checks to see if the coordinates are valid coordinates on the GridPane
     * @param col column the piece is being dragged over
     * @param row row the piece is being dragged over
     * @return
     */
    private boolean isValidCoordinate(int col, int row) {
        return col >= 0 && col < MAX_COL && row >= 0 && row < MAX_ROW;
    }

    /**
     * Move set for the rook Piece object
     * @param rook
     * @param currentCol
     * @param currentRow
     * @param targetCol
     * @param targetRow
     * @return
     */
    private boolean isValidRookMove(Piece rook, int currentCol, int currentRow, int targetCol, int targetRow) {
        // Implement rook movement rules
        // For example, you might check if the rook is moving along a straight line (either horizontally or vertically)
        // and if the path is clear of other pieces.
        return true;
    }

}
