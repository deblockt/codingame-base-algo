package com.deblock.packman.state;

import com.deblock.packman.grid.Position;
import com.deblock.packman.move.GoTo;
import com.deblock.packman.move.PacMove;

import java.util.List;

public class Utils {

    public static boolean isNotUsed(Position target, List<PacMove> partnerMoves) {
        for (PacMove move: partnerMoves) {
            if (move instanceof GoTo && ((GoTo) move).target().equals(target)) {
                return false;
            }
        }
        return true;
    }
}
