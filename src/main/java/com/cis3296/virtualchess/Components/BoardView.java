package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.Piece;
import com.cis3296.virtualchess.engine.PieceType;
import com.cis3296.virtualchess.engine.Position;
import com.cis3296.virtualchess.engine.Square;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

    private static final Logger log = LoggerFactory.getLogger(BoardView.class);

    public static final double SQUARE_SIZE = 100;
    private static final double PIECE_SIZE = 84;
    private static final double DRAG_THRESHOLD = 5;

    // Opacity of the theme's highlight color: lighter for the last move, stronger for the selected piece
    private static final double LAST_MOVE_OPACITY = 0.45;
    private static final double SELECTED_OPACITY = 0.8;
    private static final Color HINT_COLOR = Color.rgb(0, 0, 0, 0.22);

    // Promotion choices in the order the tiles appear, starting on the promotion square
    private static final PieceType[] PROMOTION_CHOICES = {PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP};
    private static final Color PROMOTION_DIM = Color.rgb(0, 0, 0, 0.5);

    private final GridPane grid = new GridPane();
    private final Pane promotionLayer = new Pane();
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
    private Square hoverSquare;

    // Called with the chosen piece; non-null exactly while the promotion picker is showing
    private Consumer<PieceType> onPromotionChosen;

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
        // The promotion layer holds the on-board promotion picker; hidden (and so ignored by the mouse) until
        // needed. The drag layer floats the dragged piece above everything without blocking mouse events.
        promotionLayer.setVisible(false);
        dragLayer.setMouseTransparent(true);
        getChildren().addAll(grid, promotionLayer, dragLayer);
        setAlignment(Pos.TOP_LEFT);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setOnMousePressed(this::handlePress);
        setOnMouseDragged(this::handleDrag);
        setOnMouseReleased(this::handleRelease);
        setOnMouseMoved(this::handleHover);
        setOnMouseExited(event -> {
            hoverSquare = null;
            setCursor(Cursor.DEFAULT);
        });
        setOnKeyPressed(this::handleKey);
        redraw();
    }

    /**
     * Shows a new position, e.g. after a move or undo. Clears any selection.
     *
     * @param position the position to draw
     * @param lastMove the move that led to it, to highlight, or null for none
     */
    public void setPosition(Position position, Move lastMove) {
        // A pending promotion choice belongs to the old position, so drop it
        hidePromotionPicker();
        this.position = position;
        this.lastMove = lastMove;
        this.selected = null;
        this.selectedMoves = List.of();
        this.hoverSquare = null; // re-check the cursor on the next mouse move: whose turn it is changed
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

    // ---- Promotion picker ----

    /**
     * Shows the promotion choices on the board itself: a column of four tiles (queen, knight, rook, bishop)
     * starting on the promotion square, over a dimmed board. Clicking a tile, or pressing Q, N, R, or B,
     * chooses; clicking anywhere else on the board or pressing Esc cancels, leaving the pawn where it was.
     * <p>
     * The picker closes by itself if the position changes first (e.g. undo), and the choice is then dropped.
     *
     * @param to       the square the pawn is promoting on (the first rank or the last rank)
     * @param color    the promoting side, so the tiles show that side's pieces
     * @param onChosen called with the chosen piece; not called if the player cancels
     */
    public void showPromotionPicker(Square to, com.cis3296.virtualchess.engine.Color color, Consumer<PieceType> onChosen) {
        hidePromotionPicker();
        clearSelection();
        hoverSquare = null;
        setCursor(Cursor.DEFAULT);

        Region dim = new Region();
        dim.setPrefSize(8 * SQUARE_SIZE, 8 * SQUARE_SIZE);
        dim.resize(8 * SQUARE_SIZE, 8 * SQUARE_SIZE);
        dim.setBackground(fill(PROMOTION_DIM));
        dim.setOnMousePressed(event -> {
            event.consume();
            log.debug("Promotion cancelled");
            hidePromotionPicker();
        });
        promotionLayer.getChildren().add(dim);

        // The column runs from the promotion square toward the middle of the board
        int towardCenter = to.rank() == 7 ? -1 : 1;
        for (int i = 0; i < PROMOTION_CHOICES.length; i++) {
            PieceType type = PROMOTION_CHOICES[i];
            Square square = Square.of(to.file(), to.rank() + towardCenter * i);
            StackPane tile = promotionTile(Piece.of(color, type));
            tile.relocate(square.file() * SQUARE_SIZE, (7 - square.rank()) * SQUARE_SIZE);
            tile.setOnMousePressed(event -> {
                event.consume();
                choosePromotion(type);
            });
            promotionLayer.getChildren().add(tile);
        }

        onPromotionChosen = onChosen;
        promotionLayer.setVisible(true);
        requestFocus(); // for the keyboard shortcuts
        log.debug("Promotion picker shown on {}", to);
    }

    /**
     * @return true while the promotion picker is waiting for a choice
     */
    public boolean isPromotionPickerShowing() {
        return onPromotionChosen != null;
    }

    private void choosePromotion(PieceType type) {
        Consumer<PieceType> handler = onPromotionChosen;
        hidePromotionPicker();
        if (handler != null) {
            log.debug("Promotion to {} chosen", type.name().toLowerCase());
            handler.accept(type);
        }
    }

    private void hidePromotionPicker() {
        onPromotionChosen = null;
        promotionLayer.getChildren().clear();
        promotionLayer.setVisible(false);
    }

    // A round tile in the light-square color with the piece on it; brightens on hover
    private StackPane promotionTile(Piece piece) {
        Circle background = new Circle(SQUARE_SIZE * 0.46, boardStyle.squareColor1);
        background.setStroke(boardStyle.highlight);
        background.setStrokeWidth(3);
        background.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

        ImageView image = pieceImageView(piece);
        image.setFitWidth(PIECE_SIZE * 0.9);
        image.setFitHeight(PIECE_SIZE * 0.9);

        StackPane tile = new StackPane(background, image);
        tile.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
        tile.resize(SQUARE_SIZE, SQUARE_SIZE);
        tile.setPickOnBounds(true); // the whole square is clickable, not just the circle
        tile.setCursor(Cursor.HAND);
        tile.setAccessibleText("Promote to " + piece.type().name().toLowerCase());
        tile.setOnMouseEntered(event -> {
            background.setFill(boardStyle.squareColor1.brighter());
            tile.setScaleX(1.06);
            tile.setScaleY(1.06);
        });
        tile.setOnMouseExited(event -> {
            background.setFill(boardStyle.squareColor1);
            tile.setScaleX(1);
            tile.setScaleY(1);
        });
        return tile;
    }

    private void handleKey(KeyEvent event) {
        if (!isPromotionPickerShowing()) {
            return;
        }
        switch (event.getCode()) {
            case ESCAPE -> {
                log.debug("Promotion cancelled");
                hidePromotionPicker();
            }
            case Q -> choosePromotion(PieceType.QUEEN);
            case N -> choosePromotion(PieceType.KNIGHT);
            case R -> choosePromotion(PieceType.ROOK);
            case B -> choosePromotion(PieceType.BISHOP);
            default -> {
                return; // leave other keys alone
            }
        }
        event.consume();
    }

    // ---- Mouse handling ----

    // Show a hand over pieces the player can pick up. Only re-checks when the mouse enters a new square.
    private void handleHover(MouseEvent event) {
        if (isPromotionPickerShowing()) {
            return; // the picker's tiles set their own cursor
        }
        Square square = squareAt(event);
        if (square == null ? hoverSquare == null : square.equals(hoverSquare)) {
            return;
        }
        hoverSquare = square;
        boolean movable = square != null && !legalMovesProvider.apply(square).isEmpty();
        setCursor(movable ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void handlePress(MouseEvent event) {
        pressedSquare = null;
        deselectOnRelease = false;
        if (event.getButton() != MouseButton.PRIMARY || isPromotionPickerShowing()) {
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
        setCursor(Cursor.CLOSED_HAND);
    }

    private void endDrag() {
        dragLayer.getChildren().clear();
        dragImage = null;
        hoverSquare = null;
        setCursor(Cursor.DEFAULT);
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
                cell.getChildren().add(tint(boardStyle.highlight.deriveColor(0, 1, 1, LAST_MOVE_OPACITY)));
            }
            if (square.equals(selected)) {
                cell.getChildren().add(tint(boardStyle.highlight.deriveColor(0, 1, 1, SELECTED_OPACITY)));
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

            // Everything drawn inside a square ignores the mouse, so presses land on the square itself.
            // A redraw replaces these nodes, and JavaFX sends the rest of a drag to whichever node was
            // pressed; if that were a piece image, the drag would go to a node that no longer exists.
            for (Node child : cell.getChildren()) {
                child.setMouseTransparent(true);
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
