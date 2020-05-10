package com.deblock;


import com.deblock.logger.CGLogger;
import com.deblock.packman.move.PacMove;
import com.deblock.packman.game.Game;
import com.deblock.packman.grid.GridReader;
import com.deblock.packman.game.Pac;

import java.util.*;
import java.util.stream.Collectors;

class Player {

    public static void main(String args[]) {
        CGLogger.submissionMode();

        Scanner in = new Scanner(System.in);
        Game game = new Game();
        game.setGrid(GridReader.readForStdin(in));
        Map<Integer, PacMove> lastMoves = new HashMap<>();
        // game loop
        while (true) {
            CGLogger.disableLog();
            game.refreshPacPlayers(in);
            game.refreshPellets(in);
            CGLogger.enableLog();
            CGLogger.startTurn();
            CGLogger.log("\n" + game.toString());

            for (Integer pacId: new ArrayList<>(lastMoves.keySet())) {
                if (!game.player1.hasPac(pacId)) {
                    lastMoves.remove(pacId);
                }
            }

            List<PacMove> moves = new ArrayList<>();
            for (Pac pac: game.getPacPlayer1().getPacs()) {
                List<PacMove> otherMoves = lastMoves.entrySet().stream().filter(entry -> entry.getKey() != pac.id).map(Map.Entry::getValue).collect(Collectors.toList());
                PacMove move = pac.play(otherMoves);
                moves.add(move);
                lastMoves.put(pac.id, move);
            }
            String moveString = moves.stream()
                    .map(PacMove::action)
                    .filter(move -> move.length() > 0)
                    .collect(Collectors.joining("|"));

            CGLogger.log("end turn");
            System.out.println(moveString);
        }
    }
}