package com.cis3296.virtualchess.Data;

import javafx.beans.property.SimpleStringProperty;

public class LeaderBoardEntry {

    private final SimpleStringProperty id;
    private final SimpleStringProperty  player1Name;
    private final SimpleStringProperty  player1Result;
    private final SimpleStringProperty  player2Name;
    private final SimpleStringProperty  player2Result;

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

    @Override
    public String toString() {
        return "ID: " + id + " Player1: " + player1Name + " Player2: " + player2Name + " ResultP1: " + player1Result + " ResultP2: " + player2Result;
    }

    public String getId() {
        return id.get();
    }

    public String getPlayer1Name() {
        return player1Name.get();
    }

    public String getPlayer2Name() {
        return player2Name.get();
    }

    public String getPlayer1Result() {
        return player1Result.get();
    }

    public String getPlayer2Result() {
        return player2Result.get();
    }
}
