package game.stores.pools;

import constants.Constants;
import game.components.SpriteAnimation;
import game.stores.pools.ability.AbilityPool;
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

    private static AssetPool instance = null;
    public static AssetPool instance() { if (instance == null) { instance = new AssetPool(); } return instance; }
    private final SplittableRandom random = new SplittableRandom();
    private final Map<String, SpriteSheetMap> spriteSheetMapMap = new HashMap<>();
    private final Map<String, SpriteSheet> spriteSheetMap = new HashMap<>();

    private AssetPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());

        spriteSheetMap.put(Constants.FLOORS_SPRITESHEET_FILEPATH,
                new SpriteSheet(Constants.FLOORS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spriteSheetMap.put(Constants.WALLS_SPRITESHEET_FILEPATH,
                new SpriteSheet(Constants.WALLS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spriteSheetMap.put(Constants.TERRAIN_SPRITESHEET_FILEPATH,
                new SpriteSheet(Constants.TERRAIN_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spriteSheetMap.put(Constants.SPECIAL_SPRITESHEET_FILEPATH,
                new SpriteSheet(Constants.SPECIAL_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spriteSheetMap.put(Constants.STRUCTURE_SPRITESHEET_FILEPATH,
                new SpriteSheet(Constants.STRUCTURE_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spriteSheetMap.put(Constants.SHADOWS_SPRITESHEET_FILEPATH,
                new SpriteSheet(Constants.SHADOWS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));


        spriteSheetMapMap.put(Constants.UNITS_SPRITESHEET_FILEPATH,
                new SpriteSheetMap(Constants.UNITS_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        spriteSheetMapMap.put(Constants.ABILITIES_SPRITESHEET_FILEPATH,
                new SpriteSheetMap(Constants.ABILITIES_SPRITESHEET_FILEPATH, Constants.BASE_SPRITE_SIZE));

        logger.banner("Finished initializing " + getClass().getSimpleName());
    }

    public BufferedImage getImage(String sheet, int row) {
        return getImage(sheet, row, -1, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage getSpecificImage(String sheet, int row, int column) {
        return getImage(sheet, row, column, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage getImage(String sheet, int row, int column, int size) {
        SpriteSheet selected = spriteSheetMap.get(sheet);
        column = column == -1 ? random.nextInt(selected.columns(row)) : column;
        BufferedImage image = selected.getSprite(row, column);
        return ImageUtils.getResizedImage(image, size, size);
    }

    public BufferedImage[] getImageAsAnimation(String sheet, int row) {
        SpriteSheet selected = spriteSheetMap.get(sheet);
        int randomColumn = random.nextInt(selected.columns(row));
        return getImageAsAnimation(sheet, row, randomColumn, Constants.CURRENT_SPRITE_SIZE);
    }

    public BufferedImage[] getImageAsAnimation(String sheet, int row, int column, int size) {
        SpriteSheet selected = spriteSheetMap.get(sheet);
        BufferedImage image = selected.getSprite(row, column);
        image = ImageUtils.getResizedImage(image, size, size);
//        return ImageUtils.spritify(image, 15, .02f, .2f);
        return ImageUtils.brightenOrDarkenAsAnimation(image, 15, .02f);
    }

//    public BufferedImage getStructureImage(int row) {
//        int randomColumn = random.nextInt(structureSheet.columns(row));
//        return getStructureImage(row, randomColumn, Constants.SPRITE_SIZE);
//    }
//    public BufferedImage getStructureImage(int row, int column, int size) {
//        BufferedImage image = structureSheet.getSprite(row, column);
//        return ImageUtils.getResizedImage(image, size, size);
//    }

    public BufferedImage[] getSpriteAnimation(String unitName) {
        return getSpriteAnimation(unitName, Constants.CURRENT_SPRITE_SIZE);
    }


    public BufferedImage[] getSpriteAnimation(String unitName, int size) {
//        BufferedImage toCopy = unitMap.getSheet(unitName).getSprite(0);
        BufferedImage toCopy = spriteSheetMapMap.get(Constants.UNITS_SPRITESHEET_FILEPATH)
                .getSheet(unitName)
                .getSprite(0);
        toCopy = ImageUtils.getResizedImage(toCopy, size, size);
        BufferedImage copy = ImageUtils.deepCopy(toCopy);
        return ImageUtils.spritify(copy, 12, 0, 1);
    }

//    public SpriteAnimation getAbilityAnimation(String animation) {
//        return new SpriteAnimation(abilityMap.getSheet(animation).getSpriteArray(0));
//    }

    public SpriteAnimation getAbilityAnimation(String animation) {
        return getAbilityAnimation(animation, Constants.CURRENT_SPRITE_SIZE);
    }


    public SpriteAnimation getAbilityAnimation(String animation, int size) {
//        BufferedImage[] toCopy = abilityMap.getSheet(animation).getSpriteArray(0);
        BufferedImage[] toCopy = spriteSheetMapMap.get(Constants.ABILITIES_SPRITESHEET_FILEPATH)
                .getSheet(animation)
                .getSpriteArray(0);
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
//
//    public int tileSprites() { return terrainSheet.rows(); }
//    public int liquidSprites() { return liquidSheet.rows(); }
    public SpriteSheet getSpriteSheet(String spriteSheetPath) {
        return spriteSheetMap.get(spriteSheetPath);
    }

    public BufferedImage[] getSpriteSheetImages(String sheetPath) {
        SpriteSheet sheet = spriteSheetMap.get(sheetPath);
        BufferedImage[] images = new BufferedImage[sheet.rows()];
        for (int i = 0; i < sheet.rows(); i++) {
            images[i] = getImage(sheetPath, i);
        }
        return images;
    }

}