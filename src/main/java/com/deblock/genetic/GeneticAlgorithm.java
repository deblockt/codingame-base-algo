package com.deblock.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GeneticAlgorithm<T extends Gene<T>> {

    private final ToCrossChooser<T> toCrossChooser;
    private final int nbOfGeneration;
    private final int populationSize;
    private final double mutationProbs;
    private InitialPopulationGenerator<T> initialPopulationGenerator;

    public GeneticAlgorithm(InitialPopulationGenerator<T> initialPopulationGenerator, ToCrossChooser<T> toCrossChooser, int nbGeneration, int populationSize, double mutationProbs) {
        this.initialPopulationGenerator = initialPopulationGenerator;
        this.toCrossChooser = toCrossChooser;
        this.nbOfGeneration = nbGeneration;
        this.populationSize = populationSize;
        this.mutationProbs = mutationProbs;
    }

    public T solve(PopulationViewer<T> populationViewer) {
        List<T> population = this.initialPopulationGenerator.generate();
        population.sort(Collections.reverseOrder(Comparator.comparingLong(Gene::getRank)));

        for (int i = 0; i < nbOfGeneration; ++i) {
            final List<T> sortedPopulation = population;
            populationViewer.show(population, i);

            final List<T> crossedPopulation = this.cross(population);
            final List<T> mutatedPopulation = this.mutate(crossedPopulation);

            final List<T> newPopulation = new ArrayList<>(sortedPopulation);
            newPopulation.addAll(mutatedPopulation);
            newPopulation.sort(Collections.reverseOrder(Comparator.comparingLong(Gene::getRank)));

            population = this.killSomeGene(newPopulation);
        }

        population.sort(Collections.reverseOrder(Comparator.comparingLong(Gene::getRank)));
        return population.get(0);
    }

    private List<T> killSomeGene(List<T> newPopulationSorted) {
        return newPopulationSorted
                .stream()
                .filter(Gene::isValid)
                .limit(populationSize)
                .collect(Collectors.toList());
    }

    private List<T> mutate(List<T> crossedPopulation) {
        int nbToMute = (int) (crossedPopulation.size() * this.mutationProbs);

        for (int i = 0; i < nbToMute; ++i) {
            crossedPopulation.get(i).mutating();
        }

        return crossedPopulation;
    }

    private List<T> cross(List<T> population) {
        final List<T> crossed = new ArrayList<>();
        for (int i = 0; i < populationSize; ++i) {
            final T first = this.toCrossChooser.choose(population);
            final T second = this.toCrossChooser.choose(population);
            crossed.addAll(first.crossing(second));
        }
        return crossed;
    }
}
