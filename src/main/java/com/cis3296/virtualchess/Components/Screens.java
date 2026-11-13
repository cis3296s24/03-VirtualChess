package com.cis3296.virtualchess.Components;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Switches the window between the app's screens (each one an FXML file). Every controller goes through here,
 * so screen changes are logged in one place and a screen that fails to load shows an error instead of
 * leaving the window half-switched.
 */
public final class Screens {

    private static final Logger log = LoggerFactory.getLogger(Screens.class);
    private static final String FXML_FOLDER = "/com/cis3296/virtualchess/";

    public static final String MAIN_MENU = "mainmenu.fxml";
    public static final String GAME_MODE = "gamemode.fxml";
    public static final String PLAYER_SELECTION_PVP = "playerSelectionScreenPVP.fxml";
    public static final String PLAYER_SELECTION_PVC = "playerSelectionScreenPVC.fxml";
    public static final String GAME = "board.fxml";
    public static final String LEADERBOARD = "leaderboard.fxml";
    public static final String SETTINGS = "settingsMenu.fxml";
    public static final String LEARN_CHESS = "Tutorial Screen.fxml";
    public static final String PIECES = "Pieces.fxml";
    public static final String BASIC_CONCEPTS = "BasicMove.fxml";
    public static final String CHECK = "Check.fxml";
    public static final String SPECIAL_MOVES = "SpecialMove.fxml";

    private Screens() {
    }

    /**
     * A loaded screen: its root node and its controller.
     */
    public record Loaded<C>(Parent root, C controller) {
    }

    /**
     * Shows a screen in the window that a button (or other control) belongs to.
     *
     * @param event the event from the control, e.g. a button's ActionEvent
     * @param fxml  one of the screen constants, e.g. {@link #MAIN_MENU}
     * @return true if the screen is now showing; false if it failed to load (already logged and reported)
     */
    public static boolean show(Event event, String fxml) {
        return show((Stage) ((Node) event.getSource()).getScene().getWindow(), fxml);
    }

    /**
     * Shows a screen in a window.
     *
     * @return true if the screen is now showing; false if it failed to load (already logged and reported,
     * and the window keeps showing the previous screen)
     */
    public static boolean show(Stage stage, String fxml) {
        try {
            stage.setScene(scene(load(fxml).root()));
            stage.show();
            log.debug("Showing screen {}", fxml);
            return true;
        } catch (IOException | RuntimeException e) {
            log.error("Couldn't open screen {}", fxml, e);
            Dialogs.error(stage, "Couldn't open that screen",
                    "Something went wrong while loading it, so you're still on the previous screen. "
                            + "The details are in the log.");
            return false;
        }
    }

    /**
     * Loads a screen without showing it, e.g. to put it in a pop-up window or to reach its controller.
     *
     * @throws IOException if the file is missing or fails to load (including errors in the controller's
     *                     initialize method, which FXMLLoader wraps in an IOException)
     */
    public static <C> Loaded<C> load(String fxml) throws IOException {
        URL url = Screens.class.getResource(FXML_FOLDER + fxml);
        if (url == null) {
            throw new IOException("Screen file not found: " + FXML_FOLDER + fxml);
        }
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        return new Loaded<>(root, loader.getController());
    }

    /**
     * @return a scene for this root with the app stylesheet applied
     */
    public static Scene scene(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Dialogs.stylesheet());
        return scene;
    }
}
