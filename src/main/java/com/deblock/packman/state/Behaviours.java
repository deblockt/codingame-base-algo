package com.deblock.packman.state;

import com.deblock.packman.game.Game;
import com.deblock.packman.move.PacMove;

import java.util.List;
import java.util.Optional;

public interface Behaviours {

    Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves);
}
