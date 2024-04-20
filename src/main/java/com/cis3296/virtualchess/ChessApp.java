package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Systems.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChessApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Database.getInstance();
        Stockfish stockfish = new Stockfish();
        stockfish.startEngine();
        String FEN = "8/6pk/8/1R5p/3K3P/8/6r1/8 b - - 0 42";
        // send commands manually
        stockfish.commandBuffer(() -> {
            stockfish.setUCI();
            stockfish.startNewGame();
        });

        // receive output dump
        System.out.println(stockfish.getOutput(1000));

        // draw board from a given position
        System.out.println("Board state :");
        stockfish.drawBoard(FEN);
        stockfish.sendCommand("g7g6");



        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainmenu.fxml")));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("menuStyle.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Database.closeDatabase();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
