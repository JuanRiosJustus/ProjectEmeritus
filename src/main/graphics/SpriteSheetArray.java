package main.graphics;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SpriteSheetArray {

    private final Map<String, SpriteSheet> spriteMap = new HashMap<>();
    private final Map<String, Integer> stringMap = new HashMap<>();
    private final Map<Integer, String> integerMap = new HashMap<>();

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteSheetArray.class);

    public SpriteSheetArray(String directoryPath, int sizeOfSprites) {
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
    public Set<String> getKeys() {
        return spriteMap.keySet();
    }
}
