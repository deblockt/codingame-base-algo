import com.deblock.logger.CGLogger;
import com.deblock.packman.game.Game;
import com.deblock.packman.game.Pac;
import com.deblock.packman.grid.GraphGrid;
import com.deblock.packman.grid.GridReader;
import com.deblock.packman.grid.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GraphGridTest {

    @Test
    public void test() {
        GraphGrid grid = GridReader.readFromArray(List.of(
                "#################################",
                "#_______#___#°#_°_#°#°°°#°°°°°°°#",
                "#_#_#####_###°#_#°#°###°#####°#°#",
                "#_#_________#___°___#°°°°°°°°°#°#",
                "#_###_#_#_#_#_#_#°#°#°#_#°#°###°#",
                "#_____#_#_____#_°_#°°°°_#°#°°°°°#",
                "#####_#_#_#####_#°#####°#°#°#####",
                "________#______1#°______#________",
                "#####_###_#_###_#°###_#_###°#####",
                "°°°_______#___#_#°#_°_#_°°°___°°°",
                "###_#_###_###_#1#°#_###_###°#°###",
                "#°°_#___#____1#_°°#_____#°°°#°°°#",
                "#°###_#_#_#_#_#1#°#°#°#°#°#°###°#",
                "#°°°#_#___#_#___°°°°#°#°°°#°#°°°#",
                "#################################"
        ));

        CGLogger.enableLog();

        System.out.println(grid.accessibleCells(Position.of(16,11), 1, 1));
        /*Game game = new Game();
        game.setGrid(grid);
        Pac player = new Pac(1, true, game, Pac.PAPER);
        player.abilityCooldown = 10;
        game.addPac(player, Position.of(3, 3));
        Pac enemy = new Pac(2, false, game, Pac.SCISSORS);
        enemy.abilityCooldown = 10;
        game.addPac(enemy, Position.of(3, 2));

        System.out.println(player.play(List.of()));*/
    }
}
