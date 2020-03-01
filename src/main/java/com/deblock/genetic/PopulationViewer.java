package com.deblock.genetic;

import java.util.List;

public interface PopulationViewer<T extends Gene> {
    void show(List<T> population, int generation);
}
