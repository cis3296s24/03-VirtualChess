package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.engine.Color;

import java.util.function.LongSupplier;

/**
 * A two-sided chess clock. It has no threads: it just remembers when the running side's clock started and
 * works out the time left whenever it's asked, so the answer is always exact. The screen asks often (on a
 * timer) and the game calls {@link #flagged()} to find out whether someone ran out of time.
 */
public final class GameClock {

    private static final long NANOS_PER_MILLI = 1_000_000L;

    private final LongSupplier nanoTime;
    private final long[] remainingNanos = new long[2];
    private Color running; // null while the clock is stopped
    private long runningSince;

    /**
     * @param minutesPerSide starting time for each side
     */
    public GameClock(int minutesPerSide) {
        this(minutesPerSide * 60_000L, System::nanoTime);
    }

    /**
     * @param millisPerSide starting time for each side
     * @param nanoTime      the time source; tests pass a fake one
     */
    public GameClock(long millisPerSide, LongSupplier nanoTime) {
        this.nanoTime = nanoTime;
        remainingNanos[0] = remainingNanos[1] = millisPerSide * NANOS_PER_MILLI;
    }

    /**
     * Runs this side's clock and stops the other's. Safe to call repeatedly with the same side (e.g. after an
     * undo): time already used is kept.
     */
    public void switchTo(Color side) {
        stop();
        running = side;
        runningSince = nanoTime.getAsLong();
    }

    /**
     * Stops both clocks (game over, or leaving the screen).
     */
    public void stop() {
        if (running != null) {
            remainingNanos[running.ordinal()] = nanosLeft(running);
            running = null;
        }
    }

    /**
     * @return the side whose clock is running, or null if stopped
     */
    public Color running() {
        return running;
    }

    /**
     * @return milliseconds this side has left (never negative)
     */
    public long remainingMillis(Color side) {
        return nanosLeft(side) / NANOS_PER_MILLI;
    }

    /**
     * @return the side that has run out of time, or null if nobody has
     */
    public Color flagged() {
        for (Color side : Color.values()) {
            if (nanosLeft(side) <= 0) {
                return side;
            }
        }
        return null;
    }

    /**
     * @return the time left as "m:ss" (rounded up, so it shows 0:00 only once time has really run out)
     */
    public String format(Color side) {
        long seconds = (remainingMillis(side) + 999) / 1000;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private long nanosLeft(Color side) {
        long left = remainingNanos[side.ordinal()];
        if (side == running) {
            left -= nanoTime.getAsLong() - runningSince;
        }
        return Math.max(left, 0);
    }
}
