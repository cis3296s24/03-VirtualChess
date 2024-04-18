package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.*;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Pieces.*;
import javafx.geometry.Insets;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    // In case we need to change these column/row/size values for any reason later on...
    public final int MAX_COL = 8, MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;

    private GridPane chessBoard;

    public ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    public ArrayList<Piece> pieces = new ArrayList<>();
    private ArrayList<StackPane> moves = new ArrayList<>();
    public HashMap<BoardSquare, Piece> pieceToSquare = new HashMap<>();

    private Game game;
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
    public Board(GridPane chessBoard, BoardSettings settings, Game game){

        this.chessBoard = chessBoard;
        this.settings = settings;
        init(this.chessBoard);
        this.game = game;
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
                square.setOnDragDropped(dragEvent ->{
                    movePiece(square);
                });
            }
        }
        addPieces();
        // Add drag-and-drop event handlers for each Piece object
        for (Piece piece : pieces) {
            piece.setOnDragDetected(event -> {
                if(piece.isTurn){
                    draggingPiece = piece;
                    Dragboard db = piece.startDragAndDrop(TransferMode.MOVE);
                    piece.showMoves(this);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(""); // You can put any content here if needed
                    db.setContent(content);
                    event.consume();
                }
            });
        }
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
            if(square.coordinates.getRow() == 7){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 7){
                    Piece rookW = new Rook(square.coordinates, "white", this);
                    addPiece(square, rookW);
                }
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 6){
                    Piece knightW = new Knight(square.coordinates, "white", this);
                    addPiece(square, knightW);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 5){
                    Piece bishopW = new Bishop(square.coordinates, "white", this);
                    addPiece(square, bishopW);
                }
                if(square.coordinates.getCol() == 3){
                    Piece queenW = new Queen(square.coordinates, "white", this);
                    addPiece(square, queenW);
                }
                if(square.coordinates.getCol() == 4){
                    Piece kingW = new King(square.coordinates, "white", this);
                    addPiece(square, kingW);
                }

            }
            if(square.coordinates.getRow() == 6){
                Piece pawnW = new Pawn(square.coordinates, "white", this);
                addPiece(square, pawnW);
            }
            if(square.coordinates.getRow() == 1){
                Piece pawnB = new Pawn(square.coordinates, "black", this);
                addPiece(square, pawnB);
            }

            if(square.coordinates.getRow() == 0){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 7){
                    Piece rookB = new Rook(square.coordinates, "black", this);
                    addPiece(square, rookB);
                }
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 6){
                    Piece knightB = new Knight(square.coordinates, "black", this);
                    addPiece(square, knightB);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 5){
                    Piece bishopB = new Bishop(square.coordinates, "black", this);
                    addPiece(square, bishopB);
                }
                if(square.coordinates.getCol() == 4){
                    Piece kingB = new King(square.coordinates, "black", this);
                    addPiece(square, kingB);
                }
                if(square.coordinates.getCol() == 3){
                    Piece queenB = new Queen(square.coordinates, "black", this);
                    addPiece(square, queenB);
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
        pieces.add(piece);
        square.getChildren().add(piece);
        pieceToSquare.put(square, piece);
        square.containsPiece = true;
    }

    /**
     * Checks to see if the move being made is valid
     * @return
     */
    public boolean isValidMove(int col, int row) {
        return draggingPiece.canMove(col, row) && isValidCoordinate(col, row);
    }

    /**
     * Checks to see if the selected castle is valid
     * @param king is the king piece that wants to castle
     * @param rook is the rook to be castled with the king
     * @return true if neither the rook or the king have been moved at all. Return false if either one has moved
     */

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
     * This method ensures that the movement of a piece is valid,
     * then calls mouse event handlers to allow for drag-and-drop of piece
     * @param destSquare the square for the piece to be set on
     */
    private void movePiece(BoardSquare destSquare){
        // Look through all board square and find the one that matches the piece's coordinates
        for(BoardSquare prevSquare: boardSquares){
            if(prevSquare.coordinates.equals(draggingPiece.coordinates)){
                // Validate the movement
                if(isValidMove(destSquare.coordinates.getCol(), destSquare.coordinates.getRow())){
                    // Remove the piece from the square
                    pieceToSquare.remove(prevSquare);
                    prevSquare.getChildren().remove(draggingPiece);
                    // If the destination square has an opponent piece, remove it
                    if(destSquare.containsPiece){
                        Piece destPiece = pieceToSquare.get(destSquare);
                        destSquare.getChildren().remove(destPiece);
                        pieceToSquare.remove(destSquare, destPiece);
                    }
                    // Add the piece to the new square
                    destSquare.containsPiece = true;
                    destSquare.getChildren().add(draggingPiece);
                    pieceToSquare.put(destSquare, draggingPiece);

                    // Set the new coordinates of the piece
                    draggingPiece.coordinates = destSquare.coordinates;

                    // handle any rules after first movement of pawn
                    if(draggingPiece.type.equals("pawn")){
                        caseOfPawnMove();
                    }

                    game.handleTurn();
                } else{
                    System.out.println("Invalid Move");
                }
            }
        }
    }

    /**
     * This method takes the coordinates of the current piece being dragged and shows the
     * @param coordinates
     */
    public void showMoves(Coordinates coordinates){
        for(BoardSquare square : boardSquares){
            if(coordinates.equals(square.coordinates)){
                //All of this for drawing the move hints
                Circle circle1 = new Circle(10, 10, 10);
                circle1.setFill(Color.BLACK);
                Sphere sphere = new Sphere(8);
                StackPane stackPane = new StackPane();
                StackPane.setMargin(circle1, new Insets(5, 5, 5, 5));
                stackPane.getChildren().addAll(circle1, sphere);
                square.getChildren().add(stackPane);
                moves.add(stackPane);
            }
        }
    }

    public void removeShownMoves(){
        for(StackPane sp : moves){
            BoardSquare boardSquare = (BoardSquare) sp.getParent();
            boardSquare.getChildren().remove(sp);
        }
        moves.clear();
    }

    /**
     * Handles a boolean value after a pawn makes its first move
     */
    public void caseOfPawnMove(){
        if(!draggingPiece.moved){
            draggingPiece.moved = true;
        }
    }
}
