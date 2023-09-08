package main.game.map.builders;

import main.game.components.Tile;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.util.List;
import java.util.Set;

public class NoBorderWithSmallRooms extends TileMapBuilder {

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            createSchemaMaps();

            getPathLayer().fill(getFloor());

            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, true);

            logger.info("Validating {}", this.getClass());
            isPathMapCompletelyConnected = TileMapOperations.isValidPath(this);

            if (isPathMapCompletelyConnected) {
                logger.debug(System.lineSeparator() + getPathLayer().debug(false));
                logger.debug(System.lineSeparator() + getPathLayer().debug(true));
            } else {
                generateNewSeed();
            }
        }

        TileMapOperations.tryPlacingLiquids(this);
        TileMapOperations.tryPlacingGreaterStructures(this);
        TileMapOperations.tryPlacingLesserStructures(this);

        return createTileMap();
    }
}
