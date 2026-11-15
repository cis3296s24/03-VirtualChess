package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.BoardView;
import com.cis3296.virtualchess.Components.Dialogs;
import com.cis3296.virtualchess.Components.Screens;
import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Components.Style;
import com.cis3296.virtualchess.Game;
import com.cis3296.virtualchess.GameSession;
import com.cis3296.virtualchess.engine.ChessGame;
import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.GameResult;
import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Square;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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
    @FXML
    private Button offerDraw;
    @FXML
    private Button rematch;
    @FXML
    private Button leave;

    private final BoardView boardView = new BoardView();
    private Game game;
    private Timeline clockRefresh;
    private boolean resultAnnounced;
    private boolean undoEnabledInSettings = true;

    /**
     * Called by the FXML loader once the screen's controls exist.
     */
    public void initialize() {
        boardContainer.getChildren().add(boardView);
    }

    /**
     * Starts the game; called by whoever opens this screen, with the choices from the setup screen.
     */
    public void start(GameSession session) {
        game = new Game(session, this::refresh);
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

        clockRefresh = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            game.tick(); // a side may have run out of time
            updateClocks();
        }));
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
            // Ask on the board itself; nothing happens until a piece is chosen (or the player cancels)
            boardView.showPromotionPicker(to, chessGame.sideToMove(), piece -> game.play(new Move(from, to, piece)));
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

        updateButtons();
        updateClocks();
    }

    /**
     * While the game is on, the side panel offers Undo, Offer Draw, and Resign; once it's over, those make no
     * sense (a finished game is already on the leaderboard), so it offers a rematch instead.
     */
    private void updateButtons() {
        boolean over = game.chessGame().isOver();
        setShown(undo, undoEnabledInSettings && !over);
        setShown(offerDraw, !over);
        setShown(resign, !over);
        setShown(rematch, over);
        leave.setText(over ? "Main Menu" : "Leave Game");

        undo.setDisable(!game.canUndo());
        resign.setDisable(!game.isHumanTurn());
        offerDraw.setDisable(!game.canOfferDraw());
    }

    // Hidden buttons also give up their space, so the move list grows into it
    private static void setShown(Button button, boolean shown) {
        button.setVisible(shown);
        button.setManaged(shown);
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
        return game.session().player(color).name;
    }

    private void announceResult(GameResult result) {
        if (resultAnnounced) {
            return;
        }
        resultAnnounced = true;
        // show() rather than showAndWait(): this can run in the middle of handling a mouse event
        Platform.runLater(() -> {
            ButtonType rematchChoice = new ButtonType("Rematch", ButtonBar.ButtonData.OK_DONE);
            ButtonType menuChoice = new ButtonType("Main Menu", ButtonBar.ButtonData.BACK_PREVIOUS);
            ButtonType viewBoardChoice = new ButtonType("View Board", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = Dialogs.themed(new Alert(Alert.AlertType.INFORMATION,
                    "Final score: " + result.score() + "\n\n" + rematchDescription(),
                    viewBoardChoice, menuChoice, rematchChoice), window());
            alert.setTitle("Game over");
            alert.setHeaderText(describe(result));
            alert.getDialogPane().lookupButton(menuChoice).getStyleClass().add("secondary");
            // Closing the window counts as View Board (the cancel button)
            alert.setOnHidden(event -> {
                if (alert.getResult() == rematchChoice) {
                    startRematch();
                } else if (alert.getResult() == menuChoice) {
                    goToMainMenu();
                }
            });
            alert.show();
        });
    }

    private String rematchDescription() {
        GameSession next = game.session().rematch();
        if (next.vsComputer()) {
            return "Play again with the same settings, or close this to look over the final position.";
        }
        return "In a rematch, " + next.white().name + " takes White. "
                + "Or close this to look over the final position.";
    }

    /**
     * @return the game window, or null before the screen is showing
     */
    private Window window() {
        return boardView.getScene() == null ? null : boardView.getScene().getWindow();
    }

    private void updateClocks() {
        timerTextWhite.setText("White Time: " + game.clock().format(Color.WHITE));
        timerTextBlack.setText("Black Time: " + game.clock().format(Color.BLACK));
    }

    /**
     * Reads the theme, hints, and undo settings and applies them to the screen.
     */
    private void applySettings() {
        Style theme = Settings.currentStyle();
        boardView.setBoardStyle(theme);
        gameScreen.setStyle(theme.cssVariables());
        boardView.setShowHints(Boolean.parseBoolean(Settings.getConfig(Settings.HINTS_CONFIG_ACCESS_STRING)));
        undoEnabledInSettings = Boolean.parseBoolean(Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING));
        updateButtons();
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
     * Button action: the side to move offers a draw; the opponent answers at the same screen.
     */
    @FXML
    private void offerDrawButton() {
        Color offerer = game.chessGame().sideToMove();
        Alert confirm = Dialogs.themed(new Alert(Alert.AlertType.CONFIRMATION,
                playerName(offerer) + " has offered a draw.\n\n" + playerName(offerer.opposite())
                        + ", do you accept?", ButtonType.YES, ButtonType.NO), window());
        confirm.setTitle("Draw offer");
        confirm.setHeaderText("Accept a draw?");
        confirm.showAndWait()
                .filter(ButtonType.YES::equals)
                .ifPresent(answer -> game.agreeDraw());
    }

    /**
     * Button action: go back to the main menu. Leaving a game in progress asks first, since unfinished games
     * aren't recorded.
     */
    @FXML
    private void leaveGame() {
        ChessGame chessGame = game.chessGame();
        if (!chessGame.isOver() && !chessGame.moves().isEmpty() && !confirmLeave()) {
            return;
        }
        goToMainMenu();
    }

    private boolean confirmLeave() {
        ButtonType leaveChoice = new ButtonType("Leave Game", ButtonBar.ButtonData.OK_DONE);
        ButtonType stayChoice = new ButtonType("Keep Playing", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirm = Dialogs.themed(new Alert(Alert.AlertType.CONFIRMATION,
                "This game isn't finished, so it won't be recorded on the leaderboard.",
                stayChoice, leaveChoice), window());
        confirm.setTitle("Leave game");
        confirm.setHeaderText("Leave this game?");
        return confirm.showAndWait().filter(leaveChoice::equals).isPresent();
    }

    /**
     * Button action (shown once the game is over): play again with the same settings.
     */
    @FXML
    private void rematchButton() {
        startRematch();
    }

    private void startRematch() {
        GameSession next = game.session().rematch();
        Stage stage = stage();
        closeGame();
        log.info("Rematch");
        // A fresh screen and controller, so nothing carries over from the last game
        if (!Screens.<GameController>show(stage, Screens.GAME, controller -> controller.start(next))) {
            Screens.show(stage, Screens.MAIN_MENU); // the game screen failed to load (already reported)
        }
    }

    private void goToMainMenu() {
        Stage stage = stage();
        closeGame();
        Screens.show(stage, Screens.MAIN_MENU);
    }

    // Stops the clocks and the computer; the game screen is about to be replaced
    private void closeGame() {
        clockRefresh.stop();
        game.leave();
    }

    private Stage stage() {
        return (Stage) window();
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
