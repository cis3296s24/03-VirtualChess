package com.cis3296.virtualchess.Components;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.cis3296.virtualchess.Components.Style.*;

/**
 * Data Class for storing settings about the board/game
 */
public class Settings {

    // Access String for getting the theme
    public static final String THEME_CONFIG_ACCESS_STRING = "theme";
    // Access String for getting if the undo button is shown
    public static final String UNDO_CONFIG_ACCESS_STRING = "undo";
    // Access String for getting if the game should show hints
    public static final String HINTS_CONFIG_ACCESS_STRING = "hints";
    // Access String for getting if the game is using AI
    public static final String AI_CONFIG_ACCESS_STRING = "ai";

    // Properties where these are stored at runtime
    private static Properties prop = new Properties();

    // The current theme that is set
    public Style currentStyle;

    /**
     * Constructor for BoardSettings. Sets the current theme
     * @param currentStyle The starting theme
     */
    public Settings(Style currentStyle){
        this.currentStyle = currentStyle;
    }

    /**
     * Static method for converting a string name to a theme({@link Style})
     * @param theme String representing a theme
     * @return The theme from the string
     */
    public static Style getStyleFromString(String theme){
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

    /**
     * Static method for setting a config value. Use one of the static access strings.
     * @param accessString One of the static access Strings representing a config value
     * @param value The value to be changed to
     */
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

    /**
     * Static method for getting a config field. Use one of the static access Strings.
     * @param accessString String representing the config field to get
     * @return The value that was held in that config field
     */
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
