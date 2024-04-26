package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Systems.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * UI Controller for the main menu screen
 */
public class MainMenuController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    ImageView imageView;

    @FXML
    AnchorPane menu;

    /**
     * Creates the chess board, loads the GIF, and makes sure the database is initialized
     */
    public void initialize() throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/menuImages/VirtualChess!.gif")));
        imageView.setImage(image);
        Database.getInstance();
        //load settings on startup
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cis3296/virtualchess/settingsMenu.fxml"));
        Parent settingsContent = loader.load();
    }

    /**
     * Button action for switching to the game mode screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    public void switchToGameMode(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/gamemode.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for switching to the leaderboard screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    public void switchToLeaderboard(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/leaderboard.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for switching to the settings screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    public void switchToSettingsMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/settingsMenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for switching to the tutorial screen
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    public void switchToTutorial(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Tutorial Screen.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
