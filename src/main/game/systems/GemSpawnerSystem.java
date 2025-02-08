package main.game.systems;

import main.constants.Direction;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.foundation.LootTable;
import main.game.main.GameModel;


public class GemSpawnerSystem extends GameSystem {
    private static final int SPAWN_ATTEMPTS = 50;
//    private final LootTable<Gem> gemDropTable = new LootTable<>();

    public GemSpawnerSystem() {
//        gemDropTable.add(Gem.RESET, .4f);
//        gemDropTable.add(Gem.MAGICAL_BUFF, .1f);
//        gemDropTable.add(Gem.PHYSICAL_BUFF, .1f);
//        gemDropTable.add(Gem.CRITICAL_BUFF, .1f);
//        gemDropTable.add(Gem.SPEED_BUFF, .1f);
//        gemDropTable.add(Gem.ENERGY_RESTORE, .1f);
//        gemDropTable.add(Gem.HEALTH_RESTORE, .1f);
    }

    public void update(GameModel model, Entity unit) {

        if (random.nextBoolean()) { return; }

        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {

            int row = random.nextInt(0, model.getRows());
            int column = random.nextInt(0, model.getColumns() - 1);

            Entity entity = model.tryFetchingEntityAt(row, column);
            if (entity == null) { continue; }
            Tile tile = entity.get(Tile.class);

            if (tile.isWall() || tile.isOccupied() || tile.isNotNavigable()) { continue; }
            if (tile.isNotNavigable()) { continue; }

            boolean hasNearby = false;
            for (Direction direction : Direction.values()) {
                Entity adjacent = model.tryFetchingEntityAt(
                        tile.getRow() + direction.y,
                        tile.getColumn() + direction.x
                );
                if (adjacent == null) { continue; }
                Tile adjTile = adjacent.get(Tile.class);
//                if (adjTile.getGem() == null)  { continue; }
                hasNearby = true;
            }

            if (hasNearby) { continue; }

//            Gem gem = gemDropTable.getDrop();
//            gem.animationId = AssetPool.getInstance().createDynamicAssetReference(
//                Constants.GEMS_SPRITESHEET_PATH, gem.type.ordinal(), "spinning");
//            tile.setGem(gem);
            
            return;
        }
    }

    @Override
    public void update(GameModel model, String id) {

    }
}
