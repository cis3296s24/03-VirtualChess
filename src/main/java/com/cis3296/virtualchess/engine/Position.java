package com.cis3296.virtualchess.engine;

import java.util.Arrays;
import java.util.Objects;

/**
 * A complete, immutable snapshot of a chess game at one moment: where every piece is, whose turn it is,
 * castling rights, the en passant square, and the move counters. This is everything a FEN string holds.
 * <p>
 * {@link #apply(Move)} returns a new position instead of changing this one, so trying a move out (for
 * example, "would this leave my king in check?") never disturbs the real game.
 */
public final class Position {

    /** FEN for the standard starting position. */
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    static final int[][] KNIGHT_OFFSETS = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
    static final int[][] KING_OFFSETS = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    static final int[][] ROOK_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    static final int[][] BISHOP_DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    // Indexed by Square.index(); null means empty
    private final Piece[] board;
    private final Color sideToMove;
    private final CastlingRights castlingRights;
    private final Square enPassantTarget;
    private final int halfmoveClock;
    private final int fullmoveNumber;

    private Position(Piece[] board, Color sideToMove, CastlingRights castlingRights,
                     Square enPassantTarget, int halfmoveClock, int fullmoveNumber) {
        this.board = board;
        this.sideToMove = sideToMove;
        this.castlingRights = castlingRights;
        this.enPassantTarget = enPassantTarget;
        this.halfmoveClock = halfmoveClock;
        this.fullmoveNumber = fullmoveNumber;
    }

    /**
     * @return the standard starting position
     */
    public static Position startingPosition() {
        return fromFen(STARTING_FEN);
    }

    /**
     * Parses a FEN string. The last two fields (halfmove clock and fullmove number) are optional and default
     * to 0 and 1.
     *
     * @param fen e.g. "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
     * @return the position it describes
     * @throws IllegalArgumentException if the FEN is malformed
     */
    public static Position fromFen(String fen) {
        String[] fields = fen.trim().split("\\s+");
        if (fields.length < 4 || fields.length > 6) {
            throw new IllegalArgumentException("FEN needs 4 to 6 fields: \"" + fen + "\"");
        }

        Piece[] board = parsePlacement(fields[0], fen);

        Color sideToMove = switch (fields[1]) {
            case "w" -> Color.WHITE;
            case "b" -> Color.BLACK;
            default -> throw new IllegalArgumentException("Side to move must be w or b: \"" + fen + "\"");
        };
        CastlingRights castling = CastlingRights.fromFen(fields[2]);
        Square enPassant = fields[3].equals("-") ? null : Square.parse(fields[3]);

        int halfmove;
        int fullmove;
        try {
            halfmove = fields.length > 4 ? Integer.parseInt(fields[4]) : 0;
            fullmove = fields.length > 5 ? Integer.parseInt(fields[5]) : 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Move counters must be numbers: \"" + fen + "\"", e);
        }

        return new Position(board, sideToMove, castling, enPassant, halfmove, fullmove);
    }

    private static Piece[] parsePlacement(String placement, String fen) {
        String[] rows = placement.split("/");
        if (rows.length != 8) {
            throw new IllegalArgumentException("FEN board needs 8 ranks: \"" + fen + "\"");
        }
        Piece[] board = new Piece[64];
        for (int row = 0; row < 8; row++) {
            int rank = 7 - row; // FEN lists rank 8 first
            int file = 0;
            for (char c : rows[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    file += c - '0';
                } else {
                    if (file > 7) {
                        throw new IllegalArgumentException("Too many squares on rank " + (rank + 1) + ": \"" + fen + "\"");
                    }
                    board[Square.of(file, rank).index()] = Piece.fromFenChar(c);
                    file++;
                }
            }
            if (file != 8) {
                throw new IllegalArgumentException("Rank " + (rank + 1) + " doesn't have 8 squares: \"" + fen + "\"");
            }
        }
        return board;
    }

    /**
     * @return this position as a FEN string
     */
    public String toFen() {
        StringBuilder fen = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            int empty = 0;
            for (int file = 0; file < 8; file++) {
                Piece piece = board[rank * 8 + file];
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(piece.fenChar());
                }
            }
            if (empty > 0) {
                fen.append(empty);
            }
            if (rank > 0) {
                fen.append('/');
            }
        }
        fen.append(sideToMove == Color.WHITE ? " w " : " b ")
                .append(castlingRights.toFen()).append(' ')
                .append(enPassantTarget == null ? "-" : enPassantTarget.toString()).append(' ')
                .append(halfmoveClock).append(' ')
                .append(fullmoveNumber);
        return fen.toString();
    }

    /**
     * @return the piece on this square, or null if it's empty
     */
    public Piece pieceAt(Square square) {
        return board[square.index()];
    }

    public Color sideToMove() {
        return sideToMove;
    }

    public CastlingRights castlingRights() {
        return castlingRights;
    }

    /**
     * @return the square a pawn could capture onto en passant (the square the last double-stepping pawn
     * skipped over), or null if the last move wasn't a pawn double step
     */
    public Square enPassantTarget() {
        return enPassantTarget;
    }

    /**
     * @return moves since the last capture or pawn move, for the fifty-move rule
     */
    public int halfmoveClock() {
        return halfmoveClock;
    }

    /**
     * @return the current move number, starting at 1 and increasing after each Black move
     */
    public int fullmoveNumber() {
        return fullmoveNumber;
    }

    /**
     * @return where this side's king is, or null if it has none (only possible in hand-made test positions)
     */
    public Square kingSquare(Color color) {
        Piece king = Piece.of(color, PieceType.KING);
        for (int index = 0; index < 64; index++) {
            if (board[index] == king) {
                return Square.fromIndex(index);
            }
        }
        return null;
    }

    /**
     * @return true if this side's king is currently attacked
     */
    public boolean isInCheck(Color color) {
        Square king = kingSquare(color);
        return king != null && isAttacked(king, color.opposite());
    }

    /**
     * @return true if the side to move is in check
     */
    public boolean isInCheck() {
        return isInCheck(sideToMove);
    }

    /**
     * Checks whether any of the attacker's pieces could capture on this square. This works the same whether
     * the square is empty or occupied, and it looks along open lines, so a king can't "escape" by stepping
     * backward along a rook's line of attack.
     *
     * @param square   the square to test
     * @param attacker the side doing the attacking
     */
    public boolean isAttacked(Square square, Color attacker) {
        // Pawns: an attacking pawn sits one rank "behind" the square (from the attacker's point of view)
        int behind = -attacker.pawnDirection();
        Piece pawn = Piece.of(attacker, PieceType.PAWN);
        if (pieceOn(square.offset(-1, behind)) == pawn || pieceOn(square.offset(1, behind)) == pawn) {
            return true;
        }

        if (attackedFromOffsets(square, KNIGHT_OFFSETS, Piece.of(attacker, PieceType.KNIGHT))
                || attackedFromOffsets(square, KING_OFFSETS, Piece.of(attacker, PieceType.KING))) {
            return true;
        }

        Piece queen = Piece.of(attacker, PieceType.QUEEN);
        return attackedAlongLines(square, ROOK_DIRECTIONS, Piece.of(attacker, PieceType.ROOK), queen)
                || attackedAlongLines(square, BISHOP_DIRECTIONS, Piece.of(attacker, PieceType.BISHOP), queen);
    }

    private boolean attackedFromOffsets(Square square, int[][] offsets, Piece attacker) {
        for (int[] offset : offsets) {
            if (pieceOn(square.offset(offset[0], offset[1])) == attacker) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedAlongLines(Square square, int[][] directions, Piece slider, Piece queen) {
        for (int[] direction : directions) {
            Square current = square.offset(direction[0], direction[1]);
            while (current != null) {
                Piece piece = board[current.index()];
                if (piece != null) {
                    if (piece == slider || piece == queen) {
                        return true;
                    }
                    break; // anything else blocks the line
                }
                current = current.offset(direction[0], direction[1]);
            }
        }
        return false;
    }

    private Piece pieceOn(Square square) {
        return square == null ? null : board[square.index()];
    }

    /**
     * Plays a move and returns the resulting position. This position is not changed.
     * <p>
     * This assumes the move is at least pseudo-legal (the piece can physically make it). It does not check
     * whether the move leaves the mover's king in check; use {@link MoveGenerator#legalMoves(Position)} to
     * get only legal moves.
     *
     * @param move the move to play
     * @return the position after the move, with the other side to move
     * @throws IllegalArgumentException if there is no piece of the side to move on the from square
     */
    public Position apply(Move move) {
        Square from = move.from();
        Square to = move.to();
        Piece moving = board[from.index()];
        if (moving == null || moving.color() != sideToMove) {
            throw new IllegalArgumentException("No " + sideToMove + " piece on " + from + " for move " + move);
        }

        Piece[] next = board.clone();
        Piece captured = next[to.index()];
        next[from.index()] = null;
        next[to.index()] = move.isPromotion() ? Piece.of(sideToMove, move.promotion()) : moving;

        boolean isPawn = moving.type() == PieceType.PAWN;

        // En passant: the captured pawn is beside the from square, not on the to square
        if (isPawn && to.equals(enPassantTarget)) {
            Square capturedPawn = Square.of(to.file(), from.rank());
            captured = next[capturedPawn.index()];
            next[capturedPawn.index()] = null;
        }

        // Castling: the king moves two files, so move the rook to the square it jumped over
        if (moving.type() == PieceType.KING && Math.abs(to.file() - from.file()) == 2) {
            boolean kingside = to.file() > from.file();
            Square rookFrom = Square.of(kingside ? 7 : 0, from.rank());
            Square rookTo = Square.of(kingside ? 5 : 3, from.rank());
            next[rookTo.index()] = next[rookFrom.index()];
            next[rookFrom.index()] = null;
        }

        CastlingRights rights = castlingRights.withoutRookOn(from).withoutRookOn(to);
        if (moving.type() == PieceType.KING) {
            rights = rights.withoutAll(sideToMove);
        }

        Square newEnPassant = null;
        if (isPawn && Math.abs(to.rank() - from.rank()) == 2) {
            newEnPassant = Square.of(from.file(), (from.rank() + to.rank()) / 2);
        }

        int newHalfmove = (isPawn || captured != null) ? 0 : halfmoveClock + 1;
        int newFullmove = sideToMove == Color.BLACK ? fullmoveNumber + 1 : fullmoveNumber;

        return new Position(next, sideToMove.opposite(), rights, newEnPassant, newHalfmove, newFullmove);
    }

    /**
     * Checks whether neither side can possibly checkmate, no matter how badly the other plays. That covers
     * king vs king, king and one bishop or knight vs king, and positions where every remaining piece besides
     * the kings is a bishop on the same color of square.
     * <p>
     * Positions like king and knight vs king and knight return false: mate is impossible to force, but it
     * can still happen if one side blunders, so under FIDE rules the game goes on.
     */
    public boolean hasInsufficientMaterial() {
        int minorPieces = 0;
        boolean onlyBishops = true;
        boolean bishopOnLightSquare = false;
        boolean bishopOnDarkSquare = false;
        for (Square square : Square.ALL_SQUARES) {
            Piece piece = board[square.index()];
            if (piece == null || piece.type() == PieceType.KING) {
                continue;
            }
            switch (piece.type()) {
                case PAWN, ROOK, QUEEN -> {
                    return false;
                }
                case KNIGHT -> {
                    minorPieces++;
                    onlyBishops = false;
                }
                case BISHOP -> {
                    minorPieces++;
                    if ((square.file() + square.rank()) % 2 == 0) {
                        bishopOnDarkSquare = true; // a1 is a dark square
                    } else {
                        bishopOnLightSquare = true;
                    }
                }
                default -> throw new IllegalStateException("Unexpected piece " + piece);
            }
        }
        return minorPieces <= 1 || (onlyBishops && !(bishopOnLightSquare && bishopOnDarkSquare));
    }

    /**
     * Two positions are equal if every field matches, including the move counters.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position that)) {
            return false;
        }
        return halfmoveClock == that.halfmoveClock
                && fullmoveNumber == that.fullmoveNumber
                && sideToMove == that.sideToMove
                && castlingRights.equals(that.castlingRights)
                && Objects.equals(enPassantTarget, that.enPassantTarget)
                && Arrays.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(board), sideToMove, castlingRights, enPassantTarget,
                halfmoveClock, fullmoveNumber);
    }

    /**
     * @return the FEN string, which is handy in test failure messages and the debugger
     */
    @Override
    public String toString() {
        return toFen();
    }
}
