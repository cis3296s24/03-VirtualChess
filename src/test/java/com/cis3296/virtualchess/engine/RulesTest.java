package com.cis3296.virtualchess.engine;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Targeted rule checks. The first group reproduces bugs found in the original Board/Piece code, so each one
 * is guarded against for good.
 */
class RulesTest {

    /**
     * Plays a sequence of UCI moves from the starting position, failing if any of them is illegal.
     */
    static Position play(String... moves) {
        return playFrom(Position.startingPosition(), moves);
    }

    static Position playFrom(Position position, String... moves) {
        for (String uci : moves) {
            Move move = Move.parse(uci);
            assertTrue(MoveGenerator.isLegal(position, move),
                    uci + " should be legal in " + position.toFen());
            position = position.apply(move);
        }
        return position;
    }

    static List<String> legalMoveStrings(Position position) {
        return MoveGenerator.legalMoves(position).stream().map(Move::toString).sorted().toList();
    }

    @Nested
    class BugsFromTheOriginalCode {

        @Test
        void cannotIgnoreCheck() {
            // 1.e4 f5 2.Qh5+ : the only way out is to block with g6
            Position position = play("e2e4", "f7f5", "d1h5");
            assertTrue(position.isInCheck());
            assertEquals(List.of("g7g6"), legalMoveStrings(position));
        }

        @Test
        void pawnCaptureOnlyRemovesTheCapturedPiece() {
            // The old code also deleted whatever stood beside the capturing pawn's start square (here, d5)
            Position position = play("e2e4", "d7d5", "e4e5", "d8d6", "e5d6");
            assertEquals("rnb1kbnr/ppp1pppp/3P4/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3", position.toFen());
        }

        @Test
        void enPassantOnlyRightAfterADoubleStep() {
            // The pawn reaches e4 in two single steps, so d4xe3 en passant is not allowed
            Position position = play("e2e3", "d7d5", "a2a3", "d5d4", "e3e4");
            assertNull(position.enPassantTarget());
            assertFalse(MoveGenerator.isLegal(position, Move.parse("d4e3")));
        }

        @Test
        void enPassantCapturesThePawnThatDoubleStepped() {
            Position position = play("e2e4", "a7a6", "e4e5", "d7d5", "e5d6");
            assertEquals("rnbqkbnr/1pp1pppp/p2P4/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3", position.toFen());
        }

        @Test
        void enPassantExpiresAfterOneMove() {
            Position position = play("e2e4", "a7a6", "e4e5", "d7d5", "h2h3", "h7h6");
            assertFalse(MoveGenerator.isLegal(position, Move.parse("e5d6")));
        }

        @Test
        void kingsCannotStandNextToEachOther() {
            // The old code overflowed the stack here instead of rejecting Kd5
            Position position = play("e2e3", "e7e6", "e1e2", "e8e7", "e2d3", "e7d6", "d3d4");
            assertFalse(MoveGenerator.isLegal(position, Move.parse("d6d5")));
        }

        @Test
        void kingCannotStepBackAlongTheLineOfAttack() {
            // Rook on a1 checks the king on d1 along the rank; e1 is still on that line
            Position position = Position.fromFen("4k3/8/8/8/8/8/8/r2K4 w - - 0 1");
            assertTrue(position.isInCheck());
            assertFalse(MoveGenerator.isLegal(position, Move.parse("d1e1")));
            assertTrue(MoveGenerator.isLegal(position, Move.parse("d1d2")));
        }

        @Test
        void pinnedPieceCannotMove() {
            // White knight on e2 is pinned to the king by the rook on e8
            Position position = Position.fromFen("4r1k1/8/8/8/8/8/4N3/4K3 w - - 0 1");
            assertTrue(MoveGenerator.legalMovesFrom(position, Square.parse("e2")).isEmpty());
        }
    }

    @Nested
    class Castling {

        static final String ROOKS_HOME = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";

        @Test
        void bothSidesWhenTheWayIsClear() {
            Position position = Position.fromFen(ROOKS_HOME);
            assertTrue(MoveGenerator.isLegal(position, Move.parse("e1g1")));
            assertTrue(MoveGenerator.isLegal(position, Move.parse("e1c1")));
        }

        @Test
        void movesTheRookToo() {
            Position position = Position.fromFen(ROOKS_HOME).apply(Move.parse("e1g1"));
            assertEquals("r3k2r/8/8/8/8/8/8/R4RK1 b kq - 1 1", position.toFen());
        }

        @Test
        void notThroughAnAttackedSquare() {
            // Black rook on f2 attacks f1, which the king would cross castling kingside
            Position position = Position.fromFen("r3k2r/8/8/8/8/8/5r2/R3K2R w KQkq - 0 1");
            assertFalse(MoveGenerator.isLegal(position, Move.parse("e1g1")));
            assertTrue(MoveGenerator.isLegal(position, Move.parse("e1c1")));
        }

        @Test
        void queensideAllowedWhenOnlyTheRooksPathIsAttacked() {
            // b1 is attacked, but only the rook crosses b1, so castling long is still legal
            Position position = Position.fromFen("r3k2r/8/8/8/8/8/1r6/R3K2R w KQkq - 0 1");
            assertTrue(MoveGenerator.isLegal(position, Move.parse("e1c1")));
        }

        @Test
        void notOutOfCheck() {
            Position position = Position.fromFen("r3k2r/8/8/8/8/8/4r3/R3K2R w KQkq - 0 1");
            assertFalse(MoveGenerator.isLegal(position, Move.parse("e1g1")));
            assertFalse(MoveGenerator.isLegal(position, Move.parse("e1c1")));
        }

        @Test
        void rightsLostWhenTheRookMovesAway() {
            // White's kingside rook and Black's queenside rook go out and come back: those rights stay lost
            Position position = playFrom(Position.fromFen(ROOKS_HOME), "h1h2", "a8a7", "h2h1", "a7a8");
            assertEquals("Qk", position.castlingRights().toFen());
            assertFalse(MoveGenerator.isLegal(position, Move.parse("e1g1")));
        }

        @Test
        void rightsLostWhenTheRookIsCaptured() {
            Position position = playFrom(Position.fromFen(ROOKS_HOME), "a1a8");
            assertEquals("Kk", position.castlingRights().toFen());
        }
    }

    @Nested
    class Promotion {

        @Test
        void offersAllFourPieces() {
            // (The white king can only go to b1: a2 and b2 are next to the black king on a3)
            Position position = Position.fromFen("8/4P3/8/8/8/k7/8/K7 w - - 0 1");
            assertEquals(List.of("a1b1", "e7e8b", "e7e8n", "e7e8q", "e7e8r"), legalMoveStrings(position));
        }

        @Test
        void replacesThePawn() {
            Position position = Position.fromFen("8/4P3/8/8/8/k7/8/K7 w - - 0 1").apply(Move.parse("e7e8n"));
            assertEquals("4N3/8/8/8/8/k7/8/K7 b - - 0 1", position.toFen());
        }
    }

    @Nested
    class GameEnd {

        @Test
        void foolsMateIsCheckmate() {
            Position position = play("f2f3", "e7e5", "g2g4", "d8h4");
            assertTrue(position.isInCheck());
            assertTrue(MoveGenerator.legalMoves(position).isEmpty());
        }

        @Test
        void stalemateHasNoMovesButNoCheck() {
            Position position = Position.fromFen("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1");
            assertFalse(position.isInCheck());
            assertTrue(MoveGenerator.legalMoves(position).isEmpty());
        }
    }

    @Nested
    class MoveCounters {

        @Test
        void halfmoveClockResetsOnPawnMovesAndCaptures() {
            Position position = play("g1f3", "g8f6");
            assertEquals(2, position.halfmoveClock());
            assertEquals(0, position.apply(Move.parse("e2e4")).halfmoveClock());
        }

        @Test
        void fullmoveNumberIncreasesAfterBlackMoves() {
            assertEquals(1, play("e2e4").fullmoveNumber());
            assertEquals(2, play("e2e4", "e7e5").fullmoveNumber());
        }
    }

    @Nested
    class Fen {

        @Test
        void roundTripsTheTestPositions() {
            for (String fen : List.of(PerftTest.START, PerftTest.KIWIPETE, PerftTest.POSITION_3,
                    PerftTest.POSITION_4, PerftTest.POSITION_5, PerftTest.POSITION_6)) {
                assertEquals(fen, Position.fromFen(fen).toFen());
            }
        }

        @Test
        void recordsTheEnPassantSquareAfterADoubleStep() {
            assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", play("e2e4").toFen());
        }

        @Test
        void moveCountersAreOptional() {
            assertEquals(Position.startingPosition(),
                    Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -"));
        }

        @Test
        void rejectsMalformedFen() {
            assertThrows(IllegalArgumentException.class, () -> Position.fromFen("not a fen"));
            assertThrows(IllegalArgumentException.class, () -> Position.fromFen("8/8/8/8/8/8/8 w - - 0 1"));
            assertThrows(IllegalArgumentException.class, () -> Position.fromFen("9/8/8/8/8/8/8/8 w - - 0 1"));
            assertThrows(IllegalArgumentException.class, () -> Position.fromFen("8/8/8/8/8/8/8/8 x - - 0 1"));
            assertThrows(IllegalArgumentException.class, () -> Position.fromFen("8/8/8/8/8/8/8/8 w KX - 0 1"));
        }
    }
}
