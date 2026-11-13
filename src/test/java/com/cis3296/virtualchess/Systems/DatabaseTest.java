package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Entities.LeaderBoardEntry;
import com.cis3296.virtualchess.Entities.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The leaderboard database, pointed at a temp folder so tests never touch the real one.
 */
class DatabaseTest {

    @TempDir
    static Path dataDir;

    @BeforeAll
    static void useTempDataFolder() {
        Database.closeDatabase(); // in case another test opened it elsewhere
        System.setProperty("virtualchess.dataDir", dataDir.toString());
    }

    @AfterAll
    static void close() {
        Database.closeDatabase();
    }

    @Test
    void recordsGamesAndReadsThemBackInOrder() {
        Database.insert(new Player("Alice"), new Player("Bob"), "Win", "Lose");
        Database.insert(new Player("Carol"), new Player("Dave"), "Tie", "Tie");

        assertTrue(Files.exists(dataDir.resolve("leaderboard.db")));
        List<LeaderBoardEntry> entries = Database.getAllEntries();
        assertEquals(2, entries.size());
        assertEquals("Alice", entries.get(0).getPlayer1Name());
        assertEquals("Lose", entries.get(0).getPlayer2Result());
        assertEquals("Carol", entries.get(1).getPlayer1Name());
    }

    @Test
    void missingPlayerNamesAreRecordedAsUnknown() {
        int before = Database.getAllEntries().size();
        assertDoesNotThrow(() -> Database.insert(null, new Player(null), "Win", "Lose"));
        List<LeaderBoardEntry> entries = Database.getAllEntries();
        assertEquals(before + 1, entries.size());
        assertEquals("Unknown", entries.getLast().getPlayer1Name());
    }

    @Test
    void closingTwiceIsSafeAndItReopensOnNextUse() {
        Database.closeDatabase();
        assertDoesNotThrow(Database::closeDatabase);
        assertTrue(Database.isAvailable(), "the database should reopen itself when used again");
    }
}
