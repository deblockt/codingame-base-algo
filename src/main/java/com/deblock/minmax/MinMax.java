package com.deblock.minmax;


import com.deblock.logger.CGLogger;

public class MinMax<BoardType, MoveType extends Move<MoveType>> {


    public MoveType move(
        MinMaxPlayer<BoardType, MoveType> player1,
        MinMaxPlayer<BoardType, MoveType> player2,
        MinMaxBoard<MoveType, BoardType> board,
        int deep
    ) {
        final RootNode<MoveType, BoardType> minMaxNode = new RootNode<MoveType, BoardType>(board);

        minMaxNode.simulate(player1, player2, new AlphaBetaZone(), deep);

        CGLogger.log("max score " + minMaxNode.getScore());
        return minMaxNode.getBestMove().getMove();
    }
}