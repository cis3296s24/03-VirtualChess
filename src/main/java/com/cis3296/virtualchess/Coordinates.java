package com.cis3296.virtualchess;
import static com.cis3296.virtualchess.Board.SQUARE_SIZE;

public class Coordinates {
    public int col, row;
    public int x, y;

    public Coordinates(int col, int row) {
        this.col = col;
        this.row = row;
        this.x = SQUARE_SIZE * col;
        this.y = SQUARE_SIZE * row;
    }

    public void setCol(int col) {
        this.col = col;
        this.x = SQUARE_SIZE * col;
    }

    public void setRow(int row) {
        this.row = row;
        this.x = SQUARE_SIZE * row;
    }

    public void setX(int x) {
        this.x = x;
        this.col = x/SQUARE_SIZE;
    }

    public void setY(int y) {
        this.y = y;
        this.row = y/SQUARE_SIZE;
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
