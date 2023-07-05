package graphics;

import logging.ELogger;
import logging.ELoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Spritemap {

    private final Map<String, Spritesheet> stringIndex = new HashMap<>();
    private final Map<Integer, Spritesheet> integerIndex = new HashMap<>();

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(Spritemap.class);

    public Spritemap(String directoryPath, int sizeOfSprites) {
        load(directoryPath, sizeOfSprites);
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
                String spritesheeetName = name.substring(0, name.indexOf('.'));

                String sheetname = spritesheeetName.toLowerCase(Locale.ROOT);
                Spritesheet sheet = new Spritesheet(filePath, size);
                stringIndex.put(sheetname, sheet);
                int index = integerIndex.size();
                integerIndex.put(index, sheet);
            }

            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
            e.printStackTrace();
        }
    }

    public Spritesheet getSpritesheetByIndex(int index) {
        return integerIndex.get(index);
    }

    public Spritesheet getSpritesheetByName(String name) {
        return stringIndex.get(name.toLowerCase(Locale.ROOT));
    }

    public int getSize() {
        return stringIndex.size();
    }
}
