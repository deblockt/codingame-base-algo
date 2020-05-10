package com.deblock.packman.state;

import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.GoTo;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GoToRandomBehaviour implements Behaviours {
    private final Pac pac;

    public GoToRandomBehaviour(Pac pac) {
        this.pac = pac;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        List<Position> cells = game.accessibleCells(pac.position, 1, pac.speed());
        return Optional.of(new GoTo(pac, cells.get(new Random().nextInt(cells.size())), "go to random cell"));
    }
}
