package main.game.systems;

import main.constants.Direction;
import main.game.components.Assets;
import main.game.components.Identity;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.utils.MathUtils;

public class TileVisualsSystem extends GameSystem {

    private int mMaxHeight = Integer.MIN_VALUE;
    private int mMinHeight = Integer.MAX_VALUE;
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;
    @Override
    public void update(GameModel model, Entity entity) {
        Tile tile = entity.get(Tile.class);
        if (tile == null) { return; }

        mSpriteWidth = model.getSettings().getSpriteWidth();
        mSpriteHeight = model.getSettings().getSpriteHeight();

        updateDirectionalShadows(model, entity);
        updateDepthShadows(model, entity);
        updateObstructions(model, entity);
        updateTerrain(model, entity);
        updateLiquid(model, entity);
        updateTileAnimation(model, entity);
    }

    public void updateLiquid(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Assets assets = tileEntity.get(Assets.class);
        String liquid = tile.getLiquid();
        if (liquid == null) {
            return;
        }
        Identity identity = tileEntity.get(Identity.class);
        String animation = AssetPool.FLICKER_ANIMATION;

        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                liquid,
                animation,
                identity.getUuid() + mSpriteWidth + mSpriteHeight
        );

        assets.put(Assets.LIQUID_ASSET, id);
        Asset asset = AssetPool.getInstance().getAsset(id);
        asset.getAnimation().update();
    }

    public void updateTerrain(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Assets assets = tileEntity.get(Assets.class);
        String terrain = tile.getTerrain();
        String animation = AssetPool.STATIC_ANIMATION;
        Identity identity = tileEntity.get(Identity.class);

        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                terrain,
                animation,
                identity.getUuid() + terrain + mSpriteWidth + mSpriteHeight
        );

        assets.put(Assets.TERRAIN_ASSET, id);
    }

    public void updateObstructions(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Assets assets = tileEntity.get(Assets.class);

        String obstruction = tile.getObstruction();
        if (obstruction == null) { return; }

        String animation = AssetPool.STRETCH_Y_ANIMATION;
        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                obstruction,
                animation,
                obstruction + tile + mSpriteWidth + mSpriteHeight
        );

        assets.put(Assets.OBSTRUCTION_ASSET, id);
        Asset asset = AssetPool.getInstance().getAsset(id);
        asset.getAnimation().update();
    }


    private void updateTileAnimation(GameModel model, Entity entity) {
        // Only update liquid animation if possible
        Tile tile = entity.get(Tile.class);
        Assets assets = entity.get(Assets.class);
//        Animation animation = assets.getAnimation(Assets.TERRAIN_ASSET);
        Identity identity = entity.get(Identity.class);
        String animation = AssetPool.STATIC_ANIMATION;


        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                tile.getTerrain(),
                animation,
                identity.getUuid() + mSpriteWidth + mSpriteHeight
        );

        assets.put(Assets.TERRAIN_ASSET, id);
        Asset asset = AssetPool.getInstance().getAsset(id);

//        if (animation == null) { return; }
//        animation.update();
    }

    private void updateDirectionalShadows(GameModel model, Entity tileEntity) {
        Tile currentTile = tileEntity.get(Tile.class);
        if (currentTile.isWall()) { return; }
        int row = currentTile.getRow();
        int column = currentTile.getColumn();
        Assets assets = tileEntity.get(Assets.class);
        String animation = AssetPool.STATIC_ANIMATION;

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

            String id = AssetPool.getInstance().getOrCreateAsset(
                    model,
                    "directional_shadows",
                    animation,
                    index,
                    Assets.DIRECTIONAL_SHADOWS_ASSET + "_" +
                            direction.name() + "_" +
                            mSpriteWidth + "_" +
                            mSpriteHeight
            );

            assets.put(Assets.DIRECTIONAL_SHADOWS_ASSET + direction.name(), id);
        }
    }

    private void updateDepthShadows(GameModel model, Entity tileEntity) {
        // map tile heights with shadows
        Tile tile = tileEntity.get(Tile.class);
        String animation = AssetPool.STATIC_ANIMATION;
        int height = tile.getHeight();
        mMinHeight = Math.min(mMinHeight, height);
        mMaxHeight = Math.max(mMaxHeight, height);
        int mapped = (int) MathUtils.map(height, mMinHeight, mMaxHeight, 5, 0); // lights depth is first

        Assets assets = tileEntity.get(Assets.class);
        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                "depth_shadows",
                animation,
                mapped,
                "depth_shadows" + mapped + mSpriteWidth + mSpriteHeight
        );

        assets.put(Assets.DEPTH_SHADOWS_ASSET, id);
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        System.out.println(asset.getId() + " = " + id);
//        System.out.println(asset.getAnimation().toImage().getWidth() +  " x " + asset.getAnimation().toImage().getHeight());
    }
}
