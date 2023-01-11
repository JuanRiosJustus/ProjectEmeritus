package graphics;

import logging.Logger;
import logging.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Spritesheet {

    private final BufferedImage raw;
    private final List<List<BufferedImage>> spritesheet;
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public Spritesheet(String path, int sizes) {
        this(path, sizes, false);
    }

    public Spritesheet(String path, int sizes, boolean allowEmpty) {
        raw = getSpritesheet(path);
        int rows = raw.getHeight() / sizes;
        int columns = raw.getWidth() / sizes;
        spritesheet = getSprites(rows, columns, sizes, allowEmpty);
        logger.log("Finished loading {0}", path);
    }


    private BufferedImage getSpritesheet(String path) {
        BufferedImage sheet = null;
        try {
            File file = new File(path);
            if (file.isDirectory()) { throw new Exception("File trying to be loaded is a directory"); }
            if (!file.exists()) { throw new Exception("File trying to be loaded doesn't exist"); }
            sheet = ImageIO.read(file);
        } catch (Exception e) {
            logger.log("Could not load SpriteSheet from {0} because {1}", path, e);
        }
        return sheet;
    }

    private boolean isNotCompletelyTransparent(BufferedImage img) {
        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
                int color = img.getRGB(pixelCol, pixelRow);
                int alpha = (color>>24) & 0xff;
                // if there is a non alpha color in this image, then it is valid
                if (alpha != 0) { return true; }
            }
        }
        return false;
    }

    private List<List<BufferedImage>> getSprites(int rows, int columns, int sizes, boolean allowEmpty) {
        List<List<BufferedImage>> listOfLists = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            List<BufferedImage> list = new ArrayList<>();
            for (int column = 0; column < columns; column++) {
                BufferedImage image = raw.getSubimage(column * sizes, row * sizes, sizes, sizes);
                if (!isNotCompletelyTransparent(image) && !allowEmpty) { continue; }
                list.add(image);
            }
            if (list.isEmpty()) { continue; }
            listOfLists.add(list);
        }
        return listOfLists;
    }

    public BufferedImage getSprite(int row, int column) { return spritesheet.get(row).get(column); }
    public BufferedImage[] getSpriteArray(int row) { return spritesheet.get(row).toArray(new BufferedImage[0]); }
    public int getColumns(int row) { return spritesheet.get(row).size(); }
    public int getRows() { return spritesheet.size(); }
}