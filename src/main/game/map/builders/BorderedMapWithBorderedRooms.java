package main.game.map.builders;

import main.game.components.tile.Tile;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.Map;
import java.util.Set;

public class BorderedMapWithBorderedRooms extends TileMapBuilder {

    public BorderedMapWithBorderedRooms(Map<String, Object> configuration) { super(configuration); }

    @Override
    public TileMap build() {
        logger.info("Constructing {}", BorderedMapWithBorderedRooms.class);

        while (!isPathMapCompletelyConnected) {

            initializeMap();
//
//            logger.debug(System.lineSeparator() + getColliderLayer().debug(false));
//            logger.debug(System.lineSeparator() + getColliderLayer().debug(true));
//
////            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, true);
////
            Set<Tile> mapOutline = TileMapOperations.placeCollidersAroundEdges(this);

            isPathMapCompletelyConnected = TileMapOperations.isValidConfiguration(this);
            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getColliderLayer().debug(false));
                logger.debug(System.lineSeparator() + getColliderLayer().debug(true));
                finalizeMap();
            } else {
                generateNewSeed();
            }
        }

//        TileMapOperations.tryPlacingDestroyableBlockers(this);
//        TileMapOperations.tryPlacingRoughTerrain(this);
//        TileMapOperations.tryPlacingExits(this);
//        TileMapOperations.tryPlacingEntrance(this);

        return createTileMap();
    }
}
