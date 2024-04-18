package com.cis3296.virtualchess.Components;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.cis3296.virtualchess.Components.BoardStyle.*;

public class BoardSettings {


    public static final String THEME_CONFIG_ACCESS_STRING = "theme";
    public static final String UNDO_CONFIG_ACCESS_STRING = "undo";
    public static final String HINTS_CONFIG_ACCESS_STRING = "hints";

    private static Properties prop = new Properties();

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

    public static void setConfig(String accessString, String value){
        prop.setProperty(accessString, value);
        try {
            File configFile = new File("config.xml");
            FileOutputStream out = new FileOutputStream(configFile);
            prop.storeToXML(out,"Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConfig(String accessString){
        File configFile;

        try {
            configFile = new File("config.xml");
            FileInputStream in = new FileInputStream(configFile);
            prop.loadFromXML(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(prop.get(accessString));
    }


}
