package com.cis3296.virtualchess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DifficultyTest {

    private static final long PLENTY_OF_TIME = 10 * 60_000L;

    @Test
    void levelsGetStrongerInOrder() {
        Difficulty[] levels = Difficulty.values();
        for (int i = 1; i < levels.length; i++) {
            assertTrue(levels[i].skillLevel() > levels[i - 1].skillLevel(), levels[i] + " vs " + levels[i - 1]);
            assertTrue(levels[i].thinkMillis() >= levels[i - 1].thinkMillis(), levels[i] + " vs " + levels[i - 1]);
        }
    }

    @Test
    void onlyUltraIsFullStrength() {
        for (Difficulty difficulty : Difficulty.values()) {
            assertEquals(difficulty == Difficulty.ULTRA, difficulty.isFullStrength(), difficulty.toString());
        }
    }

    @Test
    void ultraNeverPauses() {
        assertEquals(0, Difficulty.ULTRA.moveDelayMillis(30, 20, PLENTY_OF_TIME, new Random(1)));
    }

    @ParameterizedTest
    @EnumSource(value = Difficulty.class, names = "ULTRA", mode = EnumSource.Mode.EXCLUDE)
    void pausesVaryWithinAHumanRange(Difficulty difficulty) {
        Random random = new Random(42);
        long min = Long.MAX_VALUE;
        long max = 0;
        for (int i = 0; i < 200; i++) {
            long delay = difficulty.moveDelayMillis(30, 20, PLENTY_OF_TIME, random);
            min = Math.min(min, delay);
            max = Math.max(max, delay);
        }
        assertTrue(min >= 500, difficulty + " paused only " + min + " ms");
        assertTrue(max <= 3000, difficulty + " paused " + max + " ms");
        assertTrue(max - min >= 500, difficulty + " pauses barely vary: " + min + " to " + max);
    }

    @Test
    void openingMovesAreQuicker() {
        long opening = Difficulty.MEDIUM.moveDelayMillis(20, 1, PLENTY_OF_TIME, new Random(7));
        long middlegame = Difficulty.MEDIUM.moveDelayMillis(20, 20, PLENTY_OF_TIME, new Random(7));
        assertEquals(middlegame / 2, opening);
    }

    @Test
    void forcedMovesAreQuick() {
        for (int seed = 0; seed < 50; seed++) {
            assertTrue(Difficulty.HARD.moveDelayMillis(1, 20, PLENTY_OF_TIME, new Random(seed)) <= 600);
        }
    }

    @Test
    void neverSpendsMoreThanASmallShareOfTheClock() {
        // 20 seconds left: at most half a second per move
        for (int seed = 0; seed < 50; seed++) {
            assertTrue(Difficulty.EASY.moveDelayMillis(30, 20, 20_000, new Random(seed)) <= 500);
        }
        assertEquals(0, Difficulty.EASY.moveDelayMillis(30, 20, 0, new Random(1)));
        assertFalse(Difficulty.EASY.moveDelayMillis(30, 20, PLENTY_OF_TIME, new Random(1)) <= 500);
    }
}
