package com.deblock.minmax;

public class RootNode<MoveType extends Move<MoveType>, BoardType> extends MaxNode<MoveType, BoardType> {
    public RootNode(MinMaxBoard<MoveType, BoardType> board) {
        super(board, null);
    }

    public Move<MoveType> getBestMove() {
        return this.bestMove.getMove();
    }

}