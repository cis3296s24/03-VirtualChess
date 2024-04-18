package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Entities.Player;
import javafx.scene.text.Text;

public class TurnSystem {

    private Player whitePlayer;
    public Timer whiteTimer;

    private Player blackPlayer;
    public Timer blackTimer;

    private Player currentPlayer;

    private Text currentPlayerText;

    private static TurnSystem instance = null;

    private TurnSystem() {
    }

    public void setWhitePlayer(Player whitePlayer){
        this.whitePlayer = whitePlayer;
    }

    public void setBlackPlayer(Player blackPlayer){
        this.blackPlayer = blackPlayer;
    }

    public void start(){
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

    public void setCurrentPlayerText(Text text){
        currentPlayerText = text;
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
        currentPlayerText.setText("Current Turn:\n" + this.currentPlayer.name);
    }

    public void stop(){
        whiteTimer.stop();
        blackTimer.stop();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
