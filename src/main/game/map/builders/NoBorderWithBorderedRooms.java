package main.game.map.builders;

import main.game.components.tile.Tile;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapParameters;

import java.util.List;
import java.util.Set;

public class NoBorderWithBorderedRooms extends TileMapAlgorithm {

    @Override
    public TileMap evaluate(TileMapParameters tileMapParameters) {
        TileMap newTileMap = new TileMap(tileMapParameters);
        isPathCompletelyConnected = false;

        while (!isPathCompletelyConnected) {
            newTileMap.reset();

            // Setup rooms and carve into tilemap
            List<Set<Tile>> rooms = TileMapAlgorithm.createTileRooms(newTileMap, true);
            rooms.forEach(room -> TileMapAlgorithm.carveIntoMap(newTileMap, room, Tile.COLLIDER, "ROOM"));
            TileMapAlgorithm.debug(newTileMap, Tile.COLLIDER);

            isPathCompletelyConnected = TileMapValidator.isValid(newTileMap);
            if (isPathCompletelyConnected) {
                TileMapAlgorithm.completeTerrainLiquidAndObstruction(newTileMap,true);
            }
        }

        return newTileMap;
    }
}
