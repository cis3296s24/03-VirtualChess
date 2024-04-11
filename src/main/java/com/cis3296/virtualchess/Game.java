package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.Piece;
import javafx.scene.layout.GridPane;


public class Game {

    // Not in use for now
    public Board chessBoard;
    public BoardSettings boardSettings = new BoardSettings(BoardSettings.SANDCASTLE);

    /**
     * Constructor for the game
     * @param chessBoard
     */
    public Game(GridPane chessBoard) {
        this.chessBoard = new Board(chessBoard, boardSettings);
    }

    /**
     * Handles the dropping of a piece onto the chessboard
     * @param col The column where the piece is dropped
     * @param row The row where the piece is dropped
     */
    public void handleDragDropped(int col, int row) {
        // Get the piece that is being dragged
        Piece draggingPiece = chessBoard.getDraggingPiece();
        // If we are not dragging an empty piece:
        if (draggingPiece != null) {
            // Check if the move is valid
            if (chessBoard.isValidMove(draggingPiece, col, row)) {
                // Move the piece to the new position on the board
                chessBoard.movePiece(draggingPiece, col, row);
            } else {
                // Handle invalid move
                System.out.println("Invalid move!");
            }
        }
    }


}
