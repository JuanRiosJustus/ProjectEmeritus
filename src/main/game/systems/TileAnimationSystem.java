package main.game.systems;

import main.constants.Direction;
import main.game.components.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapBuilder;
import main.game.stores.pools.asset.AssetPool;
import main.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class TileAnimationSystem extends GameSystem {

    private int mMaxHeight = Integer.MIN_VALUE;
    private int mMinHeight = Integer.MAX_VALUE;
    @Override
    public void update(GameModel model, Entity entity) {
        Tile tile = entity.get(Tile.class);
        if (tile == null) { return; }

        int height = tile.getHeight();
        mMinHeight = Math.min(mMinHeight, height);
        mMaxHeight = Math.max(mMaxHeight, height);
        // Get the shadows surrounding this tile

        tryPlacingDirectionalShadows(model.getTileMap(), tile, tile.getRow(), tile.getColumn());
        tryPlacingDepthShadows(entity, mMinHeight, mMaxHeight);
        tryUpdatingAnimationFrame(entity);
    }

    private void tryUpdatingAnimationFrame(Entity entity) {
        // Only update liquid animation if possible
        Tile tile = entity.get(Tile.class);
        Animation animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
        if (animation == null) { return; }
        animation.update();
    }

    private void tryPlacingDirectionalShadows(TileMap tileMap, Tile currentTile, int row, int column) {
        if (row == 0 || column == 0 || tileMap.getRows() - 1 == row || tileMap.getColumns(row) - 1 == column) {
            return;
        }

        if (currentTile.isWall()) { return; }
        List<String> assetIds = new ArrayList<>();

        // Check all the tiles in all directions, starting from north, ending in north west
        for (int i = 0; i < Direction.values().length; i++) {
            Direction direction = Direction.values()[i];

            int nextRow = row + direction.y;
            int nextColumn = column + direction.x;

            Entity adjacentEntity = tileMap.tryFetchingTileAt(nextRow, nextColumn);
            if (adjacentEntity == null) { continue; }
            Tile adjacentTile = adjacentEntity.get(Tile.class);

            // If the adjacent tile is higher, add a shadow in that direction
            if (adjacentTile.getHeight() <= currentTile.getHeight() && adjacentTile.isPath()) { continue; }
            // Enhanced liquid visuals where shadows not showing on them
            if (adjacentTile.getLiquid() != null) { continue; }

            int index = direction.ordinal();

            // TODO this is showing under walls, find a way to remove it
            String id = AssetPool.getInstance().getOrCreateAsset(
                    AssetPool.getInstance().createID(direction.name()),
                    AssetPool.MISC_SPRITEMAP,
                    Tile.CARDINAL_SHADOW,
                    index,
                    AssetPool.STATIC_ANIMATION
            );
            assetIds.add(id);
        }

        String mergedId = AssetPool.getInstance().createID(String.valueOf(assetIds));

        if (!AssetPool.getInstance().contains(mergedId)) {
            mergedId = AssetPool.getInstance().mergeAssets(mergedId, assetIds);
        }

        currentTile.putAsset(Tile.CARDINAL_SHADOW, mergedId);
        currentTile.putAsset(TileMapBuilder.SHADOW_COUNT, String.valueOf(assetIds.size()));
    }

    private void tryPlacingDepthShadows(Entity entity, int min, int max) {
        // map tile heights with shadows
        Tile tile = entity.get(Tile.class);
        int height = tile.getHeight();
        int mapped = (int) MathUtils.map(height, min, max, 5, 0); // lights depth is first
        String id = AssetPool.getInstance().getOrCreateAsset(
                AssetPool.getInstance().createID(String.valueOf(mapped)),
                AssetPool.MISC_SPRITEMAP,
                Tile.DEPTH_SHADOWS,
                mapped,
                AssetPool.STATIC_ANIMATION
        );
        tile.putAsset(Tile.DEPTH_SHADOWS, id);
    }
}
