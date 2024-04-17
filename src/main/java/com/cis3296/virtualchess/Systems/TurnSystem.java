package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Game;

public class TurnSystem {

    private Player whitePlayer;
    public Timer whiteTimer;

    private Player blackPlayer;
    public Timer blackTimer;

    private Player currentPlayer;

    private Board board;

    private Game game;

    public TurnSystem(Player whitePlayer, Player blackPlayer, Game game) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        currentPlayer = blackPlayer;
        whiteTimer = new Timer(5);
        blackTimer = new Timer(5);
        whiteTimer.start();
        blackTimer.start();
        blackTimer.pause();
        this.game = game;

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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
