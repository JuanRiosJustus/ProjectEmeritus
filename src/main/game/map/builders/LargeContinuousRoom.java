package main.game.map.builders;


import main.game.components.Tile;
import main.game.map.TileMap;
import main.game.map.builders.utils.TileMapOperations;

import java.awt.Point;
import java.util.*;

public class LargeContinuousRoom extends TileMapBuilder {

    private LargeContinuousRoom() { }
    
    public static TileMapBuilder newBuilder() { return new LargeContinuousRoom(); }

    @Override
    public TileMap build() {

        while (!isPathMapCompletelyConnected) {

            createSchemaMaps();

            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, false);
            List<Set<Tile>> halls = TileMapOperations.tryConnectingRooms(this, rooms);

            TileMapOperations.tryConnectingRooms(this, rooms);

            isPathMapCompletelyConnected =  TileMapOperations.isValidPath(this);

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
