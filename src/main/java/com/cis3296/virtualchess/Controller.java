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

    Game game;

    /**
     * Creates the chess board
     */
    public void initialize() throws IOException {
        this.game = new Game(chessBoard);
    }

    public void changeStyleToSandcastle(){
        this.game.boardSettings.currentBoardStyle = BoardSettings.SANDCASTLE;
        game.chessBoard.rerenderBoard();
    }

    public void changeStyleToDusk(){
        this.game.boardSettings.currentBoardStyle = BoardSettings.DUSK;
        game.chessBoard.rerenderBoard();
    }

}

