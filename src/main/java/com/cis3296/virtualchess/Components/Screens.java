package com.cis3296.virtualchess.Components;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Switches the window between the app's screens (each one an FXML file). Every controller goes through here,
 * so screen changes are logged in one place and a screen that fails to load shows an error instead of
 * leaving the window half-switched.
 * <p>
 * The window keeps one scene for its whole life and only its contents change, so switching screens never
 * changes the window's size or takes it out of full screen. Each screen sits in a {@link ScreenFrame}, which
 * fits it to whatever size the window is.
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

    // The window opens at this size, or smaller to fit the display; game screen = 300px side panel + board
    private static final double INITIAL_WIDTH = 1180;
    private static final double INITIAL_HEIGHT = 860;
    // Smallest window that still fits the game screen's side panel next to a usable board
    private static final double MIN_WIDTH = 760;
    private static final double MIN_HEIGHT = 640;
    // Leave some of the display uncovered when it's too small for the initial size
    private static final double MAX_SHARE_OF_DISPLAY = 0.92;

    private static Stage mainStage;

    private Screens() {
    }

    /**
     * Sets up the app's window (size, full screen) and shows the main menu in it. Called once, at startup.
     *
     * @throws IOException if the main menu fails to load
     */
    public static void openMainWindow(Stage stage) throws IOException {
        Parent mainMenu = load(MAIN_MENU).root();
        Rectangle2D display = Screen.getPrimary().getVisualBounds();
        double width = Math.min(INITIAL_WIDTH, display.getWidth() * MAX_SHARE_OF_DISPLAY);
        double height = Math.min(INITIAL_HEIGHT, display.getHeight() * MAX_SHARE_OF_DISPLAY);

        Scene scene = new Scene(frame(MAIN_MENU, mainMenu), width, height);
        scene.getStylesheets().add(Dialogs.stylesheet());
        stage.setMinWidth(Math.min(MIN_WIDTH, display.getWidth()));
        stage.setMinHeight(Math.min(MIN_HEIGHT, display.getHeight()));
        stage.setScene(scene);
        FullScreen.install(stage, scene);
        mainStage = stage;
        stage.show();
    }

    /**
     * @return the app's main window, or null before {@link #openMainWindow} (e.g. in tests)
     */
    public static Stage mainStage() {
        return mainStage;
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
     * Shows a screen after giving its controller what it needs (e.g. the game screen's {@link
     * com.cis3296.virtualchess.GameSession}). The setup runs before the screen is shown.
     *
     * @return true if the screen is now showing; false if it failed to load or set up
     */
    public static <C> boolean show(Event event, String fxml, Consumer<C> setup) {
        return show((Stage) ((Node) event.getSource()).getScene().getWindow(), fxml, setup);
    }

    /**
     * Like {@link #show(Event, String, Consumer)}, for when there's no button event to find the window from
     * (e.g. a choice made in a dialog).
     */
    public static <C> boolean show(Stage stage, String fxml, Consumer<C> setup) {
        try {
            Loaded<C> loaded = load(fxml);
            setup.accept(loaded.controller());
            display(stage, fxml, loaded.root());
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
     * Shows a screen in a window.
     *
     * @return true if the screen is now showing; false if it failed to load (already logged and reported,
     * and the window keeps showing the previous screen)
     */
    public static boolean show(Stage stage, String fxml) {
        try {
            display(stage, fxml, load(fxml).root());
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

    // Puts the screen in the window, keeping the window's scene (and so its size and full-screen state)
    private static void display(Stage stage, String fxml, Parent root) {
        ScreenFrame frame = frame(fxml, root);
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(frame);
            scene.getStylesheets().add(Dialogs.stylesheet());
            stage.setScene(scene);
        } else {
            scene.setRoot(frame);
        }
        stage.show();
        log.debug("Showing screen {}", fxml);
    }

    // The game screen lays itself out at any size; the menus were designed at one size and are scaled
    private static ScreenFrame frame(String fxml, Parent root) {
        return new ScreenFrame(root, fxml.equals(GAME));
    }

    /**
     * A scene for a pop-up window (e.g. Settings during a game), with the app stylesheet and background. The
     * screen fills the pop-up rather than being scaled.
     */
    public static Scene scene(Parent root) {
        Scene scene = new Scene(new ScreenFrame(root, true));
        scene.getStylesheets().add(Dialogs.stylesheet());
        return scene;
    }
}
