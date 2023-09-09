package main.graphics;

import main.constants.Settings;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class SpriteSheetMap {

    private final Map<String, SpriteSheet> spriteMap = new LinkedHashMap<>();
    private final Map<String, Integer> stringMap = new HashMap<>();
    private final Map<Integer, String> integerMap = new HashMap<>();

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteSheetMap.class);

    public SpriteSheetMap(String directoryPath, int sizeOfSprites) {
        load(directoryPath, sizeOfSprites);
//        merge();
    }

    private void merge() {
        // TODO performance improvement
        // Merge all images in the sheet into 1
        // used the already determined

        // 1. Get the dimension for the merged image
        int newWidth = 0;
        int newHeight = 0;
        for (Map.Entry<String, SpriteSheet> entry : spriteMap.entrySet()) {
            SpriteSheet sheet = entry.getValue();
            newWidth = Math.max(newWidth, sheet.getColumns() * Settings.getInstance().getSpriteSize());
            newHeight += Settings.getInstance().getSpriteSize();
        }

        // 2. Get each row of images to merge - NOTE* each spritesheet should be 1 row only, any amount of columns
        BufferedImage[][] images = new BufferedImage[spriteMap.size()][];
        for (Map.Entry<Integer, String> entry : integerMap.entrySet()) {
            SpriteSheet sheet = spriteMap.get(entry.getValue());
            images[entry.getKey()] = sheet.getSpriteArray(0);
        }

        BufferedImage mergedImage = ImageUtils.createMergedImage(images, newWidth, newHeight);
    }

    private void load(String directory, int size) {
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
                SpriteSheet sheet = new SpriteSheet(filePath, size);

                spriteMap.put(spritesheetNameLowerCase, sheet);
                stringMap.put(spritesheetNameLowerCase, stringMap.size());
                integerMap.put(integerMap.size(), spritesheetNameLowerCase);
            }

            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
        }
    }
    public int indexOf(String name) { return stringMap.get(name); }
    public SpriteSheet get(int index) { return spriteMap.get(integerMap.get(index)); }
    public SpriteSheet get(String name) {
        return spriteMap.get(name.toLowerCase(Locale.ROOT));
    }
    public int getSize() {
        return spriteMap.size();
    }
    public List<Map.Entry<String, SpriteSheet>> getMapEntriesContaining(String txt) {
        return spriteMap.entrySet().stream().filter(e -> e.getKey().contains(txt)).toList();
    }
    public List<String> getKeysEndingWith(String txt) {
        return spriteMap.keySet().stream().filter(e -> e.endsWith(txt)).toList();
    }
    public Set<String> getKeys() {
        return spriteMap.keySet();
    }
}
