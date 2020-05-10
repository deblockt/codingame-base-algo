package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;

public class ComposableBehaviour implements Behaviours {
    private final Behaviours[] behaviours;

    public ComposableBehaviour(Behaviours... behaviours) {
        this.behaviours = behaviours;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        for (int i = 0; i < behaviours.length; ++i) {
            CGLogger.log("Start behaviour " + behaviours[i].getClass());
            Optional<PacMove> move = behaviours[i].nextMove(game, partnerMoves);
            if (move.isPresent()) {
                return move;
            }
        }
        return Optional.empty();
    }
}
