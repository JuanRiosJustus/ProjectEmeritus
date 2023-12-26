package main.game.map.builders;

import main.game.components.tile.Tile;
import main.game.map.base.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRooms extends TileMapOperations {

    public void execute(TileMap tileMap) {

        while (!isPathCompletelyConnected) {

            tileMap.init();

            List<Set<Tile>> rooms = tryCreatingRooms(tileMap, true);

            Set<Tile> mapOutline = tryPlaceCollidersAroundEdges(tileMap);

            isPathCompletelyConnected = TileMapValidator.isValid(tileMap);
            if (isPathCompletelyConnected) {
                tileMap.commit();
            }
        }

        tileMap.push();
    }
}
