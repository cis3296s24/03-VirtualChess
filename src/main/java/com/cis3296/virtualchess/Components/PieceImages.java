package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.engine.Color;
import com.cis3296.virtualchess.engine.Piece;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Loads and caches the piece images from /assets/piecesNorm, e.g. white_knight.png.
 */
public final class PieceImages {

    // Images are loaded once at this size and scaled down by the ImageView where needed
    private static final double LOAD_SIZE = 90;
    private static final Map<Piece, Image> CACHE = new HashMap<>();

    private PieceImages() {
    }

    /**
     * @return the image for this piece
     * @throws IllegalStateException if the image file is missing from the build
     */
    public static Image of(Piece piece) {
        return CACHE.computeIfAbsent(piece, PieceImages::load);
    }

    private static Image load(Piece piece) {
        String color = piece.color() == Color.WHITE ? "white" : "black";
        String type = piece.type().name().toLowerCase(Locale.ROOT);
        String path = "/assets/piecesNorm/" + color + "_" + type + ".png";
        InputStream stream = PieceImages.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Missing piece image " + path);
        }
        return new Image(stream, LOAD_SIZE, LOAD_SIZE, true, true);
    }
}
