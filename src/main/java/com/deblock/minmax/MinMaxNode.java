package com.deblock.minmax;

public interface MinMaxNode<MoveType extends Move<MoveType>, BoardType> {
    void simulate(
            MinMaxPlayer<BoardType, MoveType> player1,
            MinMaxPlayer<BoardType, MoveType> player2,
            AlphaBetaZone zone,
            int deep
    );

    double getScore();

    Move<MoveType> getMove();

}
