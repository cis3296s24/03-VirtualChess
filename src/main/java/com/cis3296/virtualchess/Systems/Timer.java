package com.cis3296.virtualchess.Systems;

public class Timer {


    private int timeSeconds;
    private boolean running;
    private boolean paused;
    private Thread timerThread;

    public Timer(int timeMinutes) {
        this.timeSeconds = timeMinutes * 60;
        this.running = false;
        this.paused = false;
    }

    public void start() {
        if (!running) {
            running = true;
            paused = false;
            timerThread = new Thread(this::timerSystem);
            timerThread.setDaemon(true);
            timerThread.start();
        }
    }

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

    public void pause() {
        if (running && !paused) {
            paused = true;
        }
    }

    public void unpause() {
        if (running && paused) {
            paused = false;
        }
    }

    private void timerSystem() {
        while (running && timeSeconds > 0) {
            if (!paused) {
                try {
//                    System.out.println("Timer " + timeSeconds + " seconds");
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

    public int getRemainingTimeMinutes() {
        return timeSeconds / 60;
    }

    public int getRemainingTimeSeconds() {
        return timeSeconds % 60;
    }

}
