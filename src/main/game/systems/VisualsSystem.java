package main.game.systems;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import main.constants.Direction;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.SecondTimer;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
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
    private List<String> mEphemeralList = new ArrayList<>();
    private final EmeritusLogger mLogger = EmeritusLogger.create(VisualsSystem.class);
//    @Override
//    public void update(GameModel model, Entity tileEntity) {
//
//        mSpriteWidth = model.getGameState().getSpriteWidth();
//        mSpriteHeight = model.getGameState().getSpriteHeight();
//
//        updateTiles(model, tileEntity);
//
//        updateStructures(model, tileEntity);
//        updateLiquid(model, tileEntity);
//        updateTileAnimation(model, tileEntity);
//    }

    public void createBackgroundImageWallpaper(GameModel model) {
        // if there is no background image, create one on a new thread
        if (mBackgroundWallpaper != null) { return; }
        if (mStartedBackgroundWallpaperWork) { return; }
        mStartedBackgroundWallpaperWork = true;
        Thread temporaryThread = new Thread(() -> {
            SecondTimer st = new SecondTimer();
            mLogger.info("Started creating blurred background");
            mBackgroundWallpaper = SwingFXUtils.toFXImage(createBlurredBackground(model), null);
//            mBackgroundWallpaper = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB); //createBlurredBackground(model);
            mLogger.info("Finished creating blurred background after {} seconds", st.elapsed());
        });
        temporaryThread.start();
    }


    public void updateLiquid(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        if (!tile.isTopLayerLiquid()) { return; }

        String liquid = tile.getTopLayerSprite();
        IdentityComponent identityComponent = tileEntity.get(IdentityComponent.class);

        String id = AssetPool.getInstance().getOrCreateFlickerAsset(
                mSpriteWidth,
                mSpriteHeight,
                liquid,
                -1,
                identityComponent.getID() + mSpriteWidth + mSpriteHeight + liquid + tile.getRow() + tile.getColumn()
        );

        assetComponent.putMainID(id);
        Asset asset = AssetPool.getInstance().getAsset(id);
        asset.getAnimation().update();
    }

    public void updateTiles(GameModel gameModel, Entity tileEntity) {

        String baseTileSpriteID = getOrCreateSprite(gameModel, tileEntity);
        String depthShadowID = getOrCreateDepthShadows(gameModel, tileEntity);
        String directionalShadowsID = getOrCreateDirectionalShadows(gameModel, tileEntity);

        mEphemeralList.clear();
        mEphemeralList.add(baseTileSpriteID);
        mEphemeralList.add(depthShadowID);
        mEphemeralList.add(directionalShadowsID);

        String finalizedMergedTileAssetID = AssetPool.getInstance().getOrCreateMergedAssets(
                mSpriteWidth,
                mSpriteHeight,
                mEphemeralList,
                String.valueOf(mEphemeralList.hashCode())
        );

        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        assetComponent.putMainID(finalizedMergedTileAssetID);
    }

    private String getOrCreateDirectionalShadows(GameModel gameModel, Entity tileEntity) {
        Tile currentTile = tileEntity.get(Tile.class);
        if (currentTile.isWall()) { return ""; }
        int currentHeight = currentTile.getHeight();

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
            Tile adjacentTile = adjacentEntity.get(Tile.class);
            int adjacentHeight = adjacentTile.getHeight();

            // Only if the adjacent tile is higher, add a shadow from that direction
            if (adjacentTile.isTopLayerSolid() && adjacentHeight <= currentHeight) {
                continue;
            } else if (currentTile.isTopLayerLiquid()) {
                continue;
            }

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
        Tile tile = tileEntity.get(Tile.class);
        String baseTileSprite = tile.getTopLayerSprite();
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
        Tile tile = tileEntity.get(Tile.class);
        int tileHeight = tile.getHeight();
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
        Tile tile = tileEntity.get(Tile.class);
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);

        String sprite = tile.getTopLayerSprite();
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

    public void updateStructures(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
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

        Asset asset = AssetPool.getInstance().getAsset(id);
        asset.getAnimation().update();
    }


    private void updateTileAnimation(GameModel model, Entity entity) {
        // Only update liquid animation if possible
        Tile tile = entity.get(Tile.class);
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);

        String id = AssetPool.getInstance().getOrCreateStaticAsset(
                mSpriteWidth,
                mSpriteHeight,
                tile.getTopLayerSprite(),
                -1,
                identityComponent.getID() + mSpriteWidth + mSpriteHeight
        );

//        assetComponent.put(AssetComponent.TERRAIN_ASSET, id);
        Asset asset = AssetPool.getInstance().getAsset(id);

//        if (animation == null) { return; }
//        animation.update();
    }


    private BufferedImage createBlurredBackground(GameModel model) {
        // Create background after first iteration where the image is guaranteed to have something

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
                Tile tile = tileEntity.get(Tile.class);
                int tileX = tile.getColumn() * tileWidth;
                int tileY = tile.getRow() * tileHeight;

                BufferedImage scaledImage = null;
                // Draw the base of the tile
                if (tile.getTopLayerSprite() != null) {
                    String id = assetComponent.getMainID();
                    Animation animation = AssetPool.getInstance().getAnimation(id);
                    scaledImage = ImageUtils.getResizedImage(animation.toImage(), tileWidth, tileHeight);
                    backgroundGraphics.drawImage(scaledImage, tileX, tileY, null);
                }
            }
        }

        return ImageUtils.createBlurredImage(bImg);
    }

    private Image createBlurredBackgroundV2(GameModel model) {
        // Create background after first iteration where the image is guaranteed to have something

        int backgroundImageWidth = model.getGameState().getMainCameraWidth();
        int backgroundImageHeight = model.getGameState().getMainCameraHeight();
        Image image = new WritableImage(backgroundImageWidth, backgroundImageHeight);

//        GraphicsContext graphicsContext = image.getPixelReader();

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
                if (tile.getTopLayerSprite() != null) {
                    String id = assetComponent.getMainID();
                    Animation animation = AssetPool.getInstance().getAnimation(id);
                    scaledImage = ImageUtils.getResizedImage(animation.toImage(), tileWidth, tileHeight);
//                    backgroundGraphics.drawImage(scaledImage, tileX, tileY, null);
                }
            }
        }

//        return ImageUtils.createBlurredImage(bImg);
        return null;
    }

    public Image getBackgroundWallpaper() {
        return mBackgroundWallpaper;
    }

    @Override
    public void update(GameModel model, String id) {

        mSpriteWidth = model.getGameState().getSpriteWidth();
        mSpriteHeight = model.getGameState().getSpriteHeight();

        Entity tileEntity = getEntityWithID(id);
        updateTiles(model, tileEntity);

        updateStructures(model, tileEntity);
        updateLiquid(model, tileEntity);
        updateTileAnimation(model, tileEntity);
    }
}
