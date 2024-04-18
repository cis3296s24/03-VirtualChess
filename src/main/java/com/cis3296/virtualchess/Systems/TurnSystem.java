package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Entities.Player;
import javafx.scene.text.Text;

public class TurnSystem {

    private Player whitePlayer;
    private Timer whiteTimer;

    private Player blackPlayer;
    private Timer blackTimer;

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

    public void getWhiteTime(Text timerTextWhite){
        int minutes = whiteTimer.getRemainingTimeMinutes();
        int seconds = whiteTimer.getRemainingTimeSeconds();
        timerTextWhite.setText(String.format(
                        "White Time: %02d:%02d",
                        minutes,
                        seconds
                )
        );
    }

    public void getBlackTime(Text timerTextBlack){
        int minutes = blackTimer.getRemainingTimeMinutes();
        int seconds = blackTimer.getRemainingTimeSeconds();
        timerTextBlack.setText(String.format(
                "Black Time: %02d:%02d",
                minutes,
                seconds
            )
        );
    }


    public void stop(){
        whiteTimer.stop();
        blackTimer.stop();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
