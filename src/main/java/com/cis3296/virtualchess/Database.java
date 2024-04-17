package com.cis3296.virtualchess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    Connection con;

    public Database(){
        con = openDatabase();
        if(con != null){

            closeDatabase();
        }
    }

    void databaseTest(){
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Opened database successfully");


        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE LEADERBOARD " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " PLAYER1           TEXT    NOT NULL, " +
                    " PLAYER1RESULT        CHAR(50), " +
                    " PLAYER2           TEXT    NOT NULL, " +
                    " PLAYER2RESULT        CHAR(50))";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );

        }
        System.out.println("Table created successfully");
    }

    private Connection openDatabase(){
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Opened database successfully");
        return c;
    }

    private void closeDatabase(){
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
