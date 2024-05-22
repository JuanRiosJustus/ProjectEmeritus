package main.game.stores.pools.asset;

import main.constants.Constants;
import main.constants.Settings;
import main.game.components.Animation;
import main.graphics.SpriteSheet;
import main.graphics.Sprite;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.*;

public class AssetPool {

    private static AssetPool instance = null;
    public static AssetPool getInstance() {
        if (instance == null) {
            instance = new AssetPool();
        }
        return instance;
    }

    private final SplittableRandom random = new SplittableRandom();
    private final Map<String, SpriteSheet> mSpriteSheetMap = new HashMap<>();
    private final Map<String, Asset> mAssetMap = new HashMap<>();
    private boolean hasUpdatedContents = false;
    public static final String FLICKER_ANIMATION = "flickering";
    public static final String SHEARING_ANIMATION = "shearing";
    public static final String SPINNING_ANIMATION = "spinning";
    public static final String STRETCH_Y_ANIMATION = "yStretch";
    public static final String STRETCH_ANIMATION = "stretch";
    public static final String STATIC_ANIMATION = "static";
    public static final String TILES_SPRITEMAP = Constants.TILES_SPRITEMAP_FILEPATH;
    public static final String UNITS_SPRITEMAP = Constants.UNITS_SPRITEMAP_FILEPATH;
    public static final String ABILITIES_SPRITEMAP = Constants.ABILITIES_SPRITEMAP_FILEPATH;
    public static final String MISC_SPRITEMAP = Constants.MISC_SPRITEMAP_FILEPATH;
    private static final int TILE_BASE_SIZE = Constants.BASE_SPRITE_SIZE;
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(AssetPool.class);

    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());

        mSpriteSheetMap.put(TILES_SPRITEMAP, new SpriteSheet(TILES_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));
        mSpriteSheetMap.put(UNITS_SPRITEMAP, new SpriteSheet(UNITS_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));
        mSpriteSheetMap.put(MISC_SPRITEMAP, new SpriteSheet(MISC_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));
        mSpriteSheetMap.put(ABILITIES_SPRITEMAP, new SpriteSheet(ABILITIES_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public boolean contains(String assetId) {
        return getAsset(assetId) != null;
    }

    public String getOrCreateAsset(String assetId, String sheet, String sprite, String animation) {
        Asset asset = getAsset(assetId);
        if (asset != null) { return assetId; }
        return createAsset(assetId, sheet, sprite, -1, animation);
    }

    public String getOrCreateAsset(String assetId, String sheet, String sprite, int column, String animation) {
        Asset asset = getAsset(assetId);
        if (asset != null) { return assetId; }
        return createAsset(assetId, sheet, sprite, column, animation);
    }

    public String createAsset(String spriteSheetName, String spriteSheetRowName, int column, String animation) {
        return createAsset(null, spriteSheetName, spriteSheetRowName, column, animation);
    }

    public String createAsset(String id, String sheetName, String spriteName, int startingFrame, String animation) {
        // Get the spriteMap and sprite sheet to use
        SpriteSheet spriteSheet = mSpriteSheetMap.get(sheetName);
        Sprite sprite = spriteSheet.get(spriteName);

        // If the given sheet not found, check if the given sheet name is using the simple sprite name
        if (sprite == null) {
            int index = spriteSheet.indexOf(spriteName);
            sprite = spriteSheet.get(index);
        }

        // Get a random frame from the sprite if the column is less than 0
        int columnInSheet = startingFrame >= 0 ? startingFrame : random.nextInt(sprite.getColumns(0));

        // Create new animation for non static assets. This was they will maintain their own frames.
        BufferedImage[] raw = null;
        int spriteWidth = Settings.getInstance().getSpriteWidth();
        int spriteHeight = Settings.getInstance().getSpriteHeight();
        // Each sprite sheet row is only a single sprite tall
        switch (animation) {
            case FLICKER_ANIMATION -> raw = createFlickeringAnimation(sprite, 0, columnInSheet, spriteWidth, spriteHeight);
            case SHEARING_ANIMATION -> raw = createShearingAnimation(sprite, 0, columnInSheet);
            case SPINNING_ANIMATION -> raw = createSpinningAnimation(sprite, 0, columnInSheet, spriteWidth, spriteHeight);
            case STRETCH_Y_ANIMATION -> raw = createStretchYAnimation(sprite, 0, columnInSheet, spriteWidth, spriteHeight);
            case STRETCH_ANIMATION -> raw = createStretchAnimation(sprite, 0, columnInSheet, spriteWidth, spriteHeight);
            case STATIC_ANIMATION -> raw = createStaticAnimation(sprite, 0, columnInSheet, spriteWidth, spriteHeight);
            default -> logger.error("Animation not supported");
        }
        if (raw == null) { return ""; }

        // Create animation for id
        if (id == null) {
            id = sheetName + "_" + spriteName + "_" + startingFrame + "_" + animation + "_" + mAssetMap.size();
        }

        Asset newAsset = new Asset(id, sheetName, spriteName, startingFrame, animation, new Animation(raw));
//        Asset asset = new AssetBuilder().setAnimation().
//        Asset newAsset = new Asset(spriteSheetRow.getName(), id, new Animation(raw), spriteSheet.indexOf(spriteSheetRowName));
//        Asset newAsset = new AssetBuilder().setId(id).setSpriteSheetName(spriteSheetName).setSpriteSheetRowName(spriteSheetRowName).setColumn(column).setAnimation(animation).setAnimation1(new Animation(raw)).createAsset();

        mAssetMap.put(id, newAsset);
        return id;
    }

//    public String createAsset(String spriteSheetName, String spriteSheetRowName, int column, String animation) {
//        // Get the spriteMap and sprite sheet to use
//        SpriteSheet spriteSheet = mSpriteSheetMap.get(spriteSheetName);
//        SpriteSheetRow spriteSheetRow = spriteSheet.get(spriteSheetRowName);
//
//        // If the given sheet not found, check if the given sheet name is using the shortened version
//        if (spriteSheetRow == null) {
//            int index = spriteSheet.indexOf(spriteSheetRowName);
//            spriteSheetRow = spriteSheet.get(index);
//        }
//
//        // Get a random column from the sheet if the column is less than 0
//        int columnInSheet = column >= 0 ? column : random.nextInt(spriteSheetRow.getColumns(0));
//
//        // Create new animation for non static assets. This was they will maintain their own frames.
//        BufferedImage[] raw = null;
//        int spriteWidth = Settings.getInstance().getSpriteWidth();
//        int spriteHeight = Settings.getInstance().getSpriteHeight();
//        // Each sprite sheet row is only a single sprite tall
//        switch (animation) {
//            case FLICKER_ANIMATION -> raw = createFlickeringAnimation(spriteSheetRow, 0, columnInSheet, spriteWidth, spriteHeight);
//            case SHEARING_ANIMATION -> raw = createShearingAnimation(spriteSheetRow, 0, columnInSheet);
//            case SPINNING_ANIMATION -> raw = createSpinningAnimation(spriteSheetRow, 0, columnInSheet, spriteWidth, spriteHeight);
//            case STRETCH_Y_ANIMATION -> raw = createStretchYAnimation(spriteSheetRow, 0, columnInSheet, spriteWidth, spriteHeight);
//            case STRETCH_ANIMATION -> raw = createStretchAnimation(spriteSheetRow, 0, columnInSheet, spriteWidth, spriteHeight);
//            case STATIC_ANIMATION -> raw = createStaticAnimation(spriteSheetRow, 0, columnInSheet, spriteWidth, spriteHeight);
////            case STATIC_ANIMATION -> {
////                int hash = Objects.hash(spriteSheetRowName, columnInSheet, animation);
////                raw = mStaticAnimationCache.get(hash);
////                if (raw == null) {
////                    raw = createStaticAnimation(spriteSheetRow, 0, columnInSheet);
////                    mStaticAnimationCache.put(hash, raw);
////                }
////            }
//            default -> logger.error("Animation not supported");
//        }
//        if (raw == null) { return ""; }
//
//        // Create animation for id
//        String id = spriteSheetName + "_" + spriteSheetRowName + "_" + column + "_" + animation + "_" + mAssetMap.size();
//        Asset newAsset = new Asset(spriteSheetRow.getName(), id, new Animation(raw), spriteSheet.indexOf(spriteSheetRowName));
//        mAssetMap.put(id, newAsset);
//        return id;
//    }

    private BufferedImage[] createSpinningAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                    int spriteWidth, int spriteHeight) {
        BufferedImage toCopy = sprite.getSprite(spriteRow, spriteColumn);
        toCopy = ImageUtils.getResizedImage(toCopy, spriteWidth, spriteHeight);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spinify(copy, .05f);
    }

    private BufferedImage[] createShearingAnimation(Sprite sheet, int row, int column) {
        return createShearingAnimation(sheet, row, column, Settings.getInstance().getSpriteSize() + 10);
    }

    private BufferedImage[] createShearingAnimation(Sprite sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createShearingAnimation(copy, 24, .05);
    }

    private BufferedImage[] createFlickeringAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                      int spriteWidth, int spriteHeight) {
        BufferedImage image = sprite.getSprite(spriteRow, spriteColumn);
        image = ImageUtils.getResizedImage(image, spriteWidth, spriteHeight);
        return ImageUtils.createFlickeringAnimation(image, 15, .02f);
    }

    private BufferedImage[] createStaticAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                  int spriteWidth, int spriteHeight) {
        BufferedImage image = sprite.getSprite(spriteRow, spriteColumn);
        return new BufferedImage[] { ImageUtils.getResizedImage(image, spriteWidth, spriteHeight) };
    }

    private BufferedImage[] createStretchYAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                    int spriteWidth, int spriteHeight) {
        BufferedImage toCopy = sprite.getSprite(spriteRow, spriteColumn);
        toCopy = ImageUtils.getResizedImage(toCopy, spriteWidth, spriteHeight);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaYStretch(copy, 12, spriteWidth * .01); // Fix animations TODO
//        return ImageUtils.createAnimationViaYStretch(copy, 12, 1); // Fix animations TODO
    }

    private BufferedImage[] createStretchAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                   int spriteWidth, int spriteHeight) {
        BufferedImage toCopy = sprite.getSprite(spriteRow, spriteColumn);
        toCopy = ImageUtils.getResizedImage(toCopy, spriteWidth, spriteHeight);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaStretch(copy, 12, 1);
    }
    public String getReticleId() {
        int width = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
        String retId = width + "_" + height + "_reticle";
        return getOrCreateAsset(retId, MISC_SPRITEMAP, "reticle", 0, STRETCH_ANIMATION);
    }
    public Animation getAnimation(String id) { return mAssetMap.getOrDefault(id, Asset.DEFAULT).getActualAnimation(); }
    public Asset getAsset(String id) { return mAssetMap.get(id); }
    public Animation getAbilityAnimation(String animationName) {
        return getAbilityAnimation(animationName, Settings.getInstance().getSpriteSize());
    }
    public Animation getAbilityAnimation(String animationName, int size) {
        Sprite sheet = mSpriteSheetMap.get(Constants.ABILITIES_SPRITEMAP_FILEPATH).get(animationName);
        if (sheet == null) { return null; }
        BufferedImage[] toCopy = sheet.getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], size, size);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new Animation(toCopy);
    }
    public SpriteSheet getSpriteMap(String name) {
        return mSpriteSheetMap.get(name);
    }
    public String createID(String... values){
        int spriteWidth = Settings.getInstance().getSpriteWidth();
        int spriteHeight = Settings.getInstance().getSpriteHeight();
        return String.valueOf(Objects.hash(Arrays.hashCode(values), spriteWidth, spriteHeight));
    }

    public boolean hasUpdate() { return hasUpdatedContents; }

    public String mergeAssets(List<String> ids) {
        String newId = createID(String.valueOf(ids));
        return mergeAssets(newId, ids);
    }

    public String mergeAssets(String assetId, List<String> ids) {
        if (ids == null || ids.isEmpty()) { return null; }
        List<BufferedImage> images = new ArrayList<>();
        List<Asset> assets = new ArrayList<>();
        for (String id : ids) {
            Asset asset = mAssetMap.get(id);
            if (asset == null) { continue; }
            images.add(asset.getActualAnimation().toImage());
            assets.add(asset);
        }
        BufferedImage newMergedAsset = ImageUtils.mergeImages(images);
        mAssetMap.put(assetId, new Asset(assetId, "merged", new Animation(newMergedAsset)));
        return assetId;
    }
}