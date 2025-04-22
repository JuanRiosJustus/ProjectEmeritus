package main.game.map.builders;


import main.game.components.tile.TileComponent;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapParameters;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;

public class LargeContinuousRoom extends TileMapAlgorithm {

    @Override
    public TileMap evaluate(TileMapParameters tileMapParameters) {
        TileMap newTileMap = new TileMap(tileMapParameters);
        isPathCompletelyConnected = false;

        while (!isPathCompletelyConnected) {
            // Setup default map state
            newTileMap.reset();

            // Fill the map as completely solid
            Rectangle entireMapRoom = new Rectangle(newTileMap.getRows(), newTileMap.getColumns());
            Set<TileComponent> allMapTiles = TileMapAlgorithm.getAllTilesOfRoom(newTileMap, entireMapRoom);
            TileMapAlgorithm.carveIntoMap(newTileMap, allMapTiles, TileComponent.COLLIDER, "MAP_FILL");
            TileMapAlgorithm.debug(newTileMap, TileComponent.COLLIDER);

            // Setup rooms and carve into tilemap
            List<Set<TileComponent>> rooms = TileMapAlgorithm.createTileRooms(newTileMap, true);
            rooms.forEach(room -> TileMapAlgorithm.carveIntoMap(newTileMap, room, TileComponent.COLLIDER, null));
            TileMapAlgorithm.debug(newTileMap, TileComponent.COLLIDER);

            // Setup halls and carve into tilemap
            Set<TileComponent> halls = TileMapAlgorithm.connectRooms(newTileMap, rooms);
            TileMapAlgorithm.carveIntoMap(newTileMap, halls, TileComponent.COLLIDER, null);
            TileMapAlgorithm.debug(newTileMap, TileComponent.COLLIDER);

            // Ensure the outermost tiles are walls
            entireMapRoom = new Rectangle(newTileMap.getRows(), newTileMap.getColumns());
            Set<TileComponent> walls = TileMapAlgorithm.getWallTilesOfRoom(newTileMap, entireMapRoom);
            TileMapAlgorithm.carveIntoMap(newTileMap, walls, TileComponent.COLLIDER, "MAP_BORDER");

            isPathCompletelyConnected =  TileMapValidator.isValid(newTileMap);
            if (isPathCompletelyConnected) {
                TileMapAlgorithm.completeTerrainLiquidAndObstruction(newTileMap,true);
            }
        }

        return newTileMap;
    }
}
