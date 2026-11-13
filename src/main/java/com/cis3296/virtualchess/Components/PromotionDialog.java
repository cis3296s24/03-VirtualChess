package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.Piece;
import com.cis3296.virtualchess.engine.PieceType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Asks which piece a pawn should promote to.
 */
public final class PromotionDialog {

    private static final PieceType[] CHOICES = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

    private PromotionDialog() {
    }

    /**
     * Shows the dialog centered over the owner window and waits for a choice.
     *
     * @param owner the game window
     * @param color the promoting side, so the buttons show that side's pieces
     * @return the chosen piece, or empty if the player closed the dialog (which cancels the move)
     */
    public static Optional<PieceType> ask(Window owner, Color color) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Promote pawn");
        stage.setResizable(false);

        PieceType[] choice = new PieceType[1];
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        for (PieceType type : CHOICES) {
            ImageView image = new ImageView(PieceImages.of(Piece.of(color, type)));
            image.setFitWidth(64);
            image.setFitHeight(64);
            image.setPreserveRatio(true);

            Button button = new Button(null, image);
            button.setAccessibleText("Promote to " + type.name().toLowerCase());
            button.setOnAction(event -> {
                choice[0] = type;
                stage.close();
            });
            buttons.getChildren().add(button);
        }

        VBox root = new VBox(12, new Label("Promote your pawn to:"), buttons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(16));
        // Match the game screen: board theme colors, styled by ".promotion-dialog" in menuStyle.css
        root.getStyleClass().add("promotion-dialog");
        root.setStyle(Settings.currentStyle().cssVariables());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Dialogs.stylesheet());
        stage.setScene(scene);

        // Center over the game window once the dialog knows its own size
        stage.setOnShown(event -> {
            stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
            stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
        });
        stage.showAndWait();
        return Optional.ofNullable(choice[0]);
    }
}
