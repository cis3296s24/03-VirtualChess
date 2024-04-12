package com.cis3296.virtualchess;

public class Player implements IPlayer {
    private String name;

    public Player(String name) {
        this.name = name;
    }

    @Override
    public void makeMove(Board board) {
        // Logic for human player making a move
        // In a GUI application, this might be handled through event listeners rather than direct method calls
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAI() {
        return false;
    }
}
