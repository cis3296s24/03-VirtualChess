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
            String sql = "CREATE TABLE LEADERBOARD " +
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
        Statement stmt;

        try{
            stmt = con.createStatement();

            String insert = "INSERT INTO LEADERBOARD (PLAYER1,PLAYER1RESULT,PLAYER2,PLAYER2RESULT) " +
                    "VALUES (" + p1.name + ", " + resultP1 + ", " + p2.name + ", " + resultP2 + " );";
            stmt.executeUpdate(insert);

            stmt.close();
            con.commit();
        } catch (Exception e){

        }
    }

    public static ArrayList<String> getAllEntries(){
        Statement stmt;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String  name = rs.getString("name");
                int age  = rs.getInt("age");
                String  address = rs.getString("address");
                float salary = rs.getFloat("salary");

                System.out.println( "ID = " + id );
                System.out.println( "NAME = " + name );
                System.out.println( "AGE = " + age );
                System.out.println( "ADDRESS = " + address );
                System.out.println( "SALARY = " + salary );
                System.out.println();
            }

            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Operation done successfully");
    }

    public static void closeDatabase(){
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
