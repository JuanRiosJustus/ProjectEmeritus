package main.game.stores.pools;

import main.constants.Constants;
import main.constants.Settings;
import main.game.components.Animation;
import main.graphics.SpriteMap;
import main.graphics.SpriteSheet;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;

public class AssetPool {

    private static AssetPool instance = null;
    public static AssetPool getInstance() {
        if (instance == null) {
            instance = new AssetPool();
            instance.reticleId = AssetPool.getInstance().createAsset(
                    MISC_SPRITEMAP, "reticle", 0, STRETCH_ANIMATION);
        }
        return instance;
    }

    private final SplittableRandom random = new SplittableRandom();
    private final Map<String, SpriteMap> mSpriteMaps = new HashMap<>();
    private final Map<String, Asset> mAssetMap = new HashMap<>();
    private final Map<Integer, BufferedImage[]> mStaticAnimationCache = new HashMap<>();
    public String reticleId = "";
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

        mSpriteMaps.put(TILES_SPRITEMAP, new SpriteMap(TILES_SPRITEMAP, TILE_BASE_SIZE));
        mSpriteMaps.put(UNITS_SPRITEMAP, new SpriteMap(UNITS_SPRITEMAP, TILE_BASE_SIZE));
        mSpriteMaps.put(MISC_SPRITEMAP, new SpriteMap(MISC_SPRITEMAP, TILE_BASE_SIZE));
        mSpriteMaps.put(ABILITIES_SPRITEMAP, new SpriteMap(ABILITIES_SPRITEMAP, TILE_BASE_SIZE));

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public String createAsset(String name, String animation) {
        return createAsset(TILES_SPRITEMAP, name, -1, animation);
    }

    public String createAsset(String map, int sheet, int column, String animation) {
        SpriteMap sheetMap = mSpriteMaps.get(map);
        SpriteSheet spriteSheet = sheetMap.get(sheet);
        if (spriteSheet == null) { return ""; }
        return createAsset(map, spriteSheet.getName(), column, animation);
    }

//    public int createAsset(String map, String sheet, int column, String animation) {
//        SpriteMap spriteMap = mSpriteMaps.get(map);
//        SpriteSheet spriteSheet = spriteMap.get(sheet);
//
//        if (spriteSheet == null) {
//            int index = spriteMap.indexOf(sheet);
//            spriteSheet = spriteMap.get(index);
//        }
//
//        // Get a random column from the sheet if the column is less than 0
//        int columnInSheet = column >= 0 ? column : random.nextInt(spriteSheet.getColumns(0));
//
//        // Create a key to lessen duplicates
//        int hash = Objects.hash(sheet, columnInSheet, animation);
//        BufferedImage[] raw = mCache.get(hash);
//
//        // Create the animation fo the first time.
//        if (raw == null) {
//            switch (animation) {
//                case FLICKER_ANIMATION -> raw = createFlickeringAnimation(spriteSheet, 0, columnInSheet);
//                case SHEARING_ANIMATION -> raw = createShearingAnimation(spriteSheet, 0, columnInSheet);
//                case SPINNING_ANIMATION -> raw = createSpinningAnimation(spriteSheet, 0, columnInSheet);
//                case STATIC_ANIMATION -> raw = createStaticAnimation(spriteSheet, 0, columnInSheet);
//                case STRETCH_Y_ANIMATION -> raw = createStretchYAnimation(spriteSheet, 0, columnInSheet);
//                case STRETCH_ANIMATION -> raw = createStretchAnimation(spriteSheet, 0, columnInSheet);
//                default -> logger.error("Animation not supported");
//            }
//            if (raw == null) { return -1; }
//        }
//
//        // Create animation for id
//        int id = mAssets.size();
//        mCache.put(id, raw);
//        mAssets.put(id, new Asset(spriteSheet.getName(), id, new Animation(raw)));
//        return id;
//    }


    public String createAsset(String map, String sheet, int column, String animation) {
        // Get the spriteMap and sprite sheet to use
        SpriteMap spriteMap = mSpriteMaps.get(map);
        SpriteSheet spriteSheet = spriteMap.get(sheet);

        // If the given sheet not found, check if the given sheet name is using the shortened version
        if (spriteSheet == null) {
            int index = spriteMap.indexOf(sheet);
            spriteSheet = spriteMap.get(index);
        }

        // Get a random column from the sheet if the column is less than 0
        int columnInSheet = column >= 0 ? column : random.nextInt(spriteSheet.getColumns(0));

        // Create new animation for non static assets. This was they will maintain their own frames.
        BufferedImage[] raw = null;
        switch (animation) {
            case FLICKER_ANIMATION -> raw = createFlickeringAnimation(spriteSheet, 0, columnInSheet);
            case SHEARING_ANIMATION -> raw = createShearingAnimation(spriteSheet, 0, columnInSheet);
            case SPINNING_ANIMATION -> raw = createSpinningAnimation(spriteSheet, 0, columnInSheet);
            case STRETCH_Y_ANIMATION -> raw = createStretchYAnimation(spriteSheet, 0, columnInSheet);
            case STRETCH_ANIMATION -> raw = createStretchAnimation(spriteSheet, 0, columnInSheet);
            case STATIC_ANIMATION -> {
                int hash = Objects.hash(sheet, columnInSheet, animation);
                raw = mStaticAnimationCache.get(hash);
                if (raw == null) {
                    raw = createStaticAnimation(spriteSheet, 0, columnInSheet);
                    mStaticAnimationCache.put(hash, raw);
                }
            }
            default -> logger.error("Animation not supported");
        }
        if (raw == null) { return ""; }

        // Create animation for id
        String id = map + "_" + sheet + "_" + column + "_" + animation + "_" + mAssetMap.size();
        mAssetMap.put(id, new Asset(spriteSheet.getName(), id, new Animation(raw)));
        return id;
    }

    private BufferedImage[] createSpinningAnimation(SpriteSheet sheet, int row, int column) {
        return createSpinningAnimation(sheet, row, column, Settings.getInstance().getSpriteSize());
    }

    private BufferedImage[] createSpinningAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spinify(copy, .05f);
    }

    private BufferedImage[] createShearingAnimation(SpriteSheet sheet, int row, int column) {
        return createShearingAnimation(sheet, row, column, Settings.getInstance().getSpriteSize() + 10);
    }

    private BufferedImage[] createShearingAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createShearingAnimation(copy, 24, .05);
    }

    private BufferedImage[] createFlickeringAnimation(SpriteSheet sheet, int row, int column) {
        return createFlickeringAnimation(sheet, row, column, Settings.getInstance().getSpriteSize());
    }

    private BufferedImage[] createFlickeringAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage image = sheet.getSprite(row, column);
        image = ImageUtils.getResizedImage(image, size, size);
        return ImageUtils.createFlickeringAnimation(image, 15, .02f);
    }

    private BufferedImage[] createStaticAnimation(SpriteSheet sheet, int row, int column) {
        return createStaticAnimation(sheet, row, column, Settings.getInstance().getSpriteSize());
    }
    private BufferedImage[] createStaticAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage image = sheet.getSprite(row, column);
        return new BufferedImage[] { ImageUtils.getResizedImage(image, size, size) };
    }
    private BufferedImage[] createStretchYAnimation(SpriteSheet sheet, int row, int column) {
        return createStretchYAnimation(sheet, row, column, Settings.getInstance().getSpriteSize());
    }
    private BufferedImage[] createStretchYAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaYStretch(copy, 12, 1);
    }

    private BufferedImage[] createStretchAnimation(SpriteSheet sheet, int row, int column) {
        return createStretchAnimation(sheet, row, column, Settings.getInstance().getSpriteSize());
    }
    private BufferedImage[] createStretchAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaStretch(copy, 12, 1);
    }
    public Animation getAnimation(String id) { return mAssetMap.getOrDefault(id, Asset.DEFAULT).getAnimation(); }
    public Asset getAsset(String id) { return mAssetMap.get(id); }
    public Animation getAbilityAnimation(String animationName) {
        return getAbilityAnimation(animationName, Settings.getInstance().getSpriteSize());
    }
    public Animation getAbilityAnimation(String animationName, int size) {
        SpriteSheet sheet = mSpriteMaps.get(Constants.ABILITIES_SPRITEMAP_FILEPATH).get(animationName);
        if (sheet == null) { return null; }
        BufferedImage[] toCopy = sheet.getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], size, size);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new Animation(toCopy);
    }
    public SpriteMap getSpriteMap(String name) {
        return mSpriteMaps.get(name);
    }
}