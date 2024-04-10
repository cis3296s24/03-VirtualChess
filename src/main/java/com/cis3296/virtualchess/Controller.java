package com.cis3296.virtualchess;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Controller {

    @FXML
    GridPane chessBoard = new GridPane();

    /**
     * Creates the chess board
     */
    public void initialize() throws IOException {
        Game game = new Game(chessBoard);

        Board gameBoard = game.getChessBoard();
        gameBoard.mouseClick();
    }

}

