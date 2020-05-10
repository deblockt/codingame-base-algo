package com.deblock.path;

import java.util.List;

public interface DijkstraPosition<SELF extends DijkstraPosition> {

    List<SELF> getNeighbour(int nbNodeToGo);
}
