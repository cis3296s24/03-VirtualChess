package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Screens;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * UI Controller for the main menu screen
 */
public class MainMenuController {

    private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
    private static final String LOGO = "/assets/menuImages/VirtualChess!.gif";

    @FXML
    ImageView imageView;

    // The screen's root layout; the CSS gives it the background gradient
    @FXML
    Pane menu;

    /**
     * Loads the animated logo. A missing logo is logged rather than stopping the menu from opening.
     */
    public void initialize() {
        URL logo = getClass().getResource(LOGO);
        if (logo == null) {
            log.warn("Main menu logo {} is missing from the build", LOGO);
            return;
        }
        imageView.setImage(new Image(logo.toExternalForm()));
    }

    /**
     * Button action for quitting the app. Goes through JavaFX's normal shutdown, so ChessApp.stop() still
     * closes the database.
     */
    @FXML
    private void quit() {
        log.info("Quit pressed");
        Platform.exit();
    }

    @FXML
    public void switchToGameMode(ActionEvent event) {
        Screens.show(event, Screens.GAME_MODE);
    }

    @FXML
    public void switchToLeaderboard(ActionEvent event) {
        Screens.show(event, Screens.LEADERBOARD);
    }

    @FXML
    public void switchToSettingsMenu(ActionEvent event) {
        Screens.show(event, Screens.SETTINGS);
    }

    @FXML
    public void switchToTutorial(ActionEvent event) {
        Screens.show(event, Screens.LEARN_CHESS);
    }
}
