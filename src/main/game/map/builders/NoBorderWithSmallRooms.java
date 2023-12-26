package main.game.map.builders;

import main.game.map.base.TileMap;
import main.game.map.builders.utils.TileMapOperations;

public class NoBorderWithSmallRooms extends TileMapOperations {

    public NoBorderWithSmallRooms(TileMap tileMap) {


        while (!isPathCompletelyConnected) {

            tileMap.init();

            tileMap.getColliderLayer().fill(tileMap.getFloor());

            isPathCompletelyConnected = TileMapValidator.isValid(tileMap);
            if (isPathCompletelyConnected) {
                tileMap.commit();
            }
        }

        tileMap.push();

//        super(tileMap);

//        while (!isPathCompletelyConnected) {
//
//            initializeMap();
//
//            getColliderLayer().fill(getFloor());
//
//            List<Set<Tile>> rooms = TileMapOperations.tryCreatingRooms(this, true);
//
//            logger.info("Validating {}", this.getClass());
//            isPathCompletelyConnected = TileMapOperations.isValidConfiguration(this);
//
//            if (isPathCompletelyConnected) {
//                logger.debug(System.lineSeparator() + getColliderLayer().debug(false));
//                logger.debug(System.lineSeparator() + getColliderLayer().debug(true));
//                finalizeMap();
//            } else {
//                generateNewSeed();
//            }
//        }

//        TileMapOperations.tryPlacingLiquids(this);
//        TileMapOperations.tryPlacingDestroyableBlockers(this);
//        TileMapOperations.tryPlacingRoughTerrain(this);
//        TileMapOperations.tryPlacingExits(this);
//        TileMapOperations.tryPlacingEntrance(this);

//        return createTileMap();
    }

    @Override
    public void execute(TileMap tileMap) {

    }
}
