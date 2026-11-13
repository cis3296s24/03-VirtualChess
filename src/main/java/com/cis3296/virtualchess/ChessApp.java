package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Components.Dialogs;
import com.cis3296.virtualchess.Components.Screens;
import com.cis3296.virtualchess.Systems.AppData;
import com.cis3296.virtualchess.Systems.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JavaFX application: opens the main menu, and handles startup, shutdown, and unexpected errors.
 */
public class ChessApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(ChessApp.class);

    /**
     * Opens the database and shows the main menu. If the main menu can't be shown, tells the player and quits
     * rather than leaving the app running with no window.
     * @param stage {@link Stage} where everything will be drawn
     */
    @Override
    public void start(Stage stage) {
        installErrorHandler();
        log.info("VirtualChess starting (Java {}, JavaFX {})",
                System.getProperty("java.version"), System.getProperty("javafx.runtime.version"));
        try {
            log.info("Data folder: {}", AppData.directory());
        } catch (RuntimeException e) {
            log.warn("Couldn't create the data folder; settings and results won't be saved", e);
        }
        Database.getInstance();

        stage.setTitle("Virtual Chess");
        try {
            stage.setScene(Screens.scene(Screens.load(Screens.MAIN_MENU).root()));
            stage.show();
        } catch (Exception e) {
            log.error("Couldn't load the main menu, so the app can't start", e);
            Dialogs.errorAndWait(null, "Virtual Chess couldn't start",
                    "The main menu failed to load. The details are in the log.");
            Platform.exit();
        }
    }

    /**
     * Closes the database when the app closes.
     * Any other logic that involves the user closing the window can go here
     */
    @Override
    public void stop() {
        log.info("VirtualChess shutting down");
        Database.closeDatabase();
    }

    /**
     * Any exception that nothing else caught (for example, from a button handler) is logged with its stack
     * trace and reported to the player, instead of silently leaving the app in a half-finished state.
     */
    private static void installErrorHandler() {
        Thread.UncaughtExceptionHandler handler = (thread, error) -> {
            log.error("Unexpected error on thread \"{}\"", thread.getName(), error);
            Runnable report = () -> Dialogs.error(null, "Something went wrong",
                    "An unexpected error occurred, so the last action may not have finished. "
                            + "The details are in the log.");
            if (Platform.isFxApplicationThread()) {
                report.run();
            } else {
                Platform.runLater(report);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(handler);
        // The JavaFX thread gets its own handler too, since exceptions from event handlers are reported there
        Thread.currentThread().setUncaughtExceptionHandler(handler);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
