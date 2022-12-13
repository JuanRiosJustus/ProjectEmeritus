package graphics;

import logging.Logger;
import logging.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SpriteSheet {

    private final BufferedImage spriteSheet;
    private BufferedImage[][] spriteArray;
    private final int spriteSize;
    private final int spritesInColumn;
    private final int spritesInRow;
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public SpriteSheet(String filePath, int sizeOfSprites) {
        spriteSheet = loadSpriteSheet(filePath);
        spriteSize = sizeOfSprites;
        spritesInColumn = spriteSheet.getWidth() / spriteSize;
        spritesInRow = spriteSheet.getHeight() / spriteSize;
        loadSpriteArray();
        logger.log("Finished loading {0}", filePath);
    }


    private BufferedImage loadSpriteSheet(String path) {
        BufferedImage sprite = null;
        try {
            File toLoad = new File(path);
            if (toLoad.isDirectory()) { throw new Exception("File trying to be loaded is a directory"); }
            if (!toLoad.exists()) { throw new Exception("File trying to be loaded doesn't exist"); }
            sprite = ImageIO.read(toLoad);
//            logger.log("{0} successfully loaded with width={1} and height={2}", path, sprite.getWidth(), sprite.getHeight());
        } catch (Exception e) {
            logger.log("Could not load SpriteSheet from {0} because {1}", path, e);
        }
        return sprite;
    }

    private void loadSpriteArray() {
        spriteArray = new BufferedImage[spritesInRow][spritesInColumn];
        for (int row = 0; row < spritesInRow; row++) {
            for (int column = 0; column < spritesInColumn; column++) {
                BufferedImage image = getSprite(row, column);
                spriteArray[row][column] = image;
            }
        }
//        logger.log("Rows: {0}, Columns: {1}", spriteArray.length, spriteArray[0].length);
    }

    public BufferedImage getSprite(int row, int column) {
        return spriteSheet.getSubimage(column * spriteSize, row * spriteSize, spriteSize, spriteSize);
    }

    public BufferedImage getSprite(int index) {
        return getSprite(index / spriteArray[0].length, index % spriteArray[0].length);
    }

    public BufferedImage[] getSpriteArray(int index) { return spriteArray[index]; }
    public int columns(int row) { return spriteArray[row].length; }
    public int rows() { return spriteArray.length; }
}