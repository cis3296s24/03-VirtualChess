package com.cis3296.virtualchess;

public class TurnSystem {

    private Player whitePlayer;
    public Timer whiteTimer;

    private Player blackPlayer;
    public Timer blackTimer;

    private Player currentPlayer;

    public TurnSystem(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        currentPlayer = blackPlayer;
        whiteTimer = new Timer(5);
        blackTimer = new Timer(5);
        whiteTimer.start();
        blackTimer.start();
        blackTimer.pause();
    }

    public void changeTurn(){
        if(currentPlayer == whitePlayer){
            currentPlayer = blackPlayer;

            whiteTimer.pause();
            blackTimer.unpause();
        } else {
            currentPlayer = whitePlayer;

            blackTimer.pause();
            whiteTimer.unpause();
        }
    }
}
