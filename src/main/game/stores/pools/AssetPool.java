package main.game.stores.pools;

import main.constants.Constants;
import main.constants.Settings;
import main.game.components.Animation;
import main.graphics.SpriteSheetMap;
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
            instance.currentSpriteSize = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
        }
        return instance;
    }
    private final SplittableRandom random = new SplittableRandom();
    private final Map<String, SpriteSheetMap> rawSpriteMap = new HashMap<>();
    private final Map<Integer, Animation> assets = new HashMap<>();
    private final Map<Integer, BufferedImage[]> cache = new HashMap<>();
    private final Map<String, SpriteSheet> spriteSheet = new HashMap<>();
    private int currentSpriteSize = -9;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());

        spriteSheet.put(Constants.GEMS_SPRITESHEET_PATH,
            new SpriteSheet(Constants.GEMS_SPRITESHEET_PATH, Constants.BASE_SPRITE_SIZE));
                
        spriteSheet.put(Constants.SHADOWS_SPRITESHEET_FILEPATH,
            new SpriteSheet(Constants.SHADOWS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        rawSpriteMap.put(Constants.TILES_SPRITESHEET_FILEPATH,
                new SpriteSheetMap(Constants.TILES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        rawSpriteMap.put(Constants.UNITS_SPRITESHEET_FILEPATH,
                new SpriteSheetMap(Constants.UNITS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        rawSpriteMap.put(Constants.ABILITIES_SPRITESHEET_FILEPATH,
                new SpriteSheetMap(Constants.ABILITIES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public int createAsset(int id, String animation) {
        return createAsset(Constants.TILES_SPRITESHEET_FILEPATH, id, animation);
    }

    public int createAsset(String assetSheet, int row, String animationType) {
        // Get spritetype a.k.a. get sheet by index
        SpriteSheetMap map = rawSpriteMap.get(assetSheet);
        SpriteSheet sheet = map.get(row);

        // Get a random column from the sheet. NOTE: these sheers only have a row
        int column = random.nextInt(sheet.getColumns(0));

        // Create a key to lessen duplicates
        int hash = Objects.hash(assetSheet, 0, column);
        int id = assets.size();

        // Create the animation fo the first time.
        BufferedImage[] raw = cache.get(hash);
        if (raw == null) {
            switch (animationType) {
                case "flickering" -> raw = createFlickeringAnimation(sheet, 0, column);
                case "shearing" -> raw = createShearingAnimation(sheet, 0, column);
                case "spinning" -> raw = createSpinningAnimation(sheet, 0, column);
                default -> raw = createStaticAssetImage(sheet, 0, column);
            }
        }
        cache.put(id, raw);
        // Create animation for id
        assets.put(id, new Animation(raw));
        return id;
    }

    private BufferedImage[] createSpinningAnimation(SpriteSheet sheet, int row, int column) {
        return createSpinningAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createSpinningAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spinify(copy, .05f);
    }

    private BufferedImage[] createShearingAnimation(SpriteSheet sheet, int row, int column) {
        return createShearingAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE + 10);
    }

    private BufferedImage[] createShearingAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createShearingAnimation(copy, 24, .05);
    }

    private BufferedImage[] createFlickeringAnimation(SpriteSheet sheet, int row, int column) {
        return createFlickeringAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createFlickeringAnimation(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage image = sheet.getSprite(row, column);
        image = ImageUtils.getResizedImage(image, size, size);
        return ImageUtils.createFlickeringAnimation(image, 15, .02f);
    }

    private BufferedImage[] createStaticAssetImage(SpriteSheet sheet, int row, int column) {
        return createStaticAssetImage(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }
    private BufferedImage[] createStaticAssetImage(SpriteSheet sheet, int row, int column, int size) {
        BufferedImage image = sheet.getSprite(row, column);
        return new BufferedImage[] { ImageUtils.getResizedImage(image, size, size) };
    }

    public Animation getAsset(int id) { return assets.get(id); }

    public int getUnitAnimation(String name) {
        BufferedImage[] frames = getUnitAnimation(name, Constants.CURRENT_SPRITE_SIZE);
        int id = assets.size();
        assets.put(id, new Animation(frames));
        return id;
    }

    private BufferedImage[] getUnitAnimation(String name, int size) {
        BufferedImage toCopy = rawSpriteMap.get(Constants.UNITS_SPRITESHEET_FILEPATH)
                .get(name)
                .getSprite(0, 0);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaYStretch(copy, 12, 1);
    }

    public Animation getAbilityAnimation(String animationName) {
        return getAbilityAnimation(animationName, Constants.CURRENT_SPRITE_SIZE);
    }

    public Animation getAbilityAnimation(String animationName, int size) {
        SpriteSheet sheet = rawSpriteMap.get(Constants.ABILITIES_SPRITESHEET_FILEPATH).get(animationName);
        if (sheet == null) { return null; }
        BufferedImage[] toCopy = sheet.getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], size, size);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new Animation(toCopy);
    }

    public SpriteSheetMap getSpriteMap(String name) {
        return rawSpriteMap.get(name);
    }

    public BufferedImage getImage(String spritesheet, int index, int row, int column) {
        return getImage(spritesheet, index, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage getImage(String spritesheet, int index, int row, int column, int size) {
        // Get spritetype a.k.a. get sheet by index
        SpriteSheet sheet = spriteSheet.get(spritesheet);
        // Get image from row and column (WARNING: SOME SHEETS HAVE ONLY 1 ROW);
        BufferedImage image = sheet.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public void updateAnimations() {
        for (Animation animation : assets.values()) { animation.update(); }
    }
}