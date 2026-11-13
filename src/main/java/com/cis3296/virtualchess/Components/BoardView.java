package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Piece;
import com.cis3296.virtualchess.engine.Position;
import com.cis3296.virtualchess.engine.Square;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Draws a chess {@link Position} and turns mouse input into move requests.
 * <p>
 * The view knows nothing about the rules. It asks {@link #setLegalMovesProvider} which moves a piece has
 * (an empty list means the piece can't be picked up), and reports what the player tried to do through
 * {@link #setOnMoveRequested}. Whoever owns the game decides what happens, then calls
 * {@link #setPosition} with the result.
 * <p>
 * Players can drag a piece to its destination, or click the piece and then click the destination.
 * White is always at the bottom.
 */
public class BoardView extends StackPane {

    public static final double SQUARE_SIZE = 100;
    private static final double PIECE_SIZE = 84;
    private static final double DRAG_THRESHOLD = 5;

    private static final Color LAST_MOVE_TINT = Color.rgb(255, 235, 59, 0.45);
    private static final Color SELECTED_TINT = Color.rgb(46, 125, 50, 0.45);
    private static final Color HINT_COLOR = Color.rgb(0, 0, 0, 0.22);

    private final GridPane grid = new GridPane();
    private final Pane dragLayer = new Pane();
    private final StackPane[] squares = new StackPane[64];

    private Style boardStyle = Style.SANDCASTLE;
    private boolean showHints = true;
    private Position position = Position.startingPosition();
    private Move lastMove;
    private Function<Square, List<Move>> legalMovesProvider = square -> List.of();
    private BiConsumer<Square, Square> onMoveRequested = (from, to) -> { };

    // Selection and drag state
    private Square selected;
    private List<Move> selectedMoves = List.of();
    private Square pressedSquare;
    private boolean deselectOnRelease;
    private double pressSceneX;
    private double pressSceneY;
    private ImageView dragImage;
    private ImageView draggedPieceView;

    public BoardView() {
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                StackPane cell = new StackPane();
                cell.setMinSize(SQUARE_SIZE, SQUARE_SIZE);
                cell.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
                cell.setMaxSize(SQUARE_SIZE, SQUARE_SIZE);
                grid.add(cell, file, 7 - rank);
                squares[Square.of(file, rank).index()] = cell;
            }
        }
        // The drag layer floats the dragged piece above the board without blocking mouse events
        dragLayer.setMouseTransparent(true);
        getChildren().addAll(grid, dragLayer);
        setAlignment(Pos.TOP_LEFT);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setOnMousePressed(this::handlePress);
        setOnMouseDragged(this::handleDrag);
        setOnMouseReleased(this::handleRelease);
        redraw();
    }

    /**
     * Shows a new position, e.g. after a move or undo. Clears any selection.
     *
     * @param position the position to draw
     * @param lastMove the move that led to it, to highlight, or null for none
     */
    public void setPosition(Position position, Move lastMove) {
        this.position = position;
        this.lastMove = lastMove;
        this.selected = null;
        this.selectedMoves = List.of();
        redraw();
    }

    /**
     * Sets the square colors. Named this way because {@code setStyle} is already used by JavaFX for CSS.
     */
    public void setBoardStyle(Style boardStyle) {
        this.boardStyle = boardStyle;
        redraw();
    }

    /**
     * @param showHints true to mark the squares a selected piece can move to
     */
    public void setShowHints(boolean showHints) {
        this.showHints = showHints;
        redraw();
    }

    /**
     * @param provider returns the legal moves for the piece on a square. Return an empty list for pieces the
     *                 player isn't allowed to pick up (the opponent's, or any piece while it isn't their turn).
     */
    public void setLegalMovesProvider(Function<Square, List<Move>> provider) {
        this.legalMovesProvider = provider;
    }

    /**
     * @param handler called with the from and to squares when the player drops or clicks a piece onto one of
     *                its legal destinations. Promotions arrive as a plain from/to; the handler picks the piece.
     */
    public void setOnMoveRequested(BiConsumer<Square, Square> handler) {
        this.onMoveRequested = handler;
    }

    // ---- Mouse handling ----

    private void handlePress(MouseEvent event) {
        pressedSquare = null;
        deselectOnRelease = false;
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        Square square = squareAt(event);
        if (square == null) {
            clearSelection();
            return;
        }

        // Second click of click-to-move
        if (selected != null && !square.equals(selected) && isTarget(square)) {
            Square from = selected;
            clearSelection();
            onMoveRequested.accept(from, square);
            return;
        }

        if (square.equals(selected)) {
            deselectOnRelease = true; // clicking the selected piece again (without dragging) deselects it
        } else {
            List<Move> moves = legalMovesProvider.apply(square);
            if (moves.isEmpty()) {
                clearSelection();
                return;
            }
            select(square, moves);
        }
        pressedSquare = square;
        pressSceneX = event.getSceneX();
        pressSceneY = event.getSceneY();
    }

    private void handleDrag(MouseEvent event) {
        if (pressedSquare == null) {
            return;
        }
        if (dragImage == null) {
            double distance = Math.hypot(event.getSceneX() - pressSceneX, event.getSceneY() - pressSceneY);
            if (distance < DRAG_THRESHOLD) {
                return;
            }
            startDrag();
        }
        Point2D point = dragLayer.sceneToLocal(event.getSceneX(), event.getSceneY());
        dragImage.relocate(point.getX() - dragImage.getBoundsInLocal().getWidth() / 2,
                point.getY() - dragImage.getBoundsInLocal().getHeight() / 2);
    }

    private void handleRelease(MouseEvent event) {
        if (pressedSquare == null) {
            return;
        }
        Square from = pressedSquare;
        pressedSquare = null;

        if (dragImage != null) {
            endDrag();
            Square target = squareAt(event);
            if (target != null && !target.equals(from) && isTarget(target)) {
                clearSelection();
                onMoveRequested.accept(from, target);
            } else if (target == null || !target.equals(from)) {
                clearSelection(); // dropped somewhere illegal: the piece snaps back
            }
            // Dropped back on its own square: stay selected, so the player can click a destination instead
        } else if (deselectOnRelease) {
            clearSelection();
        }
    }

    private void startDrag() {
        Piece piece = position.pieceAt(pressedSquare);
        if (piece == null) {
            return;
        }
        deselectOnRelease = false;
        for (var child : squares[pressedSquare.index()].getChildren()) {
            if (child instanceof ImageView view) {
                draggedPieceView = view;
                view.setVisible(false);
            }
        }
        dragImage = pieceImageView(piece);
        dragLayer.getChildren().add(dragImage);
    }

    private void endDrag() {
        dragLayer.getChildren().clear();
        dragImage = null;
        if (draggedPieceView != null) {
            draggedPieceView.setVisible(true);
            draggedPieceView = null;
        }
    }

    private Square squareAt(MouseEvent event) {
        Point2D point = grid.sceneToLocal(event.getSceneX(), event.getSceneY());
        int file = (int) Math.floor(point.getX() / SQUARE_SIZE);
        int row = (int) Math.floor(point.getY() / SQUARE_SIZE);
        return Square.isOnBoard(file, 7 - row) ? Square.of(file, 7 - row) : null;
    }

    private void select(Square square, List<Move> moves) {
        selected = square;
        selectedMoves = moves;
        redraw();
    }

    private void clearSelection() {
        if (selected != null) {
            selected = null;
            selectedMoves = List.of();
            redraw();
        }
    }

    private boolean isTarget(Square square) {
        for (Move move : selectedMoves) {
            if (move.to().equals(square)) {
                return true;
            }
        }
        return false;
    }

    // ---- Drawing ----

    private void redraw() {
        Square checkedKing = position.isInCheck() ? position.kingSquare(position.sideToMove()) : null;
        for (Square square : Square.ALL_SQUARES) {
            StackPane cell = squares[square.index()];
            cell.getChildren().clear();

            boolean light = (square.file() + square.rank()) % 2 != 0; // a1 is a dark square
            Color base = light ? boardStyle.squareColor1 : boardStyle.squareColor2;
            cell.setBackground(fill(base));

            if (lastMove != null && (square.equals(lastMove.from()) || square.equals(lastMove.to()))) {
                cell.getChildren().add(tint(LAST_MOVE_TINT));
            }
            if (square.equals(selected)) {
                cell.getChildren().add(tint(SELECTED_TINT));
            }
            if (square.equals(checkedKing)) {
                cell.getChildren().add(checkGlow());
            }
            addCoordinateLabels(cell, square, light ? boardStyle.squareColor2 : boardStyle.squareColor1);

            Piece piece = position.pieceAt(square);
            if (piece != null) {
                cell.getChildren().add(pieceImageView(piece));
            }
            if (showHints && selected != null && isTarget(square)) {
                cell.getChildren().add(piece == null ? hintDot() : captureRing());
            }
        }
    }

    private static void addCoordinateLabels(StackPane cell, Square square, Color color) {
        if (square.rank() == 0) {
            Label fileLabel = coordinateLabel(String.valueOf((char) ('a' + square.file())), color);
            StackPane.setAlignment(fileLabel, Pos.BOTTOM_RIGHT);
            cell.getChildren().add(fileLabel);
        }
        if (square.file() == 0) {
            Label rankLabel = coordinateLabel(String.valueOf(square.rank() + 1), color);
            StackPane.setAlignment(rankLabel, Pos.TOP_LEFT);
            cell.getChildren().add(rankLabel);
        }
    }

    private static Label coordinateLabel(String text, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.setTextFill(color);
        label.setPadding(new Insets(2, 5, 2, 5));
        return label;
    }

    private static ImageView pieceImageView(Piece piece) {
        ImageView view = new ImageView(PieceImages.of(piece));
        view.setFitWidth(PIECE_SIZE);
        view.setFitHeight(PIECE_SIZE);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    private static Region tint(Color color) {
        Region region = new Region();
        region.setBackground(fill(color));
        return region;
    }

    private static Region checkGlow() {
        Region region = new Region();
        region.setBackground(new Background(new BackgroundFill(new RadialGradient(0, 0, 0.5, 0.5, 0.6, true,
                CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255, 0, 0, 0.9)), new Stop(1, Color.rgb(255, 0, 0, 0))),
                null, null)));
        return region;
    }

    private static Circle hintDot() {
        return new Circle(SQUARE_SIZE * 0.15, HINT_COLOR);
    }

    private static Circle captureRing() {
        Circle ring = new Circle(SQUARE_SIZE * 0.44, Color.TRANSPARENT);
        ring.setStroke(HINT_COLOR);
        ring.setStrokeWidth(SQUARE_SIZE * 0.08);
        return ring;
    }

    private static Background fill(Color color) {
        return new Background(new BackgroundFill(color, null, null));
    }
}
