package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.BoardSettings;
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

public class PlayerSelectionScreenController {

    @FXML
    public AnchorPane menu;
    @FXML
    public TextField player1name;
    @FXML
    public TextField player2name;
    @FXML
    public TextField timer;

    private Stage stage;
    private Scene scene;
    private Parent root;



    public void initialize(){

    }

    @FXML
    public void switchToGame(ActionEvent event) throws IOException {
        TurnSystem ts = TurnSystem.getInstance();
        if(Boolean.parseBoolean(BoardSettings.getConfig(BoardSettings.AI_CONFIG_ACCESS_STRING))){
            if(!player1name.getText().isBlank()){
                ts.setWhitePlayer(new Player(player1name.getText()));
                ts.setBlackPlayer(new Player("Computer"));
                if(timer.getText().isBlank()){
                    ts.setWhiteTimer(TurnSystem.DEFAULT_TIMER_AMOUNT);
                    ts.setBlackTimer(TurnSystem.DEFAULT_TIMER_AMOUNT);
                } else{
                    ts.setWhiteTimer(Integer.parseInt(timer.getText()));
                    ts.setBlackTimer(Integer.parseInt(timer.getText()));
                }

                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/board.fxml")));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            }
        } else{
            if(!player1name.getText().isBlank() && !player2name.getText().isBlank()){
                ts.setWhitePlayer(new Player(player1name.getText()));
                ts.setBlackPlayer(new Player(player2name.getText()));
                if(timer.getText().isBlank()){
                    ts.setWhiteTimer(TurnSystem.DEFAULT_TIMER_AMOUNT);
                    ts.setBlackTimer(TurnSystem.DEFAULT_TIMER_AMOUNT);
                } else{
                    ts.setWhiteTimer(Integer.parseInt(timer.getText()));
                    ts.setBlackTimer(Integer.parseInt(timer.getText()));
                }

                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/board.fxml")));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            }
        }
    }

    @FXML
    public void switchToGamemode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/gamemode.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
