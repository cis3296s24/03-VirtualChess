package com.cis3296.virtualchess.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Works out which moves are legal in a position.
 * <p>
 * It first lists every move the pieces could physically make ("pseudo-legal" moves), then throws out any
 * move that would leave the mover's own king in check. That one filter handles pinned pieces, getting out
 * of check, and kings walking next to each other, so none of those need special cases.
 */
public final class MoveGenerator {

    private static final PieceType[] PROMOTION_CHOICES =
            {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

    private MoveGenerator() {
    }

    /**
     * @return every legal move for the side to move. Empty means checkmate (if in check) or stalemate.
     */
    public static List<Move> legalMoves(Position position) {
        List<Move> legal = new ArrayList<>();
        Color mover = position.sideToMove();
        for (Move move : pseudoLegalMoves(position)) {
            if (!position.apply(move).isInCheck(mover)) {
                legal.add(move);
            }
        }
        return legal;
    }

    /**
     * @return the legal moves for the piece on this square (empty if the square is empty or holds the
     * opponent's piece). Useful for showing move hints in the UI.
     */
    public static List<Move> legalMovesFrom(Position position, Square from) {
        List<Move> fromSquare = new ArrayList<>();
        for (Move move : legalMoves(position)) {
            if (move.from().equals(from)) {
                fromSquare.add(move);
            }
        }
        return fromSquare;
    }

    /**
     * @return true if this exact move (including promotion choice) is legal
     */
    public static boolean isLegal(Position position, Move move) {
        return legalMoves(position).contains(move);
    }

    /**
     * Every move the side to move's pieces could physically make, ignoring whether it leaves their own king
     * in check. Castling is the exception: it's only generated when the king isn't in check and doesn't pass
     * through an attacked square, since those rules aren't about where the king ends up.
     */
    static List<Move> pseudoLegalMoves(Position position) {
        List<Move> moves = new ArrayList<>();
        Color mover = position.sideToMove();
        for (Square from : Square.ALL_SQUARES) {
            Piece piece = position.pieceAt(from);
            if (piece == null || piece.color() != mover) {
                continue;
            }
            switch (piece.type()) {
                case PAWN -> addPawnMoves(position, from, mover, moves);
                case KNIGHT -> addStepMoves(position, from, mover, Position.KNIGHT_OFFSETS, moves);
                case BISHOP -> addSlidingMoves(position, from, mover, Position.BISHOP_DIRECTIONS, moves);
                case ROOK -> addSlidingMoves(position, from, mover, Position.ROOK_DIRECTIONS, moves);
                case QUEEN -> {
                    addSlidingMoves(position, from, mover, Position.ROOK_DIRECTIONS, moves);
                    addSlidingMoves(position, from, mover, Position.BISHOP_DIRECTIONS, moves);
                }
                case KING -> {
                    addStepMoves(position, from, mover, Position.KING_OFFSETS, moves);
                    addCastlingMoves(position, from, mover, moves);
                }
            }
        }
        return moves;
    }

    private static void addPawnMoves(Position position, Square from, Color mover, List<Move> moves) {
        int direction = mover.pawnDirection();
        int startRank = mover == Color.WHITE ? 1 : 6;

        // One step forward, and two from the starting rank, only onto empty squares
        Square oneStep = from.offset(0, direction);
        if (oneStep != null && position.pieceAt(oneStep) == null) {
            addPawnMove(from, oneStep, moves);
            Square twoSteps = oneStep.offset(0, direction);
            if (from.rank() == startRank && twoSteps != null && position.pieceAt(twoSteps) == null) {
                moves.add(new Move(from, twoSteps));
            }
        }

        // Diagonal captures, including en passant onto the empty square a pawn just skipped
        for (int fileDelta : new int[]{-1, 1}) {
            Square target = from.offset(fileDelta, direction);
            if (target == null) {
                continue;
            }
            Piece occupant = position.pieceAt(target);
            boolean capturesPiece = occupant != null && occupant.color() != mover;
            if (capturesPiece || target.equals(position.enPassantTarget())) {
                addPawnMove(from, target, moves);
            }
        }
    }

    // Reaching the last rank means promoting, and each promotion choice is a separate move
    private static void addPawnMove(Square from, Square to, List<Move> moves) {
        if (to.rank() == 0 || to.rank() == 7) {
            for (PieceType choice : PROMOTION_CHOICES) {
                moves.add(new Move(from, to, choice));
            }
        } else {
            moves.add(new Move(from, to));
        }
    }

    private static void addStepMoves(Position position, Square from, Color mover, int[][] offsets, List<Move> moves) {
        for (int[] offset : offsets) {
            Square to = from.offset(offset[0], offset[1]);
            if (to != null && !isOwnPiece(position, to, mover)) {
                moves.add(new Move(from, to));
            }
        }
    }

    private static void addSlidingMoves(Position position, Square from, Color mover, int[][] directions, List<Move> moves) {
        for (int[] direction : directions) {
            Square to = from.offset(direction[0], direction[1]);
            while (to != null) {
                Piece occupant = position.pieceAt(to);
                if (occupant == null) {
                    moves.add(new Move(from, to));
                } else {
                    if (occupant.color() != mover) {
                        moves.add(new Move(from, to)); // capture, then stop
                    }
                    break;
                }
                to = to.offset(direction[0], direction[1]);
            }
        }
    }

    private static void addCastlingMoves(Position position, Square from, Color mover, List<Move> moves) {
        int backRank = mover == Color.WHITE ? 0 : 7;
        CastlingRights rights = position.castlingRights();
        if (!from.equals(Square.of(4, backRank)) || (!rights.kingside(mover) && !rights.queenside(mover))) {
            return;
        }
        Color enemy = mover.opposite();
        if (position.isAttacked(from, enemy)) {
            return; // can't castle out of check
        }
        Piece ownRook = Piece.of(mover, PieceType.ROOK);

        // Kingside: f and g files empty, king doesn't pass through an attacked f-square
        if (rights.kingside(mover)
                && position.pieceAt(Square.of(7, backRank)) == ownRook
                && isEmpty(position, backRank, 5, 6)
                && !position.isAttacked(Square.of(5, backRank), enemy)) {
            moves.add(new Move(from, Square.of(6, backRank)));
        }

        // Queenside: b, c, and d files empty (only the rook crosses b), king doesn't pass through d
        if (rights.queenside(mover)
                && position.pieceAt(Square.of(0, backRank)) == ownRook
                && isEmpty(position, backRank, 1, 3)
                && !position.isAttacked(Square.of(3, backRank), enemy)) {
            moves.add(new Move(from, Square.of(2, backRank)));
        }
    }

    private static boolean isEmpty(Position position, int rank, int firstFile, int lastFile) {
        for (int file = firstFile; file <= lastFile; file++) {
            if (position.pieceAt(Square.of(file, rank)) != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isOwnPiece(Position position, Square square, Color mover) {
        Piece occupant = position.pieceAt(square);
        return occupant != null && occupant.color() == mover;
    }
}
