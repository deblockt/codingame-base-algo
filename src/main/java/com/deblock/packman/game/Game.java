package com.deblock.packman.game;

import com.deblock.logger.CGLogger;
import com.deblock.packman.grid.Grid;
import com.deblock.packman.grid.Position;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    public final Set<Position> player1HistoryPosition = new HashSet<>();
    private Grid grid;
    private Map<Position, Pellet> pellets = new HashMap<>();
    private final Set<Position> bestPellets =  new HashSet<>();
    public final PacPlayer player1 = new PacPlayer(this, true);
    private PacPlayer player2 = new PacPlayer(this, false);

    public void setGrid(Grid grid) {
        this.grid = grid;
        this.grid.allEmptyCells().forEach(position -> {
            pellets.put(position, new Pellet(position, 1));
        });
    }

    @Override
    public String toString() {
        return grid.toString(pellets.values(), this);
    }

    public Set<Position> getBestPellets() {
        return bestPellets;
    }

    public void refreshPacPlayers(Scanner in) {
        int myScore = in.nextInt();
        int opponentScore = in.nextInt();
        int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
        List<Integer> allPacIdsPlayer1 = new ArrayList<>();
        List<Integer> allPacIdsPlayer2 = new ArrayList<>();
        for (int i = 0; i < visiblePacCount; i++) {
            int pacId = in.nextInt(); // pac number (unique within a team)
            boolean mine = in.nextInt() != 0; // true if this pac is yours

            if (mine) {
                allPacIdsPlayer1.add(pacId);
            } else {
                allPacIdsPlayer2.add(pacId);
            }
            PacPlayer playerToUpdate = mine ? player1 : player2;
            playerToUpdate.refreshPac(pacId, in);
        }

        player1.removeUnseePac(allPacIdsPlayer1);
        player2.removeUnseePac(allPacIdsPlayer2);
        player1HistoryPosition.addAll(
            player1.getPacs().stream().flatMap(pac -> pac.traveledPositions.stream()).collect(Collectors.toList())
        );

        List<Pac> pacs = new ArrayList<>(player1.getPacs());
        pacs.addAll(player2.getPacs());
        this.grid.refresh(pacs);
    }

    private Set<Position> pacVision(Collection<Pac> pacs) {
        Set<Position> result = new HashSet<>();
        for (Pac pac: pacs) {
            List<Position> visibleCells = grid.visibleCells(pac.position);
            CGLogger.log("pac " + pac.id + "can see " +  visibleCells);
            result.addAll(visibleCells);
        }
        return result;
    }

    public void refreshPellets(Scanner in) {
        Set<Position> visiblePositions = this.pacVision(player1.getPacs());
        Set<Position> visiblePellets = new HashSet<>();
        int visiblePelletCount = in.nextInt();
        for (int i = 0; i < visiblePelletCount; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            Position position = Position.of(x, y);
            visiblePellets.add(position);
            int value = in.nextInt();
            if (value == 10) {
                bestPellets.add(position);
            }
        }

        Set<Position> pelletsToRemove = new HashSet<>(visiblePositions);
        pelletsToRemove.removeAll(visiblePellets);
        for (Position position: pelletsToRemove) {
            pellets.remove(position);
            player1HistoryPosition.add(position);
            bestPellets.remove(position);
        }
    }

    public Grid getGrid() {
        return this.grid;
    }

    public List<Pellet> getPellets() {
        return new ArrayList<>(this.pellets.values());
    }

    public PacPlayer getPacPlayer1() {
        return player1;
    }

    public List<Position> accessibleCells(Position from, int maximalDeep, int minimalDeep) {
        return this.grid.accessibleCells(from, maximalDeep, minimalDeep);
    }
    public List<Position> accessibleCells(Position from, int maximalDeep, int minimalDeep, List<Position> otherPacs) {
        return this.grid.accessibleCells(from, maximalDeep, minimalDeep, otherPacs);
    }

    public List<Pac> getEnemiesOn(List<Position> positions) {
        return player2.getPacs().stream()
                .filter(pac -> positions.stream().anyMatch(pos -> pos.equals(pac.position())))
                .collect(Collectors.toList());
    }

    public List<Pac> getPartnerOn(List<Position> positions) {
        return player1.getPacs().stream()
                .filter(pac -> positions.stream().anyMatch(pos -> pos.equals(pac.position())))
                .collect(Collectors.toList());
    }

    public boolean containsPellet(Position position) {
        return this.pellets.containsKey(position);
    }

    public void addPac(Pac pac, Position of) {
        if (pac.isPlayer1) {
            player1.addPac(pac);
        } else {
            player2.addPac(pac);
        }
        pac.position(of);
    }

}
