package com.deblock.tictactoe;

import com.deblock.minmax.Move;

public class TicTacToeMove implements Move<TicTacToeMove> {
    public final int x;
    public final int y;
    public final byte playerSymbol;

    public TicTacToeMove(int x, int y, byte playerSymbol) {
        this.x = x;
        this.y = y;
        this.playerSymbol = playerSymbol;
    }

    @Override
    public TicTacToeMove getMove() {
        return this;
    }
}
