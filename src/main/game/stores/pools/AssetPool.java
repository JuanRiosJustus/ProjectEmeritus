package main.game.stores.pools;

import main.constants.Constants;
import main.game.components.Animation;
import main.graphics.Spritemap;
import main.graphics.Spritesheet;
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
    public static AssetPool getInstance() { if (instance == null) { instance = new AssetPool(); } return instance; }
    private final SplittableRandom random = new SplittableRandom();

    private final Map<String, Spritemap> spritemaps = new HashMap<>();
    private final Map<Integer, Animation> animations = new HashMap<>();
    private final Map<Integer, BufferedImage[]> cache = new HashMap<>();
    private final Map<String, Spritesheet> spritesheets = new HashMap<>();

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());

        spritesheets.put(Constants.FLOORS_SPRITESHEET_FILEPATH, 
            new Spritesheet(Constants.FLOORS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));
            
        spritesheets.put(Constants.WALLS_SPRITESHEET_FILEPATH, 
            new Spritesheet(Constants.WALLS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.STRUCTURES_SPRITESHEET_FILEPATH, 
            new Spritesheet(Constants.STRUCTURES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));
            
        spritesheets.put(Constants.LIQUIDS_SPRITESHEET_FILEPATH, 
            new Spritesheet(Constants.LIQUIDS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.GEMS_SPRITESHEET_PATH, 
            new Spritesheet(Constants.GEMS_SPRITESHEET_PATH, Constants.BASE_SPRITE_SIZE));
                
        spritesheets.put(Constants.SHADOWS_SPRITESHEET_FILEPATH, 
            new Spritesheet(Constants.SHADOWS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.UNITS_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.UNITS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.ABILITIES_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.ABILITIES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    /**
     * 
     * FOR LIQUID AND STRUCTURE TILES 
     * 
     */
    public int createDynamicAssetReference(String sheetName, int row, String animation) {
        // Get spritetype a.k.a. get sheet by index
        Spritesheet sheet = spritesheets.get(sheetName);
                
        // Get a random column from the sheet        
        int column = random.nextInt(sheet.getColumns(row));

        // Create a key to lessen duplicates
        int hash = Objects.hash(sheetName, row, column);
        int id = animations.size();

        // Create the animation fo the first time.
        BufferedImage[] raw = cache.get(hash);
        if (raw == null) {
            switch (animation.toLowerCase()) {
                case "flickering" -> raw = createFlickeringAnimation(sheet, row, column);
                case "shearing" -> raw = createShearingAnimation(sheet, row, column);
                case "spinning" -> raw = createSpinningAnimation(sheet, row, column);
            }
            cache.put(id, raw);
        }
        // Create animation for id
        animations.put(id, new Animation(raw));
        return id;
    }


    private BufferedImage[] createSpinningAnimation(Spritesheet sheet, int row, int column) {
        return createSpinningAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createSpinningAnimation(Spritesheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spinify(copy, .05f);
    }

    private BufferedImage[] createShearingAnimation(Spritesheet sheet, int row, int column) {
        return createShearingAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE + 10);
    }

    private BufferedImage[] createShearingAnimation(Spritesheet sheet, int row, int column, int size) {
        BufferedImage toCopy = sheet.getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createShearingAnimation(copy, 24, .05);
    }

    private BufferedImage[] createFlickeringAnimation(Spritesheet sheet, int row, int column) {
        return createFlickeringAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createFlickeringAnimation(Spritesheet sheet, int row, int column, int size) {
        BufferedImage image = sheet.getSprite(row, column);
        image = ImageUtils.getResizedImage(image, size, size);
        return ImageUtils.createFlickeringAnimation(image, 15, .02f);
    }



    /**
     * Creates static asset reference. Used in context with the returned ID 
     * to retrieve an graphical asset
     * @param sheetName
     * @param row
     * @return
     */
    public int createStaticAssetReference(String sheetName, int row) {
        // Get spritetype a.k.a. get sheet by index
        Spritesheet sheet = spritesheets.get(sheetName);

        if (sheet == null || sheet.getRows() == 0) {
            System.currentTimeMillis();
            logger.error("Spritesheet unable to load from {}", sheet);
            // System.exit(0);
        }

        // Get a random column from the given row
        int column = random.nextInt(sheet.getColumns(row));

        // Create a key to lessen duplicates
        int hash = Objects.hash(sheet, row, column);

        // Create the animation fo the first time.
        BufferedImage[] raw = cache.get(hash);
        
        // This asset has already been made        
        if (raw == null) {
            // Asset needs to be made
            BufferedImage image = createStaticAssetImage(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
            raw = new BufferedImage[]{ image };
        }
        // set id for asset
        int id = animations.size();
        animations.put(id, new Animation(raw));        

        return id;
    }

    private BufferedImage createStaticAssetImage(Spritesheet sheet, int row, int column, int size) {
        BufferedImage image = sheet.getSprite(row, column);
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
    public Spritesheet getSheet(String name) {
        return spritesheets.get(name);
    }

    public BufferedImage getImage(String spritesheet, int index, int row, int column) {
        return getImage(spritesheet, index, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage getImage(String spritesheet, int index, int row, int column, int size) {
        // Get spritetype a.k.a. get sheet by index
        Spritesheet sheet = spritesheets.get(spritesheet);
        // Get image from row and column (WARNING: SOME SHEETS HAVE ONLY 1 ROW);
        BufferedImage image = sheet.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public void updateAnimations() {
        for (Animation animation : animations.values()) { animation.update(); }
    }
}