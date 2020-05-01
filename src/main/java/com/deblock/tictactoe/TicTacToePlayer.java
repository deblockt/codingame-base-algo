package com.deblock.tictactoe;

import com.deblock.minmax.MinMaxPlayer;
import com.deblock.minmax.Move;

import java.util.List;

public class TicTacToePlayer implements MinMaxPlayer<TicTacToeBoard, TicTacToeMove> {
    private final byte playerSymbol;

    public TicTacToePlayer(byte playerSymbol) {
        this.playerSymbol = playerSymbol;
    }

    @Override
    public List<Move<TicTacToeMove>> possibleMove(TicTacToeBoard board) {
        return board.availableMoveFor(playerSymbol);
    }
}
