package main.game.map.builders;

import main.game.components.tile.Tile;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoBorderWithSmallRooms extends TileMapBuilder {

    public NoBorderWithSmallRooms(Map<String, Object> configuration) { super(configuration); }

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            initializeMap();

            getColliderLayer().fill(getFloor());

            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, true);

            logger.info("Validating {}", this.getClass());
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
//        TileMapOperations.tryPlacingRoughTerrain(this);
//        TileMapOperations.tryPlacingExits(this);
//        TileMapOperations.tryPlacingEntrance(this);

        return createTileMap();
    }
}
