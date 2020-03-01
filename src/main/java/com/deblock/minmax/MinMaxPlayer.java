package com.deblock.minmax;

import java.util.List;

public interface MinMaxPlayer<BoardType, MoveType extends Move<MoveType>> {
    List<Move<MoveType>> possibleMove(BoardType board);
}
