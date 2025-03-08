package main.graphics;

import javafx.scene.image.Image;
import main.logging.EmeritusLogger;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Sprite {
    private final BufferedImage[][] mSheet;
    private final String mPath;
    private static final EmeritusLogger logger = EmeritusLogger.create(Sprite.class);
    public Sprite(String path, int spriteWidths, int spriteHeights) {
        BufferedImage raw = getSpritesheet(path);
        int rows = raw.getHeight() / spriteHeights;
        int columns = raw.getWidth() / spriteWidths;
        mPath = path;
        mSheet = getSprites(raw, rows, columns, spriteWidths, spriteHeights);
        logger.info("Finished loading {}", path);
    }

    public Sprite(String path, int sizes) {
        this(path, sizes, sizes);
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

    private BufferedImage[][] getSprites(BufferedImage raw, int rows, int columns, int widths, int heights) {
        BufferedImage[][] listOfRowOfImages = new BufferedImage[rows][];
        for (int row = 0; row < rows; row++) {
            BufferedImage[] rowOfImages = new BufferedImage[columns];
            for (int column = 0; column < columns; column++) {
                BufferedImage image = raw.getSubimage(column * widths, row * heights, widths, heights);
                rowOfImages[column] = image;
            }
            listOfRowOfImages[row] = rowOfImages;
        }
        return listOfRowOfImages;
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