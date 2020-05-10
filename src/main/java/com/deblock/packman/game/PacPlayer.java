package com.deblock.packman.game;

import java.util.*;
import java.util.stream.Collectors;

public class PacPlayer {
    private Map<Integer, Pac> pacs = new HashMap<>();
    private final Game game;
    private boolean isPlayer1;

    public PacPlayer(Game game, boolean isPlayer1) {
        this.game = game;
        this.isPlayer1 = isPlayer1;
    }

    public void refreshPac(int pacId, Scanner in) {
        if (!pacs.containsKey(pacId)) {
            pacs.put(pacId, new Pac(pacId, isPlayer1, game));
        }
        pacs.get(pacId).refresh(in, game);
    }

    public Collection<Pac> getPacs() {
        return pacs.values();
    }

    public void removeDeletedPac(List<Integer> allPacIds) {
        List<Integer> deletedPacId = pacs.keySet().stream().filter(pacId -> !allPacIds.contains(pacId)).collect(Collectors.toList());
        for (Integer id: deletedPacId) {
            pacs.remove(id);
        }
    }

    public boolean hasPac(Integer pacId) {
        return this.pacs.containsKey(pacId);
    }
}
