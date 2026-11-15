package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * UI Controller for choosing between two players and playing the computer
 */
public class GameModeController {

    @FXML
    public Pane menu;

    /**
     * Button action for a two-player game
     */
    @FXML
    public void switchToSelectionPVP(ActionEvent event) {
        Screens.<PlayerSelectionScreenController>show(event, Screens.PLAYER_SELECTION_PVP, c -> c.setVsComputer(false));
    }

    /**
     * Button action for a game against the computer
     */
    @FXML
    public void switchToSelectionPVC(ActionEvent event) {
        Screens.<PlayerSelectionScreenController>show(event, Screens.PLAYER_SELECTION_PVC, c -> c.setVsComputer(true));
    }

    @FXML
    public void switchToMainMenu(ActionEvent event) {
        Screens.show(event, Screens.MAIN_MENU);
    }
}
