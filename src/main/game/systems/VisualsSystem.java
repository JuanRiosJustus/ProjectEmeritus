package main.game.systems;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.constants.Direction;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.constants.SecondTimer;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.graphics.AssetPool;
import main.logging.EmeritusLogger;

import main.utils.ImageUtils;
import main.utils.MathUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class VisualsSystem extends GameSystem {

    private int mMaxTileHeight = Integer.MIN_VALUE;
    private int mMinTileHeight = Integer.MAX_VALUE;
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;
    private Image mBackgroundWallpaper = null;
    private boolean mStartedBackgroundWallpaperWork = false;
    private final List<String> mEphemeralList = new ArrayList<>();
    private final EmeritusLogger mLogger = EmeritusLogger.create(VisualsSystem.class);

    public VisualsSystem(GameModel gameModel) { super(gameModel); }

    public void createBackgroundImageWallpaper(GameModel model) {
        // if there is no background image, create one on a new thread
        if (mBackgroundWallpaper != null) { return; }
        if (mStartedBackgroundWallpaperWork) { return; }
        mStartedBackgroundWallpaperWork = true;
        Platform.runLater(() -> {
            SecondTimer st = new SecondTimer();
            try {
                mLogger.info("Started creating blurred background");
                BufferedImage backgroundWallpaper = createBlurredBackground(model);
                if (backgroundWallpaper == null) { throw new Exception("Yikes!"); }
                mBackgroundWallpaper = SwingFXUtils.toFXImage(backgroundWallpaper, null);
                mLogger.info("Finished creating blurred background after {} seconds", st.elapsed());
            } catch (Exception ex) {
                mStartedBackgroundWallpaperWork = false;
                mLogger.info("Unable to finished creating blurred background after {} seconds", st.elapsed());
            }
        });
    }


    public void updateLiquidV2(GameModel gameModel, Entity tileEntity) {
        TileComponent tile = tileEntity.get(TileComponent.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        if (!tile.isTopLayerLiquid()) { return; }


        String liquid = tile.getTopLayerAsset();
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);

        String id = AssetPool.getInstance().getOrCreateFlickerAsset(
                mSpriteWidth,
                mSpriteHeight,
                liquid,
                -1,
                gameModel.getGameState().hashCode() + identityComponent.getID() + mSpriteWidth + mSpriteHeight + liquid + tile.getRow() + tile.getColumn()
        );

        assetComponent.putMainID(id);
        AssetPool.getInstance().update(id);


//        String depthShadowID = getOrCreateDepthShadows(gameModel, tileEntity);
//        String directionalShadowsID = getOrCreateDirectionalShadows(gameModel, tileEntity);
//
//        mEphemeralList.clear();
////        mEphemeralList.add(depthShadowID);
////        mEphemeralList.add(directionalShadowsID);
//        mEphemeralList.add(id);
//
//        String finalizedMergedTileAssetID = AssetPool.getInstance().getOrCreateMergedAssets(
//                mSpriteWidth,
//                mSpriteHeight,
//                mEphemeralList,
//                String.valueOf(mEphemeralList.hashCode())
//        );
//
//
//        assetComponent.putMainID(finalizedMergedTileAssetID);
//        AssetPool.getInstance().update(finalizedMergedTileAssetID);

    }

    public void updateLiquid(GameModel model, Entity tileEntity) {
        TileComponent tile = tileEntity.get(TileComponent.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        if (!tile.isTopLayerLiquid()) { return; }

        String liquid = tile.getTopLayerAsset();
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);

        String id = AssetPool.getInstance().getOrCreateFlickerAsset(
                mSpriteWidth,
                mSpriteHeight,
                liquid,
                -1,
                mSpriteWidth + mSpriteHeight + liquid + tile.getRow() + tile.getColumn()
        );

        assetComponent.putMainID(id);
        AssetPool.getInstance().update(id);
    }




    public void updateTiles(GameModel gameModel, Entity tileEntity) {

        String baseTileSpriteID = getOrCreateSprite(gameModel, tileEntity);

        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);

        // IF there is no more tile left, dont show any base sprite
        if (baseTileSpriteID == null) { assetComponent.removeMainID(); return; }
        assetComponent.putMainID(baseTileSpriteID);
    }

    public void updateShadows(GameModel gameModel, Entity tileEntity) {

        String depthShadowID = getOrCreateDepthShadows(gameModel, tileEntity);
        String directionalShadowsID = getOrCreateDirectionalShadows(gameModel, tileEntity);

        mEphemeralList.clear();
        mEphemeralList.add(depthShadowID);
        mEphemeralList.add(directionalShadowsID);

        String finalizedMergedTileAssetID = AssetPool.getInstance().getOrCreateMergedAssets(
                mSpriteWidth,
                mSpriteHeight,
                mEphemeralList,
                String.valueOf(mEphemeralList.hashCode())
        );

        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        assetComponent.putShadowID(finalizedMergedTileAssetID);
    }

    private String getOrCreateDirectionalShadows(GameModel gameModel, Entity tileEntity) {
        TileComponent currentTile = tileEntity.get(TileComponent.class);
        if (currentTile.isWall()) { return ""; }
        int currentElevation = currentTile.getTotalElevation();
        boolean isCurrentTileSolid = currentTile.isTopLayerSolid();
        boolean isCurrentTileLiquid = currentTile.isTopLayerLiquid();

        List<String> currentIds = mEphemeralList;
        currentIds.clear();

        // Check all the tiles in all directions
        for (Direction direction : Direction.values()) {
            String directionName = direction.name();
            int directionIndex = direction.ordinal();
            int adjacentRow = currentTile.getRow() + direction.y;
            int adjacentColumn = currentTile.getColumn() + direction.x;
            Entity adjacentEntity = gameModel.tryFetchingEntityAt(adjacentRow, adjacentColumn);
            if (adjacentEntity == null) { continue; }
            TileComponent adjacentTile = adjacentEntity.get(TileComponent.class);
            int adjacentElevation = adjacentTile.getTotalElevation();
            boolean isAdjacentTileLiquid = adjacentTile.isTopLayerLiquid();
            boolean isAdjacentTileSolid = adjacentTile.isTopLayerSolid();


            // If current tile is liquid and adjacent liquid, skip adding shadow
            if (isCurrentTileSolid && isAdjacentTileLiquid) { continue; }
            // if adjacent elevation is less than current, skip adding shadow
            if (isCurrentTileSolid && currentElevation >= adjacentElevation) { continue; }
            if (isCurrentTileLiquid && isAdjacentTileLiquid) { continue; }
            if (isCurrentTileLiquid && isAdjacentTileSolid && adjacentElevation > currentElevation) { continue; }

            String id = AssetPool.getInstance().getOrCreateDirectionalShadows(
                    mSpriteWidth,
                    mSpriteHeight,
                    directionIndex,
                    "directional_shadows" + directionName + mSpriteWidth + mSpriteHeight
            );
            currentIds.add(id);
        }

        int directionalShadowsHash = currentIds.hashCode();
        String mergedDirectionalShadowsID = AssetPool.getInstance().getOrCreateMergedAssets(
                mSpriteWidth,
                mSpriteHeight,
                currentIds,
                String.valueOf(directionalShadowsHash)
        );

        return  mergedDirectionalShadowsID;
    }

    private String getOrCreateSprite(GameModel gameModel, Entity tileEntity) {
        TileComponent tile = tileEntity.get(TileComponent.class);
        String baseTileSprite = tile.getTopLayerAsset();
        // If this is null, maybe blackness or void?
        if (baseTileSprite == null) { return null; }
        String baseTileSpriteID = AssetPool.getInstance().getOrCreateStaticAsset(
                mSpriteWidth,
                mSpriteHeight,
                baseTileSprite,
                -1,
                "static_tile_sprite" + baseTileSprite + mSpriteWidth + mSpriteHeight + tile.getRow() + tile.getColumn()
        );
        return baseTileSpriteID;
    }

    private String getOrCreateDepthShadows(GameModel gameModel, Entity tileEntity) {
        TileComponent tile = tileEntity.get(TileComponent.class);
        int tileHeight = tile.getModifiedElevation();
        mMinTileHeight = Math.min(mMinTileHeight, tileHeight);
        mMaxTileHeight = Math.max(mMaxTileHeight, tileHeight);
        int depthShadowFrame = (int) MathUtils.map(tileHeight, mMinTileHeight, mMaxTileHeight, 3, 0); // lights depth is first
        String depthShadowID = AssetPool.getInstance().getOrCreateDepthShadows(
                mSpriteWidth,
                mSpriteHeight,
                depthShadowFrame,
                "depth_shadows_" + depthShadowFrame + mSpriteWidth + mSpriteHeight
        );
        return depthShadowID;
    }











    public void updateTerrainLayers(GameModel model, Entity tileEntity) {
        TileComponent tile = tileEntity.get(TileComponent.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);

        String sprite = tile.getTopLayerAsset();
        if (!tile.isTopLayerSolid()) { return; }

        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);

        String id = AssetPool.getInstance().getOrCreateStaticAsset(
                mSpriteWidth,
                mSpriteHeight,
                sprite,
                -1,
                identityComponent.getID() + sprite + mSpriteWidth + mSpriteHeight + tile.getRow() + tile.getColumn()
        );
//        int originFrame = AssetPool.getInstance().getOriginFrame(id);
//        tile.setOriginFrame(originFrame);

        assetComponent.putMainID(id);
    }

//    public void updateStructures(GameModel model, Entity tileEntity) {
//        TileComponent tile = tileEntity.get(TileComponent.class);
//        String structureID = tile.getStructureID();
//        Entity structureEntity = EntityStore.getInstance().get(structureID);
//        if (structureEntity == null) { return; }
//
//        IdentityComponent identityComponent = structureEntity.get(IdentityComponent.class);
//        AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
//        String id = AssetPool.getInstance().getOrCreateTopSwayingAsset(
//                mSpriteWidth,
//                mSpriteHeight,
//                identityComponent.getNickname(),
//                -1,
//                identityComponent.getNickname() + tile + mSpriteWidth + mSpriteHeight
//        );
//
//        assetComponent.putMainID(id);
//
////        Asset asset = AssetPool.getInstance().getAsset(id);
////        asset.getAnimation().update();
//        AssetPool.getInstance().update(id);
//    }

    public void updateStructures(GameModel model, Entity tileEntity) {
        TileComponent tile = tileEntity.get(TileComponent.class);
        String structureID = tile.getStructureID();
        Entity structureEntity = EntityStore.getInstance().get(structureID);
        if (structureEntity == null) { return; }

        IdentityComponent identityComponent = structureEntity.get(IdentityComponent.class);
        AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
        String id = AssetPool.getInstance().getOrCreateTopSwayingAsset(
                mSpriteWidth,
                mSpriteHeight,
                identityComponent.getNickname(),
                -1,
                identityComponent.getNickname() + tile + mSpriteWidth + mSpriteHeight
        );

        assetComponent.putMainID(id);

//        Asset asset = AssetPool.getInstance().getAsset(id);
//        asset.getAnimation().update();
        AssetPool.getInstance().update(id);
    }


    private void updateTileAnimation(GameModel model, Entity entity) {
        // Only update liquid animation if possible
        TileComponent tile = entity.get(TileComponent.class);
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);

//        String id = AssetPool.getInstance().getOrCreateStaticAsset(
//                mSpriteWidth,
//                mSpriteHeight,
//                tile.getTopLayerAsset(),
//                -1,
//                identityComponent.getID() + mSpriteWidth + mSpriteHeight
//        );

//        assetComponent.put(AssetComponent.TERRAIN_ASSET, id);
//        Asset asset = AssetPool.getInstance().getAsset(id);

//        if (animation == null) { return; }
//        animation.update();
    }


    private BufferedImage createBlurredBackground(GameModel model) {
        // Create background after first iteration where the image is guaranteed to have something
        BufferedImage result = null;
        try {
            int backgroundImageWidth = model.getGameState().getMainCameraWidth();
            int backgroundImageHeight = model.getGameState().getMainCameraHeight();
            BufferedImage bImg = new BufferedImage(backgroundImageWidth, backgroundImageHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D backgroundGraphics = bImg.createGraphics();

            int tileWidth = backgroundImageWidth / model.getColumns();
            int tileHeight = backgroundImageHeight / model.getRows();
            // Construct image based off of the current map
            for (int row = 0; row < model.getRows(); row++) {
                for (int column = 0; column < model.getColumns(); column++) {
                    Entity tileEntity = model.tryFetchingEntityAt(row, column);
                    AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
                    TileComponent tile = tileEntity.get(TileComponent.class);
                    int tileX = tile.getColumn() * tileWidth;
                    int tileY = tile.getRow() * tileHeight;

                    BufferedImage scaledImage = null;
                    // Draw the base of the tile
                    if (tile.getTopLayerAsset() != null) {
                        String id = assetComponent.getMainID();
                        Image image = AssetPool.getInstance().getImage(id);
                        scaledImage = SwingFXUtils.fromFXImage(image, null);
                        scaledImage = ImageUtils.getResizedImage(scaledImage, tileWidth, tileHeight);
                        backgroundGraphics.drawImage(scaledImage, tileX, tileY, null);
                    }
                }
            }
            result = ImageUtils.createBlurredImage(bImg);
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    public Image getBackgroundWallpaper() {
        return mBackgroundWallpaper;
    }

    @Override
    public void update(GameModel model, SystemContext systemContext) {

        mSpriteWidth = model.getGameState().getSpriteWidth();
        mSpriteHeight = model.getGameState().getSpriteHeight();

        createBackgroundImageWallpaper(model);

        systemContext.getAllTileEntityIDs().forEach(tileEntityID -> {
            Entity tileEntity = getEntityWithID(tileEntityID);
            updateTiles(model, tileEntity);

            updateShadows(model, tileEntity);
//            updateLiquidV2(model, tileEntity);

            updateStructures(model, tileEntity);
            updateLiquid(model, tileEntity);
//            updateTileAnimation(model, tileEntity);
        });
    }
}
