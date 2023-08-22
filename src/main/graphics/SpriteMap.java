package main.graphics;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SpriteMap {

    private final Map<String, SpriteSheet> sheetMap = new HashMap<>();
    private final List<SpriteSheet> sheetList = new ArrayList<>();

    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteMap.class);

    public SpriteMap(String directoryPath, int sizeOfSprites) {
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
                SpriteSheet sheet = new SpriteSheet(filePath, size);
                // integerIndex.put(stringIndex.size(, sheetname);
                sheetMap.put(sheetname, sheet);
                sheetList.add(sheet);
            }

            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
            e.printStackTrace();
        }
    }

    public SpriteSheet getSpritesheetByIndex(int index) {
        return sheetList.get(index);
    }

    public SpriteSheet getSpritesheetByName(String name) {
        return sheetMap.get(name.toLowerCase(Locale.ROOT));
    }

    public int getSize() {
        return sheetMap.size();
    }

    public Set<String> getSheetNameKeys() {
        return sheetMap.keySet();
    }
}
