package com.cis3296.virtualchess.Components;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Full-screen mode for the app window: F11 toggles it, Esc leaves it, and the choice is saved so the app
 * reopens the way it was left. Settings has a checkbox for it too.
 */
public final class FullScreen {

    private static final Logger log = LoggerFactory.getLogger(FullScreen.class);

    private FullScreen() {
    }

    /**
     * Adds the keyboard shortcuts to the window and restores the saved full-screen choice. Call once, for the
     * main window.
     */
    public static void install(Stage stage, Scene scene) {
        // JavaFX's own exit key is Esc, but Esc also cancels the promotion picker, which would throw the player
        // out of full screen. Handle Esc below instead, only when nothing on screen used it.
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint("Press F11 or Esc to leave full screen");

        // A handler (not a filter) runs after the focused control, so a control that uses Esc consumes it first
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && stage.isFullScreen()) {
                stage.setFullScreen(false);
                event.consume();
            }
        });

        stage.fullScreenProperty().addListener((observable, was, isFullScreen) -> {
            log.debug("Full screen {}", isFullScreen ? "on" : "off");
            Settings.setConfig(Settings.FULLSCREEN_CONFIG_ACCESS_STRING, String.valueOf(isFullScreen));
        });

        if (isSaved()) {
            stage.setFullScreen(true);
        }
    }

    /**
     * @return true if the player last left the app in full screen
     */
    public static boolean isSaved() {
        return Boolean.parseBoolean(Settings.getConfig(Settings.FULLSCREEN_CONFIG_ACCESS_STRING));
    }
}
