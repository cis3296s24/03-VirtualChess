package com.cis3296.virtualchess;

import com.cis3296.virtualchess.engine.Move;
import com.cis3296.virtualchess.engine.MoveGenerator;
import com.cis3296.virtualchess.engine.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Stockfish failures must be reported through return values, never thrown, so the game can fall back to two
 * players. Most cases use small shell scripts as fake engines, so they run without Stockfish installed.
 */
class StockfishTest {

    @TempDir
    Path dir;

    @Test
    void missingEngineFailsToStartWithoutThrowing() {
        Stockfish engine = new Stockfish(dir.resolve("no-such-engine"));
        assertFalse(engine.startEngine());
        assertFalse(engine.isRunning());
        assertNull(engine.getBestMove(Position.STARTING_FEN, 10));
        assertDoesNotThrow(() -> engine.sendCommand("isready"));
        assertDoesNotThrow(engine::stopEngine);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void nonExecutableEngineFailsToStart() throws IOException {
        Path file = Files.writeString(dir.resolve("engine"), "not a program");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-r--r--"));
        assertFalse(new Stockfish(file).startEngine());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void engineThatCrashesAtStartupFailsToStart() throws IOException {
        // Like a Stockfish build that needs CPU features this machine lacks
        Stockfish engine = new Stockfish(fakeEngine("exit 4"));
        assertFalse(engine.startEngine());
        assertFalse(engine.isRunning());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void engineThatCrashesWhileThinkingReturnsNoMove() throws IOException {
        Stockfish engine = new Stockfish(fakeEngine("""
                while read line; do
                  case "$line" in
                    uci) echo "id name FakeFish"; echo "uciok" ;;
                    isready) echo "readyok" ;;
                    go*) exit 3 ;;
                  esac
                done
                """));
        assertTrue(engine.startEngine());
        assertNull(engine.getBestMove(Position.STARTING_FEN, 10));
        assertFalse(engine.isRunning(), "a crashed engine should be reported as stopped");
        assertDoesNotThrow(engine::stopEngine);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void engineThatIgnoresQuitIsStoppedAnyway() throws IOException {
        Stockfish engine = new Stockfish(fakeEngine("""
                while read line; do
                  case "$line" in
                    uci) echo "uciok" ;;
                    quit) sleep 30 ;;
                  esac
                done
                """));
        assertTrue(engine.startEngine());
        long start = System.nanoTime();
        engine.stopEngine();
        assertTrue((System.nanoTime() - start) / 1_000_000 < 5_000, "stopEngine should give up waiting and kill it");
        assertFalse(engine.isRunning());
    }

    /**
     * Only runs if the real Stockfish is set up (see "Playing Against Stockfish" in the README).
     */
    @Test
    void realStockfishPlaysALegalMove() {
        assumeTrue(Files.isExecutable(Stockfish.defaultExecutable()), "Stockfish isn't installed in ./stockfish");
        Stockfish engine = new Stockfish();
        try {
            assertTrue(engine.startEngine());
            Position position = Position.fromFen("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
            String move = engine.getBestMove(position.toFen(), 100);
            assertTrue(move != null && MoveGenerator.isLegal(position, Move.parse(move)), "got " + move);
        } finally {
            engine.stopEngine();
        }
    }

    private Path fakeEngine(String script) throws IOException {
        Path file = Files.writeString(dir.resolve("fake-engine.sh"), "#!/bin/sh\n" + script);
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rwxr-xr-x"));
        return file;
    }
}
