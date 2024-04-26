package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.*;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Pieces.*;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;

/**
 * An 8x8 Grid the contains all the {@link BoardSquare} and {@link Piece} to play a game of chess
 */
public class Board {

    // Amount of squares in the rows and columns
    public final int MAX_COL = 8, MAX_ROW = 8;
    // Square Size in pixels
    public static final int SQUARE_SIZE = 100;

    // An array list holding all the board square. Can be indexed using getSquareAt
    public ArrayList<BoardSquare> boardSquares = new ArrayList<>();
    // An array list holding all the pieces. Can be indexed using getPieceAt
    public ArrayList<Piece> pieces = new ArrayList<>();

    private final ArrayList<StackPane> moves = new ArrayList<>();

    private Piece kingW;

    // Reference to game being passed in
    private final Game game;
    // The settings for the board/game
    private final BoardSettings settings;

    // Piece that is being held in the drag
    private Piece targetPiece;
    // Not being used currently but would function similarly to targetPiece
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
     * @param chessBoard A gridPane representing the chessboard
     * @param settings Any Settings to be used in the board
     * @param game Reference to the game that created this board
     */
    public Board(GridPane chessBoard, BoardSettings settings, Game game){
        this.settings = settings;
        init(chessBoard);
        this.game = game;
    }

    /**
     * Goes through each of the tiles in the board and sets them up to be displayed
     * @param chessBoard A GridPane representing the chessboard
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
                // Sets the Drag handler so that when a piece is dropped on the board it moves the piece there
                square.setOnDragDropped(dragEvent -> {
                    movePiece(square);
                });
            }
        }
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.KV_CONFIG_ACCESS_STRING))){
            addPiecesSimple();
        } else{
            addPieces();
        }
        for (Piece piece : this.pieces) {
            piece.currentMoveSet = piece.getMoveSetSuper();
        }
    }

    /**
     * Indexes into the {@link Board#boardSquares} arraylist to get the desired square
     * @param coordinates The {@link Coordinates} where you want to access a board square
     * @return The {@link BoardSquare} at the coordinates or null if not found
     */
    public BoardSquare getSquareAt(Coordinates coordinates) {
        int index = coordinates.getCol() * 8 + coordinates.getRow();
        if(index >= boardSquares.size() || index < 0) return null;
        return boardSquares.get(index);
    }

    /**
     * Gets the square at the coordinates and looks for pieces as children to that square
     * @param coordinates {@link Coordinates} for where to look for a piece
     * @return {@link Piece} at the coordinates or null if the square doesn't have a piece
     */
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
     * Sets the color of a given square
     * @param square The {@link BoardSquare} that's color will be changed
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

    private void addPiecesSimple(){
        boolean blackTurn = false;
        boolean whiteTurn = true;
        for(BoardSquare square : boardSquares){
            if(square.coordinates.getRow() == 7){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 6){
                    Piece rookW = new Rook(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, rookW);
                }

                if(square.coordinates.getCol() == 2){
                    Piece queenW = new Queen(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, queenW);
                }
                if(square.coordinates.getCol() == 4){
                    kingW = new King(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, kingW);
                }

            }
            if(square.coordinates.getRow() == 6){
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 7){
                    Piece knightW = new Knight(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, knightW);
                }
                if(square.coordinates.getCol() == 3 || square.coordinates.getCol() == 5){
                    Piece bishopW = new Bishop(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, bishopW);
                }
            }
            if(square.coordinates.getRow() == 5){
                if(square.coordinates.getCol() % 2 == 0) {
                    Piece pawnW = new Pawn(square.coordinates, "white", this, whiteTurn);
                    addPiece(square, pawnW);
                }
            }

            if(square.coordinates.getRow() == 2){
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 3 || square.coordinates.getCol() == 5 || square.coordinates.getCol() == 7) {
                    Piece pawnB = new Pawn(square.coordinates, "black", this, blackTurn);
                    addPiece(square, pawnB);
                }
            }
            if(square.coordinates.getRow() == 1){
                if(square.coordinates.getCol() == 0 || square.coordinates.getCol() == 6){
                    Piece knightB = new Knight(square.coordinates, "black", this, blackTurn);
                    addPiece(square, knightB);
                }
                if(square.coordinates.getCol() == 2 || square.coordinates.getCol() == 4){
                    Piece bishopB = new Bishop(square.coordinates, "black", this, blackTurn);
                    addPiece(square, bishopB);
                }
            }
            if(square.coordinates.getRow() == 0){
                if(square.coordinates.getCol() == 1 || square.coordinates.getCol() == 7){
                    Piece rookB = new Rook(square.coordinates, "black", this, blackTurn);
                    addPiece(square, rookB);
                }

                if(square.coordinates.getCol() == 3){
                    Piece queenB = new Queen(square.coordinates, "black", this, blackTurn);
                    addPiece(square, queenB);
                }
                if(square.coordinates.getCol() == 5){
                    Piece kingB = new King(square.coordinates, "black", this, blackTurn);
                    addPiece(square, kingB);
                }

            }

        }
    }

    /**
     * Goes through all the {@link BoardSquare} on the {@link Board} and sets the colors of them
     * This is used when changing the theme in settings to make sure the new color is applied
     */
    public void rerenderBoard() {
        for (BoardSquare square : boardSquares) {
            setSquareColor(square);
        }
    }

    /**
     * Adds a given {@link Piece} to a given {@link BoardSquare}
     * Only For setup
     * @param square the target boardsquare
     * @param piece the piece that will be added
     */
    public void addPiece(BoardSquare square, Piece piece){
        pieces.add(piece);
        square.getChildren().add(piece);
        square.containsPiece = true;

        // Each piece needs to be able to be dragged and dropped
        piece.setOnDragDetected(event -> {
            pieceOnInteract(event, piece);
        });
    }

    /**
     * The method that is run when a piece is picked up
     * @param event A {@link Event}
     * @param piece The {@link Piece} that is being dragged
     */
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
     * Checks if the move to a square is valid
     * @param col Destination {@link BoardSquare} column
     * @param row Destination {@link BoardSquare} row
     * @return true if the move is valid false otherwise
     */
    public boolean isValidMove(int col, int row) {
        return targetPiece.canMove(col, row) && isValidCoordinate(col, row);
    }

    /**
     * Another version of isValidMove that uses the targetPiece
     * @param col Destination {@link BoardSquare} column
     * @param row Destination {@link BoardSquare} row
     * @param targetPiece The {@link Piece} that is being moved
     * @return true if the move is valid false otherwise
     */
    public boolean isValidMove(int col, int row, Piece targetPiece) {
        if(targetPiece != null){
            return targetPiece.canMove(col, row) && isValidCoordinate(col, row);
        } else {
            return isValidCoordinate(col, row);
        }
    }

    /**
     * Checks to see if the coordinates are valid on the board
     * @param col column the {@link Piece} is being dragged over
     * @param row row the {@link Piece} is being dragged over
     * @return true if valid, false if not valid
     */
    public boolean isValidCoordinate(int col, int row) {
        return  col >= 0 &&
                col < MAX_COL &&
                        row >= 0 &&
                        row < MAX_ROW;
    }

    /**
     * Moves a {@link Piece} from one {@link Coordinates} to another
     * @param from the coordinates where the piece is moving from
     * @param to the coordinates where the piece is moving to
     */
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
                getPieceAt(to).guardedSquares.clear();
                getPieceAt(to).currentMoveSet.clear();
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
     * @param destSquare the {@link BoardSquare} for the piece to be set on
     */
    private void movePiece(BoardSquare destSquare){
        BoardSquare prevSquare = getSquareAt(targetPiece.coordinates);
        if(isValidMove(destSquare.coordinates.getCol(), destSquare.coordinates.getRow())){
            // Remove the piece from the square
            prevSquare.getChildren().remove(targetPiece);
            targetPiece.guardedSquares.clear();

            // If the destination square has an opponent piece, remove it
            Piece destPiece = getPieceAt(destSquare.coordinates);
            if(destPiece != null){
                destPiece.guardedSquares.clear();
                destPiece.currentMoveSet.clear();
                this.pieces.remove(destPiece);
                destSquare.getChildren().remove(destPiece);
                targetPiece.guardedSquares.clear();
                targetPiece.currentMoveSet.clear();
            }
            // Add the piece to the new square
            destSquare.containsPiece = true;
            destSquare.getChildren().add(targetPiece);
            // Set the new coordinates of the piece
            targetPiece.coordinates = destSquare.coordinates;

            for (Piece piece : pieces)
            {
                piece.currentMoveSet = piece.getMoveSetSuper();
            }

            pieceCheck(targetPiece, destSquare, prevSquare);

            System.out.println(targetPiece.type + " to " + Coordinates.toChessCoordinates(destSquare.coordinates));
            game.handleTurn();
        }
        // If you put the piece back down in the same place, don't print invalid move
        else if (destSquare.coordinates != prevSquare.coordinates){
            System.out.println("Invalid Move");
        }
    }

    /**
     * Checks various condition on a piece when moving to a square
     * @param targetPiece The {@link Piece} that is being moved
     * @param destSquare The {@link BoardSquare} that the piece is being moved to
     * @param prevSquare The {@link BoardSquare} that the piece is being moved from
     */
    private void pieceCheck(Piece targetPiece, BoardSquare destSquare, BoardSquare prevSquare){
        // Castling
        if(targetPiece.type.equals("king")){
            caseOfMove(destSquare);
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

        if(targetPiece.type.equals("rook")){
            caseOfMove(destSquare);
        }

        // Pawn extra checks
        if(targetPiece.type.equals("pawn")) {
            pawnPromotion();
            caseOfMove(destSquare);
            Coordinates topLeft = new Coordinates(targetPiece.coordinates.getCol() - 1, targetPiece.coordinates.getRow() - 1);
            Coordinates topRight = new Coordinates(targetPiece.coordinates.getCol() + 1, targetPiece.coordinates.getRow() - 1);
            Coordinates bottomLeft = new Coordinates(targetPiece.coordinates.getCol() - 1, targetPiece.coordinates.getRow() + 1);
            Coordinates bottomRight = new Coordinates(targetPiece.coordinates.getCol() + 1, targetPiece.coordinates.getRow() + 1);
            if (targetPiece.color.equals("white") &&
                    prevSquare.coordinates.getRow() == 3 &&
                    (prevSquare.coordinates.equals(bottomRight) || prevSquare.coordinates.equals(bottomLeft))) {
                BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow() + 1));
                System.out.println(square.coordinates);
                if (!square.getChildren().isEmpty()) {
                    square.getChildren().removeFirst();
                }
            } else if (targetPiece.color.equals("black") &&
                    prevSquare.coordinates.getRow() == 4 &&
                    (prevSquare.coordinates.equals(topRight) || prevSquare.coordinates.equals(topLeft))) {
                BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow() - 1));
                if (!square.getChildren().isEmpty()) {
                    square.getChildren().removeFirst();
                }

            }
        }
    }

    /**
     * This method takes the coordinates of the current piece being dragged and shows them on the board
     * @param coordinates The {@link Coordinates} of the {@link BoardSquare} to show the move at
     */
    public void showMoves(Coordinates coordinates){
        BoardSquare square = getSquareAt(coordinates);
        if(square == null){
            return;
        }
        StackPane hint = drawHint();
        square.getChildren().add(hint);
        moves.add(hint);
    }

    /**
     * Creates the circle hint that is shown when dragging
     * @return the {@link StackPane} representing the hint
     */
    public StackPane drawHint(){
        // All of this for drawing the move hints
        Circle circle1 = new Circle(10, 10, 10);
        circle1.setFill(Color.BLACK);
        Sphere sphere = new Sphere(8);
        StackPane stackPane = new StackPane();
        StackPane.setMargin(circle1, new Insets(5, 5, 5, 5));
        stackPane.getChildren().addAll(circle1, sphere);
        return stackPane;
    }

    /**
     * Removes all the shown moves on the board
     */
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

    /**
     * Goes through each of the pieces of the board and builds a FEN string from them
     * @see Stockfish for printing the board
     * @return A FEN String representing the board
     */
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
