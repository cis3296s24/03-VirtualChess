package com.cis3296.virtualchess.Components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Settings are loaded once per JVM, so this class points the data folder at a temp directory before the
 * first access and is the only test that touches Settings.
 */
class SettingsTest {

    @TempDir
    static Path dataDir;

    @BeforeAll
    static void useTempDataFolder() {
        System.setProperty("virtualchess.dataDir", dataDir.toString());
    }

    @Test
    void defaultsApplyWhenNoFileExistsAndChangesAreSaved() throws Exception {
        assertEquals("Sandcastle", Settings.getConfig(Settings.THEME_CONFIG_ACCESS_STRING));
        assertEquals("true", Settings.getConfig(Settings.UNDO_CONFIG_ACCESS_STRING));
        assertEquals("true", Settings.getConfig(Settings.HINTS_CONFIG_ACCESS_STRING));
        assertEquals("false", Settings.getConfig(Settings.AI_CONFIG_ACCESS_STRING));

        Settings.setConfig(Settings.THEME_CONFIG_ACCESS_STRING, "Marine");
        assertEquals("Marine", Settings.getConfig(Settings.THEME_CONFIG_ACCESS_STRING));

        Path file = dataDir.resolve("config.xml");
        assertTrue(Files.exists(file), "settings should be saved in the data folder");
        assertTrue(Files.readString(file).contains("<entry key=\"theme\">Marine</entry>"));
    }
}
