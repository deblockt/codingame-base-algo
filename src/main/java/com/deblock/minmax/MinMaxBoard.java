package com.deblock.minmax;

public interface MinMaxBoard<MoveType, BoardType> {
    MinMaxBoard<MoveType, BoardType> simulateMove(MoveType move);

    /**
     * should return this
     * @return
     */
    BoardType getBoard();

    /**
     * give the board score for the player1
     */
    double score();

}
