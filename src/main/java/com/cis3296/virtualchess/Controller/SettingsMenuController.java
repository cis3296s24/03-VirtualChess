package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Screens;
import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Components.Style;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

import static com.cis3296.virtualchess.Components.Settings.setConfig;

/**
 * UI Controller for the settings screen. Also used as a pop-up during a game, where GameController
 * replaces the Done button's action.
 */
public class SettingsMenuController {

    @FXML
    public ComboBox<String> ThemeDropDown;

    @FXML
    public Button backButton;
    @FXML
    public CheckBox undo;
    @FXML
    public CheckBox hints;
    @FXML
    public Pane menu;

    /**
     * Fills in the theme list and shows the current settings.
     */
    public void initialize() {
        for (Style style : new Style[]{Style.SANDCASTLE, Style.CORAL, Style.DUSK, Style.WHEAT, Style.MARINE, Style.EMERALD}) {
            ThemeDropDown.getItems().add(style.styleName);
        }
        ThemeDropDown.setValue(Settings.currentStyle().styleName);
        undo.setSelected(Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING)));
        hints.setSelected(Boolean.parseBoolean(Settings.getConfig(Settings.HINTS_CONFIG_ACCESS_STRING)));
    }

    /**
     * Button action for going back to the main menu
     */
    public void backToMainMenu(ActionEvent event) {
        Screens.show(event, Screens.MAIN_MENU);
    }

    /**
     * Saves the theme chosen in the dropdown
     */
    @FXML
    private void setTheme() {
        if (ThemeDropDown.getValue() != null) {
            setConfig(Settings.THEME_CONFIG_ACCESS_STRING, ThemeDropDown.getValue());
        }
    }

    /**
     * Saves the hints option
     */
    @FXML
    private void setHints() {
        setConfig(Settings.HINTS_CONFIG_ACCESS_STRING, String.valueOf(hints.isSelected()));
    }

    /**
     * Saves whether the undo button is shown
     */
    @FXML
    private void setUndoButton() {
        setConfig(Settings.UNDO_CONFIG_ACCESS_STRING, String.valueOf(undo.isSelected()));
    }
}
