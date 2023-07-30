package main.game.map.builders;

import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.SplittableRandom;

public class BasicOpenMap extends TileMapBuilder {

    private BasicOpenMap() { }
    
    public static TileMapBuilder newBuilder() { return new BasicOpenMap(); }

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

//        mapPathMapToTerrainMap(pathMap, terrainMap, mapConfigs);

//        if (mapConfigs.getLiquid() > 0) {
//            placeLiquidSafely(heightMap, liquidMap, pathMap, mapConfigs);
//        }
//
//        if (mapConfigs.getStructure() > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        TileMapOperations.tryPlacingLiquids(this);
        TileMapOperations.tryPlacingStructures(this);

        return createTileMap();
    }
}
