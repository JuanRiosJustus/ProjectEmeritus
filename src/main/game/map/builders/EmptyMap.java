package main.game.map.builders;

import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapParameters;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class EmptyMap {

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapAlgorithm.class);
    protected boolean isPathCompletelyConnected = false;
    public EmptyMap() {}

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
