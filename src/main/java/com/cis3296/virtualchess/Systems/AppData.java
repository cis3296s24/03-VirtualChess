package com.cis3296.virtualchess.Systems;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Where the app keeps per-user files (settings and the leaderboard database).
 * <p>
 * By default that's a ".virtualchess" folder in the user's home directory, e.g. ~/.virtualchess on Linux
 * and macOS or C:\Users\name\.virtualchess on Windows, so the files never end up in the repository or
 * depend on which folder the app was launched from. Setting the system property
 * {@code virtualchess.dataDir} points it somewhere else (useful for testing).
 */
public final class AppData {

    private AppData() {
    }

    /**
     * @return the data folder, created if it doesn't exist yet
     */
    public static Path directory() {
        String override = System.getProperty("virtualchess.dataDir");
        Path directory = override != null
                ? Path.of(override)
                : Path.of(System.getProperty("user.home"), ".virtualchess");
        try {
            return Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't create the data folder " + directory, e);
        }
    }

    /**
     * @return the path of a file inside the data folder
     */
    public static Path file(String name) {
        return directory().resolve(name);
    }
}
