package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Entities.Player;
import com.cis3296.virtualchess.engine.Color;

/**
 * The choices made on the setup screens for one game: who's playing, whether Black is the computer, and how
 * long each side's clock is. It's handed to the game screen, so nothing about a game lives in global state.
 *
 * @param white           the White player
 * @param black           the Black player (named "Computer" when {@code vsComputer})
 * @param vsComputer      true if Black is played by Stockfish
 * @param minutesPerSide  starting clock time for each side
 * @param difficulty      how strongly the computer plays (ignored for two players)
 */
public record GameSession(Player white, Player black, boolean vsComputer, int minutesPerSide,
                          Difficulty difficulty) {

    public static final int DEFAULT_MINUTES = 5;
    public static final int MAX_MINUTES = 180;

    public Player player(Color color) {
        return color == Color.WHITE ? white : black;
    }

    /**
     * @return the setup for a rematch: the same settings, with the players swapping colors in a two-player
     * game (against the computer the player always has White, so nothing changes)
     */
    public GameSession rematch() {
        if (vsComputer) {
            return this;
        }
        return new GameSession(black, white, false, minutesPerSide, difficulty);
    }
}
