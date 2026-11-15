package com.cis3296.virtualchess;

import java.util.random.RandomGenerator;

/**
 * How hard the computer opponent plays: Stockfish's "Skill Level", how long it may think per move, and how
 * long it pretends to think.
 * <p>
 * Stockfish answers almost instantly at the lower levels, which feels unnatural and rushed, so below Ultra the
 * computer takes a random, human-looking pause before moving ({@link #moveDelayMillis}). Ultra is Stockfish
 * at full strength: no skill limit, more time, more threads, and no pretend pause (it's really thinking).
 */
public enum Difficulty {
    EASY("Easy", 0, 50, 900, 2400,
            "Makes plenty of mistakes. Good for learning."),
    MEDIUM("Medium", 5, 150, 1000, 2800,
            "Plays sensibly, but still slips up."),
    HARD("Hard", 11, 500, 1000, 3000,
            "Punishes most mistakes."),
    EXPERT("Expert", 17, 1000, 800, 2500,
            "Very hard to beat."),
    ULTRA("Ultra", Stockfish.MAX_SKILL_LEVEL, 3000, 0, 0,
            "Stockfish at full strength, thinking up to 3 seconds a move.");

    /** Pauses never take more than this share of the computer's remaining clock time, so it can't flag itself. */
    private static final int CLOCK_SHARE_DIVISOR = 40;
    /** Moves before this number are opening moves, played about twice as fast. */
    private static final int OPENING_MOVES = 6;
    /** The longest pause for a move with only one legal option. */
    private static final int FORCED_MOVE_MAX_MILLIS = 600;

    private final String label;
    private final int skillLevel;
    private final int thinkMillis;
    private final int minDelayMillis;
    private final int maxDelayMillis;
    private final String description;

    Difficulty(String label, int skillLevel, int thinkMillis, int minDelayMillis, int maxDelayMillis,
               String description) {
        this.label = label;
        this.skillLevel = skillLevel;
        this.thinkMillis = thinkMillis;
        this.minDelayMillis = minDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        this.description = description;
    }

    /** @return Stockfish's "Skill Level", 0 to 20 (20 means no limit) */
    public int skillLevel() {
        return skillLevel;
    }

    /** @return how long Stockfish may search per move, in milliseconds */
    public int thinkMillis() {
        return thinkMillis;
    }

    /** @return true for full-strength Stockfish, which also gets extra threads and memory */
    public boolean isFullStrength() {
        return skillLevel >= Stockfish.MAX_SKILL_LEVEL;
    }

    /** @return a sentence describing how strongly this level plays, for the setup screen */
    public String description() {
        return description;
    }

    /**
     * Picks how long the computer should appear to think before moving: a random pause in this level's range,
     * shorter in the opening and when there's only one legal move, and never more than a small share of the
     * time left on its clock. The real search runs during the pause, so the move appears after whichever
     * takes longer.
     *
     * @param legalMoves     how many legal moves the computer has
     * @param moveNumber     the full-move number (1 for the first move)
     * @param remainingMillis the computer's time left on its clock
     * @param random         source of randomness (tests pass a seeded one)
     * @return the pause in milliseconds, 0 or more
     */
    public long moveDelayMillis(int legalMoves, int moveNumber, long remainingMillis, RandomGenerator random) {
        if (maxDelayMillis <= 0) {
            return 0;
        }
        long delay = minDelayMillis + random.nextLong(maxDelayMillis - minDelayMillis + 1L);
        if (moveNumber < OPENING_MOVES) {
            delay /= 2;
        }
        if (legalMoves <= 1) {
            delay = Math.min(delay, FORCED_MOVE_MAX_MILLIS);
        }
        return Math.max(0, Math.min(delay, remainingMillis / CLOCK_SHARE_DIVISOR));
    }

    @Override
    public String toString() {
        return label;
    }
}
