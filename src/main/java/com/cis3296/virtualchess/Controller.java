package com.cis3296.virtualchess;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Controller {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    GridPane chessBoard;

    /**
     * Creates the chess board
     */
    public void initialize(){
        Game game = new Game(chessBoard);
    }
}