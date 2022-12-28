package game.map.generators;

import game.map.generators.validation.SchemaMap;
import game.map.TileMap;
import game.map.generators.validation.TileMapGeneratorValidation;
import logging.Logger;
import logging.LoggerFactory;

import java.util.SplittableRandom;

public class OpenMapGenerator extends TileMapGenerator {
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    @Override
    public TileMap build(int mapRows, int mapColumns, int mapFlooring, int mapWalling) {
        logger.log("Constructing {0}", getClass());

        while (!isCompletelyConnected) {
            createSchemaMaps(mapRows, mapColumns, mapFlooring, mapWalling);

            pathMap.fill(1);

            isCompletelyConnected = TileMapGeneratorValidation.isValid(pathMap);

            if (isCompletelyConnected) {
                System.out.println(pathMap.debug(false));
                System.out.println(pathMap.debug(true));
            }
        }

        developTerrainMapFromPathMap(pathMap, terrainMap, mapFlooring, mapWalling);
        return createTileMap(pathMap, heightMap, terrainMap, specialMap, structureMap);
    }
}
