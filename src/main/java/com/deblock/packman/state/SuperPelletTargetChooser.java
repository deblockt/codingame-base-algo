package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.game.Pellet;
import com.deblock.packman.grid.Path;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.PacMove;

import java.util.*;
import java.util.stream.Collectors;

public class SuperPelletTargetChooser implements GoToTargetBehaviour.TargetChooser {

    @Override
    public Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
        CGLogger.log("verify next best pellet");
        List<Position> allBestPellet = new ArrayList<>();
        for (Position p: game.getBestPellets()) {
            if (Utils.isNotUsed(p, partnerMoves)) {
                allBestPellet.add(p);
            }
        }
        CGLogger.log("found best pellets: " + allBestPellet);
        if (allBestPellet.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(game.getGrid().getPathToNearest(pac.position, allBestPellet));
    }

    private static Map<Position, Path> pelletByPacs = null;
    public Optional<Path> getPelletForPac(Game game, Pac pac) {
        if (pelletByPacs == null) {
            pelletByPacs = new HashMap<>();
            List<Position> pacs = game.player1.getPacs().stream().map(p -> p.position).collect(Collectors.toList());
            for (Position pellet : game.getBestPellets()) {
                Path path = game.getGrid().getPathToNearest(pellet, pacs);
                if (path != null) {
                    pacs.remove(path.target());
                    pelletByPacs.put(path.target(), path.reverse().add(pellet.x, pellet.y));
                }
            }
        }
        return Optional.ofNullable(pelletByPacs.get(pac.position));
    }
    @Override
    public String behaviourInfo() {
        return "super pellet";
    }
}
