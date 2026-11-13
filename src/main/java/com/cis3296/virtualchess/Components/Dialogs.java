package com.cis3296.virtualchess.Components;

import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Gives pop-up dialogs (game over, resign, warnings) the same look as the game screen: colors from the
 * chosen board theme, styled by the "game-dialog" rules in menuStyle.css.
 */
public final class Dialogs {

    private static final String STYLESHEET = "/com/cis3296/virtualchess/menuStyle.css";

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
     * @return the app stylesheet's URL, for custom pop-up windows that build their own scene
     */
    public static String stylesheet() {
        return Objects.requireNonNull(Dialogs.class.getResource(STYLESHEET)).toExternalForm();
    }
}
