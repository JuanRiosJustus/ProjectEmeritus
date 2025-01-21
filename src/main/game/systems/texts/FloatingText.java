package main.game.systems.texts;

import main.constants.UtilityTimer;
import main.game.stores.pools.ColorPalette;
import org.json.JSONObject;

import java.awt.Color;
import java.util.SplittableRandom;

public class FloatingText extends JSONObject {

    protected static final String TEXT = "text";
    protected static final String X = "x";
    protected static final String Y = "y";
    protected static final String LIFE_EXPECTANCY = "life.expectancy";
    protected static final String CURRENT_AGE = "age";
    protected static final String FONT_SIZE = "size";
    protected static final String CENTER_TEXT = "center_text";
    protected final SplittableRandom mRandom;

    protected Color mBackground;
    protected Color mForeground;
    protected UtilityTimer mUtilityTimer;

    public FloatingText(String txt, float size, int x, int y, Color color, double lifetime) {
        put(TEXT, txt);
        put(X, x);
        put(Y, y);
        put(LIFE_EXPECTANCY, lifetime);
        put(CURRENT_AGE, 0);
        put(FONT_SIZE, size);
        put(CENTER_TEXT, true);

        mForeground = color;
        mBackground = ColorPalette.TRANSLUCENT_BLACK_LEVEL_3;
        mUtilityTimer = new UtilityTimer();
        mUtilityTimer.start();
        mRandom = new SplittableRandom();
    }

    public int getX() {
        return getInt(X);
    }

    public int getY() {
        return getInt(Y);
    }
    public boolean shouldCenterText() { return getBoolean(CENTER_TEXT); }

    public String getText() {
        return getString(TEXT);
    }

    public double getAge() {
        return getDouble(CURRENT_AGE);
    }

    public double getLifeExpectancy() {
        return getDouble(LIFE_EXPECTANCY);
    }

    public boolean hasPassedLifeExpectancy() {
        return getAge() > getLifeExpectancy();
    }
    public float getFontSize() { return getFloat(FONT_SIZE); }
    public double getElapsedSeconds() {
        return mUtilityTimer.getElapsedSeconds();
    }

    /**
     * Updates the position and appearance of the floating text.
     */
    public void update() {
        put(CURRENT_AGE, mUtilityTimer.getElapsedSeconds());

        // Move upwards
        put(Y, getY() - 1);

        // Gradual fade-out effect
        double elapsed = getAge();
        double lifeExpectancy = getLifeExpectancy();

        if (elapsed >= lifeExpectancy) {
            return; // Skip updates if already at life expectancy
        }

        double fadeStart = lifeExpectancy * 0.5; // Start fading after 50% of life expectancy
        if (elapsed >= fadeStart) {
            double fadeProgress = (elapsed - fadeStart) / (lifeExpectancy - fadeStart);
            int alpha = (int) (255 * (1 - fadeProgress)); // Linearly reduce alpha from 255 to 0
            alpha = Math.max(0, alpha); // Clamp to prevent negative values

            // Adjust colors with new alpha
            mForeground = new Color(mForeground.getRed(), mForeground.getGreen(), mForeground.getBlue(), alpha);
            mBackground = new Color(mBackground.getRed(), mBackground.getGreen(), mBackground.getBlue(), alpha);
        }
    }

    public Color getForeground() {
        return mForeground;
    }

    public Color getBackground() {
        return mBackground;
    }
}