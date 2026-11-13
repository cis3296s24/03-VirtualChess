package com.cis3296.virtualchess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Runs the Stockfish chess engine as a separate process and talks to it over UCI (the standard text
 * protocol for chess engines).
 * <p>
 * Originally from Rahul A R on GitHub; Nick Rucinski cleaned it up and extended it.
 * <p>
 * Every failure (engine missing, not executable, crashed, stopped responding) is logged and reported through
 * return values ({@link #startEngine()} returns false, {@link #getBestMove} returns null) rather than thrown,
 * so the game can fall back to two players.
 */
public class Stockfish {

    private static final Logger log = LoggerFactory.getLogger(Stockfish.class);
    private static final long QUIT_TIMEOUT_MILLIS = 1000;

    private final Path executable;
    private Process engineProcess;
    private BufferedReader processReader;
    private Writer processWriter;
    private boolean running;

    /**
     * Uses the Stockfish build for this operating system from the "stockfish" folder in the directory the app
     * was started from (see "Playing Against Stockfish" in the README).
     */
    public Stockfish() {
        this(defaultExecutable());
    }

    /**
     * @param executable the Stockfish program to run
     */
    public Stockfish(Path executable) {
        this.executable = executable;
    }

    /**
     * @return where the app looks for Stockfish on this operating system
     */
    public static Path defaultExecutable() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String name;
        if (os.contains("mac")) {
            name = "stockfish-macos-x86-64-avx2";
        } else if (os.contains("win")) {
            name = "stockfish-windows-x86-64-avx2.exe";
        } else {
            name = "stockfish-ubuntu-x86-64-avx2";
        }
        return Path.of("stockfish", name);
    }

    /**
     * Starts the engine and checks that it answers like a UCI chess engine.
     *
     * @return true if the engine is running and ready; false if it couldn't be started (the reason is logged)
     */
    public boolean startEngine() {
        Path path = executable.toAbsolutePath();
        if (!Files.exists(path)) {
            log.warn("Stockfish not found at {}. See \"Playing Against Stockfish\" in the README.", path);
            return false;
        }
        if (!Files.isExecutable(path)) {
            log.warn("Stockfish at {} isn't executable. On Linux or macOS, fix it with: chmod +x \"{}\"", path, path);
            return false;
        }
        try {
            engineProcess = new ProcessBuilder(path.toString()).redirectErrorStream(true).start();
        } catch (IOException e) {
            log.warn("Couldn't start Stockfish at {}: {}", path, e.getMessage());
            return false;
        }
        processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream(), StandardCharsets.UTF_8));
        processWriter = new OutputStreamWriter(engineProcess.getOutputStream(), StandardCharsets.UTF_8);
        running = true;

        // UCI handshake: the engine introduces itself and ends with "uciok"
        sendCommand("uci");
        String engineName = null;
        String line;
        while ((line = readLine()) != null && !line.equals("uciok")) {
            if (line.startsWith("id name ")) {
                engineName = line.substring("id name ".length());
            }
        }
        if (line == null) {
            log.warn("Stockfish at {} exited during startup (it may not support this CPU; the bundled builds need AVX2)", path);
            stopEngine();
            return false;
        }
        log.info("Started {} from {}", engineName == null ? "chess engine" : engineName, path);
        return true;
    }

    /**
     * @return true if the engine was started and hasn't stopped or crashed since
     */
    public boolean isRunning() {
        return running && engineProcess != null && engineProcess.isAlive();
    }

    /**
     * Asks the engine to quit and cleans up, killing the process if it doesn't exit in time. Safe to call
     * even if the engine never started.
     */
    public void stopEngine() {
        if (engineProcess == null) {
            return;
        }
        if (running && engineProcess.isAlive()) {
            sendCommand("quit");
        }
        running = false;
        try {
            if (!engineProcess.waitFor(QUIT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                log.warn("Stockfish didn't quit within {} ms; stopping it", QUIT_TIMEOUT_MILLIS);
                engineProcess.destroyForcibly();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            engineProcess.destroyForcibly();
        }
        closeQuietly();
        log.debug("Stockfish stopped");
    }

    /**
     * Sends any UCI command (a newline is added). If the engine has stopped, logs it and does nothing.
     *
     * @param command A UCI command to be sent to stockfish
     */
    public void sendCommand(String command) {
        if (!running) {
            log.warn("Not sending \"{}\": Stockfish isn't running", command);
            return;
        }
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
            log.debug("Stockfish << {}", command);
        } catch (IOException e) {
            engineStopped("sending \"" + command + "\"", e);
        }
    }

    /**
     * Tells the engine a new game is starting
     */
    public void setUCINewGame() {
        sendCommand("ucinewgame");
    }

    /**
     * Asks the engine for its move in a position.
     *
     * @param fen      the position, as a full FEN string
     * @param waitTime how long the engine may think, in milliseconds
     * @return the move in UCI notation (e.g. "e2e4", or "e7e8q" for a promotion), or null if the engine
     * isn't running or didn't answer (the reason is logged)
     */
    public String getBestMove(String fen, int waitTime) {
        if (!isRunning()) {
            log.warn("Can't get a move: Stockfish isn't running");
            return null;
        }
        sendCommand("position fen " + fen);
        sendCommand("go depth 1 movetime " + waitTime);

        String output = getOutput(waitTime + 20);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && !parts[1].equals("(none)")) {
                    log.debug("Stockfish >> {}", line);
                    return parts[1];
                }
            }
        }
        if (isRunning()) { // if it crashed, that's already been logged
            log.warn("Stockfish gave no move for {}. Its output was:\n{}", fen,
                    output.isBlank() ? "(nothing)" : output.strip());
        }
        return null;
    }

    /**
     * Waits, then reads everything the engine has printed. Uses the "isready"/"readyok" exchange to know
     * when the engine has finished.
     *
     * @param waitTime milliseconds to wait before reading, to give a long-running command time to finish
     * @return the engine's output, possibly incomplete if it stopped responding
     */
    public String getOutput(int waitTime) {
        StringBuilder buffer = new StringBuilder();
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return buffer.toString();
        }
        sendCommand("isready");
        String line;
        while ((line = readLine()) != null && !line.equals("readyok")) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    // One line from the engine, or null if it has exited (in which case it's marked as no longer running)
    private String readLine() {
        if (!running) {
            return null;
        }
        try {
            String line = processReader.readLine();
            if (line == null) {
                engineStopped("reading its output", null);
            }
            return line;
        } catch (IOException e) {
            engineStopped("reading its output", e);
            return null;
        }
    }

    /**
     * Records that the engine has gone away. Logged once, as a single line: a closed pipe is the expected
     * symptom of a crashed process, not something that needs a stack trace.
     */
    private void engineStopped(String whileDoing, IOException cause) {
        if (!running) {
            return;
        }
        running = false;
        String exit = "";
        try {
            if (engineProcess.waitFor(200, TimeUnit.MILLISECONDS)) {
                exit = " with exit code " + engineProcess.exitValue();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.error("Stockfish stopped unexpectedly{} while {}{}", exit, whileDoing,
                cause == null ? "" : " (" + cause.getMessage() + ")");
    }

    private void closeQuietly() {
        try {
            processReader.close();
        } catch (IOException e) {
            log.debug("Problem closing Stockfish's output: {}", e.getMessage());
        }
        try {
            processWriter.close();
        } catch (IOException e) {
            log.debug("Problem closing Stockfish's input: {}", e.getMessage());
        }
    }
}
