package com.cis3296.virtualchess.Systems;

import com.cis3296.virtualchess.engine.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameClockTest {

    private long now; // fake time, in nanoseconds
    private final GameClock clock = new GameClock(60_000, () -> now);

    private void advanceSeconds(long s) {
        now += s * 1_000_000_000L;
    }

    @Test
    void onlyTheRunningSideLosesTime() {
        clock.switchTo(Color.WHITE);
        advanceSeconds(10);
        assertEquals(50_000, clock.remainingMillis(Color.WHITE));
        assertEquals(60_000, clock.remainingMillis(Color.BLACK));
        clock.switchTo(Color.BLACK);
        advanceSeconds(5);
        assertEquals(50_000, clock.remainingMillis(Color.WHITE));
        assertEquals(55_000, clock.remainingMillis(Color.BLACK));
    }

    @Test
    void switchingToTheSameSideKeepsTimeUsed() {
        clock.switchTo(Color.WHITE);
        advanceSeconds(7);
        clock.switchTo(Color.WHITE);
        advanceSeconds(3);
        assertEquals(50_000, clock.remainingMillis(Color.WHITE));
    }

    @Test
    void stoppedClockDoesNotRun() {
        clock.switchTo(Color.WHITE);
        advanceSeconds(10);
        clock.stop();
        advanceSeconds(100);
        assertEquals(50_000, clock.remainingMillis(Color.WHITE));
        assertNull(clock.running());
        assertNull(clock.flagged());
    }

    @Test
    void flagsTheSideThatRunsOut() {
        clock.switchTo(Color.BLACK);
        advanceSeconds(59);
        assertNull(clock.flagged());
        advanceSeconds(1);
        assertEquals(Color.BLACK, clock.flagged());
        advanceSeconds(30);
        assertEquals(0, clock.remainingMillis(Color.BLACK)); // never negative
    }

    @Test
    void formatsRoundingUp() {
        assertEquals("1:00", clock.format(Color.WHITE));
        clock.switchTo(Color.WHITE);
        now += 500_000_000L;
        assertEquals("1:00", clock.format(Color.WHITE)); // 59.5s left
        advanceSeconds(59);
        assertEquals("0:01", clock.format(Color.WHITE));
        advanceSeconds(1);
        assertEquals("0:00", clock.format(Color.WHITE));
    }
}
