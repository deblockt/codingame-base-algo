package com.deblock.packman.game;

import com.deblock.logger.CGLogger;
import com.deblock.packman.grid.Position;
import com.deblock.packman.move.PacMove;
import com.deblock.packman.state.Brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Pac {
    private static String ROCK = "ROCK";
    private static String PAPER = "PAPER";
    private static String SCISSORS = "SCISSORS";

    public final int id;
    public Position position = null;
    public boolean hasMoved = false;
    public String typeId;
    public int abilityCooldown = 1000;
    public int speedTurnsLeft;
    private Brain brain;
    public final boolean isPlayer1;
    public List<Position> traveledPositions;

    public Pac(int id, boolean isPlayer1, Game game) {
        this.id = id;
        this.isPlayer1 = isPlayer1;
        this.brain = new Brain(this, game);
    }

    public void refresh(Scanner in, Game game) {
        Position newPosition = Position.of(in.nextInt(), in.nextInt());
        hasMoved = !newPosition.equals(this.position);

        traveledPositions = new ArrayList<>();
        if (this.position != null) {
            List<Position> accessibleCells = game.accessibleCells(this.position, 1, 1);
            if (!accessibleCells.contains(newPosition)) {
                List<Position> accessibleCellsFromNewPosition = game.accessibleCells(newPosition, 1, 1);
                CGLogger.log("new Position " + newPosition + " accessible cells " + accessibleCellsFromNewPosition);
                CGLogger.log("old Position " + this.position + " accessible cells " + accessibleCells);
                Optional<Position> intersect = accessibleCellsFromNewPosition.stream().filter(accessibleCells::contains).findFirst();
                if (intersect.isPresent()) {
                    traveledPositions.add(intersect.get());
                } else {
                    CGLogger.log("ERREUR truc pas logique sur la calcul des déplacements");
                }
            }
        }
        traveledPositions.add(newPosition);

        this.position = newPosition;
        this.typeId = in.next();
        this.speedTurnsLeft = in.nextInt();
        this.abilityCooldown = in.nextInt();
    }

    public PacMove play(List<PacMove> partnerMoves) {
        return this.brain.play(partnerMoves);
    }

    public int speed() {
        return this.speedTurnsLeft > 0 ? 2: 1;
    }
    public Position position() {
        return this.position;
    }

    public String typeToWin(Pac other) {
        if (other.typeId.equals(ROCK)) {
            return PAPER;
        } else if (other.typeId.equals(PAPER)) {
            return SCISSORS;
        } else {
            return ROCK;
        }
    }

    @Override
    public String toString() {
        return "[mine: " + this.isPlayer1 + " , " + this.position + "]";
    }
}
