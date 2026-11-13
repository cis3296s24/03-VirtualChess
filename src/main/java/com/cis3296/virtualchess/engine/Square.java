package com.cis3296.virtualchess.engine;

import java.util.List;

/**
 * One of the 64 squares on the board.
 * <p>
 * Files and ranks are zero-based and match chess notation: file 0 is the a-file, and rank 0 is rank 1
 * (White's back rank). So a1 is {@code (0, 0)}, e4 is {@code (4, 3)}, and h8 is {@code (7, 7)}.
 *
 * @param file 0-7, for files a-h
 * @param rank 0-7, for ranks 1-8
 */
public record Square(int file, int rank) {

    private static final Square[] ALL = new Square[64];

    static {
        for (int index = 0; index < 64; index++) {
            ALL[index] = new Square(index % 8, index / 8);
        }
    }

    /**
     * Every square, ordered a1, b1, ... h1, a2, ... h8.
     */
    public static final List<Square> ALL_SQUARES = List.of(ALL);

    public Square {
        if (!isOnBoard(file, rank)) {
            throw new IllegalArgumentException("Square off the board: file " + file + ", rank " + rank);
        }
    }

    /**
     * @return the square at this file and rank
     * @throws IllegalArgumentException if it's off the board
     */
    public static Square of(int file, int rank) {
        if (!isOnBoard(file, rank)) {
            throw new IllegalArgumentException("Square off the board: file " + file + ", rank " + rank);
        }
        return ALL[rank * 8 + file];
    }

    /**
     * @param index 0-63, as returned by {@link #index()}
     * @return the square with that index
     */
    public static Square fromIndex(int index) {
        return ALL[index];
    }

    /**
     * @param algebraic a square name such as "e4"
     * @return the matching square
     * @throws IllegalArgumentException if the name isn't a square
     */
    public static Square parse(String algebraic) {
        if (algebraic == null || algebraic.length() != 2) {
            throw new IllegalArgumentException("Not a square: \"" + algebraic + "\"");
        }
        int file = algebraic.charAt(0) - 'a';
        int rank = algebraic.charAt(1) - '1';
        if (!isOnBoard(file, rank)) {
            throw new IllegalArgumentException("Not a square: \"" + algebraic + "\"");
        }
        return of(file, rank);
    }

    /**
     * @return true if this file and rank are both in 0-7
     */
    public static boolean isOnBoard(int file, int rank) {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }

    /**
     * @return 0-63: a1 is 0, h1 is 7, a2 is 8, ... h8 is 63
     */
    public int index() {
        return rank * 8 + file;
    }

    /**
     * @return the square this many files and ranks away, or null if that's off the board
     */
    public Square offset(int fileDelta, int rankDelta) {
        int newFile = file + fileDelta;
        int newRank = rank + rankDelta;
        return isOnBoard(newFile, newRank) ? ALL[newRank * 8 + newFile] : null;
    }

    /**
     * @return the square's name in algebraic notation, e.g. "e4"
     */
    @Override
    public String toString() {
        return "" + (char) ('a' + file) + (char) ('1' + rank);
    }
}
