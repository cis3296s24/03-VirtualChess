package com.cis3296.virtualchess;

import javafx.scene.paint.Color;

public class BoardStyle {

    public String styleName;
    public Color squareColor1;
    public Color squareColor2;


    public BoardStyle(String name, Color color1, Color color2){
        this.styleName = name;
        this.squareColor1 = color1;
        this.squareColor2 = color2;
    }
}
