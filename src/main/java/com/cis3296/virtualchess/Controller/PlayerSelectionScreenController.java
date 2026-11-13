package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * UI Controller for the player selection screens (one for two players, one for playing the computer).
 * The computer version has no Player 2 field, so {@link #player2name} is null there.
 */
public class PlayerSelectionScreenController {

    private static final int MAX_MINUTES = 180;

    @FXML
    public Pane menu;
    @FXML
    public TextField player1name;
    @FXML
    public TextField player2name;
    @FXML
    public TextField timer;
    @FXML
    private Label errorLabel;

    /**
     * Button action for starting the game: checks the form, puts the players and clock time into the turn
     * system, and switches to the game screen. If something's missing or invalid, says what instead.
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToGame(ActionEvent event) throws IOException {
        boolean vsComputer = Boolean.parseBoolean(Settings.getConfig(Settings.AI_CONFIG_ACCESS_STRING));

        String whiteName = player1name.getText().strip();
        String blackName = vsComputer ? "Computer" : player2name.getText().strip();
        if (whiteName.isEmpty() || blackName.isEmpty()) {
            showError(vsComputer ? "Please enter your name." : "Please enter a name for both players.");
            return;
        }

        int minutes;
        String time = timer.getText().strip();
        if (time.isEmpty()) {
            minutes = TurnSystem.DEFAULT_TIMER_AMOUNT;
        } else {
            try {
                minutes = Integer.parseInt(time);
            } catch (NumberFormatException e) {
                minutes = -1;
            }
            if (minutes < 1 || minutes > MAX_MINUTES) {
                showError("Start time must be a whole number of minutes from 1 to " + MAX_MINUTES + ".");
                return;
            }
        }

        TurnSystem ts = TurnSystem.getInstance();
        ts.setWhitePlayer(new Player(whiteName));
        ts.setBlackPlayer(new Player(blackName));
        ts.setWhiteTimer(minutes);
        ts.setBlackTimer(minutes);
        switchTo(event, "board.fxml");
    }

    /**
     * Button action for switching to the game mode selection screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToGamemode(ActionEvent event) throws IOException {
        switchTo(event, "gamemode.fxml");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true); // hidden labels take no space until there's an error to show
        errorLabel.setVisible(true);
    }

    private void switchTo(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/" + fxml)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
