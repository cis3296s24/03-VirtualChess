package com.cis3296.virtualchess;

public interface IPlayer {
    void makeMove(Board board);
    String getName();
    boolean isAI();
}
