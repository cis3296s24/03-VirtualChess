package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Coordinates;
import com.cis3296.virtualchess.Entities.Move;
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
import javafx.scene.control.Button;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * Controller for Game UI
 */
public class GameController {

    public VBox board;

    @FXML
    public Button undo;
    @FXML
    GridPane chessBoard = new GridPane();

    @FXML
    private Text timerTextWhite;
    @FXML
    private Text timerTextBlack;
    @FXML
    private Text currentTurnText;

    private TurnSystem turnSystem;

    // Game that is to be created when this UI starts
    Game game;

    /**
     * Creates the chess board and adds functionality for the drag handling
     */
    public void initialize(){
        this.game = new Game(chessBoard);
        this.turnSystem = TurnSystem.getInstance();
        // "Refresh Rate" of the timers
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
        currentTurnText.setText("Current Turn:\n" + turnSystem.getCurrentPlayer().name);
        turnSystem.setCurrentPlayerText(currentTurnText);

        undo.setVisible(Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING)));

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

    /**
     * Update the timers on the screen each 0.1 seconds(Defined in the timeline)
     */
    public void updateTime(){
        turnSystem.getWhiteTime(timerTextWhite);

        turnSystem.getBlackTime(timerTextBlack);
    }

    /**
     * Button action for leaving the game and going to the main screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Throws if it cannot load the fxml file
     */
    public void leaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        game.endGame();

    }

    /**
     * Button action for displaying the settings
     */
    public void settings(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cis3296/virtualchess/settingsMenu.fxml"));
            Parent settingsContent = loader.load();

            Stage settingsPopup = new Stage();
            Scene settingsScene = new Scene(settingsContent);

            settingsScene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());

            settingsPopup.setScene(settingsScene);

            settingsPopup.setTitle("Settings");

            settingsPopup.initModality(Modality.APPLICATION_MODAL);


            SettingsMenuController controller = loader.getController();
            controller.backButton.setOnAction(event -> {
                Settings.setConfig(Settings.THEME_CONFIG_ACCESS_STRING, controller.ThemeDropDown.getValue().toString());

                game.getTheme();
                game.chessBoard.rerenderBoard();

                settingsPopup.close();
                undo.setVisible(Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING)));
            });

            settingsPopup.setOnCloseRequest(event -> {
                Platform.runLater(settingsPopup::close);
                undo.setVisible(Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING)));
            });

            settingsPopup.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Connected to a button on the board FXML file, this method calls a Board method to undo piece movement
     */
    public void undoMoveButton(){
        if(Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING))){
            undoMove();
            if(Boolean.parseBoolean(Settings.getConfig(Settings.AI_CONFIG_ACCESS_STRING))){
                undoMove();
            }
        }
    }

    private void undoMove(){
        // get the board
        Board chessBoard = game.chessBoard;
        // get the previous move
        if(!chessBoard.getMoveStack().empty()){
            Move previousMove = chessBoard.getMoveStack().pop();
            // get the last coordinates
            Coordinates previousCoordinates = previousMove.getPreviousCoordinates();
            // get the square at the coordinates
            BoardSquare previousSquare = chessBoard.getSquareAt(previousCoordinates);
            // signal that the piece is not an eaten piece
            boolean isEatenPiece = false;
            // set the piece back to the previous square
            chessBoard.undoPieceMove(previousSquare, previousMove.getPiece(), isEatenPiece);
        }
    }
}

