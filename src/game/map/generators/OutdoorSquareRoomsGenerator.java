package game.map.generators;

import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMap;
import game.map.TileMap;
import game.map.generators.validation.SchemaMapValidation;
import logging.ELogger;
import logging.ELoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class OutdoorSquareRoomsGenerator extends TileMapGenerator {

    private final SplittableRandom random = new SplittableRandom();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    @Override
    public TileMap build(SchemaConfigs configs) {

        while (!isPathMapCompletelyConnecting) {

            createSchemaMaps(configs);

            pathMap.fill(configs.getFloor());

            List<Set<Point>> rooms = tryCreatingRooms(pathMap, true);

            isPathMapCompletelyConnecting = SchemaMapValidation.isValidPath(pathMap);

            if (isPathMapCompletelyConnecting) {
                // System.out.println(tilePathMap.debug(false));
                // System.out.println(tilePathMap.debug(true));
            }
        }

        populateFloorAndWallMap(pathMap, floorMap, wallMap, configs);

        placeLiquidsSafely(heightMap, liquidMap, pathMap, configs, liquidLevel);

        placeStructuresSafely(pathMap, structureMap, liquidMap, configs);

        return createTileMap(pathMap, heightMap, liquidMap, structureMap, configs);
    }
}
