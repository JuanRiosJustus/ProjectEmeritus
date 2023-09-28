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
    private final Map<Integer, Asset> mAssets = new HashMap<>();
    private final Map<Integer, BufferedImage[]> cache = new HashMap<>();
    public int reticleId = -1;
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

    public int createAsset(int row, String animation) {
        return createAsset(TILES_SPRITEMAP, row, -1, animation);
    }

    public int createAsset(String map, String sheet, int column, String animation) {
        SpriteMap sheetMap = mSpriteMaps.get(map);
        int index = sheetMap.indexOf(sheet);
        if (index < 0) { return -1; }
        return createAsset(map, index, column, animation);
    }

    public int createAsset(String map, int sheet, int column, String animation) {
        SpriteMap spriteMap = mSpriteMaps.get(map);
        SpriteSheet spriteSheet = spriteMap.get(sheet);

        // Get a random column from the sheet if the column is less than 0
        int columnInSheet = column >= 0 ? column : random.nextInt(spriteSheet.getColumns(0));

        // Create a key to lessen duplicates
        int hash = Objects.hash(sheet, columnInSheet, animation);
        BufferedImage[] raw = cache.get(hash);

        // Create the animation fo the first time.
        if (raw == null) {
            switch (animation) {
                case FLICKER_ANIMATION -> raw = createFlickeringAnimation(spriteSheet, 0, columnInSheet);
                case SHEARING_ANIMATION -> raw = createShearingAnimation(spriteSheet, 0, columnInSheet);
                case SPINNING_ANIMATION -> raw = createSpinningAnimation(spriteSheet, 0, columnInSheet);
                case STATIC_ANIMATION -> raw = createStaticAnimation(spriteSheet, 0, columnInSheet);
                case STRETCH_Y_ANIMATION -> raw = createStretchYAnimation(spriteSheet, 0, columnInSheet);
                case STRETCH_ANIMATION -> raw = createStretchAnimation(spriteSheet, 0, columnInSheet);
                default -> logger.error("Animation not supported");
            }
            if (raw == null) { return -1; }
        }

        // Create animation for id
        int id = mAssets.size();
        cache.put(id, raw);
        mAssets.put(id, new Asset(spriteSheet.getName(), id, new Animation(raw)));
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

    public Animation getAssetAnimation(int id) { return mAssets.get(id).getAnimation(); }
    public Asset getAsset(int id) { return mAssets.get(id); }

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

    public String getAssetName(String map, int sheet) {
        SpriteMap spriteMap = mSpriteMaps.get(map);
        SpriteSheet spriteSheet = spriteMap.get(sheet);
        return spriteSheet.getName();
    }

    public String getAssetName(String map, String sheet) {
        SpriteMap spriteMap = mSpriteMaps.get(map);
        SpriteSheet spriteSheet = spriteMap.get(sheet);
        return spriteSheet.getName();
    }

//    public BufferedImage getImage(String spritesheet, int index, int row, int column) {
//        return getImage(spritesheet, index, row, column, Constants.CURRENT_SPRITE_SIZE);
//    }
//
//    public BufferedImage getImage(String spritesheet, int index, int row, int column, int size) {
//        // Get spritetype a.k.a. get sheet by index
//        SpriteSheet sheet = spriteSheet.get(spritesheet);
//        // Get image from row and column (WARNING: SOME SHEETS HAVE ONLY 1 ROW);
//        BufferedImage image = sheet.getSprite(row, column);
//        return ImageUtils.getResizedImage(image, size, size);
//    }

//    public void updateAnimations() {
//        for (Animation animation : assets.values()) { animation.update(); }
//    }
}