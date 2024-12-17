package main.game.systems;

import main.constants.Direction;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.SecondTimer;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;
import main.utils.MathUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class TileVisualsSystem extends GameSystem {

    private int mMaxHeight = Integer.MIN_VALUE;
    private int mMinHeight = Integer.MAX_VALUE;
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;
    private BufferedImage mBackgroundWallpaper = null;
    private boolean mStartedBackgroundWallpaperWork = false;
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(TileVisualsSystem.class);
    @Override
    public void update(GameModel model, Entity entity) {
        Tile tile = entity.get(Tile.class);
        if (tile == null) { return; }

        mSpriteWidth = model.getGameState().getSpriteWidth();
        mSpriteHeight = model.getGameState().getSpriteHeight();

        updateDirectionalShadows(model, entity);
        updateDepthShadows(model, entity);
        updateStructures(model, entity);
        updateTerrain(model, entity);
        updateLiquid(model, entity);
        updateTileAnimation(model, entity);
    }

    void createBackgroundImageWallpaper(GameModel model) {
        // if there is no background image, create one on a new thread
        if (mBackgroundWallpaper != null) { return; }
        if (mStartedBackgroundWallpaperWork) { return; }
        mStartedBackgroundWallpaperWork = true;
        Thread temporaryThread = new Thread(() -> {
            SecondTimer st = new SecondTimer();
            mLogger.info("Started creating blurred background");
            mBackgroundWallpaper = createBlurredBackground(model);
            mLogger.info("Finished creating blurred background after {} seconds", st.elapsed());
        });
        temporaryThread.start();
    }
//    public void updateLiquid(GameModel model, Entity tileEntity) {
//        Tile tile = tileEntity.get(Tile.class);
//        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
//        String liquid = tile.getLiquid();
//        if (liquid == null) {
//            return;
//        }
//        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);
//        String animation = AssetPool.FLICKER_ANIMATION;
//
//        String id = AssetPool.getInstance().getOrCreateAsset(
//                model,
//                liquid,
//                animation,
//                -1,
//                identityComponent.getUuid() + mSpriteWidth + mSpriteHeight + liquid + tile.getRow() + tile.getColumn()
//        );
//
//        assetComponent.put(AssetComponent.LIQUID_ASSET, id);
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        asset.getAnimation().update();
//    }

    public void updateLiquid(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        String liquid = tile.isLiquid();
        if (!tile.isTopLayerLiquid()) { return; }
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);
        String animation = AssetPool.FLICKER_ANIMATION;

        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                liquid,
                animation,
                -1,
                identityComponent.getID() + mSpriteWidth + mSpriteHeight + liquid + tile.getRow() + tile.getColumn()
        );

        assetComponent.putMainID(id);
        Asset asset = AssetPool.getInstance().getAsset(id);
        asset.getAnimation().update();
    }

    public void updateTerrain(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        String asset = tile.getTopLayerAsset();
//        String type = tile.getTopLayerType();
        if (!tile.isTopLayerSolid()) { return; }

        String animation = AssetPool.STATIC_ANIMATION;
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);

        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                asset,
                animation,
                -1,
                identityComponent.getID() + asset + mSpriteWidth + mSpriteHeight + tile.getRow() + tile.getColumn()
        );

        assetComponent.putMainID(id);
    }

    public void updateStructures(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        Entity structureEntity = tile.getStructure();
        if (structureEntity == null) { return; }


        IdentityComponent identityComponent = structureEntity.get(IdentityComponent.class);
        AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
        String animation = AssetPool.TOP_SWAYING_ANIMATION;
        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                identityComponent.getNickname(),
                animation,
                -1,
                identityComponent.getNickname() + tile + mSpriteWidth + mSpriteHeight
        );

//        assetComponent.put(AssetComponent.STRUCTURE_ASSET, id);
        assetComponent.putMainID(id);

        Asset asset = AssetPool.getInstance().getAsset(id);
        asset.getAnimation().update();
    }

//    public void updateStructures(GameModel model, Entity tileEntity) {
//        Tile tile = tileEntity.get(Tile.class);
//        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
//
//        String obstruction = tile.getObstruction();
//        if (obstruction == null || obstruction.isBlank()) { return; }
//
//        String animation = AssetPool.STRETCH_Y_ANIMATION;
//        String id = AssetPool.getInstance().getOrCreateAsset(
//                model,
//                obstruction,
//                animation,
//                -1,
//                obstruction + tile + mSpriteWidth + mSpriteHeight
//        );
//
//        assetComponent.put(AssetComponent.STRUCTURE_ASSET, id);
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        asset.getAnimation().update();
//    }


    private void updateTileAnimation(GameModel model, Entity entity) {
        // Only update liquid animation if possible
        Tile tile = entity.get(Tile.class);
        AssetComponent assetComponent = entity.get(AssetComponent.class);
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);
        String animation = AssetPool.STATIC_ANIMATION;


        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                tile.getTopLayerAsset(),
                animation,
                -1,
                identityComponent.getID() + mSpriteWidth + mSpriteHeight
        );

//        assetComponent.put(AssetComponent.TERRAIN_ASSET, id);
        Asset asset = AssetPool.getInstance().getAsset(id);

//        if (animation == null) { return; }
//        animation.update();
    }

    private void updateDirectionalShadows(GameModel model, Entity tileEntity) {
        Tile currentTile = tileEntity.get(Tile.class);
        if (currentTile.isWall()) { return; }
        boolean currentTileIsLiquid = currentTile.isTopLayerLiquid();
        boolean currentTileIsSolid = currentTile.isTopLayerSolid();
        boolean currentTileIsBase = currentTile.isTopLayerBase();
        AssetComponent currentAssets = tileEntity.get(AssetComponent.class);
        Entity adjacentEntity = null;
        Tile adjacentTile = null;
        String animation = AssetPool.STATIC_ANIMATION;
        // Check all the tiles in all directions
        for (Direction direction : Direction.values()) {
            int adjacentRow = currentTile.getRow() + direction.y;
            int adjacentColumn = currentTile.getColumn() + direction.x;
            adjacentEntity = model.tryFetchingEntityAt(adjacentRow, adjacentColumn);
            if (adjacentEntity == null) { continue; }
            adjacentTile = adjacentEntity.get(Tile.class);
            String key = AssetComponent.DIRECTIONAL_SHADOWS_ASSET + direction.name();

            if (currentTileIsSolid || currentTileIsBase) {
                // If the adjacent tile is higher, add a shadow in that direction
                if (adjacentTile.getHeight() <= currentTile.getHeight()) { currentAssets.remove(key); continue; }
            } else if (currentTileIsLiquid) {
                // if the adjacent tile is a solid, add shadow in the direction
                if (adjacentTile.isTopLayerLiquid()) { currentAssets.remove(key); continue; }
            }
//            // if the adjacent tile is a solid, add shadow in the direction
//
//            // If the adjacent tile is higher, add a shadow in that direction
//            if (adjacentTile.getHeight() <= currentTile.getHeight()) { currentAssets.remove(key); continue; }

            // Enhanced liquid visuals where shadows not showing on them
//            if (adjacentTile.getLiquid() != null) { continue; }
//            if (!adjacentTile.getTopLayerType().equalsIgnoreCase(Tile.LAYER_TYPE_SOLID)) { continue; }

            String id = AssetPool.getInstance().getOrCreateAsset(
                    model,
                    direction.name() + "_shadow",
                    animation,
                    -1,
                    AssetComponent.DIRECTIONAL_SHADOWS_ASSET + "_" +
                            direction.name() + "_" + mSpriteWidth + "_" + mSpriteHeight
            );
            currentAssets.put(key, id);

        }
    }

//    private void updateDirectionalShadows(GameModel model, Entity tileEntity) {
//        Tile currentTile = tileEntity.get(Tile.class);
//        if (currentTile.isWall()) { return; }
//        boolean currentTileIsLiquid = currentTile.isTopLayerLiquid();
//        boolean currentTileIsSolid = currentTile.isTopLayerSolid();
//        boolean currentTileIsBase = currentTile.isTopLayerBase();
//        AssetComponent currentAssets = tileEntity.get(AssetComponent.class);
//        Entity adjacentEntity = null;
//        Tile adjacentTile = null;
//        String animation = AssetPool.STATIC_ANIMATION;
//        // Check all the tiles in all directions
//        for (Direction direction : Direction.values()) {
//            int adjacentRow = currentTile.getRow() + direction.y;
//            int adjacentColumn = currentTile.getColumn() + direction.x;
//            adjacentEntity = model.tryFetchingTileAt(adjacentRow, adjacentColumn);
//            if (adjacentEntity == null) { continue; }
//            adjacentTile = adjacentEntity.get(Tile.class);
//            String key = AssetComponent.DIRECTIONAL_SHADOWS_ASSET + direction.name();
//
//            if (currentTileIsSolid || currentTileIsBase) {
//                // If the adjacent tile is higher, add a shadow in that direction
//                if (adjacentTile.getHeight() <= currentTile.getHeight()) { currentAssets.remove(key); continue; }
//            } else if (currentTileIsLiquid) {
//                // if the adjacent tile is a solid, add shadow in the direction
//                if (adjacentTile.isTopLayerLiquid()) { currentAssets.remove(key); continue; }
//            }
////            // if the adjacent tile is a solid, add shadow in the direction
////
////            // If the adjacent tile is higher, add a shadow in that direction
////            if (adjacentTile.getHeight() <= currentTile.getHeight()) { currentAssets.remove(key); continue; }
//
//            // Enhanced liquid visuals where shadows not showing on them
////            if (adjacentTile.getLiquid() != null) { continue; }
////            if (!adjacentTile.getTopLayerType().equalsIgnoreCase(Tile.LAYER_TYPE_SOLID)) { continue; }
//
//            String id = AssetPool.getInstance().getOrCreateAsset(
//                    model,
//                    direction.name() + "_shadow",
//                    animation,
//                    -1,
//                    AssetComponent.DIRECTIONAL_SHADOWS_ASSET + "_" +
//                            direction.name() + "_" + mSpriteWidth + "_" + mSpriteHeight
//            );
//            currentAssets.put(key, id);
//
//        }
//    }

    private void updateDepthShadows(GameModel model, Entity tileEntity) {
        // map tile heights with shadows
        Tile tile = tileEntity.get(Tile.class);
        String animation = AssetPool.STATIC_ANIMATION;
        int height = tile.getHeight();
        mMinHeight = Math.min(mMinHeight, height);
        mMaxHeight = Math.max(mMaxHeight, height);
        int mapped = (int) MathUtils.map(height, mMinHeight, mMaxHeight, 3, 0); // lights depth is first

        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                "depth_shadows",
                animation,
                mapped,
                "depth_shadows" + mapped + mSpriteWidth + mSpriteHeight
        );

        assetComponent.put(AssetComponent.DEPTH_SHADOWS_ASSET, id);
    }

    private BufferedImage createBlurredBackground(GameModel model) {
        // Create background after first iteration where the image is guaranteed to have something

        int backgroundImageWidth = model.getGameState().getViewportWidth();
        int backgroundImageHeight = model.getGameState().getViewportHeight();
        BufferedImage bImg = new BufferedImage(backgroundImageWidth, backgroundImageHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D backgroundGraphics = bImg.createGraphics();

        int tileWidth = backgroundImageWidth / model.getColumns();
        int tileHeight = backgroundImageHeight / model.getRows();
        // Construct image based off of the current map
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                Entity tileEntity = model.tryFetchingEntityAt(row, column);
                AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
                Tile tile = tileEntity.get(Tile.class);
                int tileX = tile.getColumn() * tileWidth;
                int tileY = tile.getRow() * tileHeight;

                BufferedImage scaledImage = null;
                // Draw the base of the tile
                if (tile.getTopLayerAsset() != null) {
                    String id = assetComponent.getMainID();
                    Animation animation = AssetPool.getInstance().getAnimation(id);
                    scaledImage = ImageUtils.getResizedImage(animation.toImage(), tileWidth, tileHeight);
                    backgroundGraphics.drawImage(scaledImage, tileX, tileY, null);
                }

                // Draw the directional shadows
                List<String> directionalShadowIds = assetComponent.getIds(AssetComponent.DIRECTIONAL_SHADOWS_ASSET);
                if (!directionalShadowIds.isEmpty()) {
                    for (String id : directionalShadowIds) {
                        Animation animation = AssetPool.getInstance().getAnimation(id);
                        scaledImage = ImageUtils.getResizedImage(animation.toImage(), tileWidth, tileHeight);
                        backgroundGraphics.drawImage(scaledImage, tileX, tileY, null);
                    }
                }

                if (!tile.isWall()) {
                    String id = assetComponent.getID(AssetComponent.DEPTH_SHADOWS_ASSET);
                    Animation animation = AssetPool.getInstance().getAnimation(id);
                    scaledImage = ImageUtils.getResizedImage(animation.toImage(), tileWidth, tileHeight);
                    backgroundGraphics.drawImage(scaledImage, tileX, tileY, null);
                }
            }
        }
//            ImageIO.write(mBackgroundWallpaper, "png", new File("./output_image.png"));
        return ImageUtils.createBlurredImage(bImg);
    }

    public BufferedImage getBackgroundWallpaper() {
        return mBackgroundWallpaper;
    }
}
