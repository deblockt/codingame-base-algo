package com.deblock.packman.move;

import com.deblock.packman.game.Pac;

public class SpeedUp implements PacMove {
    private final Pac pac;

    public SpeedUp(Pac pac) {
        this.pac = pac;
    }

    @Override
    public String action() {
        return "SPEED " + pac.id;
    }
}
