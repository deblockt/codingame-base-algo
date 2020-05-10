package com.deblock.path;

public interface CellWeigher<T extends DijkstraPosition<T>> {
    int weight(T from, T to);
}
