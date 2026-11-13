package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Screens;
import com.cis3296.virtualchess.Components.Settings;
import com.cis3296.virtualchess.Entities.LeaderBoardEntry;
import com.cis3296.virtualchess.Systems.Database;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.function.Function;

/**
 * UI Controller for the leaderboard screen
 */
public class LeaderBoardController {

    @FXML
    public Button backButton;
    @FXML
    public TableView<LeaderBoardEntry> table;
    @FXML
    public TableColumn<LeaderBoardEntry, String> idColumn;
    @FXML
    public TableColumn<LeaderBoardEntry, String> Player1Column;
    @FXML
    public TableColumn<LeaderBoardEntry, String> Player1ResultColumn;
    @FXML
    public TableColumn<LeaderBoardEntry, String> Player2Column;
    @FXML
    public TableColumn<LeaderBoardEntry, String> Player2ResultColumn;
    @FXML
    public Pane menu;

    /**
     * Connects each column to its field and loads the games. If the database can't be read, the table just
     * shows its "no games" message (the problem is logged by {@link Database}).
     */
    public void initialize() {
        show(idColumn, LeaderBoardEntry::getId);
        show(Player1Column, LeaderBoardEntry::getPlayer1Name);
        show(Player1ResultColumn, LeaderBoardEntry::getPlayer1Result);
        show(Player2Column, LeaderBoardEntry::getPlayer2Name);
        show(Player2ResultColumn, LeaderBoardEntry::getPlayer2Result);
        table.setItems(Database.getAllEntries());
        // Color the table to match the chosen board theme (see menuStyle.css)
        menu.setStyle(Settings.currentStyle().cssVariables());
    }

    // Type-checked replacement for PropertyValueFactory("name"), which silently shows blanks on a typo
    private static void show(TableColumn<LeaderBoardEntry, String> column, Function<LeaderBoardEntry, String> field) {
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(field.apply(cell.getValue())));
    }

    /**
     * Button action for switching to the main screen
     */
    public void backToMainMenu(ActionEvent event) {
        Screens.show(event, Screens.MAIN_MENU);
    }
}
