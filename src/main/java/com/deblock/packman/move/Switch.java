package com.deblock.packman.move;

import com.deblock.packman.game.Pac;
import com.deblock.packman.move.PacMove;

public class Switch implements PacMove {
    private final Pac pac;
    private final String typeToWin;

    public Switch(Pac pac, String typeToWin) {
        this.pac = pac;
        this.typeToWin = typeToWin;
    }

    @Override
    public String action() {
        return "SWITCH " + pac.id + " " + typeToWin;
    }
}
