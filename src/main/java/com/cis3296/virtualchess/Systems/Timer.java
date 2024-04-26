package com.cis3296.virtualchess.Systems;

/**
 * A timer for keeping track of player time
 */
public class Timer {

    // The amount of time left in the timer
    private int timeSeconds;
    // Running state of the timer
    private boolean running;
    // Paused state of the timer
    private boolean paused;
    // New thread for the timer
    private Thread timerThread;

    /**
     * Constructor for the Timer
     * @param timeMinutes The amount of time the timer should start with in minutes
     */
    public Timer(int timeMinutes) {
        this.timeSeconds = timeMinutes * 60;
        this.running = false;
        this.paused = false;
    }

    /**
     * Starts the timer and creates a new thread for it
     */
    public void start() {
        if (!running) {
            running = true;
            paused = false;
            timerThread = new Thread(this::timerSystem);
            timerThread.setDaemon(true);
            timerThread.start();
        }
    }

    /**
     * Stops the timer and the thread it was in
     */
    public void stop() {
        running = false;
        paused = false;
        if (timerThread != null) {
            try {
                timerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Pauses the timer
     */
    public void pause() {
        if (running && !paused) {
            paused = true;
        }
    }

    /**
     * Unpauses the timer
     */
    public void unpause() {
        if (running && paused) {
            paused = false;
        }
    }

    /**
     * Method that the thread uses.
     * Uses sleep for seconds and subtracts a second every time it comes back
     */
    private void timerSystem() {
        while (running && timeSeconds > 0) {
            if (!paused) {
                try {
                    Thread.sleep(1000);
                    timeSeconds--;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (timeSeconds == 0) {
            running = false;
        }
    }

    /**
     * Gets the minutes remaining
     * @return remaining time
     */
    public int getRemainingTimeMinutes() {
        return timeSeconds / 60;
    }

    /**
     * Gets the seconds remaining
     * @return remaining time
     */
    public int getRemainingTimeSeconds() {
        return timeSeconds % 60;
    }

}
