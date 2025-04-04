package main.game.systems.texts;

import main.constants.UtilityTimer;
import main.game.stores.pools.ColorPaletteV1;

import java.awt.Color;

/**
 * A variant of FloatingText that grows in font size but
 * shrinks in overall bounding "scale," leading to a
 * stylized effect.
 */
public class ShrinkingFloatingText extends FloatingText {

    // Additional fields for our "shrinking/growing" effect
    private final float mInitialFontSize;
    private final float mMaxFontSize;      // The font size we want to reach (or shrink from)
    private final float mInitialScale;
    private final float mMinScale;         // The minimal bounding scale you want


    public ShrinkingFloatingText(String txt, float size, int x, int y, Color color, double lifetime) {
        super(txt, size, x, y, color, lifetime);

        // TODO this class needs TLC

        // Inherit the typical color logic
        mForeground = color;
        mBackground = ColorPaletteV1.TRANSLUCENT_BLACK_LEVEL_3;

        // Start the timer
        mUtilityTimer = new UtilityTimer();
        mUtilityTimer.start();

        // Example “start bigger or smaller” approach
        mInitialFontSize = size;  // Starting font size
        mMaxFontSize     = mInitialFontSize * 1.5f;  // The largest the font will get

        // Example bounding scale: start fully sized at 1.0, shrink to anywhwere between .25 - .75 over time
        mInitialScale    = 1.0f;
        mMinScale        = mRandom.nextFloat(0.25f, 0.75f); // shrinks 50% in bounding box
    }

    // Accessors
    public int getX() { return getInt(X); }
    public int getY() { return getInt(Y); }
    public String getText() { return getString(TEXT); }
    public double getAge() { return getDouble(CURRENT_AGE); }
    public double getLifeExpectancy() { return getDouble(LIFE_EXPECTANCY); }

    public boolean hasPassedLifeExpectancy() {
        return getAge() > getLifeExpectancy();
    }

    public double getElapsedSeconds() {
        return mUtilityTimer.getElapsedSeconds();
    }

    /**
     * Dynamically compute the current font size based on how far we are along in life.
     */
    public float getFontSize() {
        double age = getAge();
        double maxAge = getLifeExpectancy();
        double fraction = Math.min(1.0, age / maxAge);

        // Apply a logarithmic function to slow down shrinking
        double logBase = 10; // Adjust the base of the logarithm for fine-tuning
        fraction = Math.log1p(fraction * (logBase - 1)) / Math.log(logBase); // Logarithmic scaling

        // Interpolate from mMaxFontSize => mInitialFontSize (shrinking effect)
        return mMaxFontSize - (float) fraction * (mMaxFontSize - mInitialFontSize);
    }

    /**
     * Dynamically compute a bounding scale that decreases over time.
     */
    public float getCurrentScale() {
        double age = getAge();
        double maxAge = getLifeExpectancy();
        double fraction = Math.min(1.0, age / maxAge);

        // Interpolate from mInitialScale => mMinScale
        float currentScale = mInitialScale
                - (float) fraction * (mInitialScale - mMinScale);
        return currentScale;
    }
    /**
     * For convenience, if your renderer wants to also do an alpha fade,
     * we can store that in mForeground, mBackground.
     */
    public Color getForeground() {
        return mForeground;
    }

    public Color getBackground() {
        return mBackground;
    }

    /**
     * Called each frame to update position, fade out, and
     * the special size/growth effect.
     */
    public void update() {
        put(CURRENT_AGE, mUtilityTimer.getElapsedSeconds());

        // Example: Move upwards each frame
        put(Y, getY() - 1);

        // Typical fade logic
        double elapsed         = getAge();
        double lifeExpectancy  = getLifeExpectancy();
        if (elapsed >= lifeExpectancy) {
            return; // We skip further updates if at end-of-life
        }

        double fadeStart       = lifeExpectancy * 0.5; // fade begins halfway
        if (elapsed >= fadeStart) {
            double fadeProgress = (elapsed - fadeStart) / (lifeExpectancy - fadeStart);
            int alpha = (int) (255 * (1 - fadeProgress));
            alpha = Math.max(0, alpha);

            mForeground = new Color(
                    mForeground.getRed(),
                    mForeground.getGreen(),
                    mForeground.getBlue(),
                    alpha
            );
            mBackground = new Color(
                    mBackground.getRed(),
                    mBackground.getGreen(),
                    mBackground.getBlue(),
                    alpha
            );
        }

        // We do NOT directly do the "shrinking" or "font size changing" in code here,
        // because it's easier to compute it on-the-fly in the renderer (the text doesn't
        // physically change the "X, Y" bounding box – or at least doesn't need to).
        // But if you do want to “shrink bounding box,” you can do something like:
        // put(X, getX() + 1) to horizontally compress, or store a bounding rectangle.
    }
}