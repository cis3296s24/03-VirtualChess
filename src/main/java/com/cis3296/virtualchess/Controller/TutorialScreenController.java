package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TutorialScreenController {

    @FXML
    public AnchorPane menu;

    private Stage stage;
    private Scene scene;
    private Parent root;



    public void initialize(){

    }

    @FXML
    public void switchToPieceInfo(ActionEvent event) throws IOException {
        // Assuming you have a separate FXML for pieces details
        Parent pieceRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Pieces.fxml")));
        Stage pieceStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene pieceScene = new Scene(pieceRoot);
        pieceStage.setScene(pieceScene);
        pieceStage.show();
    }


    @FXML
    public void switchToSelection(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Tutorial Screen.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}