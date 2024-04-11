package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.Piece;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Controller {

    @FXML
    GridPane chessBoard = new GridPane();

    Game game;

    /**
     * Creates the chess board and adds functionality for the drag handling
     */
    public void initialize() throws IOException {
        this.game = new Game(chessBoard);

        // Add drag-and-drop event handlers to the chessboard GridPane
        chessBoard.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        chessBoard.setOnDragDropped(event -> {
            // Retrieve the dragged piece from the event
            Piece piece = (Piece) event.getGestureSource();

            // Get the target square from the event
            double x = event.getX();
            double y = event.getY();
            int col = (int) Math.floor(x / Board.SQUARE_SIZE);
            int row = (int) Math.floor(y / Board.SQUARE_SIZE);

            // Handles the action of dropping the piece into a BoardSquare object
            game.handleDragDropped(col, row);

            event.setDropCompleted(true);
            event.consume();
        });

    }

    private void changeBoardStyle(BoardStyle style){
        this.game.boardSettings.currentBoardStyle = style;
        game.chessBoard.rerenderBoard();
    }

    public void changeStyleToSandcastle(){
        changeBoardStyle(BoardSettings.SANDCASTLE);
    }

    public void changeStyleToCoral(){
        changeBoardStyle(BoardSettings.CORAL);
    }
    public void changeStyleToDusk(){
        changeBoardStyle(BoardSettings.DUSK);
    }
    public void changeStyleToWheat(){
        changeBoardStyle(BoardSettings.WHEAT);
    }
    public void changeStyleToMarine(){
        changeBoardStyle(BoardSettings.MARINE);
    }
    public void changeStyleToEmerald(){
        changeBoardStyle(BoardSettings.EMERALD);
    }

}

