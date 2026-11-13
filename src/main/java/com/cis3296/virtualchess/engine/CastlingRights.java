package com.cis3296.virtualchess.engine;

/**
 * Which castling moves are still allowed. A right is lost permanently once the king or that rook moves, or
 * that rook is captured. Having the right doesn't mean castling is legal right now: the squares between must
 * be empty and the king can't be in, pass through, or land on an attacked square.
 *
 * @param whiteKingside  White may still castle short (e1g1)
 * @param whiteQueenside White may still castle long (e1c1)
 * @param blackKingside  Black may still castle short (e8g8)
 * @param blackQueenside Black may still castle long (e8c8)
 */
public record CastlingRights(boolean whiteKingside, boolean whiteQueenside,
                             boolean blackKingside, boolean blackQueenside) {

    public static final CastlingRights ALL = new CastlingRights(true, true, true, true);
    public static final CastlingRights NONE = new CastlingRights(false, false, false, false);

    /**
     * @param fen the castling field of a FEN string, e.g. "KQkq", "Kq", or "-"
     */
    public static CastlingRights fromFen(String fen) {
        if (fen.equals("-")) {
            return NONE;
        }
        if (!fen.matches("K?Q?k?q?") || fen.isEmpty()) {
            throw new IllegalArgumentException("Invalid castling field: \"" + fen + "\"");
        }
        return new CastlingRights(fen.contains("K"), fen.contains("Q"), fen.contains("k"), fen.contains("q"));
    }

    /**
     * @return the castling field for a FEN string, e.g. "KQkq", or "-" if no rights remain
     */
    public String toFen() {
        String fen = (whiteKingside ? "K" : "") + (whiteQueenside ? "Q" : "")
                + (blackKingside ? "k" : "") + (blackQueenside ? "q" : "");
        return fen.isEmpty() ? "-" : fen;
    }

    public boolean kingside(Color color) {
        return color == Color.WHITE ? whiteKingside : blackKingside;
    }

    public boolean queenside(Color color) {
        return color == Color.WHITE ? whiteQueenside : blackQueenside;
    }

    /**
     * @return these rights with both of one side's rights removed (used when that side's king moves)
     */
    public CastlingRights withoutAll(Color color) {
        return color == Color.WHITE
                ? new CastlingRights(false, false, blackKingside, blackQueenside)
                : new CastlingRights(whiteKingside, whiteQueenside, false, false);
    }

    /**
     * @return these rights with any right tied to a rook starting on this square removed. Used whenever a
     * move starts or ends on a corner, which covers both a rook moving and a rook being captured.
     */
    public CastlingRights withoutRookOn(Square square) {
        return new CastlingRights(
                whiteKingside && !square.equals(WHITE_KINGSIDE_ROOK),
                whiteQueenside && !square.equals(WHITE_QUEENSIDE_ROOK),
                blackKingside && !square.equals(BLACK_KINGSIDE_ROOK),
                blackQueenside && !square.equals(BLACK_QUEENSIDE_ROOK));
    }

    private static final Square WHITE_KINGSIDE_ROOK = Square.of(7, 0);
    private static final Square WHITE_QUEENSIDE_ROOK = Square.of(0, 0);
    private static final Square BLACK_KINGSIDE_ROOK = Square.of(7, 7);
    private static final Square BLACK_QUEENSIDE_ROOK = Square.of(0, 7);
}
