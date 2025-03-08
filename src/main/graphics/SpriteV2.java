package main.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import main.logging.EmeritusLogger;

import java.io.File;

public class SpriteV2 {
    private final Image[][] mSheet;
    private final String mPath;
    private static final EmeritusLogger logger = EmeritusLogger.create(SpriteV2.class);
    public SpriteV2(String path, int spriteWidths, int spriteHeights) {
        Image raw = getSpritesheet(path);
        int rows = (int) (raw.getHeight() / spriteHeights);
        int columns = (int) (raw.getWidth() / spriteWidths);
        mPath = path;
        mSheet = getSprites(raw, rows, columns, spriteWidths, spriteHeights);
        logger.info("Finished loading {}", path);
    }

    public SpriteV2(String path, int sizes) {
        this(path, sizes, sizes);
    }

    private Image getSpritesheet(String path) {
        Image sheet = null;
        try {
            File file = new File(path);
            if (file.isDirectory()) { throw new Exception("File trying to be loaded is a directory"); }
            if (!file.exists()) { throw new Exception("File trying to be loaded doesn't exist"); }
            sheet = new Image(path);
        } catch (Exception e) {
            logger.error("Could not load SpriteSheet from {} because {}", path, e);
        }
        return sheet;
    }

    private Image[][] getSprites(Image raw, int rows, int columns, int widths, int heights) {
        Image[][] listOfRowOfImages = new Image[rows][];
        for (int row = 0; row < rows; row++) {
            Image[] rowOfImages = new Image[columns];
            for (int column = 0; column < columns; column++) {
                Image image = new WritableImage(raw.getPixelReader(), column * widths, row * heights, widths, heights);
                rowOfImages[column] = image;
            }
            listOfRowOfImages[row] = rowOfImages;
        }
        return listOfRowOfImages;
    }

    public Image getSprite(int row, int column) { return mSheet[row][column]; }
    public Image[] getSpriteArray(int row) { return mSheet[row]; }
    public int getColumns(int row) { return mSheet[row].length; }
    public int getColumns() { return getColumns(0); }
    public int getRows() { return mSheet.length; }
    public String getName() { return mPath; }
}