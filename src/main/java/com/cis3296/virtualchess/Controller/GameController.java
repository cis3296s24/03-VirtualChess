package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.BoardSettings;
import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Game;
import com.cis3296.virtualchess.Systems.StockfishIntegration;
import com.cis3296.virtualchess.Systems.TurnSystem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class GameController {

    public VBox board;
    @FXML
    GridPane chessBoard = new GridPane();

    @FXML
    private Text timerTextWhite;
    @FXML
    private Text timerTextBlack;
    @FXML
    private Text currentTurnText;

    private Timeline timeline;

    private TurnSystem gameturnsystem;
    Game game;

    private StockfishIntegration stockfish; //To use stockfish

    /**
     * Creates the chess board and adds functionality for the drag handling
     */
    public void initialize() {
        stockfish = new StockfishIntegration("C:\\Users\\nasir\\stockfish\\stockfish-windows-x86-64-avx2.exe");
        this.game = new Game(chessBoard);
        this.gameturnsystem = TurnSystem.getInstance();
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
        currentTurnText.setText("Current Turn:\n" + gameturnsystem.getCurrentPlayer().name);
        gameturnsystem.setCurrentPlayerText(currentTurnText);

        // Add drag-and-drop event handlers to the chessboard GridPane
        chessBoard.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        chessBoard.setOnDragDropped(event -> {
            game.chessBoard.removeShownMoves();
            event.setDropCompleted(true);
            event.consume();
        });
    }

    public void updateTime() {
        gameturnsystem.getWhiteTime(timerTextWhite);
        gameturnsystem.getBlackTime(timerTextBlack);
    }

    public void leaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        game.endGame();
    }

    public void settings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cis3296/virtualchess/settingsMenu.fxml"));
            Parent settingsContent = loader.load();

            Stage settingsPopup = new Stage();
            Scene settingsScene = new Scene(settingsContent);
            settingsPopup.setScene(settingsScene);

            settingsPopup.setTitle("Settings");

            settingsPopup.initModality(Modality.APPLICATION_MODAL);


            SettingsMenuController controller = loader.getController();
            controller.backButton.setOnAction(event -> {
                BoardSettings.setConfig(BoardSettings.THEME_CONFIG_ACCESS_STRING, controller.ThemeDropDown.getValue().toString());

                game.getTheme();
                game.chessBoard.rerenderBoard();

                settingsPopup.close();
            });

            settingsPopup.setOnCloseRequest(event -> {
                Platform.runLater(settingsPopup::close);
            });

            settingsPopup.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle computer's move
    private void handleComputerMove() {
        // Get the current board state in FEN format
        String fen = getBoardStateAsFEN();

        // Get the best move from Stockfish
        String bestMove = stockfish.getBestMove(fen);

        // Parse and make the move on the board
        makeMoveFromStockfish(bestMove);
    }

    // Method to convert current board state to FEN format
    private String getBoardStateAsFEN() {
        // Initialize an empty FEN string
        StringBuilder fen = new StringBuilder();

        // Iterate through the board squares
        for (int row = 7; row >= 0; row--) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                BoardSquare square = game.chessBoard.boardSquares.get(row * 8 + col);
                if (square.containsPiece) {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(square.getChildren().get(0).getId());
                } else {
                    emptyCount++;
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row > 0) {
                fen.append("/");
            }
        }

        // Append current turn, castling availability, en passant square, and halfmove/fullmove clocks
        fen.append(" ").append(game.turnSystem.getCurrentPlayer().name.startsWith("white") ? "w" : "b");
        fen.append(" - - 0 1");

        return fen.toString();
    }

    // Method to make the move received from Stockfish
    private void makeMoveFromStockfish(String move){
        // Parse the move from Stockfish (e.g., "e2e4")
        String from = move.substring(0, 2);
        String to = move.substring(2);

        // Find the corresponding board squares for the move
        BoardSquare fromSquare = game.chessBoard.boardSquares.stream()
                .filter(square -> square.coordinates.toString().equals(from))
                .findFirst()
                .orElse(null);
        BoardSquare toSquare = game.chessBoard.boardSquares.stream()
                .filter(square -> square.coordinates.toString().equals(to))
                .findFirst()
                .orElse(null);

        // Ensure both squares are found
        if (fromSquare != null && toSquare != null) {
            // Move the piece from 'fromSquare' to 'toSquare'
            toSquare.getChildren().add(fromSquare.getChildren().remove(0));
            // Update the coordinates of the moved piece
            fromSquare.containsPiece = false;
            toSquare.containsPiece = true;
        }
    }
}
