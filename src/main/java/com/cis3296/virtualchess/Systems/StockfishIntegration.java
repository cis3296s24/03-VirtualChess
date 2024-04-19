package com.cis3296.virtualchess.Systems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class StockfishIntegration {
    private Process stockfishProcess;
    private BufferedReader processInput;
    private PrintWriter processOutput;

    public StockfishIntegration() {

    }

    public StockfishIntegration(String stockfishPath) {
        try {
            // Start the Stockfish engine process
            stockfishProcess = Runtime.getRuntime().exec(stockfishPath);
            processInput = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            processOutput = new PrintWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
            // Initialize Stockfish
            sendCommand("uci");
            sendCommand("ucinewgame");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBestMove(String fen) {
        // Send the current board state to Stockfish
        sendCommand("position fen " + fen);
        // Ask Stockfish for the best move
        sendCommand("go movetime 1000"); // Adjust the time as needed
        // Read the output from Stockfish
        String bestMove = null;
        try {
            String output;
            while ((output = processInput.readLine()) != null) {
                if (output.startsWith("bestmove")) {
                    bestMove = output.split(" ")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestMove;
    }

    private void sendCommand(String command) {
        processOutput.println(command);
        processOutput.flush();
    }

    public void close() {
        sendCommand("quit");
        try {
            processInput.close();
            processOutput.close();
            stockfishProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

