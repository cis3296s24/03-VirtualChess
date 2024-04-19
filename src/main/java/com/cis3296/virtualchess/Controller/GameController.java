package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Game;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class GameController {

    public VBox board;
    @FXML
    GridPane chessBoard = new GridPane();

    @FXML
    private Text timerTextWhite;
    @FXML
    private Text timerTextBlack;
    @FXML
    private Text currentTurnText;

    private Timeline timeline;

    private TurnSystem turnSystem;
    Game game;

    /**
     * Creates the chess board and adds functionality for the drag handling
     */
    public void initialize(){
        this.game = new Game(chessBoard);
        this.turnSystem = TurnSystem.getInstance();
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
        currentTurnText.setText("Current Turn:\n" + turnSystem.getCurrentPlayer().name);
        turnSystem.setCurrentPlayerText(currentTurnText);

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
        turnSystem.getWhiteTime(timerTextWhite);

        turnSystem.getBlackTime(timerTextBlack);
    }

    public void leaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        game.endGame();

    }

    public void settings(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cis3296/virtualchess/settingsMenu.fxml"));
            Parent settingsContent = loader.load();

            Stage settingsPopup = new Stage();
            Scene settingsScene = new Scene(settingsContent);
            settingsPopup.setScene(settingsScene);

            settingsPopup.setTitle("Settings");

            settingsPopup.initModality(Modality.APPLICATION_MODAL);


            SettingsMenuController controller = loader.getController();
            controller.backButton.setOnAction(event -> {
                BoardSettings.setConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING, controller.ThemeDropDown.getValue().toString());

                game.getTheme();
                game.chessBoard.rerenderBoard();

                settingsPopup.close();
            });

            settingsPopup.setOnCloseRequest(event -> {
                Platform.runLater(settingsPopup::close);
            });

            settingsPopup.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

