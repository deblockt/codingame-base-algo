package com.deblock.minmax;

import java.util.List;

public class MaxNode<MoveType extends Move<MoveType>, BoardType> implements MinMaxNode<MoveType, BoardType> {
    private final Move<MoveType> move;
    private final MinMaxBoard<MoveType, BoardType> board;
    private double maxScore = -Double.MAX_VALUE;
    protected MinMaxNode<MoveType, BoardType> bestMove = null;

    public MaxNode(MinMaxBoard<MoveType, BoardType> board, Move<MoveType> move) {
        this.board = board;
        this.move = move;
    }

    @Override
    public void simulate(
            MinMaxPlayer<BoardType, MoveType> player1,
            MinMaxPlayer<BoardType, MoveType> player2,
            AlphaBetaZone zone,
            int deep
    ) {
        if (deep <= 0) {
            return ;
        }
        final List<Move<MoveType>> possibleMove = player1.possibleMove(board.getBoard());
        for (Move<MoveType> move: possibleMove) {
            final MinMaxBoard<MoveType, BoardType> newBoard = board.simulateMove(move.getMove());
            final MinMaxNode<MoveType, BoardType>  child = new MinNode<>(newBoard, move);
            child.simulate(player1, player2, zone.cloneZone(), deep - 1);
            final double childScore = child.getScore();
            if (bestMove == null || childScore > maxScore) {
                maxScore = childScore;
                bestMove = child;
            }
            if (zone.isGreaterThanExpectedMax(childScore)) {
                break;
            } else {
                zone.newMinScore(childScore);
            }
        }
    }

    public double getScore() {
        if (bestMove == null) {
            return this.board.score();
        }
        return maxScore;
    }

    public Move<MoveType> getMove() {
        return this.move;
    }

}
