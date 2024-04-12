package com.cis3296.virtualchess;

public class AI implements IPlayer {
    private String name;
    private Stockfish stockfish;

    public AI(String name, Stockfish stockfish) {
        this.name = name;
        this.stockfish = stockfish;
    }

    @Override
    public void makeMove(Board board) {
        // Logic for AI making a move using the Stockfish engine
        // You need to translate the board state into a format Stockfish can understand, send it to Stockfish, get the move, and apply it to the board
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAI() {
        return true;
    }
}
