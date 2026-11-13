package com.cis3296.virtualchess.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotationTest {

    static String san(String fen, String uci) {
        return Notation.toSan(Position.fromFen(fen), Move.parse(uci));
    }

    static String sanAfter(String uci, String... movesBefore) {
        return Notation.toSan(RulesTest.play(movesBefore), Move.parse(uci));
    }

    @Test
    void pawnAndPieceMoves() {
        assertEquals("e4", sanAfter("e2e4"));
        assertEquals("Nf3", sanAfter("g1f3"));
    }

    @Test
    void captures() {
        assertEquals("exd5", sanAfter("e4d5", "e2e4", "d7d5"));
        assertEquals("Bxf7+", sanAfter("c4f7", "e2e4", "e7e5", "f1c4", "a7a6"));
    }

    @Test
    void enPassantIsWrittenAsAPawnCapture() {
        assertEquals("exd6", sanAfter("e5d6", "e2e4", "a7a6", "e4e5", "d7d5"));
    }

    @Test
    void castling() {
        String fen = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        assertEquals("O-O", san(fen, "e1g1"));
        assertEquals("O-O-O", san(fen, "e1c1"));
    }

    @Test
    void disambiguatesByFile() {
        // Knights on b1 and f3 can both reach d2
        String fen = "4k3/8/8/8/8/5N2/8/1N2K3 w - - 0 1";
        assertEquals("Nbd2", san(fen, "b1d2"));
        assertEquals("Nfd2", san(fen, "f3d2"));
    }

    @Test
    void disambiguatesByRankWhenTheFileIsShared() {
        // Rooks on a1 and a5 can both reach a3
        String fen = "4k3/8/8/R7/8/8/8/R3K3 w - - 0 1";
        assertEquals("R1a3", san(fen, "a1a3"));
        assertEquals("R5a3", san(fen, "a5a3"));
    }

    @Test
    void disambiguatesByFullSquareWhenNeitherIsEnough() {
        // Queens on e4, h4, and h1 can all reach e1
        String fen = "8/8/1k6/8/4Q2Q/8/8/K6Q w - - 0 1";
        assertEquals("Qh4e1", san(fen, "h4e1"));
        assertEquals("Qee1", san(fen, "e4e1"));
        assertEquals("Q1e1", san(fen, "h1e1"));
    }

    @Test
    void pinnedPieceDoesNotCauseDisambiguation() {
        // Knights on c4 and f1 can both reach d2, but the c4 knight is pinned to the king on e4 by the rook
        // on a4. Only one knight can legally go there, so no disambiguation is needed.
        String fen = "4k3/8/8/8/r1N1K3/8/8/5N2 w - - 0 1";
        assertEquals("Nd2", san(fen, "f1d2"));
    }

    @Test
    void promotion() {
        String fen = "1k6/4P3/8/8/8/8/8/4K3 w - - 0 1";
        assertEquals("e8=Q+", san(fen, "e7e8q"));
        assertEquals("e8=N", san(fen, "e7e8n"));
    }

    @Test
    void checkmateGetsAHash() {
        assertEquals("Qh4#", sanAfter("d8h4", "f2f3", "e7e5", "g2g4"));
    }

    @Test
    void rejectsIllegalMoves() {
        assertThrows(IllegalArgumentException.class, () -> sanAfter("e2e5"));
    }
}
