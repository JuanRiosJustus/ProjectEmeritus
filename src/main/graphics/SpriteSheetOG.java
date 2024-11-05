package main.graphics;

import main.game.main.GameSettings;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class SpriteSheetOG {
    private final Map<String, Sprite> mSpriteSheet = new LinkedHashMap<>();
    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteSheetOG.class);

    /**
     * This class represents a folder/table where each item represents a grouping of like sprites.
     * For example, if the Sprite map is a map of terrains, then the items represent various terrains
     * that are like in a similar image
     * @param directoryPath
     * @param sizeOfSprites
     */
    public SpriteSheetOG(String directoryPath, int sizeOfSprites) {
        load(directoryPath, sizeOfSprites, sizeOfSprites);
//        merge();
    }

    public SpriteSheetOG(String directoryPath, int spriteWidths, int spriteHeights) {
        load(directoryPath, spriteWidths, spriteHeights);
//        merge();
    }

    private void merge() {
        // TODO performance improvement
        // This will merge entire spriteMaps together
        // Merge all images in the sheet into 1
        // used the already determined

        // 1. Get the dimension for the merged image
        int newWidth = 0;
        int newHeight = 0;
        for (Map.Entry<String, Sprite> entry : mSpriteSheet.entrySet()) {
            Sprite sheet = entry.getValue();
            newWidth = Math.max(newWidth, sheet.getColumns() * GameSettings.getInstance().getSpriteSize());
            newHeight += GameSettings.getInstance().getSpriteSize();
        }

        // 2. Get each row of images to merge - NOTE* each spritesheet should be 1 row only, any amount of columns
        int index = 0;
        BufferedImage[][] images = new BufferedImage[mSpriteSheet.size()][];
        for (Map.Entry<String, Sprite> entry : mSpriteSheet.entrySet()) {
            Sprite sheet = entry.getValue();
            images[index] = sheet.getSpriteArray(0);
            index++;
        }

        BufferedImage mergedImage = ImageUtils.createMergedImage(images, newWidth, newHeight);
        System.out.println(mergedImage.getMinX());
    }

    private void load(String directory, int spriteWidths, int spriteHeights) {
        try {
            // Load content from given path
            File content = new File(directory);
            File[] files = null;
            if (content.isDirectory()) {
                files = content.listFiles();
            } else if (content.isFile()) {
                files = new File[] { content };
            }
            // Iterate through content
            for (File file : files) {
                String filePath = file.getPath();

                if (!filePath.endsWith(".png")) {
                    logger.warn(filePath + " is not a support sprite extension");
                    continue;
                }

                String name = filePath.substring(filePath.lastIndexOf('/') + 1);
                String extension = name.substring(name.lastIndexOf('.') + 1);
                String spritesheetName = name.substring(0, name.indexOf('.'));

                String spritesheetNameLowerCase = spritesheetName.toLowerCase(Locale.ROOT);
                Sprite sheet = new Sprite(filePath, spriteWidths, spriteHeights);

                mSpriteSheet.put(filePath, sheet);
            }

            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
        }
    }

//    private void load(String directory, int size) {
//        try {
//            // Load content from given path
//            File content = new File(directory);
//            File[] files = null;
//            if (content.isDirectory()) {
//                files = content.listFiles();
//            } else if (content.isFile()) {
//                files = new File[] { content };
//            }
//            // Iterate through content
//            for (File file : files) {
//                String filePath = file.getPath();
//
//                if (!filePath.endsWith(".png")) {
//                    logger.warn(filePath + " is not a support sprite extension");
//                    continue;
//                }
//
//                String name = filePath.substring(filePath.lastIndexOf('/') + 1);
//                String extension = name.substring(name.lastIndexOf('.') + 1);
//                String spritesheetName = name.substring(0, name.indexOf('.'));
//
//                String spritesheetNameLowerCase = spritesheetName.toLowerCase(Locale.ROOT);
//                SpriteSheetRow sheet = new SpriteSheetRow(filePath, size);
//
//                mSpriteSheetMap.put(filePath, sheet);
//            }
//
//            logger.info("Finished loading {}", directory);
//        } catch (Exception e) {
//            logger.error("Failed loading {} because {}", directory, e);
//        }
//    }

    public int indexOf(String name) {
        int iteration = 0;
        for (Map.Entry<String, Sprite> entry : mSpriteSheet.entrySet()) {
            if (entry.getKey().equals(name)) { return iteration; }
            if (entry.getKey().contains(name)) { return iteration; }
            iteration++;
        }
        return -1;
    }

    public Sprite get(int index) { return mSpriteSheet.get(mSpriteSheet.keySet().toArray(new String[0])[index]); }
    public Sprite get(String name) {
        return mSpriteSheet.get(name.toLowerCase(Locale.ROOT));
    }

    public int getSize() {
        return mSpriteSheet.size();
    }
    public List<String> endingWith(String txt) {
        return mSpriteSheet.keySet().stream().filter(e -> e.endsWith(txt)).toList();
    }
    public List<String> startingWith(String txt) {
        return mSpriteSheet.keySet().stream().filter(e -> e.startsWith(txt)).toList();
    }
    public List<String> contains(String txt) {
        return mSpriteSheet.keySet().stream().filter(e -> e.contains(txt)).toList();
    }
    public Set<String> getKeys() {
        return mSpriteSheet.keySet();
    }
}
