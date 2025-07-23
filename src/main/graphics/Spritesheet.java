package main.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import main.logging.EmeritusLogger;

public class Spritesheet {
    private Image[] mFrames;
    private final int mSpriteWidth;
    private final int mSpriteHeight;
    private final String mPath;
    private static final EmeritusLogger mLogger = EmeritusLogger.create(Spritesheet.class);
    public Spritesheet(String spritePath, int spriteWidth, int spriteHeight) {
        mPath = spritePath;
        mSpriteWidth = spriteWidth;
        mSpriteHeight = spriteHeight;
//        load();
    }

    public void load() {
        if (mFrames != null) { return; }
        Image raw = new Image("file:" + mPath);
        mFrames = getSingleRowSpritesheet(raw, mSpriteWidth, mSpriteHeight);
    }

    private Image[][] getMultiRowSpritesheet(Image raw, int rows, int columns, int widths, int heights) {
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

    private Image[] getSingleRowSpritesheet(Image sheet, int spriteWidths, int spriteHeights) {
        int spriteCount = (int) (sheet.getWidth() / mSpriteWidth); // Also known as columns
        Image[] frames = new Image[spriteCount];
        for (int column = 0; column < spriteCount; column++) {
            Image frame = new WritableImage(
                    sheet.getPixelReader(),
                    column * spriteWidths,
                    0,
                    spriteWidths,
                    spriteHeights
            );
            frames[column] = frame;
        }
        return frames;
    }

    public Image getFrame(int index) { return mFrames[index]; }
    public String getName() { return mPath; }
    public boolean isLoaded() { return mFrames != null; }
}