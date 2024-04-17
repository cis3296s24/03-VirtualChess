package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Components.Board;
import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.Game;

public class TurnSystem {

    private static Player whitePlayer;
    public static Timer whiteTimer;

    private static Player blackPlayer;
    public static Timer blackTimer;

    private static Player currentPlayer;

    private static TurnSystem instance = null;

    private TurnSystem() {
    }

    public static synchronized void setWhitePlayer(Player whitePlayer){
        TurnSystem.whitePlayer = whitePlayer;
    }

    public static synchronized void setBlackPlayer(Player blackPlayer){
        TurnSystem.blackPlayer = blackPlayer;
    }

    public static synchronized void start(){
        currentPlayer = whitePlayer;
        whiteTimer = new Timer(5);
        blackTimer = new Timer(5);
        whiteTimer.start();
        blackTimer.start();
        blackTimer.pause();
    }

    public static synchronized TurnSystem getInstance() {
        if(instance == null){
            instance = new TurnSystem();
        }
        return instance;
    }

    public static synchronized String changeTurn(){
        if(currentPlayer == whitePlayer){
//            currentPlayer = blackPlayer;

            whiteTimer.pause();
            blackTimer.unpause();
            return blackPlayer.name;
        } else {
            currentPlayer = whitePlayer;

            blackTimer.pause();
            whiteTimer.unpause();
            return whitePlayer.name;
        }
    }

    public static synchronized void stop(){
        whiteTimer.stop();
        blackTimer.stop();
    }

    public static synchronized Player getCurrentPlayer() {
        return currentPlayer;
    }
}
