package main.game.map.builders;

import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.Map;

public class LargeBorderedRoom extends TileMapBuilder {

    public LargeBorderedRoom(Map<String, Object> configuration) { super(configuration); }

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
        TileMapOperations.tryPlacingDestroyableBlockers(this);

        return createTileMap();
    }
}
