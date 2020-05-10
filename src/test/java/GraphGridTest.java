import com.deblock.packman.grid.GraphGrid;
import com.deblock.packman.grid.GridReader;
import com.deblock.packman.grid.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GraphGridTest {

    @Test
    public void test() {
        GraphGrid grid = GridReader.readFromArray(7, 4, List.of(
            "###############################",
            "                               ",
            "###°#°### #°#°###°#°# ###°#°###",
            "°°°°#°#°1  °#°°°°°#°°°  #°#°°°°",
            "###############################"
        ));

        List<Position> positions = grid.visibleCells(Position.of(8, 3));
        System.out.println(positions);
    }
}
