package com.cis3296.virtualchess.Components;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Pop-up dialogs in the app's style: colors from the chosen board theme, styled by the "game-dialog" rules in
 * menuStyle.css. Also the error dialog shown when something goes wrong.
 */
public final class Dialogs {

    private static final Logger log = LoggerFactory.getLogger(Dialogs.class);
    private static final String STYLESHEET = "/com/cis3296/virtualchess/menuStyle.css";

    // Only one error dialog at a time, so a failure that repeats can't pile up pop-ups
    private static Alert openError;

    private Dialogs() {
    }

    /**
     * Styles a dialog to match the board theme and centers it over the owner window.
     *
     * @param dialog the dialog to style (call before showing it)
     * @param owner  the window it belongs to, or null if there isn't one yet
     * @return the same dialog, for chaining
     */
    public static <D extends Dialog<?>> D themed(D dialog, Window owner) {
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(stylesheet());
        pane.getStyleClass().add("game-dialog");
        pane.setStyle(Settings.currentStyle().cssVariables());
        pane.setMinWidth(420);
        dialog.setGraphic(null); // the stock info/warning icons don't fit the theme
        if (owner != null) {
            dialog.initOwner(owner);
        }
        return dialog;
    }

    /**
     * Tells the player something went wrong, without blocking. Never throws: if even the dialog fails, the
     * problem is only logged. Call on the JavaFX thread; the details should already be in the log.
     *
     * @param owner   the window to show it over, or null
     * @param header  a short summary, e.g. "Couldn't open that screen"
     * @param message what it means for the player and what they can do
     */
    public static void error(Window owner, String header, String message) {
        showError(owner, header, message, false);
    }

    /**
     * Like {@link #error}, but waits for the player to close the dialog. Used before the app has to quit.
     */
    public static void errorAndWait(Window owner, String header, String message) {
        showError(owner, header, message, true);
    }

    private static void showError(Window owner, String header, String message, boolean wait) {
        if (isOpen(openError)) {
            log.debug("Not showing error dialog \"{}\": another one is already open", header);
            return;
        }
        try {
            Alert alert = themed(new Alert(Alert.AlertType.ERROR, message), owner);
            alert.setTitle("Something went wrong");
            alert.setHeaderText(header);
            openError = alert;
            if (wait) {
                alert.showAndWait();
            } else {
                alert.show();
            }
        } catch (RuntimeException e) {
            openError = null;
            log.error("Couldn't show the error dialog \"{}\"", header, e);
        }
    }

    // Checks the dialog's actual window, which stays accurate however the dialog was closed
    private static boolean isOpen(Alert alert) {
        if (alert == null || alert.getDialogPane().getScene() == null) {
            return false;
        }
        Window window = alert.getDialogPane().getScene().getWindow();
        return window != null && window.isShowing();
    }

    /**
     * @return the app stylesheet's URL, for custom pop-up windows that build their own scene
     */
    public static String stylesheet() {
        return Objects.requireNonNull(Dialogs.class.getResource(STYLESHEET)).toExternalForm();
    }
}
