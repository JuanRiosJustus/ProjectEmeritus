package main.game.map.builders;

import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.Map;

public class BasicOpenMap extends TileMapBuilder {

    public BasicOpenMap(Map<String, Object> configuration) { super(configuration); }

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            createSchemaMaps();
            getPathLayer().fill(getFloor());

            // placeLiquidsSafely(heightMap, liquidMap, pathMap, configs, seaLevel);

//            if (mapConfigs.getWalling() > 0) { placeWallingSafely(pathMap); }
            // if (configs.getWall()> 0) { tryCreatingRooms(pathMap, true); }

            // placeStructuresSafely(pathMap, wallMap, liquidMap, configs);

            isPathMapCompletelyConnected = TileMapOperations.isValidPath(this);
            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getPathLayer().debug(false));
                logger.debug(System.lineSeparator() + getPathLayer().debug(true));
            } else {
                generateNewSeed();
            }
        }

        TileMapOperations.tryPlacingLiquids(this);
        TileMapOperations.tryPlacingDestroyableBlockers(this);
        TileMapOperations.tryPlacingRoughTerrain(this);
        TileMapOperations.tryPlacingExits(this);
        TileMapOperations.tryPlacingEntrance(this);

        return createTileMap();
    }
}
