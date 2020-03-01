# Base algorithm for codingame

## Min Max

Min Max can be used to solve 1 to 1 IA game.

It can be used like that: 
```java
MinMax<ChessBoard, ChessMove> minMax = new MinMax<>();
ChessPlayer player1 = new CheesPlayer(WHITE);
ChessPlayer player2 = new ChessPlayer(BLACK);
ChessBoard board = new ChessBoard();
int minMaxDeep = 5;

// the player 1 should be you player
ChessMove move = minMax.move(player1, player2, board, minMaxDeep);

System.out.println(move);

public clas CheesPlayer implements MinMaxPlayer<ChessBoard, ChessMove> {
  // ...
}

public class ChessBoard implements MinMaxBoard<ChessMove, ChessBoard> {
    // ...
}

public class ChessPlayer implements MinMaxPlayer<ChessBoard, ChessMove> {
  // ...
}   


```

## Genetic algorithm

Genetic algorithm implementation.

It can be used like that: 

```java 
var nbGeneration = 40;
var populationSize = 70;
var mutationProbs = 0.01;
var genetic = new GeneticAlgorithm<>(
        new InitialLibraryPopulationGenerator(),
        new RandomChooser<>(),
        nbGeneration,
        populationSize,
        mutationProbs
);

genetic.solve(Viewers.noViewer);

public class GeneType implements Gene<GeneType> {

}

public class InitialLibraryPopulationGenerator implements InitialPopulationGenerator<GeneType> {

}

private static class RandomChooser<T extends GeneType> implements ToCrossChooser<T> {
    final Random random = new Random();

    @Override
    public T choose(List<T> sortedPopulation) {
        final int index = random.nextInt(sortedPopulation.size());
        return sortedPopulation.get(index);
    }
}

```

## Source generator

An helper is available to be able to merge all file on one.

You can run the class `com.deblock.builder.FileBuilder`. 
The main have the main path as parameter (like `src/main/java/Player.java`)