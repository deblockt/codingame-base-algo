package com.deblock.packman.grid;

import com.deblock.packman.game.Pac;
import com.deblock.packman.game.Pellet;

import java.util.Collection;
import java.util.List;

public interface Grid {

    Path path(Position position, Position position1);

    void refresh(List<Pac> pacs);

    List<Position> accessibleCells(Position from, int deep, int minimalDeep);

    String toString(Collection<Pellet> pellets);

    List<Position> visibleCells(Position position);

    List<Position> allEmptyCells();

    Path getPathToNearest(Position from, List<Position> allBestPellet);
}
