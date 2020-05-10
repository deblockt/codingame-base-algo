package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.GoTo;
import com.deblock.packman.move.PacMove;
import com.deblock.packman.move.Switch;

import java.util.List;
import java.util.Optional;

public class SwitchIfDontMoveAndNearEnemies implements Behaviours {
    private final Pac pac;
    private Position lastPosition;

    public SwitchIfDontMoveAndNearEnemies(Pac pac) {
        this.pac = pac;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        CGLogger.log("speed turn left " + pac.speedTurnsLeft);
        CGLogger.log(pac.id + " before " + this.lastPosition + " --> " + pac.position);
        if (this.lastPosition != null && this.lastPosition.equals(pac.position()) && pac.speedTurnsLeft != 5) {
            CGLogger.log("I can't move " + pac.id);
            List<Position> positions =  game.accessibleCells(pac.position(), 1, pac.speed());
            CGLogger.log("I try to go " + positions);
            List<Pac> enemies = game.getEnemiesOn(positions);
            List<Pac> friends = game.getPartnerOn(positions);
            CGLogger.log("I have found enemis on this cell " + enemies);
            if (enemies.isEmpty()) {
                if (!friends.isEmpty()) {
                    CGLogger.log("I am block with a friend :'(");
                } else if (pac.abilityCooldown > 0) {
                    CGLogger.log("I'm block but i can not switch " + pac.abilityCooldown);
                } else {
                    CGLogger.log("I don't know who block me. I try to switch");
                    String typeToWin = pac.typeToWin(pac);
                    this.updatePosition();
                    return Optional.of(new Switch(pac, typeToWin));
                }
            } else {
                if (pac.abilityCooldown > 0) {
                    CGLogger.log("I can not switch " + pac.abilityCooldown);
                } else {
                    String typeToWin = pac.typeToWin(enemies.get(0));
                    if (pac.typeId.equals(typeToWin)) {
                        this.updatePosition();
                        return Optional.of(new GoTo(pac, enemies.get(0).position, "go to kill enemies"));
                    } else {
                        this.updatePosition();
                        return Optional.of(new Switch(pac, typeToWin));
                    }
                }
            }
        }
        this.updatePosition();
        return Optional.empty();
    }

    private void updatePosition() {
        this.lastPosition = pac.position();
    }
}
