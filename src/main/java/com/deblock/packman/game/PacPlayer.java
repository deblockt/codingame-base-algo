package com.deblock.packman.game;

import java.util.*;
import java.util.stream.Collectors;

public class PacPlayer {
    private Map<Integer, Pac> pacs = new HashMap<>();
    private Map<Integer, Pac> deadPacs = new HashMap<>();
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
        Pac pac = pacs.get(pacId).refresh(in, game);
        if (pac.typeId.equals(Pac.DEAD)) {
            pacs.remove(pac.id);
            deadPacs.put(pac.id, pac);
        }
    }

    public Collection<Pac> getPacs() {
        return pacs.values();
    }

    public void removeUnseePac(List<Integer> allPacIds) {
        List<Integer> deletedPacId = pacs.keySet().stream().filter(pacId -> !allPacIds.contains(pacId)).collect(Collectors.toList());
        for (Integer id: deletedPacId) {
            pacs.remove(id);
        }
    }

    public boolean hasPac(Integer pacId) {
        return this.pacs.containsKey(pacId);
    }

    public void addPac(Pac pac) {
        this.pacs.put(pac.id, pac);
    }
}
