package com.cis3296.virtualchess.engine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialTest {

    private static ChessGame gameAfter(String... moves) {
        return ChessGameTest.gameAfter(moves);
    }

    private static List<Piece> capturedBy(Color side, ChessGame game) {
        return Material.capturedBy(side, game.startingPosition(), game.moves());
    }

    @Test
    void startingPositionIsEven() {
        Position start = Position.startingPosition();
        assertEquals(39, Material.total(start, Color.WHITE));
        assertEquals(39, Material.total(start, Color.BLACK));
        assertEquals(0, Material.balance(start));
        assertTrue(Material.captured(start, List.of()).isEmpty());
    }

    @Test
    void captureShowsUpForTheCapturerAndInTheBalance() {
        // 1. e4 d5 2. exd5: White wins a pawn
        ChessGame game = gameAfter("e2e4", "d7d5", "e4d5");
        assertEquals(1, Material.balance(game.position()));
        assertEquals(List.of(Piece.of(Color.BLACK, PieceType.PAWN)), capturedBy(Color.WHITE, game));
        assertTrue(capturedBy(Color.BLACK, game).isEmpty());

        // 2... Qxd5: even again, each side has taken a pawn
        game.play("d8d5");
        assertEquals(0, Material.balance(game.position()));
        assertEquals(List.of(Piece.of(Color.WHITE, PieceType.PAWN)), capturedBy(Color.BLACK, game));
    }

    @Test
    void blackAheadIsNegative() {
        // 1. e4 e5 2. Qh5 Nf6 3. Qxf7+?? Kxf7
        ChessGame game = gameAfter("e2e4", "e7e5", "d1h5", "g8f6", "h5f7", "e8f7");
        assertEquals(-8, Material.balance(game.position())); // White won a pawn, Black won the queen
        assertEquals(List.of(Piece.of(Color.WHITE, PieceType.QUEEN)), capturedBy(Color.BLACK, game));
        assertEquals(List.of(Piece.of(Color.BLACK, PieceType.PAWN)), capturedBy(Color.WHITE, game));
    }

    @Test
    void enPassantCountsAsCapturingAPawn() {
        ChessGame game = gameAfter("e2e4", "a7a6", "e4e5", "d7d5", "e5d6");
        assertEquals(List.of(Piece.of(Color.BLACK, PieceType.PAWN)), capturedBy(Color.WHITE, game));
        assertEquals(1, Material.balance(game.position()));
    }

    @Test
    void promotionCountsAsTheNewPiece() {
        ChessGame game = new ChessGame(Position.fromFen("8/4P3/8/8/8/k7/8/K7 w - - 0 1"));
        assertEquals(1, Material.balance(game.position()));
        game.play("e7e8q");
        assertEquals(9, Material.balance(game.position()));
        assertTrue(Material.captured(game.startingPosition(), game.moves()).isEmpty());
    }

    @Test
    void capturedPiecesAreListedCheapestFirst() {
        // The White queen takes a rook, then a pawn, then a knight
        ChessGame game = new ChessGame(Position.fromFen("r3k3/1p6/2n5/8/8/8/8/Q3K3 w - - 0 1"));
        game.play("a1a8"); // Qxa8+ (rook)
        game.play("e8e7");
        game.play("a8b7"); // Qxb7+ (pawn)
        game.play("e7e6");
        game.play("b7c6"); // Qxc6+ (knight)
        assertEquals(List.of(Piece.of(Color.BLACK, PieceType.PAWN), Piece.of(Color.BLACK, PieceType.KNIGHT),
                Piece.of(Color.BLACK, PieceType.ROOK)), capturedBy(Color.WHITE, game));
        assertEquals(9, Material.balance(game.position()));
    }
}
