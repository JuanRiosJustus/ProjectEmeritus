package game.stores.pools;

import constants.Constants;
import game.components.Animation;
import graphics.Spritemap;
import graphics.Spritesheet;
import logging.Logger;
import logging.LoggerFactory;
import utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class AssetPool {

    private static AssetPool instance = null;
    public static AssetPool instance() { if (instance == null) { instance = new AssetPool(); } return instance; }
    private final SplittableRandom random = new SplittableRandom();
    private final Map<String, Spritemap> spritemaps = new HashMap<>();
    private final Map<String, Spritesheet> spritesheets = new HashMap<>();
    private final Map<AssetReference, BufferedImage> images = new HashMap<>();
    private final Map<AssetReference, Animation> animations = new HashMap<>();
//    private final

    private AssetPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());

        spritesheets.put(Constants.FLOORS_SPRITESHEET_FILEPATH,
                new Spritesheet(Constants.FLOORS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.WALLS_SPRITESHEET_FILEPATH,
                new Spritesheet(Constants.WALLS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.LIQUID_SPRITESHEET_FILEPATH,
                new Spritesheet(Constants.LIQUID_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.STRUCTURE_SPRITESHEET_FILEPATH,
                new Spritesheet(Constants.STRUCTURE_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.SHADOWS_SPRITESHEET_FILEPATH,
                new Spritesheet(Constants.SHADOWS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritesheets.put(Constants.GEMS_SPRITESHEET_PATH,
                new Spritesheet(Constants.GEMS_SPRITESHEET_PATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.UNITS_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.UNITS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spritemaps.put(Constants.ABILITIES_SPRITESHEET_FILEPATH,
                new Spritemap(Constants.ABILITIES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        logger.banner("Finished initializing " + getClass().getSimpleName());
    }

    public BufferedImage getImage(String sheet, int row) {
        return getImage(sheet, row, -1, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage[] getSpecificImageAsGlowingAnimation(String sheet, int row, int column) {
        return getSpecificImageAsGlowingAnimation(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }
    public BufferedImage[] getSpecificImageAsGlowingAnimation(String sheet, int row, int column, int size) {
        Spritesheet selected = spritesheets.get(sheet);
        column = column == -1 ? random.nextInt(selected.getColumns(row)) : column;
        BufferedImage image = selected.getSprite(row, column);
        image = ImageUtils.getResizedImage(image, size, size);
        return ImageUtils.spinify(image, .05f);
//        return ImageUtils.spritify(image, 30, .2f, .25f);
    }

    public BufferedImage getSpecificImage(String sheet, int row, int column) {
        return getImage(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage getImage(String sheet, int row, int column, int size) {
        Spritesheet selected = spritesheets.get(sheet);
        column = column == -1 ? random.nextInt(selected.getColumns(row)) : column;
        BufferedImage image = selected.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }
    private BufferedImage[] createBrightenAndDarkenAnimation(String sheet, int row) {
        Spritesheet selected = spritesheets.get(sheet);
        int randomColumn = random.nextInt(selected.getColumns(row));
        return createBrightenAndDarkenAnimation(sheet, row, randomColumn, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] createBrightenAndDarkenAnimation(String sheet, int row, int column, int size) {
        Spritesheet selected = spritesheets.get(sheet);
        BufferedImage image = selected.getSprite(row, column);
        image = ImageUtils.getResizedImage(image, size, size);
        return ImageUtils.brightenAndDarkenAsAnimation(image, 15, .02f);
    }

    public BufferedImage getStaticAssetReference(AssetReference reference) {
        return images.get(reference);
    }

    public Animation getAnimatedAssetReference(AssetReference reference) { return animations.get(reference); }

    public AssetReference createStaticAssetReference(String spritesheet, int row) {
        Spritesheet sheet = spritesheets.get(spritesheet);
        int column = random.nextInt(sheet.getColumns(row));
        return createStaticAssetReference(spritesheet, row, column);
    }
    public AssetReference createStaticAssetReference(String spritesheet, int row, int column) {
        AssetReference reference = new AssetReference(spritesheet, row, column);
        images.put(reference, getImage(spritesheet, row));
        return reference;
    }
    public AssetReference createAnimatedAssetReferenceViaBrightenAndDarken(String spritesheet, int row) {
        BufferedImage[] raw = AssetPool.instance().createBrightenAndDarkenAnimation(spritesheet, row);
        Animation animation = new Animation(raw);

        AssetReference reference = new AssetReference(spritesheet, row, animations.size());
        animations.put(reference, animation);
        return reference;
    }

    public AssetReference createAnimatedAssetReferenceViaTopShearing(String spritesheet, int row) {
        Spritesheet sheet = spritesheets.get(spritesheet);
        int column = random.nextInt(sheet.getColumns(row));
        BufferedImage[] raw = getSpriteAsUnitAnimationShearing(spritesheet, row, column, Constants.CURRENT_SPRITE_SIZE);
        Animation animation = new Animation(raw);
        AssetReference reference = new AssetReference(spritesheet, row, animations.size());
        animations.put(reference, animation);
        return reference;
    }

    public AssetReference getUnitAnimation(String name) {
        BufferedImage[] frames = getUnitAnimation(name, Constants.CURRENT_SPRITE_SIZE);
        Animation animation = new Animation(frames);
//        AssetReference reference = new AssetReference()
        return null;
    }
    private BufferedImage[] getUnitAnimation(String name, int size) {
        BufferedImage toCopy = spritemaps.get(Constants.UNITS_SPRITESHEET_FILEPATH)
                .getSheet(name)
                .getSprite(0, 0);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaYStretch(copy, 12, 1);
    }

    public BufferedImage[] getSpriteAsUnitAnimation(String unitName) {
        return getSpriteAsUnitAnimation(unitName, Constants.CURRENT_SPRITE_SIZE);
    }

    private BufferedImage[] getSpriteAsUnitAnimation(String unitName, int size) {
        BufferedImage toCopy = spritemaps.get(Constants.UNITS_SPRITESHEET_FILEPATH)
                .getSheet(unitName)
                .getSprite(0, 0);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.createAnimationViaYStretch(copy, 12, 1);
    }

    private BufferedImage[] getSpriteAsUnitAnimationShearing(String sheet, int row, int column, int size) {
        BufferedImage toCopy = spritesheets.get(sheet).getSprite(row, column);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.getAnimationViaShear(copy, 24, .05);
    }

    public Animation getAbilityAnimation(String animation) {
        return getAbilityAnimation(animation, Constants.CURRENT_SPRITE_SIZE);
    }

    public Animation getAbilityAnimation(String animation, int size) {
        BufferedImage[] toCopy = spritemaps.get(Constants.ABILITIES_SPRITESHEET_FILEPATH)
                .getSheet(animation)
                .getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], size, size);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new Animation(toCopy);
    }

    public Spritesheet getSpritesheet(String spriteSheetPath) {
        return spritesheets.get(spriteSheetPath);
    }

    public BufferedImage[] getSpriteSheetImages(String sheetPath) {
        Spritesheet sheet = spritesheets.get(sheetPath);
        BufferedImage[] images = new BufferedImage[sheet.getRows()];
        for (int i = 0; i < sheet.getRows(); i++) {
            images[i] = getImage(sheetPath, i);
        }
        return images;
    }

    public void updateAnimations() {
        for (Animation animation : animations.values()) { animation.update(); }
    }
}