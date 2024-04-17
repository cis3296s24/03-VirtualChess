package com.cis3296.virtualchess.Components;

import javafx.scene.paint.Color;

public class BoardStyle {

    public String styleName;
    public Color squareColor1;
    public Color squareColor2;

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


    public BoardStyle(String name, Color color1, Color color2){
        this.styleName = name;
        this.squareColor1 = color1;
        this.squareColor2 = color2;
    }
}
