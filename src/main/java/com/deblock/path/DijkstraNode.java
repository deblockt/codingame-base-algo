package com.deblock.path;

public class DijkstraNode<T extends DijkstraPosition<T>> {
    public final T position;
    public final DijkstraNode<T> from;
    public final int totalWeigth;
    public final int pathLength;

    public DijkstraNode(T position, DijkstraNode<T> from, int totalWeight, int pathLength) {
        this.position = position;
        this.from = from;
        this.totalWeigth = totalWeight;
        this.pathLength = pathLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DijkstraNode)) {
            return false;
        }
        return this.position.equals(((DijkstraNode) obj).position);
    }

    @Override
    public int hashCode() {
        return this.position.hashCode();
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
