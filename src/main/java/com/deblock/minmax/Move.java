package com.deblock.minmax;

public interface Move<MoveType extends Move<MoveType>> {
    MoveType getMove();
}
