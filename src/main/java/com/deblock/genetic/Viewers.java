package com.deblock.genetic;

import java.util.List;

public class Viewers {
    public static PopulationViewer<?> noViewer = new NoViewer();

    private static class NoViewer<T extends Gene> implements PopulationViewer<T> {

        @Override
        public void show(List<T> population, int generation) {

        }
    }
}
