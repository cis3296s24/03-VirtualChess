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
import java.util.HashMap;
import java.util.Stack;

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
        for (Piece piece : this.pieces) {
            piece.currentMoveSet = piece.getMoveSet();
        }
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

            //            if(targetPiece.type.equals("pawn")) {
//                Coordinates topLeft = new Coordinates(targetPiece.coordinates.getCol() - 1, targetPiece.coordinates.getRow() - 1);
//                Coordinates topRight = new Coordinates(targetPiece.coordinates.getCol() + 1, targetPiece.coordinates.getRow() - 1);
//                Coordinates bottomLeft = new Coordinates(targetPiece.coordinates.getCol() - 1, targetPiece.coordinates.getRow() + 1);
//                Coordinates bottomRight = new Coordinates(targetPiece.coordinates.getCol() + 1, targetPiece.coordinates.getRow() + 1);
//                if (targetPiece.color.equals("white") &&
//                        prevSquare.coordinates.getRow() == 3 &&
//                        (prevSquare.coordinates.equals(bottomRight) || prevSquare.coordinates.equals(bottomLeft))) {
//                    BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow() + 1));
//                    System.out.println(square.coordinates);
//                    if (!square.getChildren().isEmpty()) {
//                        square.getChildren().removeFirst();
//                    }
//                } else if (targetPiece.color.equals("black") &&
//                        prevSquare.coordinates.getRow() == 4 &&
//                        (prevSquare.coordinates.equals(topRight) || prevSquare.coordinates.equals(topLeft))) {
//                    BoardSquare square = getSquareAt(new Coordinates(targetPiece.coordinates.getCol(), targetPiece.coordinates.getRow() - 1));
//                    if (!square.getChildren().isEmpty()) {
//                        square.getChildren().removeFirst();
//                    }
//
//                }
//            }

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
     * @param destSquare the square for the piece to be set on
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

    private void pieceCheck(Piece targetPiece, BoardSquare destSquare, BoardSquare prevSquare){
        // handle any rules after first movement of piece
        if(targetPiece.type.equals("pawn") || targetPiece.type.equals("king") || targetPiece.type.equals("rook")){
            caseOfMove(destSquare, targetPiece);
        }

        // Pawn promotion
        if(targetPiece.type.equals("pawn")){
            pawnPromotion(targetPiece);
        }

        if(targetPiece.type.equals("king")){
            // cast the king piece
            King king = (King) targetPiece;

            // coordinates the king can castle at
            Coordinates whiteKingLeftCastle = new Coordinates(2,7);
            Coordinates whiteKingRightCastle = new Coordinates(6,7);
            Coordinates blackKingLeftCastle = new Coordinates(2,0);
            Coordinates blackKingRightCastle = new Coordinates(6,0);

            // only can castle when king is at these coordinates
            if(destSquare.coordinates.equals(whiteKingRightCastle)
                || destSquare.coordinates.equals(whiteKingLeftCastle)
                || destSquare.coordinates.equals(blackKingLeftCastle)
                || destSquare.coordinates.equals(blackKingRightCastle)){
                /* CASE OF CASTLING*/

                // only can do castle if the king has not castled
                if(!king.castled){
                    // find the right and left rooks
                    Coordinates rightRookCoord = new Coordinates(destSquare.coordinates.getCol()+1, destSquare.coordinates.getRow());
                    Coordinates leftRookCoord = new Coordinates(destSquare.coordinates.getCol()-2, destSquare.coordinates.getRow());
                    // store the rooks for later use
                    king.rightRook = (Rook) getPieceAt(rightRookCoord);
                    king.leftRook = (Rook) getPieceAt(leftRookCoord);
                    // as long as the rooks aren't null and haven't moved, they can castle
                    if(king.rightRook != null && !king.rightRook.moved && king.rightRook.type.equals("rook")){
                        // store the current rook coordinates and get the square it's on
                        king.rightRook.coordBeforeCastle = rightRookCoord;
                        BoardSquare oldSquare = getSquareAt(rightRookCoord);
                        // find the rook's new destination coords and square
                        Coordinates destinationCoord = new Coordinates(destSquare.coordinates.getCol()-1, destSquare.coordinates.getRow());
                        king.rightRook.coordinates = destinationCoord;
                        BoardSquare newSquare = getSquareAt(destinationCoord);
                        // remove it from the old square and put it on the new one
                        oldSquare.getChildren().remove(king.rightRook);
                        addPiece(newSquare,king.rightRook);
                        // let the king and the rook know it's been castled
                        king.castled = true;
                        king.rightRook.castled = true;
                    }
                    if(king.leftRook != null && !king.leftRook.moved && king.leftRook.type.equals("rook")){
                        // store the current rook coordinates and get the square it's on
                        king.leftRook.coordBeforeCastle = leftRookCoord;
                        BoardSquare oldSquare = getSquareAt(leftRookCoord);
                        // find the rook's new destination coords and square
                        Coordinates destinationCoord = new Coordinates(destSquare.coordinates.getCol()+1, destSquare.coordinates.getRow());
                        king.leftRook.coordinates = destinationCoord;
                        BoardSquare newSquare = getSquareAt(destinationCoord);
                        // remove it from the old square and put it on the new one
                        oldSquare.getChildren().remove(king.leftRook);
                        addPiece(newSquare,king.leftRook);
                        // let the king and the rook know it's been castled
                        king.castled = true;
                        king.leftRook.castled = true;
                    }
                }
            } else if(!king.moveAfterCastle) {
                /* CASE OF UNDOING CASTLING*/

                // make sure a rook has been castled
                if(king.rightRook != null && king.rightRook.castled){
                    // get the square the rook is currently on
                    BoardSquare currentSquare = new BoardSquare(king.rightRook.coordinates);
                    // get the old square it was before castle
                    BoardSquare oldSquare = getSquareAt(king.rightRook.coordBeforeCastle);
                    // remove the rook from the current square
                    currentSquare.getChildren().remove(king.rightRook);
                    // add it back to the old one
                    addPiece(oldSquare,king.rightRook);
                    // set the rooks coord again
                    king.rightRook.coordinates = king.rightRook.coordBeforeCastle;
                    // signal that the castle was reset
                    king.castled = false;
                    king.rightRook.castled = false;
                } else if(king.leftRook != null && king.leftRook.castled){
                    // get the square the rook is currently on
                    BoardSquare currentSquare = new BoardSquare(king.leftRook.coordinates);
                    // get the old square it was before castle
                    BoardSquare oldSquare = getSquareAt(king.leftRook.coordBeforeCastle);
                    // remove the rook from the current square
                    currentSquare.getChildren().remove(king.leftRook);
                    // add it back to the old one
                    addPiece(oldSquare,king.leftRook);
                    // set the rooks coord again
                    king.leftRook.coordinates = king.leftRook.coordBeforeCastle;
                    // signal that the castle was reset
                    king.castled = false;
                    king.leftRook.castled = false;
                }
            }
        }

        if(targetPiece.type.equals("pawn")) {
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

//    public void movingIntoCheck(){
//        // Get all pieces
//        for(Piece piece: pieces){
//            // Search for king pieces only
//            if(piece.type.equals("king") && piece.color.equals("white")) {
//                // cast piece to king
//                King whiteKing = (King) piece;
//                // Other piece = all pieces
//                for (Piece otherPiece : pieces) {
//                    // Other piece is not white king (all other pieces)
//                    if (!otherPiece.equals(whiteKing)) {
//                        otherPiece.currentMoveSet = otherPiece.getMoveSet();
//                        for (Coordinates wKingCoords : whiteKing.currentMoveSet) {
//                            for (Coordinates opCoords : otherPiece.currentMoveSet) {
//                                if (wKingCoords.equals(opCoords)) {
//                                    if (otherPiece.color.equals("black")) {
//                                        System.out.println(whiteKing.color + " " + whiteKing.type + " path blocked by " + otherPiece.color + " " + otherPiece.type + " at " + opCoords);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } else if (piece.color.equals("black") && piece.type.equals("king")) {
//                // Cast the king piece
//                King blackKing = (King) piece;
//                // get the current move set of black king
//                blackKing.currentMoveSet = blackKing.getMoveSet();
//                // look through the pieces again
//                for(Piece otherPiece: pieces){
//                    // ensure we are not looking at the same king we just casted
//                    if(!otherPiece.equals(blackKing)){
//                        // get the move set of the other piece
//                        otherPiece.currentMoveSet = otherPiece.getMoveSet();
//                        // go through each move in the king's move set
//                        for(Coordinates bKingCoords: blackKing.currentMoveSet){
//                            // go through each move in the other piece's move set
//                            for(Coordinates opCoords: otherPiece.currentMoveSet){
//                                // if they're equal then "insert code" (king's path is blocked)
//                                if(bKingCoords.equals(opCoords)){
//                                    if(otherPiece.color.equals("white")){
//                                        System.out.println(blackKing.color+" "+blackKing.type+" path blocked by "+otherPiece.color+" "+otherPiece.type+" at "+opCoords);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
