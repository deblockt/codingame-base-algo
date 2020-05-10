package com.deblock.packman.state;

import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.move.PacMove;
import com.deblock.packman.move.SpeedUp;

import java.util.List;
import java.util.Optional;

public class SpeedUpBehaviour implements Behaviours {
    private final Pac pac;

    public SpeedUpBehaviour(Pac pac) {
        this.pac = pac;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        if (pac.abilityCooldown == 0) {
            return Optional.of(new SpeedUp(pac));
        }
        return Optional.empty();
    }
}
