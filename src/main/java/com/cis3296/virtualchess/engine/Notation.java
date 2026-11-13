package com.cis3296.virtualchess.engine;

/**
 * Converts moves to standard algebraic notation (SAN), the way moves are written on score sheets and shown
 * in chess apps: "e4", "Nf3", "exd5", "O-O", "Rad1", "e8=Q+", "Qxf7#".
 */
public final class Notation {

    private Notation() {
    }

    /**
     * @param before the position the move is played from
     * @param move   a legal move in that position
     * @return the move in SAN
     * @throws IllegalArgumentException if the move isn't legal in this position
     */
    public static String toSan(Position before, Move move) {
        if (!MoveGenerator.isLegal(before, move)) {
            throw new IllegalArgumentException("Illegal move " + move + " in " + before.toFen());
        }
        Piece piece = before.pieceAt(move.from());
        StringBuilder san = new StringBuilder();

        if (piece.type() == PieceType.KING && Math.abs(move.to().file() - move.from().file()) == 2) {
            san.append(move.to().file() > move.from().file() ? "O-O" : "O-O-O");
        } else {
            boolean isCapture = before.pieceAt(move.to()) != null
                    || (piece.type() == PieceType.PAWN && move.to().equals(before.enPassantTarget()));

            if (piece.type() == PieceType.PAWN) {
                if (isCapture) {
                    san.append(fileLetter(move.from()));
                }
            } else {
                san.append(Character.toUpperCase(piece.type().symbol()));
                san.append(disambiguation(before, move, piece));
            }
            if (isCapture) {
                san.append('x');
            }
            san.append(move.to());
            if (move.isPromotion()) {
                san.append('=').append(Character.toUpperCase(move.promotion().symbol()));
            }
        }

        Position after = before.apply(move);
        if (after.isInCheck()) {
            san.append(MoveGenerator.legalMoves(after).isEmpty() ? '#' : '+');
        }
        return san.toString();
    }

    /**
     * When another piece of the same type could also move to the same square, SAN adds the from-file (Nbd2),
     * or the from-rank if the file doesn't settle it (R1a3), or both if neither does (Qh4e1).
     */
    private static String disambiguation(Position before, Move move, Piece piece) {
        boolean ambiguous = false;
        boolean sameFile = false;
        boolean sameRank = false;
        for (Move other : MoveGenerator.legalMoves(before)) {
            if (other.to().equals(move.to())
                    && !other.from().equals(move.from())
                    && before.pieceAt(other.from()) == piece) {
                ambiguous = true;
                sameFile |= other.from().file() == move.from().file();
                sameRank |= other.from().rank() == move.from().rank();
            }
        }
        if (!ambiguous) {
            return "";
        }
        if (!sameFile) {
            return String.valueOf(fileLetter(move.from()));
        }
        if (!sameRank) {
            return String.valueOf(rankDigit(move.from()));
        }
        return move.from().toString();
    }

    private static char fileLetter(Square square) {
        return (char) ('a' + square.file());
    }

    private static char rankDigit(Square square) {
        return (char) ('1' + square.rank());
    }
}
