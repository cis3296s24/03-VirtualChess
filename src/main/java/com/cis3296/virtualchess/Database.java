package com.cis3296.virtualchess;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    private static Database instance = null;

    private static Connection con;

    private Database(){
        con = openDatabase();
        if(con != null){
            createOrOpenTable();
        }
    }

    public static synchronized Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    private Connection openDatabase(){
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");
            c.setAutoCommit(false);

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Opened database successfully");

        return c;
    }

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
        System.out.println("Inserted into Leaderboard");
    }

    public static ArrayList<LeaderBoardEntry> getAllEntries(){
        Statement stmt;
        ArrayList<LeaderBoardEntry> resultList = new ArrayList<>();
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
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Operation done successfully");
        return resultList;
    }

    public static void closeDatabase(){
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
