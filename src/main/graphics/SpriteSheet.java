package main.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import main.logging.EmeritusLogger;


import java.awt.image.BufferedImage;

public class SpriteSheet {
    private Image[][] mFrames;
    private final int mSpriteWidth;
    private final int mSpriteHeight;
    private final String mSpriteSheetLocation;
    private static final EmeritusLogger mLogger = EmeritusLogger.create(SpriteSheet.class);
    public SpriteSheet(String spritePath, int spriteWidth, int spriteHeight) {
        mSpriteSheetLocation = spritePath;
        mSpriteWidth = spriteWidth;
        mSpriteHeight = spriteHeight;
        load();
    }

    public void load() {
        if (mFrames != null) { return; }
        Image raw = new Image("file:" + mSpriteSheetLocation);
        int rows = (int) (raw.getHeight() / mSpriteHeight);
        int columns = (int) (raw.getWidth() / mSpriteWidth);
        mFrames = getFrames(raw, rows, columns, mSpriteWidth, mSpriteHeight);
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


    public Image getSprite(int row, int column) { return mFrames[row][column]; }
    public Image[] getSpriteArray(int row) { return mFrames[row]; }
    public int getColumns(int row) { return mFrames[row].length; }
    public int getColumns() { return getColumns(0); }
    public int getRows() { return mFrames.length; }
    public String getName() { return mSpriteSheetLocation; }
    public boolean isLoaded() { return mFrames != null; }
}