package main.game.map.builders;

import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.Map;

public class BasicOpenMap extends TileMapBuilder {

    public BasicOpenMap(Map<String, Object> configuration) { super(configuration); }

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            initializeMap();
            getColliderLayer().fill(getFloor());

            // placeLiquidsSafely(heightMap, liquidMap, pathMap, configs, seaLevel);

//            if (mapConfigs.getWalling() > 0) { placeWallingSafely(pathMap); }
            // if (configs.getWall()> 0) { tryCreatingRooms(pathMap, true); }

            // placeStructuresSafely(pathMap, wallMap, liquidMap, configs);

            isPathMapCompletelyConnected = TileMapOperations.isValidConfiguration(this);
            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getColliderLayer().debug(false));
                logger.debug(System.lineSeparator() + getColliderLayer().debug(true));
                finalizeMap();
//                TileMapOperations.tryPlacingTerrain(this);
//                TileMapOperations.tryPlacingLiquids(this);

            } else {
                generateNewSeed();
            }
        }

//        TileMapOperations.tryPlacingLiquids(this);
//        TileMapOperations.tryPlacingDestroyableBlockers(this);
//        TileMapOperations.tryPlacingRoughTerrain(this);
//        TileMapOperations.tryPlacingExits(this);
//        TileMapOperations.tryPlacingEntrance(this);

        return createTileMap();
    }
}
