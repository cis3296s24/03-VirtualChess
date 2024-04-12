package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Board.BoardSettings;
import com.cis3296.virtualchess.Board.BoardStyle;
import com.cis3296.virtualchess.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
        currentTurnText.setText("Current Turn:\n" + game.chessBoard.turnSystem.getCurrentPlayer().name);

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
        int minutesW = game.chessBoard.turnSystem.whiteTimer.getRemainingTimeMinutes();
        int secondsW = game.chessBoard.turnSystem.whiteTimer.getRemainingTimeSeconds();

        timerTextWhite.setText(String.format("White Time: %02d:%02d", minutesW, secondsW));

        int minutesB = game.chessBoard.turnSystem.blackTimer.getRemainingTimeMinutes();
        int secondsB = game.chessBoard.turnSystem.blackTimer.getRemainingTimeSeconds();

        timerTextBlack.setText(String.format("Black Time: %02d:%02d", minutesB, secondsB));
    }

    public void changeTurnButton(){
        game.chessBoard.turnSystem.changeTurn();
        currentTurnText.setText("Current Turn:\n" + game.chessBoard.turnSystem.getCurrentPlayer().name);
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
