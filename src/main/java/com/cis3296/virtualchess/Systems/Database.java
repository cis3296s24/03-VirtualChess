package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Entities.LeaderBoardEntry;
import com.cis3296.virtualchess.Entities.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite database holding the result of every finished game (the leaderboard), stored as leaderboard.db in
 * the per-user data folder ({@link AppData}).
 * <p>
 * The database opens on first use. If it can't be opened, the app keeps working without it: results aren't
 * saved, the leaderboard shows as empty, and the problem is logged.
 */
public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String FILE_NAME = "leaderboard.db";

    // The instance of the database
    private static Database instance = null;

    // Connection to the database, or null if it couldn't be opened
    private static Connection con;

    /**
     * Opens the database and creates the leaderboard table if this is the first run.
     */
    private Database() {
        con = openDatabase();
        if (con != null && !createTableIfMissing()) {
            closeQuietly();
        }
    }

    /**
     * Gets the database, opening it the first time this is called.
     * @return The database instance
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * @return true if the database is open and usable
     */
    public static synchronized boolean isAvailable() {
        getInstance();
        return con != null;
    }

    private Connection openDatabase() {
        Path file;
        try {
            file = AppData.file(FILE_NAME);
        } catch (RuntimeException e) {
            log.error("Couldn't find a place for the leaderboard database; game results won't be saved", e);
            return null;
        }
        try {
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + file);
            c.setAutoCommit(false);
            log.info("Opened leaderboard database at {}", file);
            return c;
        } catch (SQLException e) {
            log.error("Couldn't open the leaderboard database at {}; game results won't be saved", file, e);
            return null;
        }
    }

    private boolean createTableIfMissing() {
        String sql = "CREATE TABLE IF NOT EXISTS LEADERBOARD " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL," +
                " PLAYER1           TEXT    NOT NULL, " +
                " PLAYER1RESULT        CHAR(50), " +
                " PLAYER2           TEXT    NOT NULL, " +
                " PLAYER2RESULT        CHAR(50))";
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
            con.commit();
            return true;
        } catch (SQLException e) {
            log.error("Couldn't set up the leaderboard table; game results won't be saved", e);
            return false;
        }
    }

    /**
     * Records a finished game. Results should be Win/Lose/Tie. If the database isn't available, logs a
     * warning and does nothing.
     * @param p1 Player 1 (White)
     * @param p2 Player 2 (Black)
     * @param resultP1 Player 1 result
     * @param resultP2 Player 2 result
     */
    public static synchronized void insert(Player p1, Player p2, String resultP1, String resultP2) {
        if (!isAvailable()) {
            log.warn("Leaderboard unavailable, so this game wasn't recorded: {} ({}) vs {} ({})",
                    name(p1), resultP1, name(p2), resultP2);
            return;
        }
        String query = "INSERT INTO LEADERBOARD (PLAYER1, PLAYER1RESULT, PLAYER2, PLAYER2RESULT) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name(p1));
            pstmt.setString(2, resultP1);
            pstmt.setString(3, name(p2));
            pstmt.setString(4, resultP2);
            pstmt.executeUpdate();
            con.commit();
            log.info("Recorded game on the leaderboard: {} ({}) vs {} ({})", name(p1), resultP1, name(p2), resultP2);
        } catch (SQLException e) {
            log.error("Couldn't record game {} vs {} on the leaderboard", name(p1), name(p2), e);
            rollbackQuietly();
        }
    }

    /**
     * Gets all the entries in the database, oldest game first.
     * @return the entries, or an empty list if the database isn't available or can't be read
     */
    public static synchronized ObservableList<LeaderBoardEntry> getAllEntries() {
        ObservableList<LeaderBoardEntry> resultList = FXCollections.observableArrayList();
        if (!isAvailable()) {
            log.warn("Leaderboard unavailable; showing it empty");
            return resultList;
        }
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM LEADERBOARD ORDER BY ID")) {
            while (rs.next()) {
                resultList.add(new LeaderBoardEntry(
                        rs.getInt("ID"),
                        rs.getString("PLAYER1"),
                        rs.getString("PLAYER1RESULT"),
                        rs.getString("PLAYER2"),
                        rs.getString("PLAYER2RESULT")
                ));
            }
            log.debug("Loaded {} leaderboard entries", resultList.size());
        } catch (SQLException e) {
            log.error("Couldn't read the leaderboard", e);
        }
        return resultList;
    }

    /**
     * Closes the connection to the database when the app quits. Safe to call even if it never opened.
     */
    public static synchronized void closeDatabase() {
        if (con != null) {
            closeQuietly();
            log.info("Closed leaderboard database");
        }
        instance = null;
    }

    private static String name(Player player) {
        return player == null || player.name == null ? "Unknown" : player.name;
    }

    private static void rollbackQuietly() {
        try {
            con.rollback();
        } catch (SQLException e) {
            log.warn("Couldn't roll back the failed leaderboard change", e);
        }
    }

    private static void closeQuietly() {
        try {
            con.close();
        } catch (SQLException e) {
            log.warn("Problem closing the leaderboard database", e);
        } finally {
            con = null;
        }
    }
}
