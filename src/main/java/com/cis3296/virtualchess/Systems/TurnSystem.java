package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.Entities.Player;
import javafx.scene.text.Text;

/**
 * A system for keeping track of player turns and timers
 */
public class TurnSystem {

    // Default amount of time in timer if nothing is set
    public static final int DEFAULT_TIMER_AMOUNT = 5;

    // The player for white
    private Player whitePlayer;
    // The white players timer
    private Timer whiteTimer = null;

    // The player for black
    private Player blackPlayer;
    //The black players timer
    private Timer blackTimer = null;

    // Tracks the current player
    private Player currentPlayer;
    // Tracks the current players color
    public String currentColor;

    // Displays each players name if its their turn
    private Text currentPlayerText;

    // The single instance of turn system
    private static TurnSystem instance = null;

    // For determining check. Not in use
    public boolean isCheck = false;
    // For determining checkmate. Not in use
    public boolean isCheckMate = false;

    /**
     * Constructor for Turn System
     * Not being used
     * Turn System is implemented as a singleton
     */
    private TurnSystem() {}

    /**
     * Sets the white player
     * @param whitePlayer {@link Player} for the white side
     */
    public void setWhitePlayer(Player whitePlayer){
        this.whitePlayer = whitePlayer;
    }

    /**
     * Sets the black player
     * @param blackPlayer {@link Player} for the black side
     */
    public void setBlackPlayer(Player blackPlayer){
        this.blackPlayer = blackPlayer;
    }

    /**
     * Starts the turnsystem and sets up default state
     */
    public void start(){
        currentPlayer = whitePlayer;
        currentColor = "white";
        if(whiteTimer == null || blackTimer == null){
            whiteTimer = new Timer(DEFAULT_TIMER_AMOUNT);
            blackTimer = new Timer(DEFAULT_TIMER_AMOUNT);
        }
        whiteTimer.start();
        blackTimer.start();
        blackTimer.pause();
    }

    /**
     * Gets the current instance of turn system
     * or initializes it if it's the first time its being called
     * @return The instance of turn system
     */
    public static synchronized TurnSystem getInstance() {
        if(instance == null){
            instance = new TurnSystem();
        }
        return instance;
    }

    /**
     * Sets the text for whom the current player is
     * @param text {@link Text} to be displayed
     */
    public void setCurrentPlayerText(Text text){
        currentPlayerText = text;
    }

    /**
     * Handles logic for changing turns.
     * Flips the current player color and their timers
     */
    public void changeTurn(){
        if(currentPlayer == whitePlayer){
            currentPlayer = blackPlayer;
            currentColor = "black";

            whiteTimer.pause();
            blackTimer.unpause();
        } else {
            currentPlayer = whitePlayer;
            currentColor = "white";
            blackTimer.pause();
            whiteTimer.unpause();
        }
        currentPlayerText.setText("Current Turn:\n" + this.currentPlayer.name);
    }

    /**
     * Sets the white timer text to the current time from {@link Timer}
     * @param timerTextWhite {@link Text} for white's timer
     */
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

    /**
     * Sets the black timer text to the current time from {@link Timer}
     * @param timerTextBlack {@link Text} for black's timer
     */
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

    /**
     * Stops the turnsystem and all the timers
     */
    public void stop(){
        whiteTimer.stop();
        blackTimer.stop();
    }

    /**
     * Gets the player whose turn it is
     * @return {@link Player} whose turn it is
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets up whites timer
     * @param startingMinutes The starting time in minutes to be used in the timer
     */
    public void setWhiteTimer(int startingMinutes){
        whiteTimer = new Timer(startingMinutes);
    }

    /**
     * Sets up blacks timer
     * @param startingMinutes {@link Player} whose turn it is
     */
    public void setBlackTimer(int startingMinutes){
        blackTimer = new Timer(startingMinutes);
    }

    /**
     * Gets the white player
     * @return {@link Player} for white
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * Gets the black player
     * @return {@link Player} for black
     */
    public Player getBlackPlayer() {
        return blackPlayer;
    }
}
