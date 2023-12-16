package main.game.map.builders;


import main.game.components.tile.Tile;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.*;

public class LargeContinuousRoom extends TileMapBuilder {

    public LargeContinuousRoom(Map<String, Object> configuration) { super(configuration); }

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            initializeMap();

            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, false);
            List<Set<Tile>> halls = TileMapOperations.tryConnectingRooms(this, rooms);

            TileMapOperations.tryConnectingRooms(this, rooms);

            isPathMapCompletelyConnected =  TileMapOperations.isValidConfiguration(this);

            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getColliderLayer().debug(false));
                logger.debug(System.lineSeparator() + getColliderLayer().debug(true));
                finalizeMap();
            } else {
                generateNewSeed();
            }
        }
//
//        TileMapOperations.tryPlacingTerrain(this);
//        TileMapOperations.tryPlacingLiquids(this);
//
//        TileMapOperations.tryPlacingDestroyableBlockers(this);
//        TileMapOperations.tryPlacingRoughTerrain(this);
//        TileMapOperations.tryPlacingExits(this);
//        TileMapOperations.tryPlacingEntrance(this);

        return createTileMap();
    }
}
