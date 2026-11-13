package com.cis3296.virtualchess.engine;

/**
 * A move from one square to another, using the same information as UCI notation (what Stockfish speaks).
 * <p>
 * Castling is written as the king's move (e.g. e1g1), and en passant as the capturing pawn's move. The
 * {@link Position} works out the rest (moving the rook, removing the captured pawn).
 *
 * @param from      where the piece starts
 * @param to        where it ends up
 * @param promotion the piece a pawn promotes to, or null for any other move
 */
public record Move(Square from, Square to, PieceType promotion) {

    public Move {
        if (from == null || to == null) {
            throw new IllegalArgumentException("A move needs both a from and a to square");
        }
        if (promotion == PieceType.PAWN || promotion == PieceType.KING) {
            throw new IllegalArgumentException("Can't promote to " + promotion);
        }
    }

    /**
     * Creates a move that isn't a promotion.
     */
    public Move(Square from, Square to) {
        this(from, to, null);
    }

    /**
     * @param uci a move in UCI notation, e.g. "e2e4" or "e7e8q"
     * @return the parsed move
     * @throws IllegalArgumentException if the text isn't a valid UCI move
     */
    public static Move parse(String uci) {
        if (uci == null || (uci.length() != 4 && uci.length() != 5)) {
            throw new IllegalArgumentException("Not a UCI move: \"" + uci + "\"");
        }
        Square from = Square.parse(uci.substring(0, 2));
        Square to = Square.parse(uci.substring(2, 4));
        PieceType promotion = uci.length() == 5 ? PieceType.fromSymbol(uci.charAt(4)) : null;
        return new Move(from, to, promotion);
    }

    /**
     * @return true if this move promotes a pawn
     */
    public boolean isPromotion() {
        return promotion != null;
    }

    /**
     * @return the move in UCI notation, e.g. "e2e4" or "e7e8q"
     */
    @Override
    public String toString() {
        return from.toString() + to + (promotion == null ? "" : String.valueOf(promotion.symbol()));
    }
}
