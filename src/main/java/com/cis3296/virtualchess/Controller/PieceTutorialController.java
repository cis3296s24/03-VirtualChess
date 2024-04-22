/*
 * Citation:
 * All images and gifs are collected from Chess.com, Britannica kids.com and ragchess.com
 */
package com.cis3296.virtualchess.Controller;

import com.cis3296.virtualchess.Systems.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PieceTutorialController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    ImageView imageView;

    @FXML
    ImageView additionalImageView;

    @FXML
    ImageView additionalImageView2;

    @FXML
    AnchorPane piece;

    @FXML
    Label descriptionLabel;

    @FXML
    TextArea descriptionTextArea;

    public void switchToPawn(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/PawnMove.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/PawnMovearrow.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionLabel.setText("The pawn moves by one square forward but captures diagonally. It is the only piece that has a different method for moving and capturing.");
    }


    public void switchToBishop(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/BishopMove.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Bishopmovearrow.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionLabel.setText("The bishop can move diagonally, but not forward, backward, or sideways. Each side starts with two bishops, one on a light square and one on a dark square.");
    }

    public void switchToRook(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/RookMove.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Rookmovearrow.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionLabel.setText("The rook moves forwards, backward, and sideways any number of squares.");
    }

    public void switchToKnight(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/KnightMove.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Knightmovearrow.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionLabel.setText("The knight doesn't go in a straight line. The knight moves two squares in one direction and then turns and goes one square to the side.");
    }

    public void switchToKing(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/KingMove.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/kingmovearrow.gif")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionLabel.setText("The king can move to any square that’s directly next to it: up, down, sideways, or diagonal.");
    }

    public void switchToQueen(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/QueenMove.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/QueenMovearrow.gif")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionLabel.setText("The queen can move forwards, backward, sideways, and diagonally like a king. However, unlike the king, the queen can move as far as it wants to in each of these directions.");
    }

    @FXML
    public void switchToTutorial(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/Tutorial Screen.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSetupBoard(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/SetupBoard.gif")));
        imageView.setImage(image);
        descriptionLabel.setText("Lay out the light square in the bottom-right corner " +
                "Set up the pawns on the second rank " +
                "Put your rooks in the corners " +
                "Place your knights next to the rooks " +
                "Bishops go next to knights" +
                "Queen goes on her color." +
                "Place your king in the last square available." +
                "Don't forget, white moves first!.");
    }

    public void switchToCapturing(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Pieces & Basic animated/Capturing.gif")));
        imageView.setImage(image);
        descriptionLabel.setText("Capturing Opponent's pieces is the primary goal in chess. When a player makes a move, and it lands on a square where opponents piece is standing, that piece is captured by you.");
    }

    public void switchToPieceValue(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/SetupBoard.png")));
        imageView.setImage(image);
        descriptionLabel.setText("Each piece has a numerical value that can help you evaluate whether exchanging it makes sense." +
                " pawns = 1, " +
                " knights = 3," +
                " bishops = 3," +
                " rooks = 5," +
                " queens = 9," +
                " kings = the whole game!");
    }

    public void switchToCheck(ActionEvent event) {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/QueenCheck.png")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/Pawncheck.png")));
        Image additionalImage2 = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/Knightcheck.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        additionalImageView2.setImage(additionalImage2);
        descriptionTextArea.setText("When a king is attacked, it is called check. Check can be viewed as saying ,Watch out! The king is attacked! Since a king can never be captured, the term check is used when a king is threatened.");
    }

    public void switchToGettingOutofCheck(ActionEvent event) {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/gettingoutofcheck1.png")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/gettingoutofcheck2.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        descriptionTextArea.setText("If a player is in check, they must get out of check! There are three ways to get out of check are to move out of check, block the check, or capture the piece putting you in check.You can capture the piece and get out of check. for example We must capture the queen with our king! There is no way to block the check and no way to move out of check, so we must capture on f2.");
    }

    public void switchToCheckMate(ActionEvent event) {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/Checkmate.gif")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/Checkmate.png")));
        Image additionalImage2 = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/CheckmatePawn.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        additionalImageView2.setImage(additionalImage2);
        descriptionTextArea.setText("if you are in check but cannot move out of check, block the check or capture the piece putting you in check? If you are in check and cannot get out of check, then checkmate has occurred and the game is over!" +
                "Checkmate automatically ends the game, regardless of how many pieces are left on the board for either player. In some cases, one player will sacrifice many of their strongest pieces in order to end the game immediately with checkmate.");
    }

    public void switchToStalemate(ActionEvent event) {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/Stalemate1.png")));
        Image additionalImage = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/stalemate2.png")));
        Image additionalImage2 = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/Check/Stalemate3.png")));
        imageView.setImage(image);
        additionalImageView.setImage(additionalImage);
        additionalImageView2.setImage(additionalImage2);
        descriptionTextArea.setText("When a stalemate occurs, the game is considered drawn and both players receive half a point (1/2-1/2)\n" +
                "Here are different ways stalemate can occur:\n" +
                "1. Both players agree to a draw\n" +
                "2. One player is unable to move any pieces, is not in check, and it is their turn\n" +
                "3. The board looks the same for white and black 3 times throughout the game\n" +
                "4. Neither player has sufficient mating material\n" +
                "5. No major piece has moved and checkmate is not achieved within 50 moves \n" +
                "For picture1, white’s king cannot move, so stalemate \n"+
                "For picture2, the queen removes the king’s safe squares, and the king is not in check \n"+
                "For picture3, draw by repetition as the king and queen will move back and forth. No other moves are possible for white.");

    }

    public void switchToCastling(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/SpecialMoves/Castling.gif")));
        imageView.setImage(image);
        descriptionLabel.setText("The king may move two squares to either side, with the rook moving to the other side of the king. Not legal if either piece has moved or if the king would move into, out of, or through check.");
    }

    public void switchToPromotion(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/SpecialMoves/PawnPromotion.gif")));
        imageView.setImage(image);
        descriptionLabel.setText("Pawn promotion occurs when a pawn reaches the farthest rank from its original square—the eighth rank for White and first rank for Black. When this happens, the player can replace the pawn for a queen, a rook, a bishop, or a knight. ");
    }

    public void switchToEnPassant(ActionEvent event) throws IOException {
        Image image = new Image(String.valueOf(getClass().getResource("/assets/Tutorialimages/SpecialMoves/Enpassant.gif")));
        imageView.setImage(image);
        descriptionLabel.setText("The en passant rule is a special pawn capturing move in chess.En passant is a French expression that translates to in passing, which is precisely how this capture works." +
                "Pawns can usually capture only pieces that are directly and diagonally in front of them on an adjacent file. It moves to the captured piece's square and replaces it.");
    }



}
