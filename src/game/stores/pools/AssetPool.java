package game.stores.pools;

import constants.Constants;
import game.components.Animation;
import graphics.Spritemap;
import graphics.Spritesheet;
import logging.ELogger;
import logging.ELoggerFactory;
import utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;

public class AssetPool {

    private static AssetPool instance = null;
    public static AssetPool instance() { if (instance == null) { instance = new AssetPool(); } return instance; }
    private final SplittableRandom random = new SplittableRandom();

    private final Map<String, Spritemap> spritemaps = new HashMap<>();
    private final Map<Integer, Animation> animations = new HashMap<>();
    private final Map<Integer, BufferedImage[]> cache = new HashMap<>();

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());

        spritemaps.put(Constants.GEMS_SPRITESHEET_PATH, 
                new Spritemap(Constants.GEMS_SPRITESHEET_PATH, Constants.BASE_SPRITE_SIZE));
        
        spritemaps.put(Constants.SHADOWS_SPRITESHEET_FILEPATH, 
                new Spritemap(Constants.SHADOWS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.STRUCTURES_SPRITESHEET_FILEPATH, 
                new Spritemap(Constants.STRUCTURES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));
        
        spritemaps.put(Constants.LIQUIDS_SPRITESHEET_FILEPATH, 
                new Spritemap(Constants.LIQUIDS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.UNITS_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.UNITS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.ABILITIES_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.ABILITIES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.FLOORS_SPRITESHEET_FILEPATH, 
                new Spritemap(Constants.FLOORS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.WALLS_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.WALLS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    /**
     * 
     * FOR LIQUID AND STRUCTURE TILES 
     * 
     */
    public int createAnimatedAssetReference(String spritemap, int index, int column, String animation) {
        // Get spritemap requested (i.e. floors, walls, abilities, etc)
        Spritemap map = spritemaps.get(spritemap);
        
        // Get spritetype a.k.a. get sheet by index
        Spritesheet sheet = map.getSpritesheetByIndex(index);
                
        // Get a random column from the sheet        
        column = column == -1 ? random.nextInt(sheet.getColumns()) : column;

        // Create a key to lessen duplicates
        int hash = Objects.hash(spritemap, index, column);
        int id = animations.size();

        // Create the animation fo the first time.
        BufferedImage[] raw = cache.get(hash);
        if (raw == null) {
            switch (animation.toLowerCase()) {
                case "flickering" -> raw = createFlickeringAnimation(sheet, column);
                case "shearing" -> raw = createShearingAnimation(sheet, column);
                case "spinning" -> raw = createSpinningAnimation(sheet, column);
            }
            cache.put(id, raw);
        }
        // Create animation for id
        animations.put(id, new Animation(raw));
        return id;
    }

    public int createAnimatedAssetReference(String spritemap, int index, String animation) {
        return createAnimatedAssetReference(spritemap, index, -1, animation);
    }


    private BufferedImage[] createSpinningAnimation(Spritesheet sheet, int index) {
        return createSpinningAnimation(sheet, index, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createSpinningAnimation(Spritesheet sheet, int index, int size) {
        BufferedImage toCopy = sheet.getSprite(0, index);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spinify(copy, .05f);
    }

    private BufferedImage[] createShearingAnimation(Spritesheet sheet, int index) {
        return createShearingAnimation(sheet, index, Constants.CURRENT_SPRITE_SIZE + 10);
    }

    private BufferedImage[] createShearingAnimation(Spritesheet sheet, int index, int size) {
        BufferedImage toCopy = sheet.getSprite(0, index);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createShearingAnimation(copy, 24, .05);
    }

    private BufferedImage[] createFlickeringAnimation(Spritesheet sheet, int index) {
        return createFlickeringAnimation(sheet, index, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createFlickeringAnimation(Spritesheet sheet, int index, int size) {
        Spritesheet selected = sheet;
        BufferedImage image = selected.getSprite(0, index);
        image = ImageUtils.getResizedImage(image, size, size);
        return ImageUtils.createFlickeringAnimation(image, 15, .02f);
    }

    /**
     * 
     * FOR FLOOR AND WALL TILES
     * 
     */
    public int createStaticAssetReference(String spritemap, int index) {
        // Get spritemap requested (i.e. floors, walls, abilities, etc)
        Spritemap map = spritemaps.get(spritemap);
        // Get spritetype a.k.a. get sheet by index
        Spritesheet sheet = map.getSpritesheetByIndex(index);

        if (sheet == null || sheet.getRows() == 0) {
            System.currentTimeMillis();
            logger.error("Spritesheet unable to load from {}", spritemap);
            System.exit(0);
        }

        // Get a random column from the sheet
        int column = random.nextInt(sheet.getColumns());

        // Create a key to lessen duplicates
        int hash = Objects.hash(spritemap, index, column);
        int id = animations.size();

        // Create the animation fo the first time.
        BufferedImage[] raw = cache.get(hash);
        
        // This asset has already been made        
        if (raw == null) {
            // Asset needs to be made        
            BufferedImage image = createStaticAssetImage(sheet, column);
            raw = new BufferedImage[]{ image };
        }
        animations.put(id, new Animation(raw));        

        return id;
    }

    private BufferedImage createStaticAssetImage(Spritesheet sheet, int column) {
        return createStaticAssetImage(sheet, column, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage createStaticAssetImage(Spritesheet sheet, int column, int size) {
        BufferedImage image = sheet.getSprite(0, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public Animation getAnimation(int id) { return animations.get(id); }

    public int getUnitAnimation(String name) {
        BufferedImage[] frames = getUnitAnimation(name, Constants.CURRENT_SPRITE_SIZE);
        int id = animations.size();
        animations.put(id, new Animation(frames));
        return id;
    }

    private BufferedImage[] getUnitAnimation(String name, int size) {
        BufferedImage toCopy = spritemaps.get(Constants.UNITS_SPRITESHEET_FILEPATH)
                .getSpritesheetByName(name)
                .getSprite(0, 0);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaYStretch(copy, 12, 1);
    }

    public Animation getAbilityAnimation(String animationName) {
        return getAbilityAnimation(animationName, Constants.CURRENT_SPRITE_SIZE);
    }

    public Animation getAbilityAnimation(String animationName, int size) {
        Spritesheet sheet = spritemaps.get(Constants.ABILITIES_SPRITESHEET_FILEPATH).getSpritesheetByName(animationName);
        if (sheet == null) { return null; }
        BufferedImage[] toCopy = sheet.getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], size, size);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new Animation(toCopy);
    }

    public Spritemap getSpriteMap(String name) {
        return spritemaps.get(name);
    }

    public BufferedImage getImage(String spritemap, int index, int row, int column) {
        return getImage(spritemap, index, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage getImage(String spritemap, int index, int row, int column, int size) {
        // Get spritemap requested (i.e. floors, walls, abilities, etc)
        Spritemap map = spritemaps.get(spritemap);
        // Get spritetype a.k.a. get sheet by index
        Spritesheet sheet = map.getSpritesheetByIndex(index);
        // Get image from row and column (WARNING: SOME SHEETS HAVE ONLY 1 ROW);
        BufferedImage image = sheet.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public void updateAnimations() {
        for (Animation animation : animations.values()) { animation.update(); }
    }
}