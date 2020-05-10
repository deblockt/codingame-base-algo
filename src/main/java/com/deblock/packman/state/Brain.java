package com.deblock.packman.state;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.move.PacMove;
import com.deblock.packman.move.Wait;

import java.util.List;

public class Brain {
    private final Pac pac;
    private final Game game;
    private final Behaviours behaviours;

    public Brain(Pac pac, Game game) {
        this.pac = pac;
        this.game = game;
        this.behaviours = new ComposableBehaviour(
            new SwitchIfNearEnemies(pac),
            new SwitchIfDontMoveAndNearEnemies(pac),
            new SpeedUpBehaviour(pac),
            new GoToTargetBehaviour(pac, new SuperPelletTargetChooser(), true, false),
            new GoToTargetBehaviour(pac, new GoToNearestInterestingCellTargetChooser(pac), false, true),
            new GoToTargetBehaviour(pac, new RandomPelletTargetChooser(), true, false),
            new GoToRandomBehaviour(pac)
        );
    }

    public PacMove play(List<PacMove> partnerMoves) {
        CGLogger.log(pac.id + " start playing");
        PacMove move = behaviours.nextMove(game, partnerMoves).orElse(new Wait());
        CGLogger.log(pac.id + " end playing");
        return move;
    }
}
