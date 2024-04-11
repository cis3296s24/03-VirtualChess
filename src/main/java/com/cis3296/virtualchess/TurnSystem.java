package com.cis3296.virtualchess;

public class TurnSystem {

    private Player whitePlayer;
    private Timer whiteTimer;

    private Player blackPlayer;
    private Timer blackTimer;

    public TurnSystem(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        whiteTimer = new Timer();
        blackTimer = new Timer();
    }

    public void changeTurn(){

    }
}
