package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Components.Style;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.cis3296.virtualchess.Components.Settings.setConfig;

/**
 * UI Controller for the settings screen
 */
public class SettingsMenuController {

    @FXML
    public ComboBox ThemeDropDown;

    @FXML
    public Button backButton;
    @FXML
    public CheckBox undo;
    @FXML
    public CheckBox hints;
    @FXML
    public AnchorPane menu;

    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * Loads all the config options from the config file to display past options.
     * Sets up current themes for the theme dropdown
     */
    public void initialize(){
        String[] themes = {
                Style.SANDCASTLE.styleName,
                Style.CORAL.styleName,
                Style.DUSK.styleName,
                Style.WHEAT.styleName,
                Style.MARINE.styleName,
                Style.EMERALD.styleName,
        };
        ThemeDropDown.getItems().addAll(FXCollections.observableArrayList(themes));

        String lastTheme = Settings.getConfig(Settings.THEME_CONFIG_ACCESS_STRING);
        ThemeDropDown.setValue(Settings.getStyleFromString(lastTheme).styleName);

        String undoChecked = Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING);
        undo.setSelected(Boolean.parseBoolean(undoChecked));

        String hintsChecked = Settings.getConfig(Settings.HINTS_CONFIG_ACCESS_STRING);
        hints.setSelected(Boolean.parseBoolean(hintsChecked));
    }

    /**
     * Button action for switching back to the main menu
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    public void backToMainMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets the theme in the config file
     */
    @FXML
    private void setTheme(){
        setConfig(Settings.THEME_CONFIG_ACCESS_STRING, ThemeDropDown.getValue().toString());
    }

    /**
     * Sets the hints option in the config file
     */
    @FXML
    private void setHints(){
        setConfig(Settings.HINTS_CONFIG_ACCESS_STRING, String.valueOf(hints.isSelected()));
    }

    /**
     * Sets whether the undo button is shown in the config file
     */
    @FXML
    private void setUndoButton(){
        setConfig(Settings.UNDO_CONFIG_ACCESS_STRING, String.valueOf(undo.isSelected()));
    }

}
