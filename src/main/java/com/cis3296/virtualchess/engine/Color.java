package com.cis3296.virtualchess.engine;

/**
 * The two sides in a chess game.
 */
public enum Color {
    WHITE,
    BLACK;

    /**
     * @return the other side
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    /**
     * @return the rank direction this side's pawns move in: +1 for white (toward rank 8), -1 for black
     */
    public int pawnDirection() {
        return this == WHITE ? 1 : -1;
    }
}
