package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Entities.LeaderBoardEntry;
import com.cis3296.virtualchess.Entities.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * SQLite database for storing all past games
 */
public class Database {

    // The instance of the database
    private static Database instance = null;

    // Current connection to the database
    private static Connection con;

    /**
     * Constructor for the database.
     * Opens the database and opens the table
     */
    private Database(){
        con = openDatabase();
        if(con != null){
            createOrOpenTable();
        }
    }

    /**
     * Gets the current instance of the database
     * Implemented as singleton
     * Could pose problems later but for our simple use it should be fine
     * @return The database instance
     */
    public static synchronized Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    /**
     * Opens the database and starts a connection that lasts until it is closed
     * @return The {@link Connection} to the database
     */
    private Connection openDatabase(){
        Connection c = null;

        try {
            // Uses the sqlite Java DataBase Connection Lib
            Class.forName("org.sqlite.JDBC");
            // Gets a connection to the "leaderboard.db" database
            c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");
            c.setAutoCommit(false);

            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return c;
    }

    /**
     * Creates and opens a table if it is the first time opening the database, otherwise it opens the table
     */
    private void createOrOpenTable(){
        Statement stmt;

        try {
            stmt = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS LEADERBOARD " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL," +
                    " PLAYER1           TEXT    NOT NULL, " +
                    " PLAYER1RESULT        CHAR(50), " +
                    " PLAYER2           TEXT    NOT NULL, " +
                    " PLAYER2RESULT        CHAR(50))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    /**
     * Inserts a set of data into the database
     * Results should be Win/Lose/Tie
     * @param p1 Player 1
     * @param p2 Player 2
     * @param resultP1 Player 1 result
     * @param resultP2 Player 2 result
     */
    public static void insert(Player p1, Player p2, String resultP1, String resultP2){
        String query = "INSERT INTO LEADERBOARD (PLAYER1, PLAYER1RESULT, PLAYER2, PLAYER2RESULT) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, p1.name);
            pstmt.setString(2, resultP1);
            pstmt.setString(3, p2.name);
            pstmt.setString(4, resultP2);

            pstmt.executeUpdate();

            con.commit();
            System.out.println("Inserted into Leaderboard");
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Gets all the entries in the database
     * @return An {@link ObservableList} of all {@link LeaderBoardEntry}
     */
    public static ObservableList<LeaderBoardEntry> getAllEntries(){
        Statement stmt;
        ObservableList<LeaderBoardEntry> resultList = FXCollections.observableArrayList();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM LEADERBOARD;" );

            while ( rs.next() ) {
                LeaderBoardEntry lbe = new LeaderBoardEntry(
                        rs.getInt("ID"),
                        rs.getString("PLAYER1"),
                        rs.getString("PLAYER1RESULT"),
                        rs.getString("PLAYER2"),
                        rs.getString("PLAYER2RESULT")
                );
                resultList.add(lbe);
            }

            rs.close();
            stmt.close();
            System.out.println("Operation done successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return resultList;
    }

    /**
     * Closes the connection to the database.
     * Should only be done when the app quits
     */
    public static void closeDatabase(){
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
