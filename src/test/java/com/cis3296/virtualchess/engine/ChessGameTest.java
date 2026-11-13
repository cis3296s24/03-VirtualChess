package com.cis3296.virtualchess.engine;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessGameTest {

    static ChessGame gameAfter(String... moves) {
        ChessGame game = new ChessGame();
        for (String move : moves) {
            game.play(move);
        }
        return game;
    }

    static ChessGame gameFrom(String fen) {
        return new ChessGame(Position.fromFen(fen));
    }

    @Nested
    class PlayingMoves {

        @Test
        void newGameIsInProgressWithTwentyMoves() {
            ChessGame game = new ChessGame();
            assertFalse(game.isOver());
            assertNull(game.result());
            assertEquals(Color.WHITE, game.sideToMove());
            assertEquals(20, game.legalMoves().size());
        }

        @Test
        void rejectsIllegalMovesWithoutChangingTheGame() {
            ChessGame game = new ChessGame();
            assertThrows(IllegalArgumentException.class, () -> game.play("e2e5"));
            assertThrows(IllegalArgumentException.class, () -> game.play("e7e5")); // not Black's turn
            assertEquals(Position.startingPosition(), game.position());
            assertTrue(game.moves().isEmpty());
        }

        @Test
        void promotionNeedsAPieceChoice() {
            ChessGame game = gameFrom("8/4P3/8/8/8/k7/8/K7 w - - 0 1");
            assertTrue(game.isPromotion(Square.parse("e7"), Square.parse("e8")));
            assertFalse(game.isPromotion(Square.parse("a1"), Square.parse("b1")));
            assertThrows(IllegalArgumentException.class, () -> game.play("e7e8"));
            game.play("e7e8q");
            assertEquals(Piece.of(Color.WHITE, PieceType.QUEEN), game.position().pieceAt(Square.parse("e8")));
        }

        @Test
        void recordsMovesInSan() {
            assertEquals(List.of("e4", "e5", "Nf3", "Nc6", "Bb5"),
                    gameAfter("e2e4", "e7e5", "g1f3", "b8c6", "f1b5").movesInSan());
        }
    }

    @Nested
    class Undo {

        @Test
        void restoresThePreviousPosition() {
            ChessGame game = gameAfter("e2e4", "e7e5");
            Position beforeLastMove = gameAfter("e2e4").position();
            assertEquals(Move.parse("e7e5"), game.undo());
            assertEquals(beforeLastMove, game.position());
            assertEquals(List.of(Move.parse("e2e4")), game.moves());
        }

        @Test
        void canTakeBackCheckmate() {
            ChessGame game = gameAfter("f2f3", "e7e5", "g2g4", "d8h4");
            assertTrue(game.isOver());
            game.undo();
            assertFalse(game.isOver());
            assertEquals(Color.BLACK, game.sideToMove());
        }

        @Test
        void canTakeBackPastAResignation() {
            ChessGame game = gameAfter("e2e4");
            game.resign(Color.BLACK);
            game.undo();
            assertFalse(game.isOver());
        }

        @Test
        void nothingToUndoAtTheStart() {
            ChessGame game = new ChessGame();
            assertFalse(game.canUndo());
            assertThrows(IllegalStateException.class, game::undo);
        }
    }

    @Nested
    class GameEnd {

        @Test
        void checkmate() {
            ChessGame game = gameAfter("f2f3", "e7e5", "g2g4", "d8h4");
            assertEquals(GameResult.win(Color.BLACK, GameResult.Reason.CHECKMATE), game.result());
            assertEquals("0-1", game.result().score());
            assertEquals("Black wins by checkmate", game.result().describe());
            assertTrue(game.legalMoves().isEmpty());
            assertThrows(IllegalStateException.class, () -> game.play("a2a3"));
        }

        @Test
        void stalemate() {
            ChessGame game = gameFrom("7k/8/5QK1/8/8/8/8/8 w - - 0 1");
            game.play("f6f7");
            assertEquals(GameResult.draw(GameResult.Reason.STALEMATE), game.result());
            assertEquals("1/2-1/2", game.result().score());
        }

        @Test
        void insufficientMaterialAfterTheLastPieceIsCaptured() {
            ChessGame game = gameFrom("4k3/8/8/8/8/8/3q4/4K3 w - - 0 1");
            assertFalse(game.isOver());
            game.play("e1d2");
            assertEquals(GameResult.draw(GameResult.Reason.INSUFFICIENT_MATERIAL), game.result());
        }

        @Test
        void fiftyMoveRule() {
            ChessGame game = gameFrom("4k3/8/8/8/8/8/8/R3K3 w - - 99 60");
            assertFalse(game.isOver());
            game.play("a1a2");
            assertEquals(GameResult.draw(GameResult.Reason.FIFTY_MOVE_RULE), game.result());
        }

        @Test
        void checkmateOnTheFiftiethMoveStillCounts() {
            ChessGame game = gameFrom("7k/8/6K1/8/8/8/8/R7 w - - 99 60");
            game.play("a1a8");
            assertEquals(GameResult.win(Color.WHITE, GameResult.Reason.CHECKMATE), game.result());
        }

        @Test
        void threefoldRepetition() {
            ChessGame game = new ChessGame();
            String[] shuffle = {"g1f3", "g8f6", "f3g1", "f6g8"};
            for (String move : shuffle) {
                game.play(move);
            }
            for (int i = 0; i < 3; i++) {
                game.play(shuffle[i]);
            }
            assertFalse(game.isOver(), "starting position has only appeared twice so far");
            game.play(shuffle[3]);
            assertEquals(GameResult.draw(GameResult.Reason.THREEFOLD_REPETITION), game.result());
        }

        @Test
        void repetitionIgnoresAnEnPassantSquareNobodyCanUse() {
            // After 1.e4 the FEN records e3 as the en passant square, but no black pawn can capture there,
            // so the position after 3.Ng1 (no en passant square) is a repeat of it
            ChessGame game = gameAfter("e2e4", "g8f6", "g1f3", "f6g8", "f3g1", "g8f6", "g1f3", "f6g8");
            assertFalse(game.isOver());
            game.play("f3g1");
            assertEquals(GameResult.draw(GameResult.Reason.THREEFOLD_REPETITION), game.result());
        }

        @Test
        void resignation() {
            ChessGame game = gameAfter("e2e4");
            game.resign(Color.WHITE);
            assertEquals("Black wins by resignation", game.result().describe());
            assertThrows(IllegalStateException.class, () -> game.resign(Color.BLACK));
        }

        @Test
        void timeout() {
            ChessGame game = new ChessGame();
            game.timeout(Color.WHITE);
            assertEquals(GameResult.win(Color.BLACK, GameResult.Reason.TIMEOUT), game.result());
        }

        @Test
        void timeoutIsADrawWhenTheOtherSideHasOnlyAKing() {
            ChessGame game = gameFrom("4k3/8/8/8/8/8/8/R3K3 w - - 0 1");
            game.timeout(Color.WHITE);
            assertEquals(GameResult.draw(GameResult.Reason.TIMEOUT_VS_INSUFFICIENT_MATERIAL), game.result());
        }

        @Test
        void drawByAgreement() {
            ChessGame game = gameAfter("e2e4", "e7e5");
            game.agreeDraw();
            assertEquals("Draw by agreement", game.result().describe());
        }
    }

    @Nested
    class InsufficientMaterial {

        @Test
        void loneKings() {
            assertTrue(Position.fromFen("4k3/8/8/8/8/8/8/4K3 w - - 0 1").hasInsufficientMaterial());
        }

        @Test
        void singleMinorPiece() {
            assertTrue(Position.fromFen("4k3/8/8/8/8/8/8/2B1K3 w - - 0 1").hasInsufficientMaterial());
            assertTrue(Position.fromFen("4k3/8/8/8/8/8/8/1N2K3 w - - 0 1").hasInsufficientMaterial());
        }

        @Test
        void bishopsAllOnTheSameColor() {
            // c1 and f8 are both dark squares
            assertTrue(Position.fromFen("4kb2/8/8/8/8/8/8/2B1K3 w - - 0 1").hasInsufficientMaterial());
        }

        @Test
        void bishopsOnBothColorsCanStillMate() {
            // c1 is dark, f1 is light
            assertFalse(Position.fromFen("4k3/8/8/8/8/8/8/2B1KB2 w - - 0 1").hasInsufficientMaterial());
        }

        @Test
        void anyPawnRookOrQueenIsEnough() {
            assertFalse(Position.fromFen("4k3/8/8/8/8/8/4P3/4K3 w - - 0 1").hasInsufficientMaterial());
            assertFalse(Position.fromFen("4k3/8/8/8/8/8/8/R3K3 w - - 0 1").hasInsufficientMaterial());
        }

        @Test
        void knightAgainstKnightIsNotADeadPosition() {
            assertFalse(Position.fromFen("4kn2/8/8/8/8/8/8/1N2K3 w - - 0 1").hasInsufficientMaterial());
        }
    }
}
