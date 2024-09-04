package main.game.systems.texts;

import main.game.stores.pools.ColorPalette;
import main.game.main.Settings;
import main.game.components.SecondTimer;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.SplittableRandom;

public class FloatingText {

    protected static final SplittableRandom mRandom = new SplittableRandom();

    private final String mText;
    private Color mForeground;
    private Color mBackground;
    private final Rectangle mTextBounds;
    private final SecondTimer mTimer;
    private final boolean mIsStationary;
    private final double whenToRemove;
    private final Rectangle mTrackBounds;
    private final int mSpriteSize;

    public FloatingText(String value, int x, int y, int width, int height, Color color, boolean isStationary) {

        mText = value;
        mForeground = color;
        mBackground = ColorPalette.TRANSLUCENT_BLACK_V3;
        mSpriteSize = Settings.getInstance().getSpriteWidth();
        mTimer = new SecondTimer();
        mIsStationary = isStationary;
        whenToRemove = 1 + mRandom.nextDouble(0, 2);

        y = y + mRandom.nextInt(height);

        int size = (int) ((mSpriteSize * 1.5) + mRandom.nextInt(mSpriteSize));
        mTrackBounds = new Rectangle(x, y - size - height, width, height + size);
        // Y coordinate of edited bounds is the current position on the track of the origin bounds

        // Center the text
        int widthDifference = (int) Math.abs(mSpriteSize - width);
        if (width > mSpriteSize) {
            x -= widthDifference / 2;
        } else if (width < mSpriteSize) {
            x += widthDifference / 2;
            // Make things of centered for fun
            x += mRandom.nextInt(-4, 4);
        }

        mTextBounds = new Rectangle(x, y, width, height);
    }

    public boolean canRemove() {

        return  mTextBounds.y < mTrackBounds.y;
//        return mTimer.elapsed() >= whenToRemove;

    }
    public void update() {
        if (!mIsStationary && mTextBounds.y >= mTrackBounds.y) {
            mTextBounds.y -= 1;

            if (mTextBounds.y < mTrackBounds.y + mSpriteSize && mTextBounds.y % 3 == 0) {
                mForeground = new Color(mForeground.getRed(), mForeground.getGreen(),
                    mForeground.getBlue(), mForeground.getAlpha() / 2);
                mBackground = new Color(mBackground.getRed(), mBackground.getGreen(),
                    mBackground.getBlue(), mBackground.getAlpha() / 2);
            }
        }
    }

    public String getValue() { return mText; }
    public Color getForeground() { return mForeground; }
    public Color getBackground() { return mBackground; }
    public int getX() { return mTextBounds.x; }
    public int getY() { return mTextBounds.y; }
    public int getWidth() { return mTextBounds.width; }
    public int getHeight() { return mTextBounds.height; }
//    public void debug(Graphics g) {
//        int bx = Camera.getInstance().globalX(mTrackBounds.x);
//        int by = Camera.getInstance().globalY(mTrackBounds.y);
//        g.setColor(ColorPalette.TRANSLUCENT_BLACK_V3);
//        g.fillRect(bx, by, mTrackBounds.width, mTrackBounds.height);
//    }
}
