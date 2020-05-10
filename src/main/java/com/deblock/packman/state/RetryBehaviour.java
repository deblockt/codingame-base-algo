package com.deblock.packman.state;

import com.deblock.packman.game.Game;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;

public class RetryBehaviour implements Behaviours {
    private final Behaviours b;

    public RetryBehaviour(Behaviours b1) {
        this.b = b1;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        for (int i = 0; i < 5; ++i) {
            Optional<PacMove> move = b.nextMove(game, partnerMoves);
            if (move.isPresent()) {
                return move;
            }
        }
        return Optional.empty();
    }
}
