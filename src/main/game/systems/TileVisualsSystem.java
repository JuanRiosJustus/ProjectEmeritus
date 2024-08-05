package main.game.systems;

import main.constants.Direction;
import main.game.components.Animation;
import main.game.components.Assets;
import main.game.components.Identity;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.map.base.TileMap;
import main.game.stores.pools.asset.AssetPool;
import main.utils.MathUtils;

public class TileVisualsSystem extends GameSystem {

    private int mMaxHeight = Integer.MIN_VALUE;
    private int mMinHeight = Integer.MAX_VALUE;
    @Override
    public void update(GameModel model, Entity entity) {
        Tile tile = entity.get(Tile.class);
        if (tile == null) { return; }

        updateDirectionalShadows(model, entity);
        updateDepthShadows(entity);
        updateObstructions(model, entity);
        updateTerrain(model, entity);
        updateLiquid(model, entity);
        updateTileAnimation(entity);
    }

    public void updateLiquid(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Assets assets = tileEntity.get(Assets.class);
        String liquid = tile.getLiquid();
        if (liquid == null) {
            return;
        }
        String spriteMap = AssetPool.TILES_SPRITEMAP;
        Identity identity = tileEntity.get(Identity.class);
        assets.put(
                Assets.LIQUID_ASSET,
                identity.getUuid(),
                spriteMap,
                liquid,
                -1,
                AssetPool.FLICKER_ANIMATION
        );
        Animation animation = assets.getAnimation(Assets.LIQUID_ASSET);
        animation.update();
    }

    public void updateTerrain(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Assets assets = tileEntity.get(Assets.class);
        String spriteMap = AssetPool.TILES_SPRITEMAP;
        String terrain = tile.getTerrain();
        Identity identity = tileEntity.get(Identity.class);
        assets.put(
                Assets.TERRAIN_ASSET,
                identity.getUuid(),
                spriteMap,
                terrain,
                -1,
                AssetPool.STATIC_ANIMATION
        );

        Animation animation = assets.getAnimation(Assets.TERRAIN_ASSET);
        animation.update();
    }

    public void updateObstructions(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Assets assets = tileEntity.get(Assets.class);

        String obstruction = tile.getObstruction();
        if (obstruction == null) { return; }
        String spriteMap = AssetPool.TILES_SPRITEMAP;
        boolean isShearing = obstruction.contains(Tile.OBSTRUCTION_DESTROYABLE_BLOCKER);
//        String animationType = isShearing ? AssetPool.SHEARING_ANIMATION : AssetPool.STATIC_ANIMATION;

        String animationType = AssetPool.STRETCH_Y_ANIMATION;

        assets.put(
                Assets.OBSTRUCTION_ASSET,
                obstruction + tile.toString(),
                spriteMap,
                obstruction,
                -1,
                animationType
        );

        Animation animation = assets.getAnimation(Assets.OBSTRUCTION_ASSET);
        animation.update();
    }


    private void updateTileAnimation(Entity entity) {
        // Only update liquid animation if possible
        Tile tile = entity.get(Tile.class);
        Assets assets = entity.get(Assets.class);
        Animation animation = assets.getAnimation(Assets.TERRAIN_ASSET);
        if (animation == null) { return; }
        animation.update();
    }

    private void updateDirectionalShadows(GameModel model, Entity tileEntity) {
        Tile currentTile = tileEntity.get(Tile.class);
        if (currentTile.isWall()) { return; }
        int row = currentTile.getRow();
        int column = currentTile.getColumn();
        Assets assets = tileEntity.get(Assets.class);

        // Check all the tiles in all directions, starting from north, ending in north west
        for (int i = 0; i < Direction.values().length; i++) {
            Direction direction = Direction.values()[i];

            int nextRow = row + direction.y;
            int nextColumn = column + direction.x;

            Entity adjacentEntity = model.tryFetchingTileAt(nextRow, nextColumn);
            if (adjacentEntity == null) { continue; }
            Tile adjacentTile = adjacentEntity.get(Tile.class);

            // If the adjacent tile is higher, add a shadow in that direction
            if (adjacentTile.getHeight() <= currentTile.getHeight() && adjacentTile.isPath()) { continue; }
            // Enhanced liquid visuals where shadows not showing on them
//            if (adjacentTile.getLiquid() != null) { continue; }

            int index = direction.ordinal();

            assets.put(
                    Assets.DIRECTIONAL_SHADOWS_ASSET + direction.name() + currentTile.toString(),
                    AssetPool.getInstance().getOrCreateID(direction.name()),
                    AssetPool.MISC_SPRITEMAP,
                    Tile.CARDINAL_SHADOW,
                    index,
                    AssetPool.STATIC_ANIMATION
            );
        }
    }

    private void updateDepthShadows(Entity tileEntity) {
        // map tile heights with shadows
        Tile tile = tileEntity.get(Tile.class);
        int height = tile.getHeight();
        mMinHeight = Math.min(mMinHeight, height);
        mMaxHeight = Math.max(mMaxHeight, height);
        int mapped = (int) MathUtils.map(height, mMinHeight, mMaxHeight, 5, 0); // lights depth is first

        Assets assets = tileEntity.get(Assets.class);
        assets.put(
                Assets.DEPTH_SHADOWS_ASSET,
                AssetPool.getInstance().getOrCreateID(String.valueOf(mapped)),
                AssetPool.MISC_SPRITEMAP,
                Tile.DEPTH_SHADOWS,
                mapped,
                AssetPool.STATIC_ANIMATION
        );
    }
}
