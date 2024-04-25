package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Components.BoardStyle;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static com.cis3296.virtualchess.Components.BoardSettings.setConfig;

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


    public void initialize(){
        String[] themes = {
                BoardStyle.SANDCASTLE.styleName,
                BoardStyle.CORAL.styleName,
                BoardStyle.DUSK.styleName,
                BoardStyle.WHEAT.styleName,
                BoardStyle.MARINE.styleName,
                BoardStyle.EMERALD.styleName,
        };
        ThemeDropDown.getItems().addAll(FXCollections.observableArrayList(themes));

        String lastTheme = BoardSettings.getConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING);
        ThemeDropDown.setValue(BoardSettings.getStyleFromString(lastTheme).styleName);

        String undoChecked = BoardSettings.getConfig(BoardSettings.UNDO_CONFIG_ACCESS_STRING);
        undo.setSelected(Boolean.parseBoolean(undoChecked));

        String hintsChecked = BoardSettings.getConfig(BoardSettings.HINTS_CONFIG_ACCESS_STRING);
        hints.setSelected(Boolean.parseBoolean(hintsChecked));
    }

    /**
     *
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


    @FXML
    private void setTheme(){
        setConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING, ThemeDropDown.getValue().toString());
    }

    @FXML
    private void setHints(){
        setConfig(BoardSettings.HINTS_CONFIG_ACCESS_STRING, String.valueOf(hints.isSelected()));
    }

    @FXML
    private void setUndoButton(){
        setConfig(BoardSettings.UNDO_CONFIG_ACCESS_STRING, String.valueOf(undo.isSelected()));
    }

}
