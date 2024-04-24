package com.cis3296.virtualchess.Entities;

import javafx.beans.property.SimpleStringProperty;

/**
 * A dataclass for an entry in the leaderboard
 * Setup in a way where the database can auto create these when given data
 */
public class LeaderBoardEntry {

    private final SimpleStringProperty id;
    private final SimpleStringProperty  player1Name;
    private final SimpleStringProperty  player1Result;
    private final SimpleStringProperty  player2Name;
    private final SimpleStringProperty  player2Result;

    /**
     * Constructor for the leaderboard entries in the database
     * @param id Primary key and ID for the entry
     * @param player1Name Name of the first player
     * @param player1Result Win/Lose/Tie for the first player
     * @param player2Name Name of the second player
     * @param player2Result Win/Lose/Tie for the second player
     */
    public LeaderBoardEntry(
            int id,
            String player1Name,
            String player1Result,
            String player2Name,
            String player2Result
    ) {
        this.id = new SimpleStringProperty(String.valueOf(id));
        this.player1Name = new SimpleStringProperty(player1Name);
        this.player2Name = new SimpleStringProperty(player2Name);
        this.player1Result = new SimpleStringProperty(player1Result);
        this.player2Result = new SimpleStringProperty(player2Result);
    }

    /**
     * Creates a string with all the entry variable with spaces in between
     * @return String representation of LeaderBoardEntry
     */
    @Override
    public String toString() {
        return "ID: " + id + " Player1: " + player1Name + " Player2: " + player2Name + " ResultP1: " + player1Result + " ResultP2: " + player2Result;
    }

    /**
     * Getter for the ID
     * @return
     */
    public String getId() {
        return id.get();
    }

    /**
     * Getter for player 1 name
     * @return String for the players name
     */
    public String getPlayer1Name() {
        return player1Name.get();
    }

    /**
     * Getter for player 2 name
     * @return String for the players name
     */
    public String getPlayer2Name() {
        return player2Name.get();
    }

    /**
     * Getter for player 1 result
     * @return String for the players result
     */
    public String getPlayer1Result() {
        return player1Result.get();
    }

    /**
     * Getter for player 2 result
     * @return String for the players result
     */
    public String getPlayer2Result() {
        return player2Result.get();
    }
}
