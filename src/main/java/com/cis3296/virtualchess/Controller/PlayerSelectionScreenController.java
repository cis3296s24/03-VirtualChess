package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Difficulty;
import com.cis3296.virtualchess.GameSession;
import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Components.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UI Controller for the player selection screens (one for two players, one for playing the computer).
 * The computer version has no Player 2 field, so {@link #player2name} is null there.
 */
public class PlayerSelectionScreenController {

    private static final Logger log = LoggerFactory.getLogger(PlayerSelectionScreenController.class);

    @FXML
    public Pane menu;

    /**
     * Called by the FXML loader once the screen's controls exist.
     */
    public void initialize() {
        if (difficulty != null) {
            difficulty.getItems().setAll(Difficulty.values());
            difficulty.setValue(Difficulty.MEDIUM);
        }
    }

    @FXML
    public TextField player1name;
    @FXML
    public TextField player2name;
    @FXML
    public TextField timer;
    @FXML
    private Label errorLabel;
    // Only on the computer version of the screen
    @FXML
    private ComboBox<Difficulty> difficulty;

    private boolean vsComputer;

    /**
     * Set by whoever opens this screen: which of the two setup screens this is.
     */
    public void setVsComputer(boolean vsComputer) {
        this.vsComputer = vsComputer;
    }

    /**
     * Button action for starting the game: checks the form, bundles the players and clock time into a
     * {@link GameSession}, and switches to the game screen. If something's missing or invalid, says what instead.
     * @param event {@link ActionEvent} for the click
     */
    @FXML
    public void switchToGame(ActionEvent event) {
        String whiteName = player1name.getText().strip();
        String blackName = vsComputer ? "Computer" : player2name.getText().strip();
        if (whiteName.isEmpty() || blackName.isEmpty()) {
            showError(vsComputer ? "Please enter your name." : "Please enter a name for both players.");
            return;
        }

        int minutes;
        String time = timer.getText().strip();
        if (time.isEmpty()) {
            minutes = GameSession.DEFAULT_MINUTES;
        } else {
            try {
                minutes = Integer.parseInt(time);
            } catch (NumberFormatException e) {
                minutes = -1;
            }
            if (minutes < 1 || minutes > GameSession.MAX_MINUTES) {
                showError("Start time must be a whole number of minutes from 1 to " + GameSession.MAX_MINUTES + ".");
                return;
            }
        }

        GameSession session = new GameSession(new Player(whiteName), new Player(blackName), vsComputer, minutes,
                difficulty == null ? Difficulty.MEDIUM : difficulty.getValue());
        log.info("Starting {} game: {} (White) vs {} (Black), {} min each",
                vsComputer ? "player vs computer" : "two-player", whiteName, blackName, minutes);
        Screens.<GameController>show(event, Screens.GAME, game -> game.start(session));
    }

    /**
     * Button action for switching to the game mode selection screen
     * @param event {@link ActionEvent} for the click
     */
    @FXML
    public void switchToGamemode(ActionEvent event) {
        Screens.show(event, Screens.GAME_MODE);
    }

    private void showError(String message) {
        log.debug("Game setup form rejected: {}", message);
        errorLabel.setText(message);
        errorLabel.setManaged(true); // hidden labels take no space until there's an error to show
        errorLabel.setVisible(true);
    }
}
