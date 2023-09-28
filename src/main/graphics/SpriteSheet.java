package main.graphics;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SpriteSheet {
    private final BufferedImage[][] mSheet;
    private final String mPath;
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteSheet.class);
    public SpriteSheet(String path, int sizes) {
        BufferedImage raw = getSpritesheet(path);
        int rows = raw.getHeight() / sizes;
        int columns = raw.getWidth() / sizes;
        mPath = path;
        mSheet = getSprites(raw, rows, columns, sizes);
        logger.info("Finished loading {}", path);
    }
    private BufferedImage getSpritesheet(String path) {
        BufferedImage sheet = null;
        try {
            File file = new File(path);
            if (file.isDirectory()) { throw new Exception("File trying to be loaded is a directory"); }
            if (!file.exists()) { throw new Exception("File trying to be loaded doesn't exist"); }
            sheet = ImageIO.read(file);
        } catch (Exception e) {
            logger.error("Could not load SpriteSheet from {} because {}", path, e);
        }
        return sheet;
    }

    private BufferedImage[][] getSprites(BufferedImage raw, int rows, int columns, int sizes) {
        BufferedImage[][] listOfRowOfImages = new BufferedImage[rows][];
        for (int row = 0; row < rows; row++) {
            BufferedImage[] rowOfImages = new BufferedImage[columns];
            for (int column = 0; column < columns; column++) {
                BufferedImage image = raw.getSubimage(column * sizes, row * sizes, sizes, sizes);
                rowOfImages[column] = image;
            }
            listOfRowOfImages[row] = rowOfImages;
        }
        return listOfRowOfImages;
    }

    public BufferedImage getSprite(int row, int column) { return mSheet[row][column]; }
    public BufferedImage[] getSpriteArray(int row) { return mSheet[row]; }
    public int getColumns(int row) { return mSheet[row].length; }
    public int getColumns() { return getColumns(0); }
    public int getRows() { return mSheet.length; }
    public String getName() { return mPath; }
}