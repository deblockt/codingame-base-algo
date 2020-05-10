package com.deblock.packman.game;

import com.deblock.packman.grid.Position;

public class Pellet {
    public final Position position;
    public final int value;

    public Pellet(Position position, int value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pellet[ " + this.position + " / " + value + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pellet)) {
            return false;
        }
        return this.position.equals(((Pellet) obj).position);
    }

    @Override
    public int hashCode() {
        return this.position.hashCode();
    }
}
