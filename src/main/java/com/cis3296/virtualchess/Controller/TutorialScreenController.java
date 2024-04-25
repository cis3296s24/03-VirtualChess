package com.cis3296.virtualchess.Controller;

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
 * UI Controller for the tutorial screen
 */
public class TutorialScreenController {

    @FXML
    public AnchorPane menu;

    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * Button action for going to the Piece Info Tutorial
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToPieceInfo(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Pieces.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for going to the Basic Move Tutorial
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToBasic(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/BasicMove.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for going to the Check Tutorial
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToCheckScreen(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Check.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for going to the Special Moves Tutorial
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToSpecialMove(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/SpecialMove.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Button action for going to the main menu
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToMainMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}