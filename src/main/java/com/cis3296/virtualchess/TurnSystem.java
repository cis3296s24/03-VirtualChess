package com.cis3296.virtualchess;

import com.cis3296.virtualchess.Board.Board;
import com.cis3296.virtualchess.Data.Player;
import com.cis3296.virtualchess.Pieces.Piece;

public class TurnSystem {

    private Player whitePlayer;
    public Timer whiteTimer;

    private Player blackPlayer;
    public Timer blackTimer;

    private Player currentPlayer;

    private Board board;

    public TurnSystem(Player whitePlayer, Player blackPlayer, Board board) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        currentPlayer = blackPlayer;
        whiteTimer = new Timer(5);
        blackTimer = new Timer(5);
        whiteTimer.start();
        blackTimer.start();
        blackTimer.pause();
        this.board = board;

    }

    public void changeTurn(){
        if(currentPlayer == whitePlayer){
            currentPlayer = blackPlayer;

            whiteTimer.pause();
            blackTimer.unpause();

            for(Piece piece: board.pieces){
                if(piece.color.equals("white")){
                    piece.isTurn = false;
                } else {
                    piece.isTurn = true;
                }
            }

        } else {
            currentPlayer = whitePlayer;

            blackTimer.pause();
            whiteTimer.unpause();

            for(Piece piece: board.pieces){
                if(piece.color.equals("black")){
                    piece.isTurn = false;
                } else {
                    piece.isTurn = true;
                }
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
