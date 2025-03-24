package main.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import main.logging.EmeritusLogger;


import java.awt.image.BufferedImage;

public class Sprite {
    private final Image[][] mFrames;
    private final String mPath;
    private static final EmeritusLogger mLogger = EmeritusLogger.create(Sprite.class);
    public Sprite(String path, int spriteWidths, int spriteHeights) {
        mPath = path;
        Image raw = getSpritesheet(path);
        int rows = (int) (raw.getHeight() / spriteHeights);
        int columns = (int) (raw.getWidth() / spriteWidths);
        mFrames = getFrames(raw, rows, columns, spriteWidths, spriteHeights);
    }

    private Image getSpritesheet(String path) {
        Image sheet = null;
        try {
            sheet = new Image("file:" + path);
        } catch (Exception e) {
            mLogger.error("Could not load SpriteSheet from {} because {}", path, e);
        }
        return sheet;
    }

    private Image[][] getFrames(Image raw, int rows, int columns, int widths, int heights) {
        Image[][] listOfRowOfFrames = new Image[rows][];
        for (int row = 0; row < rows; row++) {
            Image[] rowOfFrames = new Image[columns];
            for (int column = 0; column < columns; column++) {
                Image frame = new WritableImage(
                        raw.getPixelReader(),
                        column * widths,
                        row * heights,
                        widths, heights
                );
                rowOfFrames[column] = frame;
            }
            listOfRowOfFrames[row] = rowOfFrames;
        }
        return listOfRowOfFrames;
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

    public Image getSprite(int row, int column) { return mFrames[row][column]; }
    public Image[] getSpriteArray(int row) { return mFrames[row]; }
    public int getColumns(int row) { return mFrames[row].length; }
    public int getColumns() { return getColumns(0); }
    public int getRows() { return mFrames.length; }
    public String getName() { return mPath; }
}