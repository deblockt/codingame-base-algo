package com.deblock.packman.grid;

import java.util.HashMap;
import java.util.Map;

public class Position {
    public final int x;
    public final int y;
    public static Map<Integer, Position> positions = new HashMap<>();

    protected Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public int hashCode() {
        return this.x * 1000 + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }
        return this.x == ((Position) obj).x && this.y == ((Position) obj).y;
    }

    public static Position of(int x, int y) {
        int index = x * 100000 + y;
        if (!positions.containsKey(index)) {
            positions.put(index, new Position(x, y));
        }
        return positions.get(index);
    }
}
