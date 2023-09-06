package main.game.map.builders;

import main.game.components.Tile;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRooms extends TileMapBuilder {

    @Override
    public TileMap build() {
        logger.info("Constructing {}", BorderedMapWithBorderedRooms.class);

        while (!isPathMapCompletelyConnected) {

            createSchemaMaps();

            getPathLayer().fill(getFloor());

            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, true);

            Set<Tile> mapOutline = TileMapOperations.createWallForMap(this);

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
