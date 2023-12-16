package main.game.map.builders;

import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.Map;

public class LargeBorderedRoom extends TileMapBuilder {

    public LargeBorderedRoom(Map<String, Object> configuration) { super(configuration); }

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            initializeMap();

            // tilePathMap.fill(1);
            getColliderLayer().fill(getFloor());

            TileMapOperations.placeCollidersAroundEdges(this);

            isPathMapCompletelyConnected = TileMapOperations.isValidConfiguration(this);

            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getColliderLayer().debug(false));
                logger.debug(System.lineSeparator() + getColliderLayer().debug(true));
                finalizeMap();
            } else {
                generateNewSeed();
            }
        }

//        TileMapOperations.tryPlacingLiquids(this);
//        TileMapOperations.tryPlacingDestroyableBlockers(this);

        return createTileMap();
    }
}
