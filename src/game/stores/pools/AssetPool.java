package game.stores.pools;

import constants.Constants;
import game.components.SpriteAnimation;
import graphics.SpriteSheetMap;
import graphics.SpriteSheet;
import logging.Logger;
import logging.LoggerFactory;
import utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class AssetPool {

    private static final AssetPool instance = new AssetPool();
    private final SplittableRandom random = new SplittableRandom();

    private final SpriteSheet tilesSheet;
    private final SpriteSheet structureSheet;
    private final SpriteSheet depthSheet;
    private final SpriteSheet liquidSheet;
    private final SpriteSheetMap unitMap;
    private final SpriteSheetMap abilityMap;
    private final Map<Integer, SpriteAnimation> liquidAnimationMap = new HashMap<>();

    private AssetPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());

        tilesSheet = new SpriteSheet(Constants.TILES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE);
        liquidSheet = new SpriteSheet(Constants.LIQUID_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE);
        structureSheet = new SpriteSheet(Constants.STRUCTURE_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE);
        depthSheet = new SpriteSheet(Constants.DEPTHS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE);

        unitMap = new SpriteSheetMap(Constants.UNITS_GRAPHICS_DIRECTORY, Constants.BASE_SPRITE_SIZE);
        abilityMap = new SpriteSheetMap(Constants.ABILITY_GRAPHICS_DIRECTORY, Constants.BASE_SPRITE_SIZE);

        logger.banner("Finished initializing " + getClass().getSimpleName());
    }

    public static AssetPool instance() { return instance; }

    public BufferedImage getLiquidImage(int row) {
        int randomColumn = random.nextInt(liquidSheet.columns(row));
        return getLiquidImage(row, randomColumn, Constants.SPRITE_SIZE);
    }
    public BufferedImage getLiquidImage(int row, int column, int size) {
        BufferedImage image = liquidSheet.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public BufferedImage[] getLiquidAnimation(int row) {
        BufferedImage image = getLiquidImage(row);
        return ImageUtils.brightenOrDarkenAsAnimation(image, 15, .02f);
    }

    public BufferedImage getStructureImage(int row) {
        int randomColumn = random.nextInt(structureSheet.columns(row));
        return getStructureImage(row, randomColumn, Constants.SPRITE_SIZE);
    }
    public BufferedImage getStructureImage(int row, int column, int size) {
        BufferedImage image = structureSheet.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public BufferedImage getTileImage(int row) {
        int randomColumn = random.nextInt(tilesSheet.columns(row));
        return getTileImage(row, randomColumn, Constants.SPRITE_SIZE);
    }
    public BufferedImage getTileImage(int row, int column, int size) {
        BufferedImage image = tilesSheet.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public BufferedImage[] getSpriteAnimation(String unitName) {
        return getSpriteAnimation(unitName, Constants.SPRITE_SIZE);
    }


    public BufferedImage[] getSpriteAnimation(String unitName, int size) {
        BufferedImage toCopy = unitMap.getSheet(unitName).getSprite(0);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spritify(copy, 12, 0, 1);
    }

//    public SpriteAnimation getAbilityAnimation(String animation) {
//        return new SpriteAnimation(abilityMap.getSheet(animation).getSpriteArray(0));
//    }

    public SpriteAnimation getAbilityAnimation(String animation) {
        return getAbilityAnimation(animation, Constants.SPRITE_SIZE);
    }


    public SpriteAnimation getAbilityAnimation(String animation, int size) {
        BufferedImage[] toCopy = abilityMap.getSheet(animation).getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], size, size);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new SpriteAnimation(toCopy);
    }


//    public Animation createUnitAnimation(int index) {
//        return createUnitAnimation(index, Constants.SPRITE_SIZE);
//    }

//    public Animation createUnitAnimation(int index, int size) {
//        BufferedImage toCopy = unitsSheet.getSprite(index);
//        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
//        BufferedImage copy = ImageUtils.deepCopy(toCopy);
//        return new Animation(ImageUtils.spritify(copy, 12, 0, 1));
//    }

    public int tileSprites() { return tilesSheet.rows(); }
    public int liquidSprites() { return liquidSheet.rows(); }

}