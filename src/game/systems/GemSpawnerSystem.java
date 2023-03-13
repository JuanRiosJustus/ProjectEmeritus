package game.systems;

import constants.Constants;
import designer.fundamentals.Direction;
import game.GameModel;
import game.collectibles.Gem;
import game.components.Tile;
import game.entity.Entity;
import game.stores.pools.AssetPool;


public class GemSpawnerSystem extends GameSystem {
    private static final int SPAWN_ATTEMPTS = 50;
    public void update(GameModel model, Entity unit) {

        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            int row = random.nextInt(0, model.getRows());
            int column = random.nextInt(0, model.getColumns() - 1);

            Entity entity = model.tryFetchingTileAt(row, column);
            if (entity == null) { continue; }
            Tile tile = entity.get(Tile.class);

            if (tile.isWall() || tile.isOccupied() || tile.isStructure()) { continue; }
            if (tile.isStructureUnitOrWall()) { continue; }

            boolean hasNearby = false;
            for (Direction direction : Direction.values()) {
                Entity adjacent = model.tryFetchingTileAt(tile.row + direction.y, tile.column + direction.x);
                if (adjacent == null) { continue; }
                Tile adjTile = adjacent.get(Tile.class);
                if (adjTile.getGem() == null)  { continue; }
                hasNearby = true;
            }

            if (hasNearby) { continue; }

            Gem b = new Gem();
            b.type = getRandomGemType();
            b.animationId = AssetPool.instance().createAnimatedAssetReference(
                Constants.GEMS_SPRITESHEET_PATH, 0, b.type.ordinal(), "spinning");
            tile.setGem(b);
            
            return;
        }
    }

    private Gem.Type getRandomGemType() {
        return Gem.Type.values()[random.nextInt(Gem.Type.values().length)];
    }
}
