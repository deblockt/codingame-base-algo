package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.Path;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.GoTo;
import com.deblock.packman.move.PacMove;
import com.deblock.packman.move.Switch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SwitchIfNearEnemies implements Behaviours {
    private final Pac pac;

    public SwitchIfNearEnemies(Pac pac) {
        this.pac = pac;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        List<Position> accessibleCells =  game.accessibleCells(pac.position(), pac.speed() * 2, 1);
        List<Pac> enemies = game.getEnemiesOn(accessibleCells);
        CGLogger.log("enemies " + enemies );
        if (!enemies.isEmpty()) {
            Pac enemy = enemies.get(0);
            String typeToWin = pac.typeToWin(enemy);
            if (pac.abilityCooldown == 0 && !typeToWin.equals(pac.typeId)) {
                CGLogger.log("I switch to avoid to be killed");
                return Optional.of(new Switch(pac, typeToWin));
            } else if (typeToWin.equals(pac.typeId) && enemy.abilityCooldown > 0 && enemy.speed() <= pac.speed()) {
                CGLogger.log("Go to enemies cell to kill it");
                Path path = new Path();
                path.add(enemy.position.x, enemy.position.y);
                return Optional.of(new GoTo(enemy.position, path, pac, "I will kill you"));
            } else if ((!typeToWin.equals(pac.typeId) && !pac.typeId.equals(enemy.typeId)) || enemy.abilityCooldown == 0) {
                CGLogger.log("I go back. I don't want to die");
                List<Position> directAccessibleCells =  game.accessibleCells(pac.position(), pac.speed(), 1);
                CGLogger.log("accessible cells: " + directAccessibleCells);
                List<Position> safeCells = directAccessibleCells.stream()
                        .filter(cell -> !haveEnemiesNear(cell, game, enemies, pac.speed()))
                        .collect(Collectors.toList());
                if (safeCells.size() == 1) {
                    CGLogger.log("found only one safe cell. go to " + safeCells.get(0));
                    return Optional.of(new GoTo(pac, safeCells.get(0), "go back"));
                } else {
                    CGLogger.log("found many safe cell. search cell with the much path " + safeCells);
                    Optional<Position> moreSafeCell = safeCells.stream()
                                        .max(Comparator.comparingInt(cell -> scoreCell(game, cell, pac.position)));
                    CGLogger.log("found " + moreSafeCell);
                    return moreSafeCell.map(cell -> new GoTo(pac, cell, "go back"));
                }
            }
        }

        return Optional.empty();
    }

    private int scoreCell(Game game, Position cell, Position pacPosition) {
        List<Position> avoidToWalk = new ArrayList<>();
        avoidToWalk.add(pacPosition);
        int nbAccessCells = game.getGrid().accessibleCells(cell, 10, 1, avoidToWalk).size();
        List<Position> nbAccessibleCellsForPellet = game.getGrid().accessibleCells(cell, 2, 1, avoidToWalk);
        int nbPelletAtTwoStep = (int) nbAccessibleCellsForPellet.stream().filter(game::containsPellet).count();
        CGLogger.log("cell " + cell + ", score " + (nbAccessCells - nbPelletAtTwoStep) + " nbAccessible " + nbAccessCells + " nbPellet " + nbPelletAtTwoStep);
        return nbAccessCells + nbPelletAtTwoStep;
    }

    private boolean haveEnemiesNear(Position cell, Game game, List<Pac> enemies, int speed) {
        List<Position> positions = game.accessibleCells(cell, speed, 1);
        positions.add(cell);
        for (Position pos: positions) {
            if (enemies.stream().anyMatch(e -> e.position.equals(pos))) {
                return true;
            }
        }
        return false;
    }
}
