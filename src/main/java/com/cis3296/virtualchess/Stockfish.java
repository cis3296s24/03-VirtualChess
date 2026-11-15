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
import java.util.ArrayList;
import java.util.List;
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
     * Uses the Stockfish found by {@link #defaultExecutable()}.
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
     * System property that names the Stockfish program to use, overriding the search in
     * {@link #defaultExecutable()}.
     */
    public static final String EXECUTABLE_PROPERTY = "virtualchess.stockfish";

    /**
     * Finds the Stockfish program to run, trying in order: the file named by the
     * {@value #EXECUTABLE_PROPERTY} system property, the engine bundled with the packaged app (a
     * {@code stockfish} folder beside the app's jar or up to two folders above it, where each OS's package
     * layout puts it), {@code stockfish/stockfish} (with {@code .exe} on Windows) in the directory the app was
     * started from, then a {@code stockfish} on the PATH.
     *
     * @return the first that exists, or the local {@code stockfish/} path if none does, so error messages say
     * where the app looked
     */
    public static Path defaultExecutable() {
        String configured = System.getProperty(EXECUTABLE_PROPERTY);
        if (configured != null && !configured.isBlank() && Files.isExecutable(Path.of(configured))) {
            return Path.of(configured);
        }
        for (Path folder : bundledFolders()) {
            Path bundled = folder.resolve("stockfish").resolve(programName());
            if (Files.isExecutable(bundled)) {
                return bundled;
            }
        }
        Path local = Path.of("stockfish", programName());
        if (Files.isExecutable(local)) {
            return local;
        }
        String pathVariable = System.getenv("PATH");
        if (pathVariable != null) {
            for (String directory : pathVariable.split(java.io.File.pathSeparator)) {
                if (directory.isBlank()) {
                    continue;
                }
                try {
                    Path onPath = Path.of(directory, programName());
                    if (Files.isExecutable(onPath)) {
                        return onPath;
                    }
                } catch (java.nio.file.InvalidPathException e) {
                    // a malformed PATH entry; skip it
                }
            }
        }
        return local;
    }

    // The folder holding the app's jar and its parents: where a packaged app keeps its bundled files
    private static List<Path> bundledFolders() {
        try {
            Path jar = Path.of(Stockfish.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (!Files.isRegularFile(jar)) {
                return List.of(); // running from compiled classes (tests, javafx:run), not a packaged app
            }
            List<Path> folders = new ArrayList<>();
            for (Path folder = jar.getParent(); folder != null && folders.size() < 3; folder = folder.getParent()) {
                folders.add(folder);
            }
            return folders;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static String programName() {
        boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
        return windows ? "stockfish.exe" : "stockfish";
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
     * Sets how strongly the engine plays, using Stockfish's "Skill Level" option. Engines without the option
     * ignore it.
     *
     * @param level 0 (weakest) to 20 (full strength)
     */
    public void setSkillLevel(int level) {
        sendCommand("setoption name Skill Level value " + Math.max(0, Math.min(20, level)));
    }

    /**
     * Asks the engine for its move in a position and waits for the answer. This blocks for about
     * {@code thinkMillis}, so call it from a background thread, not the UI thread.
     *
     * @param fen        the position, as a full FEN string
     * @param thinkMillis how long the engine may think, in milliseconds
     * @return the move in UCI notation (e.g. "e2e4", or "e7e8q" for a promotion), or null if the engine
     * isn't running or didn't answer (the reason is logged)
     */
    public String getBestMove(String fen, int thinkMillis) {
        if (!isRunning()) {
            log.warn("Can't get a move: Stockfish isn't running");
            return null;
        }
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + thinkMillis);

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = readLine()) != null) {
            if (line.startsWith("bestmove")) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && !parts[1].equals("(none)")) {
                    log.debug("Stockfish >> {}", line);
                    return parts[1];
                }
                output.append(line).append('\n');
                break;
            }
            output.append(line).append('\n');
        }
        if (isRunning()) { // if it crashed or was stopped, that's already been logged
            log.warn("Stockfish gave no move for {}. Its output was:\n{}", fen,
                    output.isEmpty() ? "(nothing)" : output.toString().strip());
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
