package com.cis3296.virtualchess.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Counts material: the standard piece values (pawn 1, knight 3, bishop 3, rook 5, queen 9), who is ahead,
 * and which pieces each side has captured.
 */
public final class Material {

    // Sorts captured pieces into groups, cheapest first, the way they're usually shown beside a board
    private static final Comparator<Piece> BY_VALUE =
            Comparator.comparingInt((Piece piece) -> value(piece.type())).thenComparing(Piece::type);

    private Material() {
    }

    /**
     * @return the piece's value in pawns; the king counts as 0 since it can never be traded
     */
    public static int value(PieceType type) {
        return switch (type) {
            case PAWN -> 1;
            case KNIGHT, BISHOP -> 3;
            case ROOK -> 5;
            case QUEEN -> 9;
            case KING -> 0;
        };
    }

    /**
     * @return the total value of this side's pieces on the board
     */
    public static int total(Position position, Color side) {
        int total = 0;
        for (Square square : Square.ALL_SQUARES) {
            Piece piece = position.pieceAt(square);
            if (piece != null && piece.color() == side) {
                total += value(piece.type());
            }
        }
        return total;
    }

    /**
     * Who is ahead, counting what's on the board (so a promoted pawn counts as the piece it became).
     *
     * @return how many points White is ahead: positive if White leads, negative if Black does
     */
    public static int balance(Position position) {
        return total(position, Color.WHITE) - total(position, Color.BLACK);
    }

    /**
     * Replays a game to find every piece that was captured, including pawns taken en passant.
     *
     * @param start the position the game started from
     * @param moves the moves played since, all legal
     * @return the captured pieces, cheapest first (pawns, then knights, bishops, rooks, queens)
     */
    public static List<Piece> captured(Position start, List<Move> moves) {
        List<Piece> captured = new ArrayList<>();
        Position position = start;
        for (Move move : moves) {
            Piece target = position.pieceAt(move.to());
            Piece moving = position.pieceAt(move.from());
            if (target != null) {
                captured.add(target);
            } else if (moving != null && moving.type() == PieceType.PAWN && move.to().equals(position.enPassantTarget())) {
                captured.add(Piece.of(moving.color().opposite(), PieceType.PAWN));
            }
            position = position.apply(move);
        }
        captured.sort(BY_VALUE);
        return captured;
    }

    /**
     * @return the pieces {@code capturer} has taken from the opponent, cheapest first
     */
    public static List<Piece> capturedBy(Color capturer, Position start, List<Move> moves) {
        return captured(start, moves).stream().filter(piece -> piece.color() != capturer).toList();
    }
}
