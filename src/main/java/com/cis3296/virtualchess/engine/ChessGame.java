package com.cis3296.virtualchess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A game in progress: the moves played so far, whose turn it is, and whether it has ended.
 * <p>
 * Only legal moves are accepted. After every move the game checks whether it's over: checkmate, stalemate,
 * insufficient material, the fifty-move rule, or threefold repetition. Draws that the rules let a player
 * <i>claim</i> (fifty moves, threefold repetition) are applied automatically here, so nobody needs to press
 * a "claim draw" button.
 * <p>
 * Clocks live outside this class. When a player runs out of time, call {@link #timeout(Color)}.
 */
public final class ChessGame {

    // positions.get(0) is the starting position; positions.get(i + 1) is the result of moves.get(i)
    private final List<Position> positions = new ArrayList<>();
    private final List<Move> moves = new ArrayList<>();
    // For threefold repetition, kept in step with positions
    private final List<String> repetitionKeys = new ArrayList<>();
    private GameResult result;

    /**
     * Starts a game from the standard starting position.
     */
    public ChessGame() {
        this(Position.startingPosition());
    }

    /**
     * Starts a game from any position, e.g. one loaded from FEN.
     */
    public ChessGame(Position start) {
        positions.add(start);
        repetitionKeys.add(repetitionKey(start));
        result = detectResult();
    }

    /**
     * @return the current position
     */
    public Position position() {
        return positions.getLast();
    }

    /**
     * @return the position the game started from
     */
    public Position startingPosition() {
        return positions.getFirst();
    }

    /**
     * @return the moves played so far, oldest first (read-only)
     */
    public List<Move> moves() {
        return Collections.unmodifiableList(moves);
    }

    /**
     * @return the moves played so far in standard algebraic notation, e.g. ["e4", "e5", "Nf3"]
     */
    public List<String> movesInSan() {
        List<String> san = new ArrayList<>(moves.size());
        for (int i = 0; i < moves.size(); i++) {
            san.add(Notation.toSan(positions.get(i), moves.get(i)));
        }
        return san;
    }

    public Color sideToMove() {
        return position().sideToMove();
    }

    /**
     * @return every legal move for the side to move, or an empty list if the game is over
     */
    public List<Move> legalMoves() {
        return isOver() ? List.of() : MoveGenerator.legalMoves(position());
    }

    /**
     * @return the legal moves for the piece on this square, or an empty list if the game is over
     */
    public List<Move> legalMovesFrom(Square from) {
        return isOver() ? List.of() : MoveGenerator.legalMovesFrom(position(), from);
    }

    public boolean isLegal(Move move) {
        return legalMoves().contains(move);
    }

    /**
     * Checks whether moving from one square to another is a legal pawn promotion, meaning the UI needs to
     * ask the player which piece they want before calling {@link #play(Move)}.
     */
    public boolean isPromotion(Square from, Square to) {
        for (Move move : legalMovesFrom(from)) {
            if (move.to().equals(to) && move.isPromotion()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Plays a move.
     *
     * @throws IllegalStateException    if the game is already over
     * @throws IllegalArgumentException if the move isn't legal (including a promotion without a piece choice)
     */
    public void play(Move move) {
        if (isOver()) {
            throw new IllegalStateException("The game is over: " + result.describe());
        }
        if (!MoveGenerator.isLegal(position(), move)) {
            throw new IllegalArgumentException("Illegal move " + move + " in " + position().toFen());
        }
        Position next = position().apply(move);
        moves.add(move);
        positions.add(next);
        repetitionKeys.add(repetitionKey(next));
        result = detectResult();
    }

    /**
     * Plays a move given in UCI notation, e.g. "e2e4" or "e7e8q" (handy for Stockfish moves and tests).
     */
    public void play(String uci) {
        play(Move.parse(uci));
    }

    public boolean canUndo() {
        return !moves.isEmpty();
    }

    /**
     * Takes back the last move. Any result is recalculated, so undoing out of checkmate, or past a
     * resignation or timeout, puts the game back in progress.
     *
     * @return the move that was taken back
     * @throws IllegalStateException if no moves have been played
     */
    public Move undo() {
        if (!canUndo()) {
            throw new IllegalStateException("No moves to undo");
        }
        positions.removeLast();
        repetitionKeys.removeLast();
        Move undone = moves.removeLast();
        result = detectResult();
        return undone;
    }

    /**
     * Ends the game with this side resigning.
     */
    public void resign(Color loser) {
        requireInProgress();
        result = GameResult.win(loser.opposite(), GameResult.Reason.RESIGNATION);
    }

    /**
     * Ends the game because this side ran out of time. Following FIDE rules, it's a draw instead of a loss
     * if the other side has only a king left, since a lone king can never deliver checkmate.
     */
    public void timeout(Color loser) {
        requireInProgress();
        Color winner = loser.opposite();
        result = hasOnlyKing(position(), winner)
                ? GameResult.draw(GameResult.Reason.TIMEOUT_VS_INSUFFICIENT_MATERIAL)
                : GameResult.win(winner, GameResult.Reason.TIMEOUT);
    }

    /**
     * Ends the game as a draw both players agreed to.
     */
    public void agreeDraw() {
        requireInProgress();
        result = GameResult.draw(GameResult.Reason.AGREEMENT);
    }

    /**
     * @return how the game ended, or null if it's still in progress
     */
    public GameResult result() {
        return result;
    }

    public boolean isOver() {
        return result != null;
    }

    private void requireInProgress() {
        if (isOver()) {
            throw new IllegalStateException("The game is already over: " + result.describe());
        }
    }

    private GameResult detectResult() {
        Position position = position();
        // Checkmate and stalemate come first: a mate delivered on the 50th move still counts as mate
        if (MoveGenerator.legalMoves(position).isEmpty()) {
            return position.isInCheck()
                    ? GameResult.win(position.sideToMove().opposite(), GameResult.Reason.CHECKMATE)
                    : GameResult.draw(GameResult.Reason.STALEMATE);
        }
        if (position.hasInsufficientMaterial()) {
            return GameResult.draw(GameResult.Reason.INSUFFICIENT_MATERIAL);
        }
        if (position.halfmoveClock() >= 100) {
            return GameResult.draw(GameResult.Reason.FIFTY_MOVE_RULE);
        }
        if (Collections.frequency(repetitionKeys, repetitionKeys.getLast()) >= 3) {
            return GameResult.draw(GameResult.Reason.THREEFOLD_REPETITION);
        }
        return null;
    }

    /**
     * Positions count as repeated when the pieces, side to move, castling rights, and en passant
     * possibilities are the same. The move counters don't matter, and an en passant square only counts if
     * a pawn can actually capture there (otherwise every double step would make the position "different").
     */
    private static String repetitionKey(Position position) {
        String[] fen = position.toFen().split(" ");
        Square enPassant = position.enPassantTarget();
        boolean enPassantPossible = enPassant != null && MoveGenerator.legalMoves(position).stream()
                .anyMatch(move -> move.to().equals(enPassant)
                        && position.pieceAt(move.from()).type() == PieceType.PAWN);
        return fen[0] + ' ' + fen[1] + ' ' + fen[2] + ' ' + (enPassantPossible ? fen[3] : "-");
    }

    private static boolean hasOnlyKing(Position position, Color color) {
        for (Square square : Square.ALL_SQUARES) {
            Piece piece = position.pieceAt(square);
            if (piece != null && piece.color() == color && piece.type() != PieceType.KING) {
                return false;
            }
        }
        return true;
    }
}
