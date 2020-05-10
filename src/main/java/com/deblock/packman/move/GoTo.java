package com.deblock.packman.move;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Pac;
import com.deblock.packman.game.Pellet;
import com.deblock.packman.grid.Path;
import com.deblock.packman.grid.Position;

public class GoTo implements PacMove {
    private final Path path;
    private final Position target;
    private final Pac pac;
    private final String comment;

    public GoTo(Position target, Path path, Pac pac, String comment) {
        this.path = path;
        this.target = target;
        this.pac = pac;
        this.comment = comment;
    }

    public GoTo(Pac pac, Position position, String message) {
        this(position, new Path(), pac, message + " " + position);
        this.path.add(position.x, position.y);
    }

    public Position target() {
        return this.target;
    }

    public String action() {
        if (this.path == null || this.path.size() == 0) {
            CGLogger.log("NO PATH FOUND TO GO TO " + target + " for pac " + pac.id);
            return "";
        }
        final Position nextPosition;
        if (pac.speedTurnsLeft > 0) {
            nextPosition = this.path.pop(2);
        } else {
            nextPosition = this.path.pop();
        }
        return "MOVE " + pac.id + " " + nextPosition.x + " " + nextPosition.y + " " + comment;
    }
}
