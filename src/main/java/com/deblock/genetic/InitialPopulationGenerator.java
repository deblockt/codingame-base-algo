package com.deblock.genetic;

import java.util.List;

public interface InitialPopulationGenerator<T extends Gene> {
    List<T> generate();
}
