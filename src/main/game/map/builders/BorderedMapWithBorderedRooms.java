package main.game.map.builders;

import main.game.components.tile.Tile;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapParameters;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;

public class BorderedMapWithBorderedRooms extends TileMapAlgorithm {

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

            // Ensure the outermost tiles are walls
            Rectangle entireMapRoom = new Rectangle(newTileMap.getRows(), newTileMap.getColumns());
            Set<Tile> walls = TileMapAlgorithm.getWallTilesOfRoom(newTileMap, entireMapRoom);
            TileMapAlgorithm.carveIntoMap(newTileMap, walls, Tile.COLLIDER, "MAP_BORDER");

            isPathCompletelyConnected = TileMapValidator.isValid(newTileMap);
            if (isPathCompletelyConnected) {
                TileMapAlgorithm.completeTerrainLiquidAndObstruction(newTileMap,false);
            }
        }

        return newTileMap;
    }
}
