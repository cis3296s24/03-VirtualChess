package com.cis3296.virtualchess.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * UI Controller for the four Learn Chess topic pages (Pieces, Basic Concepts, Check, Special Moves).
 * Each topic button shows a title, a description, and up to three pictures.
 * <p>
 * Citation: all images and gifs are collected from Chess.com, Britannica kids.com and ragchess.com
 */
public class PieceTutorialController {

    private static final String IMAGE_FOLDER = "/assets/Tutorialimages/";

    // Picture slots, left to right. Pages with fewer pictures leave the extra fields null.
    @FXML
    ImageView imageView;

    @FXML
    ImageView additionalImageView;

    @FXML
    ImageView additionalImageView2;

    // The page's root layout (a VBox on every tutorial page); the CSS gives it the background gradient
    @FXML
    Pane piece;

    @FXML
    Label topicTitle;

    @FXML
    Label descriptionLabel;

    /**
     * Empty picture slots take up no space, so whatever pictures a topic has stay centered.
     * (An ImageView with a fit size keeps that size even with no image.)
     */
    @FXML
    private void initialize() {
        for (ImageView view : pictureSlots()) {
            if (view != null) {
                view.managedProperty().bind(view.imageProperty().isNotNull());
            }
        }
    }

    /**
     * Shows a topic: its title, description, and pictures (paths relative to /assets/Tutorialimages/).
     * Picture slots without a picture for this topic are cleared.
     */
    private void showTopic(String title, String description, String... imagePaths) {
        topicTitle.setText(title);
        descriptionLabel.setText(description);
        ImageView[] slots = pictureSlots();
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) {
                slots[i].setImage(i < imagePaths.length ? loadImage(imagePaths[i]) : null);
            }
        }
    }

    private ImageView[] pictureSlots() {
        return new ImageView[]{imageView, additionalImageView, additionalImageView2};
    }

    private Image loadImage(String path) {
        URL url = Objects.requireNonNull(getClass().getResource(IMAGE_FOLDER + path), "Missing tutorial image " + path);
        return new Image(url.toExternalForm());
    }

    /**
     * Button action for going back to the Learn Chess menu
     * @param event {@link ActionEvent} for the click
     * @throws IOException Can throw if screen cant be loaded
     */
    @FXML
    public void switchToTutorial(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Tutorial Screen.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    // ---- Pieces ----

    public void switchToPawn() {
        showTopic("Pawn",
                "Pawns move straight forward one square, or two squares on their very first move. "
                        + "They can never move backward.\n\n"
                        + "Pawns are the only piece that captures differently from how it moves: "
                        + "one square diagonally forward. A pawn that reaches the far side of the board is promoted.",
                "Pieces & Basic animated/PawnMove.gif", "PawnMovearrow.png");
    }

    public void switchToRook() {
        showTopic("Rook",
                "The rook moves any number of squares in a straight line: forward, backward, or sideways.\n\n"
                        + "It can't jump over other pieces. Rooks also take part in castling.",
                "Pieces & Basic animated/RookMove.gif", "Rookmovearrow.png");
    }

    public void switchToBishop() {
        showTopic("Bishop",
                "The bishop moves any number of squares diagonally, as long as nothing is in the way.\n\n"
                        + "Because it only moves diagonally, a bishop stays on the same color square all game. "
                        + "Each side starts with one light-squared and one dark-squared bishop.",
                "Pieces & Basic animated/BishopMove.gif", "Bishopmovearrow.png");
    }

    public void switchToKnight() {
        showTopic("Knight",
                "The knight moves in an L shape: two squares in one direction, then one square to the side.\n\n"
                        + "It's the only piece that can jump over other pieces, and it always lands on a square "
                        + "of the opposite color.",
                "Pieces & Basic animated/KnightMove.gif", "Knightmovearrow.png");
    }

    public void switchToKing() {
        showTopic("King",
                "The king moves one square in any direction: forward, backward, sideways, or diagonally.\n\n"
                        + "It can never move onto a square where it would be in check. Keep it safe: if your king "
                        + "is checkmated, you lose.",
                "Pieces & Basic animated/KingMove.gif", "kingmovearrow.gif");
    }

    public void switchToQueen() {
        showTopic("Queen",
                "The queen is the most powerful piece. She moves any number of squares in a straight line "
                        + "or diagonally, like a rook and a bishop combined.\n\n"
                        + "Like them, she can't jump over other pieces.",
                "Pieces & Basic animated/QueenMove.gif", "QueenMovearrow.gif");
    }

    // ---- Basic concepts ----

    public void switchToSetupBoard() {
        showTopic("Setting Up the Board",
                "Turn the board so each player has a light square in their bottom-right corner.\n\n"
                        + "1. Pawns fill the second rank.\n"
                        + "2. Rooks go in the corners.\n"
                        + "3. Knights go next to the rooks.\n"
                        + "4. Bishops go next to the knights.\n"
                        + "5. The queen goes on her own color (white on d1, black on d8).\n"
                        + "6. The king takes the last square.\n\n"
                        + "White always moves first.",
                "Pieces & Basic animated/SetupBoard.gif");
    }

    public void switchToCapturing() {
        showTopic("Capturing",
                "You capture an opponent's piece by moving one of your pieces onto its square. "
                        + "The captured piece is removed from the board.\n\n"
                        + "Every piece except the pawn captures the same way it moves. You can't capture your own "
                        + "pieces, and the king is never captured: the goal of the game is checkmate.",
                "Pieces & Basic animated/Capturing.gif");
    }

    public void switchToPieceValue() {
        showTopic("Value of the Pieces",
                "Piece values help you judge whether a trade is a good idea:\n\n"
                        + "•  Pawn = 1\n"
                        + "•  Knight = 3\n"
                        + "•  Bishop = 3\n"
                        + "•  Rook = 5\n"
                        + "•  Queen = 9\n\n"
                        + "The king has no value, because losing it means losing the game. "
                        + "These are guidelines: the position matters too.",
                "SetupBoard.png");
    }

    // ---- Check ----

    public void switchToCheck() {
        showTopic("Check",
                "A king that is under attack is in check. You must get out of check on your very next move: "
                        + "no other move is allowed. These pictures show a king in check from a queen, a pawn, and a knight.",
                "Check/QueenCheck.png", "Check/Pawncheck.png", "Check/Knightcheck.png");
    }

    public void switchToGettingOutofCheck() {
        showTopic("Getting Out of Check",
                "There are three ways to get out of check: move the king to a safe square, block the attack by "
                        + "putting a piece in the way, or capture the attacking piece.\n\n"
                        + "Here the queen on f2 is giving check. It can't be blocked and the king has no safe square "
                        + "to move to, so the king captures the queen.",
                "Check/gettingoutofcheck1.png", "Check/gettingoutofcheck2.png");
    }

    public void switchToCheckMate() {
        showTopic("Checkmate",
                "Checkmate is a check that can't be escaped: the king can't move to safety, the attack can't be "
                        + "blocked, and the attacker can't be captured.\n\n"
                        + "Checkmate ends the game immediately, no matter how many pieces each side has left. That's "
                        + "why players sometimes sacrifice their strongest pieces to deliver it.",
                "Check/Checkmate.gif", "Check/Checkmate.png", "Check/CheckmatePawn.png");
    }

    public void switchToStalemate() {
        showTopic("Stalemate",
                "Stalemate happens when the player to move is not in check but has no legal move. "
                        + "The game ends at once in a draw, even if one side has far more pieces.\n\n"
                        + "In pictures 1 and 2 it's White's turn. The king isn't in check, but every square it could "
                        + "move to is attacked, so it's stalemate.\n\n"
                        + "Picture 3 is a different draw: Black can check the king back and forth forever, and the same "
                        + "position appearing three times is a draw. Games are also drawn by agreement, when neither "
                        + "side can checkmate, or after 50 moves with no capture or pawn move.",
                "Check/Stalemate1.png", "Check/stalemate2.png", "Check/Stalemate3.png");
    }

    // ---- Special moves ----

    public void switchToCastling() {
        showTopic("Castling",
                "The king moves two squares toward a rook, and that rook jumps to the square on the king's "
                        + "other side. It's the only move where two pieces move at once.\n\n"
                        + "You can castle only if neither piece has moved yet, the squares between them are empty, "
                        + "and the king is not in check and doesn't pass through or land on an attacked square.",
                "SpecialMoves/Castling.gif");
    }

    public void switchToPromotion() {
        showTopic("Promotion",
                "When a pawn reaches the far end of the board (the 8th rank for White, the 1st rank for Black), "
                        + "it is promoted: you swap it for a queen, rook, bishop, or knight of your color.\n\n"
                        + "Most players choose a queen, so it's possible to have more than one!",
                "SpecialMoves/PawnPromotion.gif");
    }

    public void switchToEnPassant() {
        showTopic("En Passant",
                "En passant (French for \"in passing\") is a special pawn capture.\n\n"
                        + "When a pawn moves two squares forward from its starting square and lands right beside an "
                        + "enemy pawn, that pawn may capture it as if it had moved only one square, by moving "
                        + "diagonally onto the square it skipped over.\n\n"
                        + "The capture must be made on the very next move, or the chance is gone.",
                "SpecialMoves/Enpassant.gif");
    }
}
