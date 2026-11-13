package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Components.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * UI Controller for the Learn Chess menu, which links to the four topic pages
 */
public class TutorialScreenController {

    @FXML
    public Pane menu;

    @FXML
    public void switchToPieceInfo(ActionEvent event) {
        Screens.show(event, Screens.PIECES);
    }

    @FXML
    public void switchToBasic(ActionEvent event) {
        Screens.show(event, Screens.BASIC_CONCEPTS);
    }

    @FXML
    public void switchToCheckScreen(ActionEvent event) {
        Screens.show(event, Screens.CHECK);
    }

    @FXML
    public void switchToSpecialMove(ActionEvent event) {
        Screens.show(event, Screens.SPECIAL_MOVES);
    }

    @FXML
    public void switchToMainMenu(ActionEvent event) {
        Screens.show(event, Screens.MAIN_MENU);
    }
}
