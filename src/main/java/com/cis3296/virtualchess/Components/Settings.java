package com.cis3296.virtualchess.Components;

import com.cis3296.virtualchess.Systems.AppData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.cis3296.virtualchess.Components.Style.*;

/**
 * The player's settings, saved to config.xml in the per-user data folder (see {@link AppData}).
 * <p>
 * The file is read once, the first time a setting is needed, and rewritten whenever one changes. Settings
 * missing from the file (or a missing file, on first launch) fall back to the defaults below.
 */
public class Settings {

    private static final Logger log = LoggerFactory.getLogger(Settings.class);

    // Access String for getting the theme
    public static final String THEME_CONFIG_ACCESS_STRING = "theme";
    // Access String for getting if the undo button is shown
    public static final String UNDO_CONFIG_ACCESS_STRING = "undo";
    // Access String for getting if the game should show hints
    public static final String HINTS_CONFIG_ACCESS_STRING = "hints";
    // Access String for getting if the game is using AI
    public static final String AI_CONFIG_ACCESS_STRING = "ai";

    private static final String FILE_NAME = "config.xml";

    private static final Properties DEFAULTS = new Properties();

    static {
        DEFAULTS.setProperty(THEME_CONFIG_ACCESS_STRING, SANDCASTLE.styleName);
        DEFAULTS.setProperty(UNDO_CONFIG_ACCESS_STRING, "true");
        DEFAULTS.setProperty(HINTS_CONFIG_ACCESS_STRING, "true");
        DEFAULTS.setProperty(AI_CONFIG_ACCESS_STRING, "false");
    }

    // Loaded on first use; falls back to DEFAULTS for anything not in the file
    private static Properties prop;

    private Settings() {
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
     * @return the board theme the player has chosen
     */
    public static Style currentStyle() {
        return getStyleFromString(getConfig(THEME_CONFIG_ACCESS_STRING));
    }

    /**
     * Static method for setting a config value and saving it. Use one of the static access strings.
     * @param accessString One of the static access Strings representing a config value
     * @param value The value to be changed to
     */
    public static synchronized void setConfig(String accessString, String value){
        Object previous = properties().setProperty(accessString, value);
        if (!value.equals(previous)) {
            log.info("Setting \"{}\" changed to {}", accessString, value);
        }
        try (OutputStream out = Files.newOutputStream(AppData.file(FILE_NAME))) {
            properties().storeToXML(out, "VirtualChess settings");
        } catch (IOException | RuntimeException e) {
            log.warn("Couldn't save settings; the change only lasts until the app closes", e);
        }
    }

    /**
     * Static method for getting a config field. Use one of the static access Strings.
     * @param accessString String representing the config field to get
     * @return The saved value, or the default if it was never saved
     */
    public static synchronized String getConfig(String accessString){
        return properties().getProperty(accessString);
    }

    private static Properties properties() {
        if (prop == null) {
            prop = new Properties(DEFAULTS);
            Path file;
            try {
                file = AppData.file(FILE_NAME);
            } catch (RuntimeException e) {
                log.warn("Couldn't find the data folder; using default settings", e);
                return prop;
            }
            if (Files.exists(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    prop.loadFromXML(in);
                    log.info("Loaded settings from {}", file);
                } catch (IOException e) {
                    log.warn("Couldn't read settings from {}; using defaults", file, e);
                }
            } else {
                log.info("No settings file yet; using defaults (will be saved to {})", file);
            }
        }
        return prop;
    }
}
