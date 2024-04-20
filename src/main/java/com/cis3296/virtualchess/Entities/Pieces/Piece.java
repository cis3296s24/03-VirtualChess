package com.cis3296.virtualchess.Entities.Pieces;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Game;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Piece extends ImageView {

    public Coordinates coordinates;
    public String color;
    public String type;
    public Board board;
    public boolean isTurn;
    public boolean moved;
    public boolean twoStepped;

    /**
     * Constructor for a standard Piece
     * @param coordinates are the coordinates of the Piece on the board
     * @param color chooses the color of the piece
     */
    public Piece(Coordinates coordinates, String color, Board board, boolean isTurn) {
        this.coordinates = coordinates;
        this.color = color;
        this.board = board;
        this.isTurn = isTurn;
        moved = false;
        setDragHandlers();
    }

    /**
     * This method allows the Piece objects to be dragged
     */
    private void setDragHandlers() {
        // Set event handlers for drag-and-drop operations
        setOnDragDetected(event -> {
            startFullDrag();
            event.consume();
        });


        setOnMouseDragged(Event::consume);
    }

    public void setImage() {
        this.setPiece(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + this.color + "_" + this.type + ".png")), 90, 90, true, true));
    }
    public void setPiece(Image image) {
        this.setImage(image);
    }

    /**
     * Determines if a piece can move to a desired space or not
     * No additional code is needed here as of now
     * Each child class will overwrite this method to distinguish different piece movement
     * @param targetCol is the targeted column for movement
     * @param targetRow is the targeted row for movement
     * @return false by default, preventing erratic movement
     */
    public boolean canMove(int targetCol, int targetRow){
        Coordinates targetCoordinates = new Coordinates(targetCol, targetRow);
        for(Coordinates coordinates : getMoveSet()){
            if(coordinates.equals(targetCoordinates)){
                return true;
            }
        }
        return false;
    }

    public void showMoves(Board board){
        for(Coordinates coordinates : getMoveSet()){
            board.showMoves(coordinates);
        }
    }
    public abstract ArrayList<Coordinates> getMoveSet();

    /**
     * This method confirms adds the coordinates to the piece move set if the moves
     * do not conflict with other pieces
     * @param moveSet the ObservableList the coordinates get added to
     * @param targetCoordinates the coordinates of the move being made
     */
    public void addCoordinates(ArrayList<Coordinates> moveSet, Coordinates targetCoordinates){
        // Go through all board squares and find the square the piece is supposed to move to
        for(BoardSquare destinationSquare: board.boardSquares){
            if(destinationSquare.coordinates.equals(targetCoordinates)){
                // Check to see if the square has a piece on it
                if(board.pieceToSquare.containsKey(destinationSquare)){
                    // Check to see if the piece is not of the same color
                    Piece opponentPiece = board.pieceToSquare.get(destinationSquare);
                    String opponentColor = opponentPiece.color;
                    if(!this.color.equals(opponentColor)){
                        // Add the coordinate as a possible move
                        moveSet.add(targetCoordinates);
                    }
                } else {
                    // Add the coordinate as a possible move
                    moveSet.add(targetCoordinates);
                }
            }
        }
    }
}