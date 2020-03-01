package com.deblock.minmax;

import java.util.List;

public class MinNode<MoveType  extends Move<MoveType>, BoardType> implements MinMaxNode<MoveType, BoardType> {
    private final Move<MoveType> move;
    private final MinMaxBoard<MoveType, BoardType> board;
    private double minScore = Double.MAX_VALUE;
    private MinMaxNode<MoveType, BoardType> bestMove = null;

    public MinNode(MinMaxBoard<MoveType, BoardType> board, Move<MoveType> move) {
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
            return;
        }
        final List<Move<MoveType>> possibleMove = player2.possibleMove(board.getBoard());
        for (Move<MoveType> move: possibleMove) {
            final MinMaxBoard<MoveType, BoardType> newBoard = board.simulateMove(move.getMove());
            final MinMaxNode<MoveType, BoardType>  child = new MaxNode<>(newBoard, move);
            child.simulate(player1, player2, zone.cloneZone(), deep - 1);
            final double childScore = child.getScore();
            if (bestMove == null || childScore < this.minScore) {
                this.minScore = childScore;
                this.bestMove = child;
            }
            if (zone.isLessThanExpectedMin(childScore)) {
                break;
            } else {
                zone.newMaxScore(childScore);
            }
        }
    }

    @Override
    public double getScore() {
        if (bestMove == null) {
            return this.board.score();
        }
        return this.minScore;
    }

    @Override
    public Move<MoveType> getMove() {
        return this.move;
    }

}
