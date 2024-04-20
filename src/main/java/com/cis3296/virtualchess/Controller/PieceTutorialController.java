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

public class PieceTutorialController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    ImageView imageView;

    @FXML
    AnchorPane piece;

    /**
     * Creates the chess board
     *//*
    public void initialize(){
        Image image = new Image(String.valueOf(getClass().getResource("/assets/menuImages/VirtualChess!.gif")));
        imageView.setImage(image);
        Database.getInstance();
    }*/

    public void switchToPawn(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Animated_moves/PawnMove.gif")));
        imageView.setImage(image);

    }

    public void switchToBishop(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Animated_moves/BishopMove.gif")));
        imageView.setImage(image);
    }

    public void switchToRook(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Animated_moves/RookMove.gif")));
        imageView.setImage(image);
    }

    public void switchToKnight(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Animated_moves/KnightMove.gif")));
        imageView.setImage(image);
    }

    public void switchToKing(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Animated_moves/KingMove.gif")));
        imageView.setImage(image);
    }

    public void switchToQueen(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Animated_moves/QueenMove.gif")));
        imageView.setImage(image);
    }


}
