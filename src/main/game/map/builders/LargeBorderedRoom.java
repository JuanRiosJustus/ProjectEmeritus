package main.game.map.builders;

import main.game.components.tile.TileComponent;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapParameters;

import java.awt.Rectangle;
import java.util.Set;

//public class LargeBorderedRoom extends TileMapAlgorithm {
public class LargeBorderedRoom extends TileMapAlgorithm {

    @Override
    public TileMap evaluate(TileMapParameters tileMapParameters) {
        TileMap newTileMap = new TileMap(tileMapParameters);
        isPathCompletelyConnected = false;

        while (!isPathCompletelyConnected) {
            newTileMap.reset();

            // Ensure the outermost tiles are walls
            Rectangle entireMapRoom = new Rectangle(newTileMap.getRows(), newTileMap.getColumns());
            Set<TileComponent> walls = TileMapAlgorithm.getWallTilesOfRoom(newTileMap, entireMapRoom);
            TileMapAlgorithm.carveIntoMap(newTileMap, walls, TileComponent.COLLIDER, "MAP_BORDER");

            isPathCompletelyConnected = TileMapValidator.isValid(newTileMap);
            if (isPathCompletelyConnected) {
                TileMapAlgorithm.completeTerrainLiquidAndObstruction(newTileMap,false);
            }
        }

        return newTileMap;
    }
}
