package com.deblock.genetic;

import java.util.List;

public interface ToCrossChooser<T extends Gene> {
    /**
     * choose a population to cross them
     * @param sortedPopulation the population sorted by rank
     */
    T choose(List<T> sortedPopulation);
}
