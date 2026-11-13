package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.BoardView;
import com.cis3296.virtualchess.Components.Dialogs;
import com.cis3296.virtualchess.Components.PromotionDialog;
import com.cis3296.virtualchess.Components.Screens;
import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Components.Style;
import com.cis3296.virtualchess.Game;
import com.cis3296.virtualchess.Systems.TurnSystem;
import com.cis3296.virtualchess.engine.ChessGame;
import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.GameResult;
import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Square;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the game screen (board.fxml): connects the {@link BoardView}, side panel, and buttons to
 * a {@link Game}.
 */
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    // The whole screen; gets the board theme's colors as CSS variables so the side panel matches the board
    @FXML
    private Pane gameScreen;
    @FXML
    private StackPane boardContainer;
    @FXML
    private Text currentTurnText;
    @FXML
    private Text timerTextWhite;
    @FXML
    private Text timerTextBlack;
    @FXML
    private ListView<String> moveList;
    @FXML
    private Button undo;
    @FXML
    private Button resign;

    private final TurnSystem turnSystem = TurnSystem.getInstance();
    private final BoardView boardView = new BoardView();
    private Game game;
    private Timeline clockRefresh;
    private boolean resultAnnounced;

    /**
     * Called by the FXML loader once the screen's controls exist.
     */
    public void initialize() {
        boardContainer.getChildren().add(boardView);
        boolean vsComputer = Boolean.parseBoolean(Settings.getConfig(Settings.AI_CONFIG_ACCESS_STRING));
        game = new Game(vsComputer, this::refresh);
        game.setOnComputerFailure(() -> {
            Alert alert = Dialogs.themed(new Alert(Alert.AlertType.WARNING,
                    "The game continues as two players at one board: you can now move the black pieces too.\n\n"
                            + "The details are in the log."), window());
            alert.setTitle("Computer opponent stopped");
            alert.setHeaderText("Stockfish stopped responding");
            alert.show();
        });

        applySettings();
        boardView.setLegalMovesProvider(game::movesFor);
        boardView.setOnMoveRequested(this::onMoveRequested);

        clockRefresh = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> updateClocks()));
        clockRefresh.setCycleCount(Timeline.INDEFINITE);
        clockRefresh.play();
        updateClocks();

        refresh();

        if (game.isComputerUnavailable()) {
            // Wait until the window is showing so the alert has something to sit on top of
            Platform.runLater(() -> {
                Alert alert = Dialogs.themed(new Alert(Alert.AlertType.WARNING,
                        "This game is for two players at one board instead.\n\n"
                                + "See \"Playing Against Stockfish\" in the README for where the app looks for it."),
                        window());
                alert.setTitle("Stockfish not found");
                alert.setHeaderText("Stockfish couldn't be started");
                alert.show();
            });
        }
    }

    private void onMoveRequested(Square from, Square to) {
        ChessGame chessGame = game.chessGame();
        if (chessGame.isPromotion(from, to)) {
            PromotionDialog.ask(boardView.getScene().getWindow(), chessGame.sideToMove())
                    .ifPresent(piece -> game.play(new Move(from, to, piece)));
        } else {
            game.play(new Move(from, to));
        }
    }

    /**
     * Redraws everything from the current game state.
     */
    private void refresh() {
        ChessGame chessGame = game.chessGame();
        List<Move> moves = chessGame.moves();
        boardView.setPosition(chessGame.position(), moves.isEmpty() ? null : moves.getLast());

        moveList.getItems().setAll(formatMoveList(chessGame.movesInSan()));
        if (!moveList.getItems().isEmpty()) {
            moveList.scrollTo(moveList.getItems().size() - 1);
        }

        GameResult result = chessGame.result();
        if (result == null) {
            currentTurnText.setText("Current Turn:\n" + playerName(chessGame.sideToMove()));
        } else {
            currentTurnText.setText("Game over\n" + describe(result));
            announceResult(result);
        }

        undo.setDisable(!game.canUndo());
        resign.setDisable(!game.isHumanTurn());
    }

    /**
     * Pairs up moves for display: "1. e4  e5", "2. Nf3  Nc6", ...
     */
    private static List<String> formatMoveList(List<String> san) {
        List<String> rows = new ArrayList<>();
        for (int i = 0; i < san.size(); i += 2) {
            String black = i + 1 < san.size() ? san.get(i + 1) : "";
            rows.add(String.format("%d.  %-8s %s", i / 2 + 1, san.get(i), black).stripTrailing());
        }
        return rows;
    }

    private String describe(GameResult result) {
        if (result.isDraw()) {
            return result.describe();
        }
        return playerName(result.winner()) + " wins by " + result.reason().description();
    }

    private String playerName(Color color) {
        return color == Color.WHITE ? turnSystem.getWhitePlayer().name : turnSystem.getBlackPlayer().name;
    }

    private void announceResult(GameResult result) {
        if (resultAnnounced) {
            return;
        }
        resultAnnounced = true;
        // show() rather than showAndWait(): this can run in the middle of handling a mouse event
        Platform.runLater(() -> {
            Alert alert = Dialogs.themed(new Alert(Alert.AlertType.INFORMATION,
                    "Final score: " + result.score() + "\n\n"
                            + "You can look over the final position, then press End Game to return to the menu."),
                    window());
            alert.setTitle("Game over");
            alert.setHeaderText(describe(result));
            alert.show();
        });
    }

    /**
     * @return the game window, or null before the screen is showing
     */
    private Window window() {
        return boardView.getScene() == null ? null : boardView.getScene().getWindow();
    }

    private void updateClocks() {
        turnSystem.getWhiteTime(timerTextWhite);
        turnSystem.getBlackTime(timerTextBlack);
    }

    /**
     * Reads the theme, hints, and undo settings and applies them to the screen.
     */
    private void applySettings() {
        Style theme = Settings.currentStyle();
        boardView.setBoardStyle(theme);
        gameScreen.setStyle(theme.cssVariables());
        boardView.setShowHints(Boolean.parseBoolean(Settings.getConfig(Settings.HINTS_CONFIG_ACCESS_STRING)));
        undo.setVisible(Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING)));
    }

    /**
     * Button action: take back the last move (and the computer's reply, against the computer).
     */
    @FXML
    private void undoMoveButton() {
        game.undo();
    }

    /**
     * Button action: the player whose turn it is resigns, after confirming.
     */
    @FXML
    private void resignButton() {
        String name = playerName(game.chessGame().sideToMove());
        Alert confirm = Dialogs.themed(new Alert(Alert.AlertType.CONFIRMATION,
                name + ", do you want to resign? Your opponent will win the game.",
                ButtonType.YES, ButtonType.NO), window());
        confirm.setTitle("Resign");
        confirm.setHeaderText("Resign this game?");
        confirm.showAndWait()
                .filter(ButtonType.YES::equals)
                .ifPresent(answer -> game.resign());
    }

    /**
     * Button action: leave the game and go back to the main menu. Unfinished games aren't recorded.
     */
    @FXML
    private void leaveGame(ActionEvent event) {
        clockRefresh.stop();
        game.leave();
        Screens.show(event, Screens.MAIN_MENU);
    }

    /**
     * Button action: open the settings window, then apply any changes.
     */
    @FXML
    private void settings() {
        Screens.Loaded<SettingsMenuController> loaded;
        try {
            loaded = Screens.load(Screens.SETTINGS);
        } catch (IOException | RuntimeException e) {
            log.error("Couldn't open the settings window", e);
            Dialogs.error(window(), "Couldn't open Settings", "Something went wrong while loading it. The details are in the log.");
            return;
        }

        Stage settingsPopup = new Stage();
        settingsPopup.setScene(Screens.scene(loaded.root()));
        settingsPopup.setTitle("Settings");
        // An owner keeps the pop-up on top of the game; without one, a modal window can end up hidden behind
        // the game window, leaving the game blocked with no visible way out
        settingsPopup.initOwner(window());
        settingsPopup.initModality(Modality.WINDOW_MODAL);

        SettingsMenuController controller = loaded.controller();
        controller.backButton.setOnAction(event -> settingsPopup.close());

        settingsPopup.showAndWait();
        applySettings();
    }
}
