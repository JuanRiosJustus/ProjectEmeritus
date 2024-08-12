package main.game.stores.pools.asset;

import main.constants.Constants;
import main.constants.Settings;
import main.game.components.Animation;
import main.game.main.GameModel;
import main.graphics.SpriteSheetV2;
import main.graphics.SpriteSheet;
import main.graphics.Sprite;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.*;

public class AssetPool {

    public static final String WALL_TILES = "wall_tiles";
    public static final String FLOOR_TILES = "floor_tiles";
    public static final String LIQUIDS_TILES = "liquids";
    private final SplittableRandom random = new SplittableRandom();
    private final Map<String, SpriteSheet> mSpriteSheetMap = new HashMap<>();
    private final Map<String, Asset> mAssetMap = new HashMap<>();
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
    private static SpriteSheetV2 mSpriteSheetV2;
    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());

//        mSpriteSheetMap.put(TILES_SPRITEMAP, new SpriteSheet(TILES_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));
//        mSpriteSheetMap.put(UNITS_SPRITEMAP, new SpriteSheet(UNITS_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));
//        mSpriteSheetMap.put(MISC_SPRITEMAP, new SpriteSheet(MISC_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));
//        mSpriteSheetMap.put(ABILITIES_SPRITEMAP, new SpriteSheet(ABILITIES_SPRITEMAP, TILE_BASE_SIZE, TILE_BASE_SIZE));

        mSpriteSheetV2 = new SpriteSheetV2("./res/graphics", TILE_BASE_SIZE, TILE_BASE_SIZE);

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }
    private static AssetPool instance = null;
    public static AssetPool getInstance() {
        if (instance == null) {
            instance = new AssetPool();
        }
        return instance;
    }

    public boolean contains(String assetId) {
        return getAsset(assetId) != null;
    }

    public String getOrCreateAsset(GameModel model, String name, String effect, String id) {
        // Get the spriteMap and sprite sheet to use
        Sprite sprite = mSpriteSheetV2.getSprite(name);
        // Get a random frame from the sprite
        int randomFrame = random.nextInt(sprite.getColumns(0));
        return getOrCreateAsset(model, name, effect, randomFrame, id);
    }

    public String getOrCreateAsset(GameModel model, String name, String effect, int frame, String id) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        return getOrCreateAsset(spriteWidth, spriteHeight, name, effect, frame, id);
    }

    public String getOrCreateAsset(int width, int height, String name, String effect, int frame, String id) {
        // if this id exists, animation for this already exists
        Asset newAsset = mAssetMap.get(id);
        if (newAsset != null) { return id; }
        // Get the spriteMap and sprite sheet to use
        Sprite sprite = mSpriteSheetV2.getSprite(name);
        if (sprite == null) {
            return null;
        }
        // Create new animation for non-static assets. This was they will maintain their own frames.
        BufferedImage[] raw = null;
        // Each sprite sheet row is only a single sprite tall
        switch (effect) {
            case FLICKER_ANIMATION -> raw = createFlickeringAnimation(sprite, 0, frame, width, height);
            case SHEARING_ANIMATION -> raw = createShearingAnimation(sprite, 0, frame, width, height);
            case SPINNING_ANIMATION -> raw = createSpinningAnimation(sprite, 0, frame, width, height);
            case STRETCH_Y_ANIMATION -> raw = createStretchYAnimation(sprite, 0, frame, width, height);
            case STRETCH_ANIMATION -> raw = createStretchAnimation(sprite, 0, frame, width, height);
            case STATIC_ANIMATION -> raw = createStaticAnimation(sprite, 0, frame, width, height);
            default -> logger.error("Animation not supported");
        }
        if (raw == null) { return ""; }
        // Create new asset with the provided criteria
        newAsset = new Asset(id, effect, new Animation(raw));
        mAssetMap.put(id, newAsset);
        return id;
    }


    private BufferedImage[] createSpinningAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                    int spriteWidth, int spriteHeight) {
        BufferedImage toCopy = sprite.getSprite(spriteRow, spriteColumn);
        toCopy = ImageUtils.getResizedImage(toCopy, spriteWidth, spriteHeight);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spinify(copy, .05f);
    }

    private BufferedImage[] createShearingAnimation(Sprite sheet, int row, int column, int width, int height) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, width, height);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createShearingAnimation(copy, 24, .04);
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
    }

    private BufferedImage[] createStretchAnimation(Sprite sprite, int spriteRow, int spriteColumn,
                                                   int spriteWidth, int spriteHeight) {
        BufferedImage toCopy = sprite.getSprite(spriteRow, spriteColumn);
        toCopy = ImageUtils.getResizedImage(toCopy, spriteWidth, spriteHeight);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaStretch(copy, 12, 1);
    }

    public String getBlueReticleId(GameModel model) {
        int width = model.getSettings().getSpriteWidth();
        int height = model.getSettings().getSpriteHeight();

        String id = getOrCreateAsset(
                model,
                "blue_reticle",
                STRETCH_ANIMATION,
                "blue_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getRedReticleId(GameModel model) {
        int width = model.getSettings().getSpriteWidth();
        int height = model.getSettings().getSpriteHeight();

        String id = getOrCreateAsset(
                model,
                "red_reticle",
                STRETCH_ANIMATION,
                "red_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getYellowReticleId(GameModel model) {
        int width = model.getSettings().getSpriteWidth();
        int height = model.getSettings().getSpriteHeight();

        String id = getOrCreateAsset(
                model,
                "yellow_reticle",
                STRETCH_ANIMATION,
                "yellow_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }


    public Animation getAnimation(String id) {
        Asset asset = mAssetMap.get(id);
        if (asset == null) {
            return null;
        }
        return asset.getAnimation();
    }
    public String getAnimationType(String id) { return mAssetMap.get(id).getAnimationType(); }
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

    public List<String> getBucket(String bucket) {
        return mSpriteSheetV2.getBucket(bucket);
    }
    public String getOrCreateID(Object... values) {
        int spriteWidth = Settings.getInstance().getSpriteWidth();
        int spriteHeight = Settings.getInstance().getSpriteHeight();
        return String.valueOf(Objects.hash(Arrays.hashCode(values), spriteWidth, spriteHeight));
    }

//    public String mergeAssets(String assetId, List<String> ids) {
//        if (ids == null || ids.isEmpty()) { return null; }
//        List<BufferedImage> images = new ArrayList<>();
//        List<Asset> assets = new ArrayList<>();
//        for (String id : ids) {
//            Asset asset = mAssetMap.get(id);
//            if (asset == null) { continue; }
//            images.add(asset.getAnimation().toImage());
//            assets.add(asset);
//        }
//        BufferedImage newMergedAsset = ImageUtils.mergeImages(images);
//        mAssetMap.put(assetId, new Asset(assetId, "merged", new Animation(newMergedAsset)));
//        return assetId;
//    }
}