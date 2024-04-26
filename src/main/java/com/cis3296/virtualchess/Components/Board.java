package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.*;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Move;
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
import java.util.Stack;

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

    private Stack<Move> moveStack = new Stack<>();


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
//                square.setOnMouseClicked(mouseEvent -> pieceOnInteract(mouseEvent, getPieceAt(square.coordinates)));
            }
        }
        addPieces();
        for (Piece piece : this.pieces) {
            piece.currentMoveSet = piece.getMoveSet();
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
     * Undoes the most recent move done on the board
     * @param oldSquare the square the piece was on previously
     * @param pieceMovingBack the piece moving back to the old square
     * @param isEatenPiece if the piece is an eaten piece, it goes through a different process
     */
    public void undoPieceMove(BoardSquare oldSquare, Piece pieceMovingBack, boolean isEatenPiece){
        BoardSquare prevSquare = getSquareAt(pieceMovingBack.coordinates);

        // make sure it's not an eaten piece
        if(!isEatenPiece){
            /* CASE FOR MOVED PIECE */

            // if the piece has any eaten pieces put them back in the correct order
            if(!pieceMovingBack.eatenPieces.isEmpty()){
                // look for the most recent eaten piece
                Piece eatenPiece = pieceMovingBack.eatenPieces.peek();
                if(eatenPiece != null){
                    // ensure that the moment the eaten piece was eaten is the correct time it's put back
                    if(eatenPiece.otherPieceMoveWhenEaten == pieceMovingBack.timesMoved){
                        // take the piece from the stack of eaten pieces
                        eatenPiece = pieceMovingBack.eatenPieces.pop();
                        // get the destination square it had before
                        BoardSquare destination = getSquareAt(eatenPiece.coordinates);
                        // undo the move
                        undoPieceMove(destination, eatenPiece, true);
                    }
                }
            }
            // Remove the piece from the square
            prevSquare.getChildren().remove(pieceMovingBack);
            // Add the piece to the previous square
            addPiece(oldSquare, pieceMovingBack);
            // Set the coordinates of the piece
            pieceMovingBack.coordinates = oldSquare.coordinates;

            // decrement the amount of times the piece moved
            pieceMovingBack.timesMoved--;

            // check the piece to regain any movement if necessary
            pieceCheck(pieceMovingBack, oldSquare, prevSquare);

            // ensure the right turn
            game.handleTurn();
        } else {
            /* CASE FOR DELETED PIECE */

            // add the piece back to the square
            addPiece(oldSquare, pieceMovingBack);

            // Set the new coordinates of the piece
            pieceMovingBack.coordinates = oldSquare.coordinates;

        }

        System.out.println(pieceMovingBack.color+" "+pieceMovingBack.type + " back to " + Coordinates.toChessCoordinates(oldSquare.coordinates));
    }

    /**
     * This method ensures that the movement of a piece is valid,
     * then calls mouse event handlers to allow for drag-and-drop of piece
     * @param destSquare the {@link BoardSquare} for the piece to be set on
     */
    private void movePiece(BoardSquare destSquare){
        BoardSquare prevSquare = getSquareAt(targetPiece.coordinates);
        if(isValidMove(destSquare.coordinates.getCol(), destSquare.coordinates.getRow())){

            // increment the amount of times the piece is moved
            targetPiece.timesMoved++;

            // Remove the piece from the square
            prevSquare.getChildren().remove(targetPiece);
            targetPiece.guardedSquares.clear();

            // If the destination square has an opponent piece, remove it
            Piece destPiece = getPieceAt(destSquare.coordinates);
            if(destPiece != null){
                // Store the move the piece was eaten at
                destPiece.otherPieceMoveWhenEaten = targetPiece.timesMoved;
                // Store it in the eaten pieces
                targetPiece.eatenPieces.add(destPiece);

                destPiece.guardedSquares.clear();
                destPiece.currentMoveSet.clear();
                this.pieces.remove(destPiece);
                destSquare.getChildren().remove(destPiece);
                targetPiece.guardedSquares.clear();
                targetPiece.currentMoveSet.clear();
            }

            // store the previous move before making the move
            Move currentMove = new Move(targetPiece, targetPiece.coordinates);
            moveStack.add(currentMove);

            // Add the piece to the new square
            destSquare.containsPiece = true;
            destSquare.getChildren().add(targetPiece);
            // Set the new coordinates of the piece
            targetPiece.coordinates = destSquare.coordinates;

            for (Piece piece : pieces)
            {
                piece.currentMoveSet = piece.getMoveSet();
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
        // handle any rules after first movement of piece
        if(targetPiece.type.equals("pawn") || targetPiece.type.equals("king") || targetPiece.type.equals("rook")){
            caseOfMove(destSquare, targetPiece);
        }

        // Pawn promotion
        if(targetPiece.type.equals("pawn")){
            pawnPromotion(targetPiece);
        }

        // Castling
        if(targetPiece.type.equals("king")){
            caseOfMove(destSquare, targetPiece);
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
            caseOfMove(destSquare, targetPiece);
        }

        // Pawn extra checks
        if(targetPiece.type.equals("pawn")) {
            pawnPromotion(targetPiece);
            caseOfMove(destSquare, targetPiece);
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
                    Piece piece = (Piece) square.getChildren().removeFirst();
                    piece.otherPieceMoveWhenEaten = targetPiece.timesMoved;
                    targetPiece.eatenPieces.add(piece);
                }
            } else if (targetPiece.color.equals("black") &&
                    prevSquare.coordinates.getRow() == 4 &&
                    (prevSquare.coordinates.equals(topRight) || prevSquare.coordinates.equals(topLeft))) {
                BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow() - 1));
                if (!square.getChildren().isEmpty()) {
                    Piece piece = (Piece) square.getChildren().removeFirst();
                    piece.otherPieceMoveWhenEaten = targetPiece.timesMoved;
                    targetPiece.eatenPieces.add(piece);
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
     * Handles a boolean value after a piece makes its first move
     */
    private void caseOfMove(BoardSquare destSquare, Piece targetPiece){
//        System.out.println("Moved: "+targetPiece.timesMoved);
        // Ensure the piece knows how many times they moved
        if(targetPiece.timesMoved == 0){
            // Used so the resets can be done
            // If the timesMoved is set back to zero, the piece should act like it hasn't move
            // Ex: pawn can only move twice on first move
            targetPiece.moved = false;
        } else {
            targetPiece.moved = true;
        }

        // if a pawn has made it's first turn, make sure it can't move twice again unless it's been reset
        if (targetPiece.type.equals("pawn")){
            if((targetPiece.color.equals("white") && destSquare.coordinates.getRow() == 4)
            || (targetPiece.color.equals("black") && destSquare.coordinates.getRow() == 3)) {
                targetPiece.twoStepped = true;
            } else if((targetPiece.color.equals("white") && destSquare.coordinates.getRow() == 5)
                    || (targetPiece.color.equals("black") && destSquare.coordinates.getRow() == 2)){
                targetPiece.twoStepped = false;
            }
        }

        // any cases for special king moves
        if(targetPiece.type.equals("king")){
            // cast it to king
            King king = (King) targetPiece;
            // if the king has moved after it castled, it will not undo castle until it has regressed to a certain move
            if(king.timesMoved>1 && king.castled){
                king.moveAfterCastle = true;
            } else {
                king.moveAfterCastle = false;
            }
        }

    }

    /**
     * Handles the promotion of the pawns once they reach the other side of the board
     */
    public void pawnPromotion(Piece targetPiece){
        // The current row of the pawn
        int currentRow = targetPiece.coordinates.getRow();
        // If the pawn is at the opposite side then promote it
        Pawn pawnToPromote = (Pawn) targetPiece;
        if(targetPiece.color.equals("white") && currentRow == 0 || targetPiece.color.equals("black") && currentRow == 7){
            // CASE OF PAWN PROMOTION

            // Get the destination
            BoardSquare currentSquare = getSquareAt(targetPiece.coordinates);
            // Promote the piece on the board
            pawnToPromote.promote(currentSquare, this);
            // set the promotion to true
            pawnToPromote.wasPromoted = true;
        } else if(pawnToPromote.wasPromoted == true) {
            // CASE OF UNDOING PAWN PROMOTION

            // Get the square the piece was promoted on
            BoardSquare endSquare = getSquareAt(pawnToPromote.piecePromotedTo.coordinates);
            // take the promoted piece off the square
            endSquare.getChildren().remove(pawnToPromote.piecePromotedTo);
            // signal that the pawn is not promoted
            pawnToPromote.wasPromoted = false;
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

    /**
     * Getter for the stack of moves made by pieces
     * @return a Stack of Move
     */
    public Stack<Move> getMoveStack(){
        return moveStack;
    }
}
