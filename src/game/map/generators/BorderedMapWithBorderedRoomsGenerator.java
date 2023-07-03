package game.map.generators;

import game.map.TileMap;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMapValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRoomsGenerator extends TileMapGenerator {

    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(SchemaConfigs mapConfigs) {
        logger.info("Constructing {0}", getClass());

        while (!isPathMapCompletelyConnecting) {

            createSchemaMaps(mapConfigs);

            tilePathMap.fill(1);

            List<Set<Point>> rooms = tryCreatingRooms(tilePathMap, true);

            Set<Point> mapOutline = createWallForMap(tilePathMap);

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
        
//        if (mapConfigs.structure > 0) {
//            placeStructuresSafely(pathMap, structureMap, liquidMap, mapConfigs);
//        }

        return createTileMap(tilePathMap, tileHeightMap, tileTerrainMap, tileLiquidMap, tileStructureMap);
    }
}
