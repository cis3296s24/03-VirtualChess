package com.cis3296.virtualchess.Entities.Pieces.PieceUtilities;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Components.BoardSquare;
import com.cis3296.virtualchess.Entities.Pieces.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

/**
 * Handles the pawn promotion logic
 */
public class PawnPromotionPopup {

    private Pawn pawn;
    private String choice;

    /**
     * Constructor for pawn promotion
     * @param pawn the pawn being promoted
     */
    public PawnPromotionPopup(Pawn pawn){
        this.pawn = pawn;
    }

    /**
     * Method to display promotion buttons around the pawn and choose the promotion type
     * @param currentSquare the board square that we want the new promoted piece on
     * @param board the board that houses the squares
     */
    public void displayPromotionButtons(BoardSquare currentSquare, Board board) {
        // Create a new Stage for the pop-up window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Pawn Promotion");

        // Create buttons for promotion options
        Button queenButton = new Button();
        Button rookButton = new Button();
        Button bishopButton = new Button();
        Button knightButton = new Button();

        // Load images for buttons
        Image queenImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + pawn.color + "_queen.png")), 90, 90, true, true);
        Image rookImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + pawn.color + "_rook.png")), 90, 90, true, true);
        Image bishopImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + pawn.color + "_bishop.png")), 90, 90, true, true);
        Image knightImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/piecesNorm/" + pawn.color + "_knight.png")), 90, 90, true, true);

        // Create ImageView objects for each image
        ImageView queenImageView = new ImageView(queenImage);
        ImageView rookImageView = new ImageView(rookImage);
        ImageView bishopImageView = new ImageView(bishopImage);
        ImageView knightImageView = new ImageView(knightImage);

        // Set the size of ImageView objects (optional)
        queenImageView.setFitWidth(50);
        queenImageView.setFitHeight(50);
        rookImageView.setFitWidth(50);
        rookImageView.setFitHeight(50);
        bishopImageView.setFitWidth(50);
        bishopImageView.setFitHeight(50);
        knightImageView.setFitWidth(50);
        knightImageView.setFitHeight(50);

        // Set ImageView objects as graphics for buttons
        queenButton.setGraphic(queenImageView);
        rookButton.setGraphic(rookImageView);
        bishopButton.setGraphic(bishopImageView);
        knightButton.setGraphic(knightImageView);

        // Set up event handlers for the buttons
        queenButton.setOnMouseClicked(e -> queenMouseClick(queenButton, currentSquare, board));
        rookButton.setOnMouseClicked(e -> rookMouseClick(rookButton, currentSquare, board));
        bishopButton.setOnMouseClicked(e -> bishopMouseClick(bishopButton,currentSquare, board));
        knightButton.setOnMouseClicked(e -> knightMouseClick(knightButton, currentSquare, board));

        // Arrange buttons in a layout
        HBox buttonsLayout = new HBox(10);
        buttonsLayout.getChildren().addAll(queenButton, rookButton, bishopButton, knightButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        // Create a StackPane to hold the buttons
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(buttonsLayout);

        // Create a scene and set it on the stage
        Scene scene = new Scene(stackPane, 325, 75);
        popupStage.setScene(scene);

        // Position the pop-up window around the pawn
        double popupX = pawn.coordinates.getX();
        double popupY = pawn.coordinates.getY();
        popupStage.setX(popupX);
        popupStage.setY(popupY);

        // Show the pop-up window
        popupStage.show();
    }

    /**
     * Method called when the queen button is clicked
     * @param queenButton takes button to close the window after clicked
     * @param currentSquare board square the new promoted piece is to be set to
     * @param board the board to help with piece remove and add
     */
    public void queenMouseClick(Button queenButton, BoardSquare currentSquare, Board board){
        // Initialize a new piece
        Queen newQueen = new Queen(pawn.coordinates, pawn.color, board, pawn.isTurn);

        // Remove the pawn from the square
        currentSquare.getChildren().remove(pawn);

        // Add the piece to the board
        board.addPiece(currentSquare, newQueen);

        // Close the pop-up window
        Stage popupStage = (Stage) queenButton.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Method called when the rook button is clicked
     * @param rookButton takes button to close the window after clicked
     * @param currentSquare board square the new promoted piece is to be set to
     * @param board the board to help with piece remove and add
     */
    public void rookMouseClick(Button rookButton, BoardSquare currentSquare, Board board){
        // Initialize a new piece
        Rook newRook = new Rook(pawn.coordinates, pawn.color, board, pawn.isTurn);

        // Remove the pawn from the square
        currentSquare.getChildren().remove(pawn);

        // Add the piece to the board
        board.addPiece(currentSquare, newRook);

        // Close the pop-up window
        Stage popupStage = (Stage) rookButton.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Method called when the bishop button is clicked
     * @param bishopButton takes button to close the window after clicked
     * @param currentSquare board square the new promoted piece is to be set to
     * @param board the board to help with piece remove and add
     */
    public void bishopMouseClick(Button bishopButton, BoardSquare currentSquare, Board board){
        // Initialize a new piece
        Bishop newBishop = new Bishop(pawn.coordinates, pawn.color, board, pawn.isTurn);

        // Remove the pawn from the square
        currentSquare.getChildren().remove(pawn);

        // Add the piece to the board
        board.addPiece(currentSquare, newBishop);

        // Close the pop-up window
        Stage popupStage = (Stage) bishopButton.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Method called when the knight button is clicked
     * @param knightButton takes button to close the window after clicked
     * @param currentSquare board square the new promoted piece is to be set to
     * @param board the board to help with piece remove and add
     */
    public void knightMouseClick(Button knightButton, BoardSquare currentSquare, Board board){
        // Initialize a new piece
        Knight newKnight = new Knight(pawn.coordinates, pawn.color, board, pawn.isTurn);

        // Remove the pawn from the square
        currentSquare.getChildren().remove(pawn);

        // Add the piece to the board
        board.addPiece(currentSquare, newKnight);

        // Close the pop-up window
        Stage popupStage = (Stage) knightButton.getScene().getWindow();
        popupStage.close();
    }

}

