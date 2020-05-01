package com.deblock.minmax;

public interface Move<MoveType extends Move<MoveType>> {
    /**
     * should return this
     * @return
     */
    MoveType getMove();
}
