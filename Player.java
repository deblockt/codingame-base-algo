import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
class Player {
	private static class CGLogger {
	    private static long startTurn = 0;
	    private static boolean logEnabled = false;
	    private static boolean submissionMode = false;
	    public static void startTurn() {
	        startTurn = System.currentTimeMillis();
	    }
	    public static void enableLog() {
	        logEnabled = true;
	    }
	    public static void disableLog() {
	        logEnabled = false;
	    }
	    public static void log(String message) {
	        if (!submissionMode && logEnabled) {
	            long duration = System.currentTimeMillis() - startTurn;
	            System.err.println(duration + " : " + message);
	        }
	    }
	    public static void submissionMode() {
	        submissionMode = true;
	    }
	    public static boolean isSubmissionMode() {
	        return submissionMode;
	    }
	}
	private static class PacPlayer {
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
	private static class Pellet {
	    public final Position position;
	    public final int value;
	    public Pellet(Position position, int value) {
	        this.position = position;
	        this.value = value;
	    }
	    @Override
	    public String toString() {
	        return "Pellet[ " + this.position + " / " + value + "]";
	    }
	    @Override
	    public boolean equals(Object obj) {
	        if (!(obj instanceof Pellet)) {
	            return false;
	        }
	        return this.position.equals(((Pellet) obj).position);
	    }
	    @Override
	    public int hashCode() {
	        return this.position.hashCode();
	    }
	}
	private static class Switch implements PacMove {
	    private final Pac pac;
	    private final String typeToWin;
	    public Switch(Pac pac, String typeToWin) {
	        this.pac = pac;
	        this.typeToWin = typeToWin;
	    }
	    @Override
	    public String action() {
	        return "SWITCH " + pac.id + " " + typeToWin;
	    }
	}
	private static class GoTo implements PacMove {
	    private final Path path;
	    private final Position target;
	    private final Pac pac;
	    private final String comment;
	    public GoTo(Position target, Path path, Pac pac, String comment) {
	        this.path = path;
	        this.target = target;
	        this.pac = pac;
	        this.comment = comment;
	    }
	    public GoTo(Pac pac, Position position, String message) {
	        this(position, new Path(), pac, message + " " + position);
	        this.path.add(position.x, position.y);
	    }
	    public Position target() {
	        return this.target;
	    }
	    public String action() {
	        if (this.path == null || this.path.size() == 0) {
	            CGLogger.log("NO PATH FOUND TO GO TO " + target + " for pac " + pac.id);
	            return "";
	        }
	        final Position nextPosition;
	        CGLogger.log("GOTO " + pac.speedTurnsLeft);
	        if (pac.speedTurnsLeft > 0) {
	            CGLogger.log("using path " + this.path);
	            nextPosition = this.path.pop(2);
	        } else {
	            nextPosition = this.path.pop();
	        }
	        return "MOVE " + pac.id + " " + nextPosition.x + " " + nextPosition.y + " " + comment;
	    }
	    @Override
	    public String toString() {
	        return this.action();
	    }
	}
	private static class SpeedUp implements PacMove {
	    private final Pac pac;
	    public SpeedUp(Pac pac) {
	        this.pac = pac;
	    }
	    @Override
	    public String action() {
	        return "SPEED " + pac.id;
	    }
	}
	private static class Wait implements PacMove {
	    @Override
	    public String action() {
	        return "";
	    }
	}
	private static class SpeedUpBehaviour implements Behaviours {
	    private final Pac pac;
	    public SpeedUpBehaviour(Pac pac) {
	        this.pac = pac;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        if (pac.abilityCooldown == 0) {
	            return Optional.of(new SpeedUp(pac));
	        }
	        return Optional.empty();
	    }
	}
	private static class SuperPelletTargetChooser implements GoToTargetBehaviour.TargetChooser {
	    @Override
	    public Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
	        return getPelletForPac(game, pac);
	    }
	    private static Map<Integer, Path> pelletByPacs = null;
	    public static Optional<Path> getPelletForPac(Game game, Pac pac) {
	        CGLogger.log("search best pellet solution 2");
	        if (pelletByPacs == null) {
	            pelletByPacs = new HashMap<>();
	            Map<Position, Pac> myPacPosition = game.player1.getPacs().stream().collect(Collectors.toMap(Pac::position, Function.identity()));
	            List<Position> pacs = duplicateBySymmetry(game, myPacPosition.keySet());
	            for (Position pellet : game.getBestPellets()) {
	                Path path = game.getGrid().getPathToNearest(pellet, pacs);
	                if (path != null) {
	                    if (myPacPosition.containsKey(path.target())) {
	                        pacs.remove(path.target());
	                        CGLogger.log("pellet " + pellet + " is for pac " + path.target());
	                        pelletByPacs.put(myPacPosition.get(path.target()).id, path.reverse().add(pellet.x, pellet.y));
	                    } else {
	                        CGLogger.log("pellet " + pellet + " is for enemy");
	                    }
	                }
	            }
	        }
	        return Optional.ofNullable(pelletByPacs.get(pac.id))
	                .filter(path -> {
	                    CGLogger.log("found a pellet " + path.target());
	                    boolean pelletExists = game.getBestPellets().contains(path.target());
	                    CGLogger.log("is the pellet exists " + pelletExists);
	                    return pelletExists;
	                });
	    }
	    public static List<Position> duplicateBySymmetry(Game game, Collection<Position> myPacPosition) {
	        int symCenter = game.getGrid().getWidth() / 2;
	        List<Position> sym = new ArrayList<>();
	        for (Position pac: myPacPosition) {
	            sym.add(pac);
	            sym.add(Position.of(symCenter * 2 - pac.x, pac.y));
	        }
	        return sym;
	    }
	    @Override
	    public String behaviourInfo() {
	        return "super pellet";
	    }
	}
	private static class SwitchIfDontMoveAndNearEnemies implements Behaviours {
	    private final Pac pac;
	    private Position lastPosition;
	    public SwitchIfDontMoveAndNearEnemies(Pac pac) {
	        this.pac = pac;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        CGLogger.log("speed turn left " + pac.speedTurnsLeft);
	        CGLogger.log(pac.id + " before " + this.lastPosition + " --> " + pac.position);
	        if (this.lastPosition != null && this.lastPosition.equals(pac.position()) && pac.speedTurnsLeft != 5) {
	            CGLogger.log("I can't move " + pac.id);
	            List<Position> positions =  game.accessibleCells(pac.position(), 1, pac.speed());
	            CGLogger.log("I try to go " + positions);
	            List<Pac> enemies = game.getEnemiesOn(positions);
	            List<Pac> friends = game.getPartnerOn(positions);
	            CGLogger.log("I have found enemis on this cell " + enemies);
	            if (enemies.isEmpty()) {
	                if (!friends.isEmpty()) {
	                    CGLogger.log("I am block with a friend :'(");
	                } else if (pac.abilityCooldown > 0) {
	                    CGLogger.log("I'm block but i can not switch " + pac.abilityCooldown);
	                } else {
	                    CGLogger.log("I don't know who block me. I try to switch");
	                    String typeToWin = pac.typeToWin(pac);
	                    this.updatePosition();
	                    return Optional.of(new Switch(pac, typeToWin));
	                }
	            } else {
	                if (pac.abilityCooldown > 0) {
	                    CGLogger.log("I can not switch " + pac.abilityCooldown);
	                } else {
	                    String typeToWin = pac.typeToWin(enemies.get(0));
	                    if (pac.typeId.equals(typeToWin)) {
	                        this.updatePosition();
	                        return Optional.of(new GoTo(pac, enemies.get(0).position, "go to kill enemies"));
	                    } else {
	                        this.updatePosition();
	                        return Optional.of(new Switch(pac, typeToWin));
	                    }
	                }
	            }
	        }
	        this.updatePosition();
	        return Optional.empty();
	    }
	    private void updatePosition() {
	        this.lastPosition = pac.position();
	    }
	}
	private static class RetryBehaviour implements Behaviours {
	    private final Behaviours b;
	    public RetryBehaviour(Behaviours b1) {
	        this.b = b1;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        for (int i = 0; i < 5; ++i) {
	            Optional<PacMove> move = b.nextMove(game, partnerMoves);
	            if (move.isPresent()) {
	                return move;
	            }
	        }
	        return Optional.empty();
	    }
	}
	private static interface Behaviours {
	    Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves);
	}
	private static class GoToNearestInterestingCellTargetChooser implements GoToTargetBehaviour.TargetChooser {
	    private final Pac pac;
	    public GoToNearestInterestingCellTargetChooser(Pac pac) {
	        this.pac = pac;
	    }
	    @Override
	    public Optional<Position> chooseTarget(Pac pac, Game game, List<PacMove> partnerMoves) {
	        for (int i = pac.speed(); i <= 10; ++i) {
	            Optional<Position> moveTry = tryToMove(game, i);
	            if (moveTry.isPresent()) {
	                return moveTry;
	            }
	        }
	        return Optional.empty();
	    }
	    private Optional<Position> tryToMove(Game game, int deep) {
	        List<Position> cells = game.accessibleCells(pac.position, deep, pac.speed());
	        List<Position> nonVisitedPositions = cells.stream().filter(p -> !game.player1HistoryPosition.contains(p)).collect(Collectors.toList());
	        Optional<Position> nonVisitedPositionsWithPellet = nonVisitedPositions.stream().filter(game::containsPellet).max(Comparator.comparingInt(pos -> nbPelletNear(game, pos)));
	        CGLogger.log("accessible cells " + cells);
	        CGLogger.log("non visited cells " + nonVisitedPositions);
	        CGLogger.log("non visited position with pellet " + nonVisitedPositionsWithPellet);
	        if (nonVisitedPositionsWithPellet.isPresent()) {
	            CGLogger.log("go to nearest pellet");
	            return nonVisitedPositionsWithPellet;
	        } else if (nonVisitedPositions.size() > 0) {
	            CGLogger.log("go to nearest non visited position");
	            return Optional.of(nonVisitedPositions.get(0));
	        } else {
	            return Optional.empty();
	        }
	    }
	    private int nbPelletNear(Game game, Position pos) {
	        return (int) game.accessibleCells(pos, 1, 1)
	                .stream()
	                .filter(game::containsPellet)
	                .count();
	    }
	    @Override
	    public String behaviourInfo() {
	        return "interesting";
	    }
	}
	private static class Utils {
	    public static boolean isNotUsed(Position target, List<PacMove> partnerMoves) {
	        for (PacMove move: partnerMoves) {
	            if (move instanceof GoTo && ((GoTo) move).target().equals(target)) {
	                return false;
	            }
	        }
	        return true;
	    }
	}
	private static class GoToTargetBehaviour implements Behaviours {
	    private final TargetChooser targetChooser;
	    private final Pac pac;
	    private Position target;
	    private final boolean stopIfNoPellet;
	    private final boolean recomputeEachTurn;
	    public GoToTargetBehaviour(Pac pac, TargetChooser targetChooser, boolean stopIfNoPellet, boolean recomputeEachTurn) {
	        this.targetChooser = targetChooser;
	        this.pac = pac;
	        this.stopIfNoPellet = stopIfNoPellet;
	        this.recomputeEachTurn = recomputeEachTurn;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        CGLogger.log("using " + this.targetChooser.behaviourInfo());
	        Path targetPath = null;
	        if (recomputeEachTurn || this.target == null ||
	            pac.position.equals(target) || (stopIfNoPellet && !game.containsPellet(target)) ||
	            !Utils.isNotUsed(target, partnerMoves)
	        ) {
	            targetPath = this.targetChooser.chooseTargetPath(pac, game, partnerMoves).orElse(null);
	            if (targetPath != null) {
	                target = targetPath.target();
	            } else {
	                target = null;
	            }
	        }
	        if (this.target == null) {
	            CGLogger.log("no target... Do nothing");
	            return Optional.empty();
	        }
	        CGLogger.log("current target " + target);
	        if (targetPath == null) {
	            targetPath = game.getGrid().path(pac.position, target);
	        }
	        if (targetPath == null) {
	            target = null;
	            return Optional.empty();
	        }
	        return Optional.of(new GoTo(target, targetPath, pac, targetChooser.behaviourInfo() + " > " + this.target));
	    }
	    public interface TargetChooser {
	        default Optional<Position> chooseTarget(Pac pac, Game game, List<PacMove> partnerMoves) {
	            return Optional.empty();
	        }
	        default Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
	            return this.chooseTarget(pac, game, partnerMoves)
	                .flatMap(target -> Optional.ofNullable(game.getGrid().path(pac.position, target)));
	        }
	        String behaviourInfo();
	    }
	}
	private static class GoToRandomBehaviour implements Behaviours {
	    private final Pac pac;
	    public GoToRandomBehaviour(Pac pac) {
	        this.pac = pac;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        List<Position> cells = game.accessibleCells(pac.position, 1, pac.speed());
	        return Optional.of(new GoTo(pac, cells.get(new Random().nextInt(cells.size())), "go to random cell"));
	    }
	}
	private static class SwitchIfNearEnemies implements Behaviours {
	    private final Pac pac;
	    public SwitchIfNearEnemies(Pac pac) {
	        this.pac = pac;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        List<Position> accessibleCells =  game.accessibleCells(pac.position(), pac.speed() * 2, 1);
	        List<Pac> enemies = game.getEnemiesOn(accessibleCells);
	        CGLogger.log("enemies " + enemies);
	        if (!enemies.isEmpty()) {
	            Pac enemy = enemies.get(0);
	            String typeToWin = pac.typeToWin(enemy);
	            if (pac.abilityCooldown == 0 && !typeToWin.equals(pac.typeId)) {
	                CGLogger.log("I switch to avoid to be killed");
	                return Optional.of(new Switch(pac, typeToWin));
	            } else if (typeToWin.equals(pac.typeId) && enemy.abilityCooldown > 0 && enemy.speed() <= pac.speed()) {
	                CGLogger.log("Go to enemies cell to kill it");
	                Path path = new Path();
	                path.add(enemy.position.x, enemy.position.y);
	                return Optional.of(new GoTo(enemy.position, path, pac, "I will kill you"));
	            } else if ((!typeToWin.equals(pac.typeId) && !pac.typeId.equals(enemy.typeId)) || enemy.abilityCooldown == 0){
	                CGLogger.log("I go back. I don't want to die");
	                List<Position> directAccessibleCells =  game.accessibleCells(pac.position(), pac.speed(), 1);
	                CGLogger.log("accessible cells: " + directAccessibleCells);
	                List<Position> safeCells = directAccessibleCells.stream()
	                        .filter(cell -> !haveEnemiesNear(cell, game, enemies, pac.speed()))
	                        .collect(Collectors.toList());
	                if (safeCells.size() == 1) {
	                    CGLogger.log("found only one safe cell. go to " + safeCells.get(0));
	                    return Optional.of(new GoTo(pac, safeCells.get(0), "go back"));
	                } else {
	                    CGLogger.log("found many safe cell. search cell with the much path " + safeCells);
	                    Optional<Position> moreSafeCell = safeCells.stream()
	                                        .max(Comparator.comparingInt(cell -> scoreCell(game, cell, pac.position)));
	                    CGLogger.log("found " + moreSafeCell);
	                    return moreSafeCell.map(cell -> new GoTo(pac, cell, "go back"));
	                }
	            }
	        }
	        return Optional.empty();
	    }
	    private int scoreCell(Game game, Position cell, Position pacPosition) {
	        int nbAccessCells = game.getGrid().accessibleCells(cell, 10, 1, pacPosition).size();
	        List<Position> nbAccessibleCellsForPellet = game.getGrid().accessibleCells(cell, 2, 1, pacPosition);
	        int nbPelletAtTwoStep = (int) nbAccessibleCellsForPellet.stream().filter(game::containsPellet).count();
	        CGLogger.log("cell " + cell + ", score " + (nbAccessCells - nbPelletAtTwoStep) + " nbAccessible " + nbAccessCells + " nbPellet " + nbPelletAtTwoStep);
	        return nbAccessCells + nbPelletAtTwoStep;
	    }
	    private boolean haveEnemiesNear(Position cell, Game game, List<Pac> enemies, int speed) {
	        List<Position> positions = game.accessibleCells(cell, speed, 1);
	        positions.add(cell);
	        for (Position pos: positions) {
	            if (enemies.stream().anyMatch(e -> e.position.equals(pos))) {
	                return true;
	            }
	        }
	        return false;
	    }
	}
	private static class RandomPelletTargetChooser implements GoToTargetBehaviour.TargetChooser {
	    @Override
	    public Optional<Path> chooseTargetPath(Pac pac, Game game, List<PacMove> partnerMoves) {
	        List<Position> bestPellets = game.getPellets().stream()
	                .filter(p -> Utils.isNotUsed(p.position, partnerMoves))
	                .map(p -> p.position)
	                .collect(Collectors.toList());
	        CGLogger.log("available pellets " + game.getPellets());
	        CGLogger.log("bestPellets " + bestPellets);
	        if (bestPellets.size() == 0) {
	            return Optional.empty();
	        }
	        return Optional.of(game.getGrid().getPathToNearest(pac.position, bestPellets));
	    }
	    @Override
	    public String behaviourInfo() {
	        return "random";
	    }
	}
	private static class ComposableBehaviour implements Behaviours {
	    private final Behaviours[] behaviours;
	    public ComposableBehaviour(Behaviours... behaviours) {
	        this.behaviours = behaviours;
	    }
	    @Override
	    public Optional<PacMove> nextMove(Game game, List<PacMove> partnerMoves) {
	        for (int i = 0; i < behaviours.length; ++i) {
	            CGLogger.log("Start behaviour " + behaviours[i].getClass());
	            Optional<PacMove> move = behaviours[i].nextMove(game, partnerMoves);
	            if (move.isPresent()) {
	                return move;
	            }
	        }
	        return Optional.empty();
	    }
	}
	private static class Brain {
	    private final Pac pac;
	    private final Game game;
	    private final Behaviours behaviours;
	    public Brain(Pac pac, Game game) {
	        this.pac = pac;
	        this.game = game;
	        this.behaviours = new ComposableBehaviour(
	            new SwitchIfNearEnemies(pac),
	            new SwitchIfDontMoveAndNearEnemies(pac),
	            new SpeedUpBehaviour(pac),
	            new GoToTargetBehaviour(pac, new SuperPelletTargetChooser(), true, false),
	            new GoToTargetBehaviour(pac, new GoToNearestInterestingCellTargetChooser(pac), false, true),
	            new GoToTargetBehaviour(pac, new RandomPelletTargetChooser(), true, false),
	            new GoToRandomBehaviour(pac)
	        );
	    }
	    public PacMove play(List<PacMove> partnerMoves) {
	        CGLogger.log(pac.id + " start playing");
	        PacMove move = behaviours.nextMove(game, partnerMoves).orElse(new Wait());
	        CGLogger.log(pac.id + " end playing");
	        return move;
	    }
	}
	private static class Pac {
	    public final static String ROCK = "ROCK";
	    public final static String PAPER = "PAPER";
	    public final static String SCISSORS = "SCISSORS";
	    public final static String DEAD = "DEAD";
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
	    public Pac(int id, boolean isPlayer1, Game game, String type) {
	        this.id = id;
	        this.isPlayer1 = isPlayer1;
	        this.typeId = type;
	        this.brain = new Brain(this, game);
	    }
	    public Pac refresh(Scanner in, Game game) {
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
	        return this;
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
	    public void position(Position pos) {
	        this.position = pos;
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
	private static interface Grid {
	    Path path(Position position, Position position1);
	    void refresh(List<Pac> pacs);
	    List<Position> accessibleCells(Position from, int deep, int minimalDeep);
	    List<Position> accessibleCells(Position from, int deep, int minimalDeep, Position avoidWalkOn);
	    String toString(Collection<Pellet> pellets, Game game);
	    List<Position> visibleCells(Position position);
	    List<Position> allEmptyCells();
	    Path getPathToNearest(Position from, List<Position> allBestPellet);
	    int getWidth();
	    int getHeight();
	}
	private static class Game {
	    public final Set<Position> player1HistoryPosition = new HashSet<>();
	    private Grid grid;
	    private Map<Position, Pellet> pellets = new HashMap<>();
	    private final Set<Position> bestPellets =  new HashSet<>();
	    public final PacPlayer player1 = new PacPlayer(this, true);
	    private PacPlayer player2 = new PacPlayer(this, false);
	    public void setGrid(Grid grid) {
	        this.grid = grid;
	        this.grid.allEmptyCells().forEach(position -> {
	            pellets.put(position, new Pellet(position, 1));
	        });
	    }
	    @Override
	    public String toString() {
	        return grid.toString(pellets.values(), this);
	    }
	    public Set<Position> getBestPellets() {
	        return bestPellets;
	    }
	    public void refreshPacPlayers(Scanner in) {
	        int myScore = in.nextInt();
	        int opponentScore = in.nextInt();
	        int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
	        List<Integer> allPacIdsPlayer1 = new ArrayList<>();
	        List<Integer> allPacIdsPlayer2 = new ArrayList<>();
	        for (int i = 0; i < visiblePacCount; i++) {
	            int pacId = in.nextInt(); // pac number (unique within a team)
	            boolean mine = in.nextInt() != 0; // true if this pac is yours
	            if (mine) {
	                allPacIdsPlayer1.add(pacId);
	            } else {
	                allPacIdsPlayer2.add(pacId);
	            }
	            PacPlayer playerToUpdate = mine ? player1 : player2;
	            playerToUpdate.refreshPac(pacId, in);
	        }
	        player1.removeUnseePac(allPacIdsPlayer1);
	        player2.removeUnseePac(allPacIdsPlayer2);
	        player1HistoryPosition.addAll(
	            player1.getPacs().stream().flatMap(pac -> pac.traveledPositions.stream()).collect(Collectors.toList())
	        );
	        List<Pac> pacs = new ArrayList<>(player1.getPacs());
	        pacs.addAll(player2.getPacs());
	        this.grid.refresh(pacs);
	    }
	    private Set<Position> pacVision(Collection<Pac> pacs) {
	        Set<Position> result = new HashSet<>();
	        for (Pac pac: pacs) {
	            List<Position> visibleCells = grid.visibleCells(pac.position);
	            CGLogger.log("pac " + pac.id + "can see " +  visibleCells);
	            result.addAll(visibleCells);
	        }
	        return result;
	    }
	    public void refreshPellets(Scanner in) {
	        Set<Position> visiblePositions = this.pacVision(player1.getPacs());
	        Set<Position> visiblePellets = new HashSet<>();
	        int visiblePelletCount = in.nextInt();
	        for (int i = 0; i < visiblePelletCount; i++) {
	            int x = in.nextInt();
	            int y = in.nextInt();
	            Position position = Position.of(x, y);
	            visiblePellets.add(position);
	            int value = in.nextInt();
	            if (value == 10) {
	                bestPellets.add(position);
	            }
	        }
	        Set<Position> pelletsToRemove = new HashSet<>(visiblePositions);
	        pelletsToRemove.removeAll(visiblePellets);
	        for (Position position: pelletsToRemove) {
	            pellets.remove(position);
	            player1HistoryPosition.add(position);
	            bestPellets.remove(position);
	        }
	    }
	    public Grid getGrid() {
	        return this.grid;
	    }
	    public List<Pellet> getPellets() {
	        return new ArrayList<>(this.pellets.values());
	    }
	    public PacPlayer getPacPlayer1() {
	        return player1;
	    }
	    public List<Position> accessibleCells(Position from, int maximalDeep, int minimalDeep) {
	        return this.grid.accessibleCells(from, maximalDeep, minimalDeep);
	    }
	    public List<Pac> getEnemiesOn(List<Position> positions) {
	        return player2.getPacs().stream()
	                .filter(pac -> positions.stream().anyMatch(pos -> pos.equals(pac.position())))
	                .collect(Collectors.toList());
	    }
	    public List<Pac> getPartnerOn(List<Position> positions) {
	        return player1.getPacs().stream()
	                .filter(pac -> positions.stream().anyMatch(pos -> pos.equals(pac.position())))
	                .collect(Collectors.toList());
	    }
	    public boolean containsPellet(Position position) {
	        return this.pellets.containsKey(position);
	    }
	    public void addPac(Pac pac, Position of) {
	        if (pac.isPlayer1) {
	            player1.addPac(pac);
	        } else {
	            player2.addPac(pac);
	        }
	        pac.position(of);
	    }
	}
	private static interface DijkstraPosition<SELF extends DijkstraPosition> {
	    List<SELF> getNeighbour(int nbNodeToGo);
	}
	private static interface CellWeigher<T extends DijkstraPosition<T>> {
	    int weight(T from, T to);
	}
	private static class DijkstraNode<T extends DijkstraPosition<T>> {
	    public final T position;
	    public final DijkstraNode<T> from;
	    public final int totalWeigth;
	    public final int pathLength;
	    public DijkstraNode(T position, DijkstraNode<T> from, int totalWeight, int pathLength) {
	        this.position = position;
	        this.from = from;
	        this.totalWeigth = totalWeight;
	        this.pathLength = pathLength;
	    }
	    @Override
	    public boolean equals(Object obj) {
	        if (!(obj instanceof DijkstraNode)) {
	            return false;
	        }
	        return this.position.equals(((DijkstraNode) obj).position);
	    }
	    @Override
	    public int hashCode() {
	        return this.position.hashCode();
	    }
	    @Override
	    public String toString() {
	        return position.toString();
	    }
	}
	private static class Dijkstra<T extends DijkstraPosition<T>> {
	    private final CellWeigher<T> cellWeigher;
	    public Dijkstra(CellWeigher<T> cellWeigher) {
	        this.cellWeigher = cellWeigher;
	    }
	    public List<T> path(T from, T to) {
	        List<T> list = new ArrayList<>();
	        list.add(to);
	        return pathToNearest(from, list);
	    }
	    public List<T> pathToNearest(T from, List<T> to) {
	        CGLogger.log("path to nearest. from " + from + " to " + to);
	        final DijkstraNode<T> firstNode = new DijkstraNode<>(from, null, 0, 0);
	        final Set<T> optimized = new HashSet<>();
	        final PriorityQueue<DijkstraNode<T>> unvisited = new PriorityQueue<>(Comparator.comparingInt(n -> n.totalWeigth));
	        unvisited.add(firstNode);
	        DijkstraNode<T> foundNode = null;
	        while (foundNode == null && !unvisited.isEmpty()) {
	            DijkstraNode<T> current = unvisited.poll();
	            optimized.add(current.position);
	            if (to.contains(current.position)) {
	                foundNode = current;
	            } else {
	                List<T> neighbour = current.position.getNeighbour(current.pathLength);
	                for (T nei: neighbour) {
	                    if (!optimized.contains(nei)) {
	                        DijkstraNode<T> node = new DijkstraNode<T>(
	                                nei, current,
	                                current.totalWeigth + cellWeigher.weight(current.position, nei),
	                                current.pathLength + 1
	                        );
	                        unvisited.remove(node);
	                        unvisited.add(node);
	                    }
	                }
	            }
	        }
	        CGLogger.log("found node " + foundNode);
	        if (foundNode != null) {
	            return this.buildPath(foundNode);
	        } else {
	            return null;
	        }
	    }
	    private List<T> buildPath(DijkstraNode<T> foundNode) {
	        List<T> reversePath = new ArrayList<>();
	        DijkstraNode<T> currentNode = foundNode;
	        while (currentNode.from != null) {
	            reversePath.add(currentNode.position);
	            currentNode = currentNode.from;
	        }
	        Collections.reverse(reversePath);
	        return reversePath;
	    }
	}
	private static class GraphGrid implements Grid {
	    private final int height;
	    private final int width;
	    private List<Pac> pacs = new ArrayList<>();
	    private HashMap<Position, GridPosition> nodes = new HashMap<>();
	    private static Dijkstra<GridPosition> dijkstra = new Dijkstra<>((from, to) -> 1);
	    public GraphGrid(int width, int height) {
	        this.width = width;
	        this.height = height;
	    }
	    public void addLink(Position pos, Position accessible) {
	        if (!nodes.containsKey(pos)) {
	            nodes.put(pos, new GridPosition(pos, this));
	        }
	        if (!nodes.containsKey(accessible)) {
	            nodes.put(accessible, new GridPosition(accessible, this));
	        }
	        nodes.get(pos).addNeighbour(nodes.get(accessible));
	        nodes.get(accessible).addNeighbour(nodes.get(pos));
	    }
	    public void refresh(List<Pac> pacs) {
	        this.pacs = pacs;
	    }
	    @Override
	    public List<Position> accessibleCells(Position from, int deep, int minimalDeep) {
	        return accessibleCells(from, deep, minimalDeep, null);
	    }
	    @Override
	    public List<Position> accessibleCells(Position from, int deep, int minimalDeep, Position avoidWalkOn) {
	        CGLogger.log("start computing accessible cells from " + from + " deep = " + deep);
	        Set<Position> positions = new HashSet<>();
	        Set<GridPosition> done = new HashSet<>();
	        Set<Position> toDo = new HashSet<>();
	        toDo.add(from);
	        done.add(nodes.get(from));
	        if (avoidWalkOn != null) {
	            done.add(nodes.get(avoidWalkOn));
	        }
	        int currentDeep = 0;
	        while (!toDo.isEmpty() && currentDeep <= deep) {
	            Set<Position> newPos = new HashSet<>();
	            for (Position pos: toDo) {
	                Set<GridPosition> nei =  nodes.get(pos).neighbour;
	                if (currentDeep >= minimalDeep || nei.size() == 1) {
	                    positions.add(pos);
	                }
	                for (GridPosition accessible: nei) {
	                    if (!done.contains(accessible)) {
	                        newPos.add(accessible);
	                        done.add(accessible);
	                    }
	                }
	            }
	            toDo = newPos;
	            ++currentDeep;
	        }
	        CGLogger.log("end of computation");
	        return new ArrayList<>(positions);
	    }
	    @Override
	    public Path getPathToNearest(Position from, List<Position> goToPositions) {
	        return buildPath(dijkstra.pathToNearest(nodes.get(from), goToPositions.stream().map(nodes::get).collect(Collectors.toList())));
	    }
	    @Override
	    public int getWidth() {
	        return this.width;
	    }
	    @Override
	    public int getHeight() {
	        return this.height;
	    }
	    @Override
	    public String toString(Collection<Pellet> pellets, Game game) {
	        if (CGLogger.isSubmissionMode()) {
	            return "";
	        }
	        Map<Position, Pellet> pelletsByPosition = pellets.stream().collect(Collectors.toMap(p -> p.position, Function.identity()));
	        Map<Position, Pac> pacsByPosition = pacs.stream().collect(Collectors.toMap(p -> p.position, Function.identity()));
	        String result = "";
	        for (int y = 0; y < height; ++y) {
	            for (int x = 0; x < width; ++x) {
	                Position position = Position.of(x, y);
	                if (pelletsByPosition.containsKey(position)) {
	                    result += "°";
	                } else if (pacsByPosition.containsKey(position)) {
	                    result += pacsByPosition.get(position).isPlayer1 ? "1": "2";
	                } else if (game.player1HistoryPosition.contains(position)) {
	                    result += '_';
	                } else if (nodes.containsKey(position)) {
	                    result += ' ';
	                } else {
	                    result += '#';
	                }
	            }
	            result += "\n";
	        }
	        return result;
	    }
	    @Override
	    public List<Position> visibleCells(Position position) {
	        Set<Position> result = new HashSet<>();
	        GridPosition gridPosition = nodes.get(position);
	        result.addAll(gridPosition.tops());
	        result.addAll(gridPosition.lefts());
	        result.addAll(gridPosition.rights());
	        result.addAll(gridPosition.bottoms());
	        return result.stream().map(p -> Position.of(p.x, p.y)).collect(Collectors.toList());
	    }
	    @Override
	    public List<Position> allEmptyCells() {
	        return new ArrayList<>(nodes.values());
	    }
	    @Override
	    public Path path(Position from, Position to) {
	        CGLogger.log("start computing path between " + from + " and " + to);
	        List<GridPosition> path = dijkstra.path(nodes.get(from), nodes.get(to));
	        CGLogger.log("found path " + path);
	        return buildPath(path);
	    }
	    private Path buildPath(List<GridPosition> positions) {
	        if (positions == null) {
	            return null;
	        }
	        Path result = new Path();
	        for (GridPosition position: positions) {
	            result.add(position.x, position.y);
	        }
	        return result;
	    }
	    private static class GridPosition extends Position implements DijkstraPosition<GridPosition> {
	        private final Set<GridPosition> neighbour = new HashSet<>();
	        private GridPosition top;
	        private GridPosition right;
	        private GridPosition left;
	        private GridPosition bottom;
	        private GraphGrid grid;
	        private GridPosition(Position pos, GraphGrid grid) {
	            super(pos.x, pos.y);
	            this.grid = grid;
	        }
	        @Override
	        public List<GridPosition> getNeighbour(int nbNodeToGo) {
	            return new ArrayList<>(this.neighbour);
	        }
	        public void addNeighbour(GridPosition gridPosition) {
	            neighbour.add(gridPosition);
	            if (gridPosition.x == this.x + 1 || (this.x == grid.width - 1 && gridPosition.x == 0)) {
	                right = gridPosition;
	            } else if (gridPosition.x == this.x - 1 || (this.x == 0 && gridPosition.x == grid.width - 1)) {
	                left = gridPosition;
	            } else if (gridPosition.y == this.y + 1 || (this.y == grid.height - 1 && gridPosition.y == 0)) {
	                bottom = gridPosition;
	            } else if (gridPosition.y == this.y - 1 || (this.y == 0 && gridPosition.y == grid.height - 1)) {
	                top = gridPosition;
	            }
	        }
	        public List<GridPosition> tops() {
	            GridPosition current = this;
	            List<GridPosition> positions = new ArrayList<>();
	            while (current != null && !positions.contains(current)) {
	                positions.add(current);
	                current = current.top;
	            }
	            return positions;
	        }
	        public List<GridPosition> bottoms() {
	            GridPosition current = this;
	            List<GridPosition> positions = new ArrayList<>();
	            while (current != null && !positions.contains(current)) {
	                positions.add(current);
	                current = current.bottom;
	            }
	            return positions;
	        }
	        public List<GridPosition> lefts() {
	            GridPosition current = this;
	            List<GridPosition> positions = new ArrayList<>();
	            while (current != null && !positions.contains(current)) {
	                positions.add(current);
	                current = current.left;
	            }
	            return positions;
	        }
	        public List<GridPosition> rights() {
	            GridPosition current = this;
	            List<GridPosition> positions = new ArrayList<>();
	            while (current != null && !positions.contains(current)) {
	                positions.add(current);
	                current = current.right;
	            }
	            return positions;
	        }
	    }
	}
	private static class Path {
	    List<Position> path = new ArrayList<>();
	    public Position pop() {
	        return path.get(0);
	    }
	    public Position pop(int maxDeep) {
	        if (path.size() < maxDeep) {
	            return path.get(path.size() - 1);
	        }
	        return path.get(maxDeep - 1);
	    }
	    public Path add(int toX, int toY) {
	        path.add(Position.of(toX, toY));
	        return this;
	    }
	    public List<Position> all() {
	        return path;
	    }
	    public int size() {
	        return path.size();
	    }
	    public Position target() {
	        return this.path.get(this.path.size() - 1);
	    }
	    public Path reverse() {
	        Path pathObj = new Path();
	        for (int i = path.size() - 2; i >= 0; --i) {
	            pathObj.path.add(path.get(i));
	        }
	        return pathObj;
	    }
	    @Override
	    public String toString() {
	        return this.path.size() + ": " + this.path.toString();
	    }
	}
	private static class GridReader {
	    public static Grid readForStdin(Scanner in) {
	        int width = in.nextInt();
	        int height = in.nextInt();
	        if (in.hasNextLine()) {
	            in.nextLine();
	        }
	        List<String> rows = new ArrayList<>();
	        for (int i = 0; i < height; i++) {
	            rows.add(in.nextLine());
	        }
	        return readFromArray(rows);
	    }
	    public static GraphGrid readFromArray(List<String> rows) {
	        int width = rows.get(0).length();
	        int height = rows.size();
	        GraphGrid gird = new GraphGrid(width, height);
	        List<Position> positions = new ArrayList<>();
	        Position firstEmptyCell = null;
	        for (int y = 0; firstEmptyCell == null && y < rows.size(); ++y) {
	            for (int x = 0; firstEmptyCell == null && x < rows.get(y).length(); ++x) {
	                if (rows.get(y).charAt(x) != '#') {
	                    firstEmptyCell = Position.of(x, y);
	                }
	            }
	        }
	        Set<Position> toDo = new HashSet<>();
	        toDo.add(firstEmptyCell);
	        while (!toDo.isEmpty()) {
	            Set<Position> newPos = new HashSet<>();
	            for (Position pos: toDo) {
	                for (Position accessible: directAccessibleCells(rows, pos.x, pos.y)) {
	                    gird.addLink(pos, accessible);
	                    if (!positions.contains(accessible)) {
	                        newPos.add(accessible);
	                        positions.add(accessible);
	                    }
	                }
	            }
	            toDo = newPos;
	        }
	        return gird;
	    }
	    private static List<Position> directAccessibleCells(List<String> rows, int x, int y) {
	        List<Position> positions = new ArrayList<>();
	        if (y == 0) {
	            positions.add(Position.of(x, rows.size() - 1));
	        } else if (rows.get(y - 1).charAt(x) != '#') {
	            positions.add(Position.of(x, y - 1));
	        }
	        if (y == rows.size() - 1) {
	            positions.add(Position.of(x, 0));
	        } else if (rows.get(y + 1).charAt(x) != '#') {
	            positions.add(Position.of(x, y + 1));
	        }
	        if (x == 0) {
	            positions.add(Position.of(rows.get(0).length() - 1, y));
	        } else if (rows.get(y).charAt(x - 1) != '#') {
	            positions.add(Position.of(x - 1, y));
	        }
	        if (x == rows.get(0).length() - 1) {
	            positions.add(Position.of(0, y));
	        } else if (rows.get(y).charAt(x + 1) != '#') {
	            positions.add(Position.of(x + 1, y));
	        }
	        return positions;
	    }
	}
	private static class Position {
	    public final int x;
	    public final int y;
	    public static Map<Integer, Position> positions = new HashMap<>();
	    protected Position(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	    @Override
	    public String toString() {
	        return "[" + x + ", " + y + "]";
	    }
	    @Override
	    public int hashCode() {
	        return this.x * 1000 + y;
	    }
	    @Override
	    public boolean equals(Object obj) {
	        if (!(obj instanceof Position)) {
	            return false;
	        }
	        return this.x == ((Position) obj).x && this.y == ((Position) obj).y;
	    }
	    public static Position of(int x, int y) {
	        int index = x * 100000 + y;
	        if (!positions.containsKey(index)) {
	            positions.put(index, new Position(x, y));
	        }
	        return positions.get(index);
	    }
	}
	private static interface PacMove {
	    String action();
	}
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Game game = new Game();
        game.setGrid(GridReader.readForStdin(in));
        Map<Integer, PacMove> lastMoves = new HashMap<>();
        while (true) {
            CGLogger.disableLog();
            game.refreshPacPlayers(in);
            game.refreshPellets(in);
            CGLogger.enableLog();
            CGLogger.startTurn();
            SuperPelletTargetChooser.getPelletForPac(game, game.player1.getPacs().iterator().next());
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
