package com.cis3296.virtualchess.engine;

/**
 * The six kinds of chess piece, independent of color.
 */
public enum PieceType {
    PAWN('p'),
    KNIGHT('n'),
    BISHOP('b'),
    ROOK('r'),
    QUEEN('q'),
    KING('k');

    private final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the lowercase letter used for this piece in FEN and UCI notation (e.g. 'n' for knight)
     */
    public char symbol() {
        return symbol;
    }

    /**
     * @param symbol a piece letter in either case (e.g. 'N' or 'n')
     * @return the matching piece type
     * @throws IllegalArgumentException if the letter isn't a piece
     */
    public static PieceType fromSymbol(char symbol) {
        char lower = Character.toLowerCase(symbol);
        for (PieceType type : values()) {
            if (type.symbol == lower) {
                return type;
            }
        }
        throw new IllegalArgumentException("Not a piece symbol: '" + symbol + "'");
    }
}
