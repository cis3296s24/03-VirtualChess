package com.cis3296.virtualchess.engine;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * "Perft" (performance test) counts every sequence of legal moves to a fixed depth and compares the total
 * with known-correct values. One missing or extra move anywhere in the tree changes the count, so matching
 * these numbers is strong evidence the move generator handles every rule correctly.
 * <p>
 * Positions and expected counts are the standard ones from https://www.chessprogramming.org/Perft_Results
 */
class PerftTest {

    static final String START = Position.STARTING_FEN;
    // "Kiwipete": castling both ways, en passant, promotions, pins, and discovered checks
    static final String KIWIPETE = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
    // Sparse endgame: en passant that would expose the king along a rank, and checks along open lines
    static final String POSITION_3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
    // Black to castle, promotions with capture, and a pinned pawn
    static final String POSITION_4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
    // Promotion with check, and a position reached mid-game (move counters not at the start)
    static final String POSITION_5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
    // A quiet, symmetrical middlegame with lots of pieces
    static final String POSITION_6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10";

    @ParameterizedTest(name = "{0} at depth {1} = {2}")
    @CsvSource({
            "START,      1, 20",
            "START,      2, 400",
            "START,      3, 8902",
            "START,      4, 197281",
            "START,      5, 4865609",
            "KIWIPETE,   1, 48",
            "KIWIPETE,   2, 2039",
            "KIWIPETE,   3, 97862",
            "KIWIPETE,   4, 4085603",
            "POSITION_3, 1, 14",
            "POSITION_3, 2, 191",
            "POSITION_3, 3, 2812",
            "POSITION_3, 4, 43238",
            "POSITION_3, 5, 674624",
            "POSITION_4, 1, 6",
            "POSITION_4, 2, 264",
            "POSITION_4, 3, 9467",
            "POSITION_4, 4, 422333",
            "POSITION_5, 1, 44",
            "POSITION_5, 2, 1486",
            "POSITION_5, 3, 62379",
            "POSITION_5, 4, 2103487",
            "POSITION_6, 1, 46",
            "POSITION_6, 2, 2079",
            "POSITION_6, 3, 89890",
            "POSITION_6, 4, 3894594",
    })
    void moveCountsMatchKnownValues(String positionName, int depth, long expected) {
        Position position = Position.fromFen(fenFor(positionName));
        assertEquals(expected, perft(position, depth));
    }

    /**
     * @return the number of distinct legal move sequences of exactly this many moves
     */
    static long perft(Position position, int depth) {
        var moves = MoveGenerator.legalMoves(position);
        if (depth == 1) {
            return moves.size();
        }
        long total = 0;
        for (Move move : moves) {
            total += perft(position.apply(move), depth - 1);
        }
        return total;
    }

    private static String fenFor(String name) {
        return switch (name) {
            case "START" -> START;
            case "KIWIPETE" -> KIWIPETE;
            case "POSITION_3" -> POSITION_3;
            case "POSITION_4" -> POSITION_4;
            case "POSITION_5" -> POSITION_5;
            case "POSITION_6" -> POSITION_6;
            default -> throw new IllegalArgumentException("Unknown test position " + name);
        };
    }
}
