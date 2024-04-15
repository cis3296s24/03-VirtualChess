package com.cis3296.virtualchess.Board;


import static com.cis3296.virtualchess.Board.BoardStyle.*;

public class BoardSettings {

    public static final String CONFIG_ACCESS_STRING = "theme";

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
