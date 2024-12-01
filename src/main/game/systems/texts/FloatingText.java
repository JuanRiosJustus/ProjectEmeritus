package main.game.systems.texts;

import main.constants.UtilityTimer;
import main.game.stores.pools.ColorPalette;
import org.json.JSONObject;

import java.awt.Color;

public class FloatingText extends JSONObject {

    private static final String TEXT = "text";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String STATIONARY = "stationary";
    private static final String LIFE_EXPECTANCY = "life.expectancy";
    private static final String AGE = "age";

    private Color mBackground;
    private Color mForeground;
    private final UtilityTimer mUtilityTimer;

    public FloatingText(String txt, int x, int y, Color color, boolean stationary) {
        this(txt, x, y, color, stationary, 2);
    }

    public FloatingText(String txt, int x, int y, Color color, boolean isStationary, double lifetime) {
        put(TEXT, txt);
        put(X, x);
        put(Y, y);
        put(STATIONARY, isStationary);
        put(LIFE_EXPECTANCY, lifetime);
        put(AGE, 0);

        mForeground = color;
        mBackground = ColorPalette.TRANSLUCENT_BLACK_LEVEL_3;
        mUtilityTimer = new UtilityTimer();
        mUtilityTimer.start();
    }

    public int getX() {
        return getInt(X);
    }

    public int getY() {
        return getInt(Y);
    }

    public String getText() {
        return getString(TEXT);
    }

    public boolean isStationary() {
        return getBoolean(STATIONARY);
    }

    public double getAge() {
        return getDouble(AGE);
    }

    public double getLifeExpectancy() {
        return getDouble(LIFE_EXPECTANCY);
    }

    public boolean hasPassedLifeExpectancy() {
        return getAge() > getLifeExpectancy();
    }

    public double getElapsedSeconds() {
        return mUtilityTimer.getElapsedSeconds();
    }

    /**
     * Updates the position and appearance of the floating text.
     */
    public void update() {
        put(AGE, mUtilityTimer.getElapsedSeconds());

        if (isStationary()) {
            return;
        }

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