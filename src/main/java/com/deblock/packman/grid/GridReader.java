package com.deblock.packman.grid;

import java.util.*;

public class GridReader {

    public static Grid readForStdin(Scanner in) {
        int width = in.nextInt();
        int height = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }

        List<String> rows = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            rows.add(in.nextLine());
        }
        return readFromArray(rows);
    }

    public static GraphGrid readFromArray(List<String> rows) {
        int width = rows.get(0).length();
        int height = rows.size();
        GraphGrid gird = new GraphGrid(width, height);
        List<Position> positions = new ArrayList<>();
        Position firstEmptyCell = null;

        for (int y = 0; firstEmptyCell == null && y < rows.size(); ++y) {
            for (int x = 0; firstEmptyCell == null && x < rows.get(y).length(); ++x) {
                if (rows.get(y).charAt(x) != '#') {
                    firstEmptyCell = Position.of(x, y);
                }
            }
        }

        Set<Position> toDo = new HashSet<>();
        toDo.add(firstEmptyCell);
        while (!toDo.isEmpty()) {
            Set<Position> newPos = new HashSet<>();
            for (Position pos: toDo) {
                for (Position accessible: directAccessibleCells(rows, pos.x, pos.y)) {
                    gird.addLink(pos, accessible);
                    if (!positions.contains(accessible)) {
                        newPos.add(accessible);
                        positions.add(accessible);
                    }
                }
            }
            toDo = newPos;
        }

        return gird;
    }

    private static List<Position> directAccessibleCells(List<String> rows, int x, int y) {
        List<Position> positions = new ArrayList<>();
        if (y == 0) {
            positions.add(Position.of(x, rows.size() - 1));
        } else if (rows.get(y - 1).charAt(x) != '#') {
            positions.add(Position.of(x, y - 1));
        }
        if (y == rows.size() - 1) {
            positions.add(Position.of(x, 0));
        } else if (rows.get(y + 1).charAt(x) != '#') {
            positions.add(Position.of(x, y + 1));
        }

        if (x == 0) {
            positions.add(Position.of(rows.get(0).length() - 1, y));
        } else if (rows.get(y).charAt(x - 1) != '#') {
            positions.add(Position.of(x - 1, y));
        }

        if (x == rows.get(0).length() - 1) {
            positions.add(Position.of(0, y));
        } else if (rows.get(y).charAt(x + 1) != '#') {
            positions.add(Position.of(x + 1, y));
        }

        return positions;
    }

}
