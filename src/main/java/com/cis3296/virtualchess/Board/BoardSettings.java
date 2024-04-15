package com.cis3296.virtualchess.Board;

import javafx.scene.paint.Color;

public class BoardSettings {

    public static final String CONFIG_ACCESS_STRING = "theme";

    public static final BoardStyle SANDCASTLE = new BoardStyle(
            "Sandcastle",
            Color.web("#e4c16f"),
            Color.web("#b88b4a")
    );

    public static final BoardStyle CORAL = new BoardStyle(
            "Coral",
            Color.web("#b1e4b9"),
            Color.web("#70a2a3")
    );

    public static final BoardStyle DUSK = new BoardStyle(
            "Dusk",
            Color.web("#cbb7ae"),
            Color.web("#716677")
    );

    public static final BoardStyle WHEAT = new BoardStyle(
            "Wheat",
            Color.web("#eaefce"),
            Color.web("#bbbe65")
    );

    public static final BoardStyle MARINE = new BoardStyle(
            "Marine",
            Color.web("#9dacff"),
            Color.web("#6f74d2")
    );

    public static final BoardStyle EMERALD = new BoardStyle(
            "Emerald",
            Color.web("#adbd90"),
            Color.web("#6e8f72")
    );

    public BoardStyle currentBoardStyle;

    public BoardSettings(BoardStyle currentStyle){
        this.currentBoardStyle = currentStyle;
    }

    public static BoardStyle getStyleFromString(String theme){
        return switch (theme) {
            case "Sandcastle" -> SANDCASTLE;
            case "Coral" -> CORAL;
            case "Dusk" -> DUSK;
            case "Wheat" -> WHEAT;
            case "Marine" -> MARINE;
            case "Emerald" -> EMERALD;
            default ->
                    SANDCASTLE;
        };
    }


}
