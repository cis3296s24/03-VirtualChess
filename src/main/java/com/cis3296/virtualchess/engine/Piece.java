package com.cis3296.virtualchess.engine;

/**
 * A chess piece: a type and a color. Pieces have no position; the {@link Position} knows where they are.
 *
 * @param color the side the piece belongs to
 * @param type  what kind of piece it is
 */
public record Piece(Color color, PieceType type) {

    private static final Piece[][] CACHE = new Piece[Color.values().length][PieceType.values().length];

    static {
        for (Color color : Color.values()) {
            for (PieceType type : PieceType.values()) {
                CACHE[color.ordinal()][type.ordinal()] = new Piece(color, type);
            }
        }
    }

    /**
     * @return the shared instance for this color and type
     */
    public static Piece of(Color color, PieceType type) {
        return CACHE[color.ordinal()][type.ordinal()];
    }

    /**
     * @param fenChar a FEN piece letter: uppercase for white, lowercase for black (e.g. 'K', 'p')
     * @return the matching piece
     */
    public static Piece fromFenChar(char fenChar) {
        Color color = Character.isUpperCase(fenChar) ? Color.WHITE : Color.BLACK;
        return of(color, PieceType.fromSymbol(fenChar));
    }

    /**
     * @return the FEN letter for this piece: uppercase for white, lowercase for black
     */
    public char fenChar() {
        return color == Color.WHITE ? Character.toUpperCase(type.symbol()) : type.symbol();
    }
}
