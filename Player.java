import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.time.Duration;
import java.util.ArrayList;
class Player {
	private static class AlphaBetaZone {
	    private Double min;
	    private Double max;
	    AlphaBetaZone() { }
	    private AlphaBetaZone(AlphaBetaZone zone) {
	        this.min = zone.min;
	        this.max = zone.max;
	    }
	    public AlphaBetaZone cloneZone() {
	        return new AlphaBetaZone(this);
	    }
	    public void newMinScore(double min) {
	        if (this.min == null || min > this.min) {
	           this.min = min;
	        }
	    }
	    public void newMaxScore(double max) {
	        if (this.max == null || max < this.max) {
	            this.max = max;
	        }
	    }
	    public boolean isLessThanExpectedMin(double childScore) {
	        return this.min != null && childScore <= this.min;
	    }
	    public boolean isGreaterThanExpectedMax(double childScore) {
	        return this.max != null && childScore >= this.max;
	    }
	}
	private static class MaxNode<MoveType extends Move<MoveType>, BoardType> implements MinMaxNode<MoveType, BoardType> {
	    private final Move<MoveType> move;
	    private final MinMaxBoard<MoveType, BoardType> board;
	    private double maxScore = -Double.MAX_VALUE;
	    protected MinMaxNode<MoveType, BoardType> bestMove = null;
	    public MaxNode(MinMaxBoard<MoveType, BoardType> board, Move<MoveType> move) {
	        this.board = board;
	        this.move = move;
	    }
	    @Override
	    public void simulate(
	            MinMaxPlayer<BoardType, MoveType> player1,
	            MinMaxPlayer<BoardType, MoveType> player2,
	            AlphaBetaZone zone,
	            int deep
	    ) {
	        if (deep <= 0) {
	            return ;
	        }
	        final List<Move<MoveType>> possibleMove = player1.possibleMove(board.getBoard());
	        for (Move<MoveType> move: possibleMove) {
	            final MinMaxBoard<MoveType, BoardType> newBoard = board.simulateMove(move.getMove());
	            final MinMaxNode<MoveType, BoardType>  child = new MinNode<>(newBoard, move);
	            child.simulate(player1, player2, zone.cloneZone(), deep - 1);
	            final double childScore = child.getScore();
	            if (bestMove == null || childScore > maxScore) {
	                maxScore = childScore;
	                bestMove = child;
	            }
	            if (zone.isGreaterThanExpectedMax(childScore)) {
	                break;
	            } else {
	                zone.newMinScore(childScore);
	            }
	        }
	    }
	    public double getScore() {
	        if (bestMove == null) {
	            return this.board.score();
	        }
	        return maxScore;
	    }
	    public Move<MoveType> getMove() {
	        return this.move;
	    }
	}
	private static interface MinMaxBoard<MoveType, BoardType> {
	    MinMaxBoard<MoveType, BoardType> simulateMove(MoveType move);
	    BoardType getBoard();
	    double score();
	}
	private static class RootNode<MoveType extends Move<MoveType>, BoardType> extends MaxNode<MoveType, BoardType> {
	    public RootNode(MinMaxBoard<MoveType, BoardType> board) {
	        super(board, null);
	    }
	    public Move<MoveType> getBestMove() {
	        return this.bestMove.getMove();
	    }
	}
	private static interface MinMaxNode<MoveType extends Move<MoveType>, BoardType> {
	    void simulate(
	            MinMaxPlayer<BoardType, MoveType> player1,
	            MinMaxPlayer<BoardType, MoveType> player2,
	            AlphaBetaZone zone,
	            int deep
	    );
	    double getScore();
	    Move<MoveType> getMove();
	}
	private static interface MinMaxPlayer<BoardType, MoveType extends Move<MoveType>> {
	    List<Move<MoveType>> possibleMove(BoardType board);
	}
	private static interface Move<MoveType extends Move<MoveType>> {
	    MoveType getMove();
	}
	private static class MinNode<MoveType  extends Move<MoveType>, BoardType> implements MinMaxNode<MoveType, BoardType> {
	    private final Move<MoveType> move;
	    private final MinMaxBoard<MoveType, BoardType> board;
	    private double minScore = Double.MAX_VALUE;
	    private MinMaxNode<MoveType, BoardType> bestMove = null;
	    public MinNode(MinMaxBoard<MoveType, BoardType> board, Move<MoveType> move) {
	        this.board = board;
	        this.move = move;
	    }
	    @Override
	    public void simulate(
	            MinMaxPlayer<BoardType, MoveType> player1,
	            MinMaxPlayer<BoardType, MoveType> player2,
	            AlphaBetaZone zone,
	            int deep
	    ) {
	        if (deep <= 0) {
	            return;
	        }
	        final List<Move<MoveType>> possibleMove = player2.possibleMove(board.getBoard());
	        for (Move<MoveType> move: possibleMove) {
	            final MinMaxBoard<MoveType, BoardType> newBoard = board.simulateMove(move.getMove());
	            final MinMaxNode<MoveType, BoardType>  child = new MaxNode<>(newBoard, move);
	            child.simulate(player1, player2, zone.cloneZone(), deep - 1);
	            final double childScore = child.getScore();
	            if (bestMove == null || childScore < this.minScore) {
	                this.minScore = childScore;
	                this.bestMove = child;
	            }
	            if (zone.isLessThanExpectedMin(childScore)) {
	                break;
	            } else {
	                zone.newMaxScore(childScore);
	            }
	        }
	    }
	    @Override
	    public double getScore() {
	        if (bestMove == null) {
	            return this.board.score();
	        }
	        return this.minScore;
	    }
	    @Override
	    public Move<MoveType> getMove() {
	        return this.move;
	    }
	}
	private static class MinMax<BoardType, MoveType extends Move<MoveType>> {
	    public MoveType move(
	        MinMaxPlayer<BoardType, MoveType> player1,
	        MinMaxPlayer<BoardType, MoveType> player2,
	        MinMaxBoard<MoveType, BoardType> board,
	        int deep
	    ) {
	        final RootNode<MoveType, BoardType> minMaxNode = new RootNode<MoveType, BoardType>(board);
	        minMaxNode.simulate(player1, player2, new AlphaBetaZone(), deep);
	        System.err.println("max score " + minMaxNode.getScore());
	        return minMaxNode.getBestMove().getMove();
	    }
	}
	private static class TicTacToePlayer implements MinMaxPlayer<TicTacToeBoard, TicTacToeMove> {
	    private final byte playerSymbol;
	    public TicTacToePlayer(byte playerSymbol) {
	        this.playerSymbol = playerSymbol;
	    }
	    @Override
	    public List<Move<TicTacToeMove>> possibleMove(TicTacToeBoard board) {
	        return board.availableMoveFor(playerSymbol);
	    }
	}
	private static class TicTacToeMove implements Move<TicTacToeMove> {
	    public final int x;
	    public final int y;
	    public final byte playerSymbol;
	    public TicTacToeMove(int x, int y, byte playerSymbol) {
	        this.x = x;
	        this.y = y;
	        this.playerSymbol = playerSymbol;
	    }
	    @Override
	    public TicTacToeMove getMove() {
	        return this;
	    }
	}
	private static class TicTacToeBoard implements MinMaxBoard<TicTacToeMove, TicTacToeBoard> {
	    public static final byte PLAYER1 = 1;
	    public static final byte PLAYER2 = 2;
	    public static final byte EMPTY = 0;
	    public static final byte TIE = 3;
	    private byte[][] totalGrid = new byte[][]{
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}
	    };
	    private byte[][] zoneWinner = new byte[][]{
	        new byte[]{EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY},
	        new byte[]{EMPTY, EMPTY, EMPTY}
	    };
	    private List<int[]> nextPlayableZone;
	    public TicTacToeBoard() {
	    }
	    public TicTacToeBoard(TicTacToeBoard original) {
	        this.totalGrid = new byte[9][9];
	        for (int y = 0; y < 9; ++y) {
	            System.arraycopy(original.totalGrid[y], 0, this.totalGrid[y], 0, 9);
	        }
	        this.zoneWinner = new byte[3][3];
	        for (int y = 0; y < 3; ++y) {
	            System.arraycopy(original.zoneWinner[y], 0, this.zoneWinner[y], 0, 3);
	        }
	    }
	    public void refresh(Scanner in) {
	        int opponentRow = in.nextInt();
	        int opponentCol = in.nextInt();
	        if (opponentCol != -1) {
	            this.withMove(new TicTacToeMove(opponentCol, opponentRow, PLAYER2));
	        }
	        this.nextPlayableZone = new ArrayList<>();
	        int validActionCount = in.nextInt();
	        for (int i = 0; i < validActionCount; i++) {
	            int row = in.nextInt();
	            int col = in.nextInt();
	            this.nextPlayableZone.add(new int[]{col, row});
	        }
	    }
	    public void load(String grid, int[] previousPlayedCell) {
	        String[] lines = grid.trim().split(System.lineSeparator());
	        totalGrid = new byte[9][9];
	        for (int y = 0; y < 9; ++y) {
	            for (int x = 0; x < 9; ++x) {
	                switch(lines[y].charAt(x)) {
	                    case '0':
	                        totalGrid[y][x] = EMPTY;
	                        break;
	                    case '1':
	                        totalGrid[y][x] = PLAYER1;
	                        break;
	                    case '2':
	                        totalGrid[y][x] = PLAYER2;
	                        break;
	                }
	            }
	        }
	        for (int y = 0; y < 3; ++y) {
	            for (int x = 0; x < 3; ++x) {
	                final byte zoneWinner = this.getZoneWinner(this.extractZone(x, y));
	                if (zoneWinner != EMPTY) {
	                    this.zoneWinner[y][x] = zoneWinner;
	                }
	            }
	        }
	        this.computeNextPlayableZoneAfter(new TicTacToeMove(previousPlayedCell[0], previousPlayedCell[1], PLAYER2));
	    }
	    @Override
	    public MinMaxBoard<TicTacToeMove, TicTacToeBoard> simulateMove(TicTacToeMove move) {
	        return new TicTacToeBoard(this)
	                .withMove(move)
	                .computeNextPlayableZoneAfter(move);
	    }
	    @Override
	    public TicTacToeBoard getBoard() {
	        return this;
	    }
	    public TicTacToeBoard withMove(TicTacToeMove move) {
	        this.totalGrid[move.y][move.x] = move.playerSymbol;
	        final int[] zoneCoordinate = this.zoneOf(move.x, move.y);
	        final byte[][] zone = this.extractZone(zoneCoordinate[0], zoneCoordinate[1]);
	        final byte zoneWinner = getZoneWinner(zone);
	        if (zoneWinner != EMPTY) {
	            this.zoneWinner[zoneCoordinate[1]][zoneCoordinate[0]] = zoneWinner;
	        }
	        return this;
	    }
	    private byte getZoneWinner(byte[][] zone) {
	        byte[][] winnableRows = new byte[][]{
	            zone[0],
	            zone[1],
	            zone[2],
	            new byte[]{zone[0][0], zone[1][0], zone[2][0]},
	            new byte[]{zone[0][1], zone[1][1], zone[2][1]},
	            new byte[]{zone[0][2], zone[1][2], zone[2][2]},
	            new byte[]{zone[0][0], zone[1][1], zone[2][2]},
	            new byte[]{zone[0][2], zone[1][1], zone[2][0]},
	        };
	        boolean haveEmptyCell = false;
	        for (byte[] row: winnableRows) {
	            if (row[0] != EMPTY && row[0] == row[1] && row[1] == row[2]) {
	                return row[0];
	            }
	            haveEmptyCell = haveEmptyCell || row[0] == EMPTY || row[1] == EMPTY || row[2] == EMPTY;
	        }
	        return haveEmptyCell ? EMPTY : TIE;
	    }
	    private byte[][] extractZone(int zoneX, int zoneY) {
	        final int zoneTop = zoneY * 3;
	        final int zoneLeft = zoneX * 3;
	        return new byte[][]{
	            Arrays.copyOfRange(this.totalGrid[zoneTop], zoneLeft, zoneLeft + 3),
	            Arrays.copyOfRange(this.totalGrid[zoneTop + 1], zoneLeft, zoneLeft + 3),
	            Arrays.copyOfRange(this.totalGrid[zoneTop + 2], zoneLeft, zoneLeft + 3)
	        };
	    }
	    private int[] zoneOf(int x, int y) {
	        final int zoneX = x / 3;
	        final int zoneY = y / 3;
	        return new int[]{zoneX, zoneY};
	    }
	    private int[] nextZoneToPlayAfter(int x, int y) {
	        final int zoneX = x - 3 * (x / 3);
	        final int zoneY = y - 3 * (y / 3);
	        return new int[]{zoneX, zoneY};
	    }
	    private List<int[]> listEmptyCell(int zoneX, int zoneY) {
	        if (this.zoneWinner[zoneY][zoneX] != EMPTY) {
	            return new ArrayList<>();
	        }
	        final List<int[]> availableMove = new ArrayList<>();
	        final int startLeft = zoneX * 3;
	        final int startTop = zoneY * 3;
	        for (int x = startLeft; x < startLeft + 3; ++x) {
	            for (int y = startTop; y < startTop + 3; ++y) {
	                if (this.totalGrid[y][x] == EMPTY) {
	                    availableMove.add(new int[]{x, y});
	                }
	            }
	        }
	        return availableMove;
	    }
	    private TicTacToeBoard computeNextPlayableZoneAfter(TicTacToeMove move) {
	        final int[] nextZone = nextZoneToPlayAfter(move.x, move.y);
	        List<int[]> availableMove = listEmptyCell(nextZone[0], nextZone[1]);
	        if (availableMove.isEmpty()) {
	            availableMove = new ArrayList<>();
	            for (int y = 0; y < 3; ++y) {
	                for (int x = 0; x < 3; ++x) {
	                    availableMove.addAll(listEmptyCell(x, y));
	                }
	            }
	        }
	        this.nextPlayableZone = availableMove;
	        return this;
	    }
	    @Override
	    public double score() {
	        final int generalWinner = this.getZoneWinner(this.zoneWinner);
	        if (generalWinner == PLAYER1) {
	            return 10000000;
	        } else if (generalWinner == PLAYER2) {
	            return -1000000;
	        }
	        int countWon = 0;
	        int countLose = 0;
	        for (int x = 0; x < 3; ++x) {
	            for (int y = 0; y < 3; ++y) {
	                switch (zoneWinner[y][x]) {
	                    case PLAYER2:
	                        ++countLose;
	                        break;
	                    case PLAYER1:
	                        ++countWon;
	                        break;
	                    default:
	                }
	            }
	        }
	        return countWon * 100 - countLose * 110;
	    }
	    public List<Move<TicTacToeMove>> availableMoveFor(byte playerSymbol) {
	        final List<Move<TicTacToeMove>> moves = new ArrayList<>();
	        for (int[] move: this.nextPlayableZone) {
	            moves.add(new TicTacToeMove(move[0], move[1], playerSymbol));
	        }
	        return moves;
	    }
	    public String toString() {
	        String result = "";
	        for (int y = 0; y < 9; ++y) {
	            byte[] grid = this.totalGrid[y];
	            if (y % 3 == 0) {
	                result += "-------------\n";
	            }
	            result += "|";
	            result += grid[0];
	            result += grid[1];
	            result += grid[2];
	            result += "|";
	            result += grid[3];
	            result += grid[4];
	            result += grid[5];
	            result += "|";
	            result += grid[6];
	            result += grid[7];
	            result += grid[8];
	            result += "\n";
	        }
	        result += "\n\n";
	        for (int y = 0; y < 3; ++y) {
	            byte[] grid = this.zoneWinner[y];
	            result += "|";
	            result += grid[0];
	            result += grid[1];
	            result += grid[2];
	            result += "|";
	            result += "\n";
	        }
	        return result;
	    }
	}
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        MinMax<TicTacToeBoard, TicTacToeMove> minMax = new MinMax<>();
        TicTacToeBoard board = new TicTacToeBoard();
        TicTacToePlayer player1 = new TicTacToePlayer(TicTacToeBoard.PLAYER1);
        TicTacToePlayer player2 = new TicTacToePlayer(TicTacToeBoard.PLAYER2);
        while (true) {
            long time = System.nanoTime();
            board.refresh(in);
            final TicTacToeMove bestMove = minMax.move(player1, player2, board, 6);
            System.err.println(board.toString());
            System.err.println("duration: " + Duration.ofNanos(System.nanoTime() - time).toMillis());
            System.out.println(bestMove.y + " " + bestMove.x);
            board.withMove(new TicTacToeMove(bestMove.x, bestMove.y, TicTacToeBoard.PLAYER1));
        }
    }
}
