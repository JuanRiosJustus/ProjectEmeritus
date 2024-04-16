package main.game.map.builders;


import main.game.components.tile.Tile;
import main.game.map.base.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.List;
import java.util.Set;

public class LargeContinuousRoom extends TileMapOperations {


    public void execute(TileMap tileMap) {
        while (!isPathCompletelyConnected) {

            tileMap.init();
            // any non null collider value
            tileMap.getColliderLayer().fill(String.valueOf(mRandom.nextInt(1, 9)));

            List<Set<Tile>> rooms = tryCreatingRooms(tileMap, true);

            List<Set<Tile>> halls = tryConnectingRooms(tileMap, rooms);

            Set<Tile> mapOutline = tryPlaceCollidersAroundEdges(tileMap);

            System.out.println(tileMap.getColliderLayer().debug(true));
            System.out.println(tileMap.getColliderLayer().debug(false));

            tryConnectingRooms(tileMap, rooms);

            isPathCompletelyConnected =  TileMapValidator.isValid(tileMap);

            if (isPathCompletelyConnected) {
                tileMap.commit();
            }
        }
        tileMap.push();
    }
}
