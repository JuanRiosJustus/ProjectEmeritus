package main.game.map.builders;

import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapParameters;

public class BasicOpenMap extends TileMapAlgorithm {

    @Override
    public TileMap evaluate(TileMapParameters tileMapParameters) {
        TileMap newTileMap = new TileMap(tileMapParameters);
        isPathCompletelyConnected = false;

        while (!isPathCompletelyConnected) {
            newTileMap.reset();

            isPathCompletelyConnected = TileMapValidator.isValid(newTileMap);
            if (isPathCompletelyConnected) {
                TileMapAlgorithm.completeTerrainLiquidAndObstruction(newTileMap,false);
            }
        }

        return newTileMap;
    }
}
