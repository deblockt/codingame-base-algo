package com.deblock.packman.grid;

import java.util.ArrayList;
import java.util.List;

public class Path {
    List<Position> path = new ArrayList<>();

    public Position pop() {
        return path.get(0);
    }

    public Position pop(int maxDeep) {
        if (path.size() < maxDeep) {
            return path.get(path.size() - 1);
        }
        return path.get(maxDeep - 1);
    }

    public Path add(int toX, int toY) {
        path.add(Position.of(toX, toY));
        return this;
    }

    public List<Position> all() {
        return path;
    }

    public int size() {
        return path.size();
    }

    public Position target() {
        return this.path.get(this.path.size() - 1);
    }

    public Path reverse() {
        Path pathObj = new Path();
        for (int i = path.size() - 1; i >= 0; --i) {
            pathObj.path.add(path.get(i));
        }
        return pathObj;
    }
}
