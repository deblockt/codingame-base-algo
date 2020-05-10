package com.deblock.path;

import com.deblock.logger.CGLogger;

import java.util.*;

public class Dijkstra<T extends DijkstraPosition<T>> {

    private final CellWeigher<T> cellWeigher;

    public Dijkstra(CellWeigher<T> cellWeigher) {
        this.cellWeigher = cellWeigher;
    }

    public List<T> path(T from, T to) {
        List<T> list = new ArrayList<>();
        list.add(to);
        return pathToNearest(from, list);
    }

    /**
     * DijkstraPosition should implement equals and hashcode
     */
    public List<T> pathToNearest(T from, List<T> to) {
        CGLogger.log("path to nearest. from " + from + " to " + to);
        final DijkstraNode<T> firstNode = new DijkstraNode<>(from, null, 0, 0);

        final Set<T> optimized = new HashSet<>();
        final PriorityQueue<DijkstraNode<T>> unvisited = new PriorityQueue<>(Comparator.comparingInt(n -> n.totalWeigth));
        unvisited.add(firstNode);
        DijkstraNode<T> foundNode = null;

        while (foundNode == null && !unvisited.isEmpty()) {
            DijkstraNode<T> current = unvisited.poll();
            optimized.add(current.position);

            if (to.contains(current.position)) {
                foundNode = current;
            } else {
                List<T> neighbour = current.position.getNeighbour(current.pathLength);
                for (T nei: neighbour) {
                    if (!optimized.contains(nei)) {
                        DijkstraNode<T> node = new DijkstraNode<T>(
                                nei, current,
                                current.totalWeigth + cellWeigher.weight(current.position, nei),
                                current.pathLength + 1
                        );
                        unvisited.remove(node);
                        unvisited.add(node);
                    }
                }
            }
        }

        CGLogger.log("found node " + foundNode);
        if (foundNode != null) {
            return this.buildPath(foundNode);
        } else {
            return null;
        }
    }

    private List<T> buildPath(DijkstraNode<T> foundNode) {
        List<T> reversePath = new ArrayList<>();
        DijkstraNode<T> currentNode = foundNode;
        while (currentNode.from != null) {
            reversePath.add(currentNode.position);
            currentNode = currentNode.from;
        }
        Collections.reverse(reversePath);
        return reversePath;
    }
}
