package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.Path;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.PacMove;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SuperPelletTargetChooser implements GoToTargetBehaviour.TargetChooser {

    @Override
    public Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
        return getPelletForPac(game, pac);
    }

    private static Map<Integer, Path> pelletByPacs = null;
    public static Optional<Path> getPelletForPac(Game game, Pac pac) {
        CGLogger.log("search best pellet solution 2");
        if (pelletByPacs == null) {
            pelletByPacs = new HashMap<>();
            Map<Position, Pac> myPacPosition = game.player1.getPacs().stream().collect(Collectors.toMap(Pac::position, Function.identity()));
            List<Position> pacs = duplicateBySymmetry(game, myPacPosition.keySet());
            for (Position pellet : game.getBestPellets()) {
                Path path = game.getGrid().getPathToNearest(pellet, pacs);
                if (path != null) {
                    if (myPacPosition.containsKey(path.target())) {
                        pacs.remove(path.target());
                        CGLogger.log("pellet " + pellet + " is for pac " + path.target());
                        pelletByPacs.put(myPacPosition.get(path.target()).id, path.reverse().add(pellet.x, pellet.y));
                    } else {
                        CGLogger.log("pellet " + pellet + " is for enemy");
                    }
                }
            }
        }
        return Optional.ofNullable(pelletByPacs.get(pac.id))
                .filter(path -> {
                    CGLogger.log("found a pellet " + path.target());
                    boolean pelletExists = game.getBestPellets().contains(path.target());
                    CGLogger.log("is the pellet exists " + pelletExists);
                    return pelletExists;
                });
    }

    public static List<Position> duplicateBySymmetry(Game game, Collection<Position> myPacPosition) {
        int symCenter = game.getGrid().getWidth() / 2;
        List<Position> sym = new ArrayList<>();
        for (Position pac: myPacPosition) {
            sym.add(pac);
            sym.add(Position.of(symCenter * 2 - pac.x, pac.y));
        }

        return sym;
    }

    @Override
    public String behaviourInfo() {
        return "super pellet";
    }
}
