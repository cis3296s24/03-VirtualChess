package com.cis3296.virtualchess;

public class TurnSystem {

    private Player whitePlayer;
    private Timer whiteTimer;

    private Player blackPlayer;
    private Timer blackTimer;

    private Player currentPlayer;

    public TurnSystem(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        currentPlayer = blackPlayer;
        whiteTimer = new Timer();
        blackTimer = new Timer();
    }

    public void changeTurn(){
        if(currentPlayer == whitePlayer){
            currentPlayer = blackPlayer;

            whiteTimer.stop();
        } else {
            currentPlayer = whitePlayer;

            blackTimer.stop();
        }
    }
}
