package com.deblock.packman.grid;

import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Pac;
import com.deblock.packman.game.Pellet;
import com.deblock.path.Dijkstra;
import com.deblock.path.DijkstraPosition;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphGrid implements Grid {
    private final int height;
    private final int width;
    private List<Pac> pacs = new ArrayList<>();
    private HashMap<Position, GridPosition> nodes = new HashMap<>();
    private static Dijkstra<GridPosition> dijkstra = new Dijkstra<>((from, to) -> 1);

    public GraphGrid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addLink(Position pos, Position accessible) {
        if (!nodes.containsKey(pos)) {
            nodes.put(pos, new GridPosition(pos));
        }
        if (!nodes.containsKey(accessible)) {
            nodes.put(accessible, new GridPosition(accessible));
        }
        nodes.get(pos).addNeighbour(nodes.get(accessible));
        nodes.get(accessible).addNeighbour(nodes.get(pos));
    }

    public void refresh(List<Pac> pacs) {
        this.pacs = pacs;
    }

    @Override
    public List<Position> accessibleCells(Position from, int deep, int minimalDeep) {
        CGLogger.log("start computing accessible cells from " + from + " deep = " + deep);
        Set<GridPosition> positions = new HashSet<>();
        Set<GridPosition> done = new HashSet<>();
        Set<Position> toDo = new HashSet<>();
        toDo.add(from);
        int currentDeep = 1;
        while (!toDo.isEmpty() && currentDeep <= deep) {
            Set<Position> newPos = new HashSet<>();
            for (Position pos: toDo) {
                for (GridPosition accessible: nodes.get(pos).neighbour) {
                    if (!done.contains(accessible)) {
                        newPos.add(accessible);
                        done.add(accessible);
                        if (currentDeep >= minimalDeep) {
                            positions.add(accessible);
                        }
                    }
                }
            }
            toDo = newPos;
            ++currentDeep;
        }
        CGLogger.log("end of computation");
        return new ArrayList<>(positions);
    }

    @Override
    public Path getPathToNearest(Position from, List<Position> goToPositions) {
        return buildPath(dijkstra.pathToNearest(nodes.get(from), goToPositions.stream().map(nodes::get).collect(Collectors.toList())));
    }


    @Override
    public String toString(Collection<Pellet> pellets) {
        Map<Position, Pellet> pelletsByPosition = pellets.stream().collect(Collectors.toMap(p -> p.position, Function.identity()));
        Map<Position, Pac> pacsByPosition = pacs.stream().collect(Collectors.toMap(p -> p.position, Function.identity()));
        String result = "";
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Position position = Position.of(x, y);
                if (pelletsByPosition.containsKey(position)) {
                    result += "°";
                } else if (pacsByPosition.containsKey(position)) {
                    result += pacsByPosition.get(position).isPlayer1 ? "1": "2";
                } else if (nodes.containsKey(position)) {
                    result += ' ';
                } else {
                    result += '#';
                }
            }
            result += "\n";
        }
        return result;
    }

    @Override
    public List<Position> visibleCells(Position position) {
        Set<Position> result = new HashSet<>();
        GridPosition gridPosition = nodes.get(position);
        result.addAll(gridPosition.tops());
        result.addAll(gridPosition.lefts());
        result.addAll(gridPosition.rights());
        result.addAll(gridPosition.bottoms());
        return result.stream().map(p -> Position.of(p.x, p.y)).collect(Collectors.toList());
    }

    @Override
    public List<Position> allEmptyCells() {
        return new ArrayList<>(nodes.values());
    }

    @Override
    public Path path(Position from, Position to) {
        CGLogger.log("start computing path between " + from + " and " + to);
        List<GridPosition> path = dijkstra.path(nodes.get(from), nodes.get(to));
        CGLogger.log("found path " + path);
        return buildPath(path);
    }

    private Path buildPath(List<GridPosition> positions) {
        if (positions == null) {
            return null;
        }
        Path result = new Path();
        for (GridPosition position: positions) {
            result.add(position.x, position.y);
        }
        return result;
    }

    private static class GridPosition extends Position implements DijkstraPosition<GridPosition> {
        private final Set<GridPosition> neighbour = new HashSet<>();
        private GridPosition top;
        private GridPosition right;
        private GridPosition left;
        private GridPosition bottom;

        private GridPosition(Position pos) {
            super(pos.x, pos.y);
        }

        @Override
        public List<GridPosition> getNeighbour(int nbNodeToGo) {
            return new ArrayList<>(this.neighbour);
        }

        public void addNeighbour(GridPosition gridPosition) {
            neighbour.add(gridPosition);
            if (this.x < gridPosition.x) {
                left = gridPosition;
            } else if (this.x > gridPosition.x) {
                right = gridPosition;
            } else if (this.y < gridPosition.y) {
                top = gridPosition;
            } else if (this.y > gridPosition.y) {
                bottom = gridPosition;
            }
        }

        public List<GridPosition> tops() {
            GridPosition current = this;
            List<GridPosition> positions = new ArrayList<>();
            while (current != null && !positions.contains(current)) {
                positions.add(current);
                current = current.top;
            }
            return positions;
        }

        public List<GridPosition> bottoms() {
            GridPosition current = this;
            List<GridPosition> positions = new ArrayList<>();
            while (current != null && !positions.contains(current)) {
                positions.add(current);
                current = current.bottom;
            }
            return positions;
        }

        public List<GridPosition> lefts() {
            GridPosition current = this;
            List<GridPosition> positions = new ArrayList<>();
            while (current != null && !positions.contains(current)) {
                positions.add(current);
                current = current.left;
            }
            return positions;
        }

        public List<GridPosition> rights() {
            GridPosition current = this;
            List<GridPosition> positions = new ArrayList<>();
            while (current != null && !positions.contains(current)) {
                positions.add(current);
                current = current.right;
            }
            return positions;
        }
    }
}
