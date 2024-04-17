package com.cis3296.virtualchess.Data;

public class LeaderBoardEntry {

    public int id;
    public String player1Name;
    public String player1Result;
    public String player2Name;
    public String player2Result;

    public LeaderBoardEntry(
            int id,
            String player1Name,
            String player1Result,
            String player2Name,
            String player2Result
    ) {
        this.id = id;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Result = player1Result;
        this.player2Result = player2Result;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Player1: " + player1Name + " Player2: " + player2Name + " ResultP1: " + player1Result + " ResultP2: " + player2Result;
    }
}
