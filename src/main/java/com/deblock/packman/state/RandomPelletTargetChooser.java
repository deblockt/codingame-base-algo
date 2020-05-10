package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.game.Pellet;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomPelletTargetChooser implements GoToTargetBehaviour.TargetChooser {

    @Override
    public Optional<Position> chooseTarget(Pac pac, Game game, List<PacMove> partnerMoves) {
        List<Pellet> bestPellets = game.getPellets().stream()
                .filter(p -> Utils.isNotUsed(p.position, partnerMoves))
                .collect(Collectors.toList());
        CGLogger.log("available pellets " + game.getPellets());
        CGLogger.log("bestPellets " + bestPellets);
        if (bestPellets.size() == 0) {
            return Optional.empty();
        }
        Position position = bestPellets.get(new Random().nextInt(bestPellets.size())).position;
        CGLogger.log("go to " + position);
        return Optional.of(position);
    }

    @Override
    public String behaviourInfo() {
        return "random";
    }
}
