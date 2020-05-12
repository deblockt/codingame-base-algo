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
                "###################################",
                "#°°°°°°°°°#°°°°°#°#°°°°°#__°°°°__°#",
                "###2###°#°#°###°#°#°###°#_#°###°###",
                "#°°1#°°°#°°°°°#°°°°°#°°°°_#°°°#°°°#",
                "#°#°#°#°#°###°#°###°#°###_#°#°#°#°#",
                "#°#°#°°°#°°°#°°°°°°°°°#°1_#°°°#°#°#",
                "###°#°#°###°#°#°###°#°#°###°#°#°###",
                "#°°°°°#°°°°°°°#°°°°°#°°°°°°°#°°°°°#",
                "#°#°#°###°###°#°###°#°###°###°#°#°#",
                "###################################"
        ));

        CGLogger.enableLog();
        Game game = new Game();
        game.setGrid(grid);
        Pac player = new Pac(1, true, game, Pac.PAPER);
        player.abilityCooldown = 10;
        game.addPac(player, Position.of(3, 3));
        Pac enemy = new Pac(2, false, game, Pac.SCISSORS);
        enemy.abilityCooldown = 10;
        game.addPac(enemy, Position.of(3, 2));

        System.out.println(player.play(List.of()));
    }
}
