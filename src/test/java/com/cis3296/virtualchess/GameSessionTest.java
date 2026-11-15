package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Entities.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private final Player alice = new Player("Alice");
    private final Player bob = new Player("Bob");

    @Test
    void twoPlayerRematchSwapsColorsAndKeepsTheClock() {
        GameSession first = new GameSession(alice, bob, false, 10, Difficulty.MEDIUM);
        GameSession rematch = first.rematch();
        assertSame(bob, rematch.white());
        assertSame(alice, rematch.black());
        assertFalse(rematch.vsComputer());
        assertEquals(10, rematch.minutesPerSide());
    }

    @Test
    void rematchOfARematchSwapsBack() {
        GameSession first = new GameSession(alice, bob, false, 10, Difficulty.MEDIUM);
        assertEquals(first, first.rematch().rematch());
    }

    @Test
    void computerRematchKeepsThePlayerOnWhite() {
        Player computer = new Player("Computer");
        GameSession first = new GameSession(alice, computer, true, 5, Difficulty.HARD);
        GameSession rematch = first.rematch();
        assertSame(alice, rematch.white());
        assertSame(computer, rematch.black());
        assertTrue(rematch.vsComputer());
        assertEquals(Difficulty.HARD, rematch.difficulty());
    }
}
