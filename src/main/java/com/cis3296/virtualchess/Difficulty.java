package com.cis3296.virtualchess;

/**
 * How hard the computer opponent plays: Stockfish's "Skill Level" and how long it may think per move.
 */
public enum Difficulty {
    EASY("Easy", 1, 200),
    MEDIUM("Medium", 7, 500),
    HARD("Hard", 14, 1000),
    EXPERT("Expert", 20, 1500);

    private final String label;
    private final int skillLevel;
    private final int thinkMillis;

    Difficulty(String label, int skillLevel, int thinkMillis) {
        this.label = label;
        this.skillLevel = skillLevel;
        this.thinkMillis = thinkMillis;
    }

    /** @return Stockfish's "Skill Level", 0 to 20 */
    public int skillLevel() {
        return skillLevel;
    }

    /** @return how long the computer may think per move, in milliseconds */
    public int thinkMillis() {
        return thinkMillis;
    }

    @Override
    public String toString() {
        return label;
    }
}
