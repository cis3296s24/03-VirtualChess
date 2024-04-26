package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the Game Mode UI
 */
public class GameModeController {

    @FXML
    public AnchorPane menu;

    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * Button action for switching to the Player vs Player screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToSelectionPVP(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/playerSelectionScreenPVP.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        Settings.setConfig(Settings.AI_CONFIG_ACCESS_STRING, "false");
        Settings.setConfig(Settings.KV_CONFIG_ACCESS_STRING, "false");
    }

    /**
     * Button action for switching to the Player vs Computer screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToSelectionPVC(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/playerSelectionScreenPVC.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        Settings.setConfig(Settings.AI_CONFIG_ACCESS_STRING, "true");
        Settings.setConfig(Settings.KV_CONFIG_ACCESS_STRING, "false");
    }

    /**
     * Button action for switching to the main screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void kensVersion(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/playerSelectionScreenPVP.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        Settings.setConfig(Settings.KV_CONFIG_ACCESS_STRING, "true");
    }
}