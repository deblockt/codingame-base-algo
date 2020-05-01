package com.deblock;

import com.deblock.minmax.MinMax;
import com.deblock.tictactoe.TicTacToeBoard;
import com.deblock.tictactoe.TicTacToeMove;
import com.deblock.tictactoe.TicTacToePlayer;

import java.time.Duration;
import java.util.Scanner;

public class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        MinMax<TicTacToeBoard, TicTacToeMove> minMax = new MinMax<>();
        TicTacToeBoard board = new TicTacToeBoard();
        TicTacToePlayer player1 = new TicTacToePlayer(TicTacToeBoard.PLAYER1);
        TicTacToePlayer player2 = new TicTacToePlayer(TicTacToeBoard.PLAYER2);

        // game loop
        while (true) {
            long time = System.nanoTime();
            board.refresh(in);

            final TicTacToeMove bestMove = minMax.move(player1, player2, board, 6);

            System.err.println(board.toString());
            System.err.println("duration: " + Duration.ofNanos(System.nanoTime() - time).toMillis());
            System.out.println(bestMove.y + " " + bestMove.x);

            board.withMove(new TicTacToeMove(bestMove.x, bestMove.y, TicTacToeBoard.PLAYER1));
        }
    }
}
