package com.cis3296.virtualchess.Components;

import javafx.scene.paint.Color;

/**
 * Board themes: the two square colors, plus a highlight color for the last move and the selected piece.
 * The rest of the game screen, the leaderboard, and the game dialogs derive their colors from these
 * (see {@link #cssVariables()}).
 */
public class Style {

    // The name of the style
    public String styleName;
    // The light squares (a1 is dark, so h1 and a8 are light)
    public Color squareColor1;
    // The dark squares
    public Color squareColor2;
    // Tints the last move's squares and the selected piece's square. Chosen per theme so it stands out
    // on both square colors (a single yellow turns muddy on blue boards and vanishes on yellow ones).
    public Color highlight;

    // A Tan Theme
    public static final Style SANDCASTLE = new Style(
            "Sandcastle",
            Color.web("#e4c16f"),
            Color.web("#b88b4a"),
            Color.web("#fff04a")
    );

    // A Teal Theme
    public static final Style CORAL = new Style(
            "Coral",
            Color.web("#b1e4b9"),
            Color.web("#70a2a3"),
            Color.web("#fff27a")
    );

    // A Dark Purple Theme
    public static final Style DUSK = new Style(
            "Dusk",
            Color.web("#cbb7ae"),
            Color.web("#716677"),
            Color.web("#ffb98a")
    );

    // A White and Yellow Theme
    public static final Style WHEAT = new Style(
            "Wheat",
            Color.web("#eaefce"),
            Color.web("#bbbe65"),
            Color.web("#ff9f43")
    );

    // A Blue Theme
    public static final Style MARINE = new Style(
            "Marine",
            Color.web("#9dacff"),
            Color.web("#6f74d2"),
            Color.web("#5fe3ff")
    );

    // A Green Theme
    public static final Style EMERALD = new Style(
            "Emerald",
            Color.web("#adbd90"),
            Color.web("#6e8f72"),
            Color.web("#f5e663")
    );

    /**
     * @param name      Name of the theme
     * @param light     Light square color
     * @param dark      Dark square color
     * @param highlight Color for last-move and selection highlights
     */
    public Style(String name, Color light, Color dark, Color highlight){
        this.styleName = name;
        this.squareColor1 = light;
        this.squareColor2 = dark;
        this.highlight = highlight;
    }

    /**
     * The theme's colors as CSS variables, so other screens can match the board. Apply with
     * {@code node.setStyle(style.cssVariables())}; rules in menuStyle.css then use {@code -theme-light}
     * (the light squares) and {@code -theme-dark} (the dark squares), e.g. {@code derive(-theme-dark, -60%)}.
     */
    public String cssVariables() {
        return "-theme-light: " + toHex(squareColor1) + "; -theme-dark: " + toHex(squareColor2) + ";";
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
                Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255));
    }
}
