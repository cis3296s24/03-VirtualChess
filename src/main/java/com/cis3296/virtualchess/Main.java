package com.cis3296.virtualchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //Loads the board file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("board.fxml"));
        //800 by 800 because each tile is 100 and its 8 by 8
        Scene scene = new Scene(fxmlLoader.load(), 810, 800);
        stage.setTitle("VirtualChess");
        stage.setScene(scene);
        // Not resizable for now but that can be something we figure out later
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}