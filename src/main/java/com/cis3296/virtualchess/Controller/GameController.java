package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class GameController {

    @FXML
    GridPane chessBoard = new GridPane();

    @FXML
    private Text timerTextWhite;
    @FXML
    private Text timerTextBlack;
    @FXML
    private Text currentTurnText;

    private Timeline timeline;

    Game game;

    /**
     * Creates the chess board and adds functionality for the drag handling
     */
    public void initialize(){
        this.game = new Game(chessBoard);
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
        currentTurnText.setText("Current Turn:\n" + game.turnSystem.getCurrentPlayer().name);

        // Add drag-and-drop event handlers to the chessboard GridPane
        chessBoard.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        chessBoard.setOnDragDropped(event -> {
            game.chessBoard.removeShownMoves();
            event.setDropCompleted(true);
            event.consume();
        });

    }

    public void updateTime(){
        int minutesW = game.turnSystem.whiteTimer.getRemainingTimeMinutes();
        int secondsW = game.turnSystem.whiteTimer.getRemainingTimeSeconds();

        timerTextWhite.setText(String.format("White Time: %02d:%02d", minutesW, secondsW));

        int minutesB = game.turnSystem.blackTimer.getRemainingTimeMinutes();
        int secondsB = game.turnSystem.blackTimer.getRemainingTimeSeconds();

        timerTextBlack.setText(String.format("Black Time: %02d:%02d", minutesB, secondsB));
    }

    public void changeTurnButton(){
        game.turnSystem.changeTurn();
        currentTurnText.setText("Current Turn:\n" + game.turnSystem.getCurrentPlayer().name);
    }

    public void leaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void settings(){

    }

}

