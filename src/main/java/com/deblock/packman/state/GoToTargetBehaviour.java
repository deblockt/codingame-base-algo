package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.Path;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.GoTo;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;

public class GoToTargetBehaviour implements Behaviours {
    private final TargetChooser targetChooser;
    private final Pac pac;
    private Position target;
    private final boolean stopIfNoPellet;
    private final boolean recomputeEachTurn;

    public GoToTargetBehaviour(Pac pac, TargetChooser targetChooser, boolean stopIfNoPellet, boolean recomputeEachTurn) {
        this.targetChooser = targetChooser;
        this.pac = pac;
        this.stopIfNoPellet = stopIfNoPellet;
        this.recomputeEachTurn = recomputeEachTurn;
    }

    @Override
    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
        CGLogger.log("using " + this.targetChooser.behaviourInfo());
        Path targetPath = null;
        if (recomputeEachTurn || this.target == null ||
            pac.position.equals(target) || (stopIfNoPellet && !game.containsPellet(target)) ||
            !Utils.isNotUsed(target, partnerMoves)
        ) {
            targetPath = this.targetChooser.chooseTargetPath(pac, game, partnerMoves).orElse(null);
            if (targetPath != null) {
                target = targetPath.target();
            } else {
                target = null;
            }
        }

        if (this.target == null) {
            CGLogger.log("no target... Do nothing");
            return Optional.empty();
        }
        CGLogger.log("current target " + target);
        if (targetPath == null) {
            targetPath = game.getGrid().path(pac.position, target);
        }
        if (targetPath == null) {
            target = null;
            return Optional.empty();
        }
        return Optional.of(new GoTo(target, targetPath, pac, targetChooser.behaviourInfo() + " > " + this.target));
    }

    public interface TargetChooser {
        default Optional<Position> chooseTarget(Pac pac, Game game, List<PacMove> partnerMoves) {
            return Optional.empty();
        }

        default Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
            return this.chooseTarget(pac, game, partnerMoves)
                .flatMap(target -> Optional.ofNullable(game.getGrid().path(pac.position, target)));
        }

        String behaviourInfo();
    }
}
