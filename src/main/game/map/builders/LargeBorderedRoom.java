package main.game.map.builders;

import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.SplittableRandom;

public class LargeBorderedRoom extends TileMapBuilder {

    private LargeBorderedRoom() { }
    
    public static TileMapBuilder newBuilder() { return new LargeBorderedRoom(); }


    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            createSchemaMaps();

            // tilePathMap.fill(1);
            getPathLayer().fill(getFloor());

            TileMapOperations.createWallForMap(this);

            isPathMapCompletelyConnected = TileMapOperations.isValidPath(this);

            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getPathLayer().debug(false));
                logger.debug(System.lineSeparator() + getPathLayer().debug(true));
            } else {
                generateNewSeed();
            }
        }

        TileMapOperations.tryPlacingLiquids(this);
        TileMapOperations.tryPlacingStructures(this);

        return createTileMap();
    }
}
