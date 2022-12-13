package graphics;

import logging.Logger;
import logging.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpriteSheetMap {

    private final Map<String, SpriteSheet> map = new HashMap<>();
    private final static Logger logger = LoggerFactory.instance().logger(SpriteSheetMap.class);

    public SpriteSheetMap(String directoryPath, int sizeOfSprites) {
        loadSpriteSheets(directoryPath, sizeOfSprites);
    }

    private void loadSpriteSheets(String directoryPath, int sizeOfSprites) {
        try {
            File folder = new File(directoryPath);
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isDirectory()) { continue; }
//                BufferedImage image = ImageIO.read(file);
                String filePath = file.getPath();

                if (!filePath.endsWith(".png")) {
                    logger.error(filePath + " is not support sprite extension");
                    continue;
                }

                String name = filePath.substring(filePath.lastIndexOf('/') + 1);
                String extension = name.substring(name.lastIndexOf('.') + 1);
                String spriteName = name.substring(0, name.indexOf('.'));

                map.put(spriteName.toLowerCase(Locale.ROOT), new SpriteSheet(filePath, sizeOfSprites));
            }
            logger.log("Sprites loaded from " + directoryPath);
        } catch (Exception ex) {
            logger.log("Failed loading sprites from " + directoryPath + " | " + ex);
        }
    }

    public SpriteSheet getSheet(String name) {
        return map.get(name.toLowerCase(Locale.ROOT));
    }
}
