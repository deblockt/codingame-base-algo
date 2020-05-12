package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.game.Pellet;
import com.deblock.packman.grid.Path;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RandomPelletTargetChooser implements GoToTargetBehaviour.TargetChooser {

    @Override
    public Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
        List<Position> bestPellets = game.getPellets().stream()
                .filter(p -> Utils.isNotUsed(p.position, partnerMoves))
                .map(p -> p.position)
                .collect(Collectors.toList());
        CGLogger.log("available pellets " + game.getPellets());
        CGLogger.log("bestPellets " + bestPellets);
        if (bestPellets.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(game.getGrid().getPathToNearest(pac.position, bestPellets));
    }

    @Override
    public String behaviourInfo() {
        return "random";
    }
}
