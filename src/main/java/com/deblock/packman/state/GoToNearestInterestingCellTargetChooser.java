package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.PacMove;

import java.util.*;
import java.util.stream.Collectors;

public class GoToNearestInterestingCellTargetChooser implements GoToTargetBehaviour.TargetChooser {
    private final Pac pac;

    public GoToNearestInterestingCellTargetChooser(Pac pac) {
        this.pac = pac;
    }

    @Override
    public Optional<Position> chooseTarget(Pac pac, Game game, List<PacMove> partnerMoves) {
        for (int i = pac.speed(); i <= 10; ++i) {
            Optional<Position> moveTry = tryToMove(game, i);
            if (moveTry.isPresent()) {
                return moveTry;
            }
        }
        return Optional.empty();
    }

    private Optional<Position> tryToMove(Game game, int deep) {
        List<Position> otherPacs = game.player1.getPacs().stream()
                .filter(pac -> pac.id != this.pac.id)
                .map(pac -> pac.position)
                .collect(Collectors.toList());
        List<Position> cells = game.accessibleCells(pac.position, deep, pac.speed(), otherPacs);
        List<Position> nonVisitedPositions = cells.stream()
                .filter(p -> !game.player1HistoryPosition.contains(p))
                .collect(Collectors.toList());
        Optional<Position> nonVisitedPositionsWithPellet = nonVisitedPositions.stream().max(Comparator.comparingInt(pos -> nbPelletNear(game, pos)));
        CGLogger.log("accessible cells " + cells);
        CGLogger.log("non visited cells " + nonVisitedPositions);
        CGLogger.log("non visited position with pellet " + nonVisitedPositionsWithPellet);
        if (nonVisitedPositionsWithPellet.isPresent()) {
            CGLogger.log("go to nearest pellet");
            return nonVisitedPositionsWithPellet;
        } /*else if (nonVisitedPositions.size() > 0) {
            CGLogger.log("go to nearest non visited position");
            return Optional.of(nonVisitedPositions.get(0));
        } */else {
            return Optional.empty();
        }
    }

    private int nbPelletNear(Game game, Position pos) {
        return (int) game.accessibleCells(pos, 1, 1)
                .stream()
                .filter(game::containsPellet)
                .count();
    }

    @Override
    public String behaviourInfo() {
        return "interesting";
    }
}
