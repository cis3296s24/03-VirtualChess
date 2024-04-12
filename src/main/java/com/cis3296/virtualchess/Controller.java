package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Pieces.Piece;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class Controller {

    @FXML
    GridPane chessBoard = new GridPane();

    @FXML
    private Text timerTextWhite;
    @FXML
    private Text timerTextBlack;

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

        // Add drag-and-drop event handlers to the chessboard GridPane
        chessBoard.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        chessBoard.setOnDragDropped(event -> {
            event.setDropCompleted(true);
            event.consume();
        });

    }

    public void updateTime(){
        int minutesW = game.chessBoard.turnSystem.whiteTimer.getRemainingTimeMinutes();
        int secondsW = game.chessBoard.turnSystem.whiteTimer.getRemainingTimeSeconds();

        timerTextWhite.setText(String.format("White Time: %02d:%02d", minutesW, secondsW));

        int minutesB = game.chessBoard.turnSystem.blackTimer.getRemainingTimeMinutes();
        int secondsB = game.chessBoard.turnSystem.blackTimer.getRemainingTimeSeconds();

        timerTextBlack.setText(String.format("Black Time: %02d:%02d", minutesB, secondsB));
    }

    public void changeTurnButton(){
        game.chessBoard.turnSystem.changeTurn();
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

