package com.cis3296.virtualchess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * A simple and efficient client to run Stockfish from Java
 * Originally from Rahul A R on GitHub
 * Nick Rucinski worked on cleaning it up and extending it
 */
public class Stockfish {

	private Process engineProcess;
	private BufferedReader processReader;
	private OutputStreamWriter processWriter;

	/**
	 * Starts Stockfish engine as a process and initializes it
	 * Checks for the users OS and launches the correct version if available
	 * @return True on success. False otherwise
	 */
	public boolean startEngine() {
		try {
			// FOR MAC:
			// Install homebrew,
			// Install stockfish,
			// Replace 'insert' with "/usr/local/bin/stockfish"
			// Runtime.getRuntime().exec( 'insert' );
			String PATH;
			// Find the right OS
			String osName = System.getProperty("os.name");
			if (osName.toLowerCase().contains("mac")) {
				PATH = "stockfish/stockfish-macos-x86-64-avx2";
			} else if (osName.toLowerCase().contains("win")) {
				PATH = "stockfish/stockfish-windows-x86-64-avx2.exe";
			} else {
				PATH = "stockfish/stockfish-ubuntu-x86-64-avx2";
			}
			// Startup Stockfish
			engineProcess = Runtime.getRuntime().exec(PATH);
			processReader  = new BufferedReader(new InputStreamReader(
					engineProcess.getInputStream()));
			processWriter = new OutputStreamWriter(
					engineProcess.getOutputStream());
		} catch (Exception e) {
			// Failed to open
			return false;
		}
		// Everything okay
		return true;
	}

	/**
	 * Stops Stockfish and cleans up before closing it
	 */
	public void stopEngine() {
		try {
			sendCommand("quit");
			processReader.close();
			processWriter.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Takes in any valid UCI command and executes it. Adds a newline character automatically
	 *
	 * @param command A UCI command to be sent to stockfish
	 */
	public void sendCommand(String command) {
		try {
			processWriter.write(command + "\n");
			processWriter.flush();
			System.out.println("Sending command: " + command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the command to start a new game with UCI
	 */
	public void setUCINewGame() {
		sendCommand("ucinewgame");
	}

	/**
	 * Sets the positions on the board
	 * @param fen A FEN string that represents the board
	 */
	public void setPosition(String fen){
		String buffer = "position fen " + fen;
		sendCommand(buffer);
	}

	/**
	 * Gets a move from Stockfish and parses it into coordinates if found
	 * @param fen A FEN string that represents the board
	 * @param waitTime The amount of time that Stockfish should wait before responding
	 * @return The coordinates from and two to move
	 * ex. a2a4 is move white pawn up two squares.
	 * or null if the move in not received
	 */
	public String getBestMove(String fen, int waitTime) {
		sendCommand("position fen " + fen);
		sendCommand("go depth 1 movetime " + waitTime);

		// Wait for the output to be ready
		String output = getOutput(waitTime + 20);

		// Split the output to find the best move
		String[] lines = output.split("\n");
		for (String line : lines) {
			if (line.startsWith("bestmove")) {
				String[] parts = line.split(" ");
				if (parts.length >= 2) {
					return parts[1]; // Return the best move
				}
			}
		}

		// Return null if no best move found
		return null;
	}

	/**
	 * This is generally called right after 'sendCommand' for getting the raw
	 * output from Stockfish
	 *
	 * @param waitTime
	 *            Time in milliseconds for which the function waits before
	 *            reading the output. Useful when a long running command is
	 *            executed
	 * @return Raw output from Stockfish
	 */
	public String getOutput(int waitTime) {
		StringBuffer buffer = new StringBuffer();
		try {
			Thread.sleep(waitTime);
			sendCommand("isready");
			while (true) {
				String text = processReader.readLine();
				if (text.equals("readyok"))
					break;
				else
					buffer.append(text + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * Draws the current state of the chess board
	 * 
	 * @param fen
	 *            Position string with using FEN
	 */
	public void drawBoard(String fen) {
		sendCommand("position fen " + fen);
		sendCommand("d");

		String[] rows = getOutput(0).split("\n");

		for (int i = 1; i < 18; i++) {
			System.out.println(rows[i]);
		}
	}
}
