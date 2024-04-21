package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.*;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Pieces.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;

public class Board {
    // In case we need to change these column/row/size values for any reason later on...
    public final int MAX_COL = 8, MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;

    public ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    private final ArrayList<StackPane> moves = new ArrayList<>();
    public ArrayList<Piece> pieces = new ArrayList<>();

    private Piece kingW;

    private final Game game;
    private final BoardSettings settings;

    private Piece targetPiece;
    private BoardSquare targetSquare;


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
     * @param chessBoard - A gridPane representing the chessboard
     */
    public Board(GridPane chessBoard, BoardSettings settings, Game game){

        this.settings = settings;
        init(chessBoard);
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
                square.setOnDragDropped(dragEvent -> {
                    movePiece(square);
                });
//                square.setOnMouseClicked(mouseEvent -> pieceOnInteract(mouseEvent, getPieceAt(square.coordinates)));
            }
        }
        addPieces();
    }

    public BoardSquare getSquareAt(Coordinates coordinates) {
        int index = coordinates.getCol() * 8 + coordinates.getRow();
        if(index >= boardSquares.size() || index < 0) return null;
        return boardSquares.get(index);
    }

    public Piece getPieceAt(Coordinates coordinates) {
        BoardSquare square = getSquareAt(coordinates);

        if(square == null) return null;

        if (!square.getChildren().isEmpty()) {
            Node child = square.getChildren().getFirst();
            if (child instanceof Piece) {
                return (Piece) child;
            } else {
                return null;
            }
        } else {
            return null;
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
        boolean blackTurn = false;
        boolean whiteTurn = true;
        for(BoardSquare square : boardSquares){
            if(square.coordinates.getRow() == 7){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 7){
                    Piece rookW = new Rook(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, rookW);
                }
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 6){
                    Piece knightW = new Knight(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, knightW);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 5){
                    Piece bishopW = new Bishop(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, bishopW);
                }
                if(square.coordinates.getCol() == 3){
                    Piece queenW = new Queen(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, queenW);
                }
                if(square.coordinates.getCol() == 4){
                    kingW = new King(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, kingW);
                }

            }
            if(square.coordinates.getRow() == 6){
                Piece pawnW = new Pawn(square.coordinates, "white", this, whiteTurn);
                addPiece(square, pawnW);
            }
            if(square.coordinates.getRow() == 1){
                Piece pawnB = new Pawn(square.coordinates, "black", this, blackTurn);
                addPiece(square, pawnB);
            }

            if(square.coordinates.getRow() == 0){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 7){
                    Piece rookB = new Rook(square.coordinates, "black", this, blackTurn);
                    addPiece(square, rookB);
                }
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 6){
                    Piece knightB = new Knight(square.coordinates, "black", this, blackTurn);
                    addPiece(square, knightB);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 5){
                    Piece bishopB = new Bishop(square.coordinates, "black", this, blackTurn);
                    addPiece(square, bishopB);
                }
                if(square.coordinates.getCol() == 4){
                    Piece kingB = new King(square.coordinates, "black", this, blackTurn);
                    addPiece(square, kingB);
                }
                if(square.coordinates.getCol() == 3){
                    Piece queenB = new Queen(square.coordinates, "black", this, blackTurn);
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

    public void addPiece(BoardSquare square, Piece piece){
        pieces.add(piece);
        square.getChildren().add(piece);
        square.containsPiece = true;

        // each piece needs to be able to be dragged and dropped
        piece.setOnDragDetected(event -> {
            pieceOnInteract(event, piece);
        });
    }

    private void pieceOnInteract(Event event, Piece piece){
        if(piece.isTurn){
            targetPiece = piece;
//            if(event instanceof DragEvent){
                Dragboard db = piece.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(piece.getImage());
                ClipboardContent content = new ClipboardContent();
                content.putString(""); // You can put any content here if needed
                db.setContent(content);
//            }
            if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.HINTS_CONFIG_ACCESS_STRING))){
                piece.showMoves(this);
            }
            event.consume();
        }
    }

    /**
     * Checks to see if the move being made is valid
     * @return true if the move is valid, false if not
     */
    public boolean isValidMove(int col, int row) {
        return targetPiece.canMove(col, row) && isValidCoordinate(col, row);
    }
    public boolean isValidMove(int col, int row, Piece targetPiece) {
        if(targetPiece != null){
            return targetPiece.canMove(col, row) && isValidCoordinate(col, row);
        } else {
            return isValidCoordinate(col, row);
        }
    }

    /**
     * Checks to see if the coordinates are valid coordinates on the GridPane
     * @param col column the piece is being dragged over
     * @param row row the piece is being dragged over
     * @return true if valid, false if not valid
     */
    public boolean isValidCoordinate(int col, int row) {
        return col >= 0 && col < MAX_COL && row >= 0 && row < MAX_ROW;
    }

    public void moveFromTo(Coordinates from, Coordinates to){
        BoardSquare fromSquare = getSquareAt(from);
        BoardSquare toSquare = getSquareAt(to);
        Piece fromPiece = getPieceAt(from);
        Piece toPiece = getPieceAt(to);
        if(fromPiece != null && isValidMove(to.getCol(), to.getRow(), fromPiece)){
            // Remove the piece from the square
            fromSquare.getChildren().remove(fromPiece);
            // If the destination square has an opponent piece, remove it
            if(toPiece != null){
                toSquare.getChildren().remove(toPiece);
            }
            // Add the piece to the new square
            toSquare.containsPiece = true;
            toSquare.getChildren().add(fromPiece);

            // Set the new coordinates of the piece
            fromPiece.coordinates = toSquare.coordinates;

//            pieceCheck(fromPiece, toSquare);

            System.out.println(fromPiece.type + " to " + Coordinates.toChessCoordinates(to));
            game.handleTurn();

        } else{
            System.out.println("Invalid Move");
        }
    }

    /**
     * This method ensures that the movement of a piece is valid,
     * then calls mouse event handlers to allow for drag-and-drop of piece
     * @param destSquare the square for the piece to be set on
     */
    private void movePiece(BoardSquare destSquare){
        BoardSquare prevSquare = getSquareAt(targetPiece.coordinates);
        if(isValidMove(destSquare.coordinates.getCol(), destSquare.coordinates.getRow())){
            // Remove the piece from the square
            prevSquare.getChildren().remove(targetPiece);
            // If the destination square has an opponent piece, remove it
            if(destSquare.containsPiece){
                Piece destPiece = getPieceAt(destSquare.coordinates);
                destSquare.getChildren().remove(destPiece);
            }
            // Add the piece to the new square
            destSquare.containsPiece = true;
            destSquare.getChildren().add(targetPiece);

            // Set the new coordinates of the piece
            targetPiece.coordinates = destSquare.coordinates;

            pieceCheck(targetPiece, destSquare);

            System.out.println(targetPiece.type + " to " + Coordinates.toChessCoordinates(destSquare.coordinates));

            game.handleTurn();
        } else{
            System.out.println("Invalid Move");
        }
    }

    private void pieceCheck(Piece targetPiece, BoardSquare destSquare){
        // Pawn promotion
        if(targetPiece.type.equals("pawn")){
            pawnPromotion();
        }

        if(targetPiece.type.equals("king")){
            Coordinates rightRookCoord = new Coordinates(destSquare.coordinates.getCol()+1, destSquare.coordinates.getRow());
            Coordinates leftRookCoord = new Coordinates(destSquare.coordinates.getCol()-2, destSquare.coordinates.getRow());
            Piece rightRook = getPieceAt(rightRookCoord);
            Piece leftRook = getPieceAt(leftRookCoord);
            if(rightRook != null && !rightRook.moved && rightRook.type.equals("rook")){
                BoardSquare square = getSquareAt(new Coordinates(destSquare.coordinates.getCol()-1, destSquare.coordinates.getRow()));
                square.getChildren().remove(rightRook);
                square.getChildren().add(rightRook);
            }
            if(leftRook != null && !leftRook.moved && leftRook.type.equals("rook")){
                BoardSquare square = getSquareAt(new Coordinates(destSquare.coordinates.getCol()+1, destSquare.coordinates.getRow()));
                square.getChildren().remove(leftRook);
                square.getChildren().add(leftRook);
            }
        }

            // handle any rules after first movement of pawn
            if(targetPiece.type.equals("pawn") || targetPiece.type.equals("king") || targetPiece.type.equals("rook")){
                caseOfMove(destSquare);
            }
            if(targetPiece.type.equals("pawn")){
                Coordinates topLeft = new Coordinates(targetPiece.coordinates.getCol()-1, targetPiece.coordinates.getRow()-1);
                Coordinates topRight = new Coordinates(targetPiece.coordinates.getCol()+1, targetPiece.coordinates.getRow()-1);
                Coordinates bottomLeft = new Coordinates(targetPiece.coordinates.getCol()-1, targetPiece.coordinates.getRow()+1);
                Coordinates bottomRight = new Coordinates(targetPiece.coordinates.getCol()+1, targetPiece.coordinates.getRow()+1);
                if(targetPiece.color.equals("white") &&
                        prevSquare.coordinates.getRow() == 3 &&
                        (prevSquare.coordinates.equals(bottomRight) || prevSquare.coordinates.equals(bottomLeft))){
                    BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow()+1));
                    System.out.println(square.coordinates);
                    if(!square.getChildren().isEmpty()){
                        square.getChildren().removeFirst();
                    }
                } else if(targetPiece.color.equals("black") &&
                        prevSquare.coordinates.getRow() == 4 &&
                        (prevSquare.coordinates.equals(topRight) || prevSquare.coordinates.equals(topLeft))){
                    BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow()-1));
                    if(!square.getChildren().isEmpty()){
                        square.getChildren().removeFirst();
                    }

                }
            }

            System.out.println(targetPiece.type + " to " + Coordinates.toChessCoordinates(destSquare.coordinates));
            game.handleTurn();
        } else{
            System.out.println("Invalid Move");
        }
    }

    /**
     * This method takes the coordinates of the current piece being dragged and shows the
     * @param coordinates on the board
     */
    public void showMoves(Coordinates coordinates){
        BoardSquare square = getSquareAt(coordinates);
        if(square == null){
            return;
        }
        // All of this for drawing the move hints
        Circle circle1 = new Circle(10, 10, 10);
        circle1.setFill(Color.BLACK);
        Sphere sphere = new Sphere(8);
        StackPane stackPane = new StackPane();
        StackPane.setMargin(circle1, new Insets(5, 5, 5, 5));
        stackPane.getChildren().addAll(circle1, sphere);
        square.getChildren().add(stackPane);
        moves.add(stackPane);
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
    public void caseOfMove(BoardSquare destSquare){
        if(!targetPiece.moved){
            targetPiece.moved = true;
        }
        if (targetPiece.type.equals("pawn")){
            if(targetPiece.color.equals("white") && destSquare.coordinates.getRow() == 4) {
                targetPiece.twoStepped = true;
            }
            else if(targetPiece.color.equals("black") && destSquare.coordinates.getRow() == 3){
                targetPiece.twoStepped = true;
            }
        }
    }

    /**
     * Handles the promotion of the pawns once they reach the other side of the board
     */
    public void pawnPromotion(){
        // The current row of the pawn
        int currentRow = targetPiece.coordinates.getRow();
        // If the pawn is at the opposite side then promote it
        if(targetPiece.color.equals("white") && currentRow == 0 || targetPiece.color.equals("black") && currentRow == 7){
            BoardSquare currentSquare = getSquareAt(targetPiece.coordinates);
            ((Pawn) targetPiece).promote(currentSquare, this);
        }
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        int empty = 0;

        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                Piece piece = getPieceAt(new Coordinates(col, row));
                if (piece != null) {
                    if (empty != 0) {
                        board.append(empty);
                        empty = 0;
                    }
                    String pieceChar = piece.toString();
                    board.append(pieceChar);
                } else {
                    empty++;
                }
            }
            if (empty != 0) {
                board.append(empty);
                empty = 0;
            }
            if (row < MAX_ROW - 1) {
                board.append("/");
            }
        }
        return board.toString();
    }

}


