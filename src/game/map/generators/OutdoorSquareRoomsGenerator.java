package game.map.generators;

import game.map.generators.validation.SchemaConfigs;
import game.map.TileMap;
import game.map.generators.validation.SchemaMapValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class OutdoorSquareRoomsGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {

        while (!isPathMapCompletelyConnecting) {

            createSchemaMaps(mapConfigs);

            tilePathMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(tilePathMap, true);

            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(tilePathMap);

            // System.out.println(tilePathMap.debug(false));
            // System.out.println(tilePathMap.debug(true));
            if (isPathMapCompletelyConnecting) {
                // System.out.println(tilePathMap.debug(false));
                // System.out.println(tilePathMap.debug(true));
            }
        }

        mapPathMapToTerrainMap(tilePathMap, tileTerrainMap, mapConfigs);

        placeLiquidsSafely(tileHeightMap, tileLiquidMap, tilePathMap, mapConfigs, tileSeaLevelMap);

        placeStructuresSafely(tilePathMap, tileStructureMap, tileLiquidMap, mapConfigs);

        return createTileMap(tilePathMap, tileHeightMap, tileTerrainMap, tileLiquidMap, tileStructureMap);
    }
}
