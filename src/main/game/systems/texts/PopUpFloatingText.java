package main.game.systems.texts;

import javafx.scene.paint.Color;
import main.constants.UtilityTimer;
import main.game.stores.ColorPalette;

public class PopUpFloatingText extends FloatingText {

    private final float mStartScale = 0.2f;
    private final float mMaxScale = 1.0f;
    private final double mPopupDuration = 0.3; // Seconds to "pop"
    private final double mHoldDuration = 0.2;  // Seconds to stay after pop
    private final double mFadeDuration = 0.5;  // Seconds to fade out

    private double mScale = mStartScale;

    public PopUpFloatingText(String txt, float size, int x, int y, Color color, double totalLifetime) {
        super(txt, size, x, y, color, totalLifetime);
        mForeground = color;
        mBackground = ColorPalette.BLACK_LEVEL_4;
    }

    public float getScale() {
        return (float) mScale;
    }

    @Override
    public void update() {
        double age = mUtilityTimer.getElapsedSeconds();
        put(CURRENT_AGE, age);

        double y = getY();

        // --- POP-UP (quick upward movement + scaling)
        if (age < mPopupDuration) {
            double progress = age / mPopupDuration;
            // Fast upward movement (e.g., 20 pixels max)
            double popUpOffset = 0.05 * (1.0 - Math.pow(1.0 - progress, 2)); // ease-out
            put(Y, (int)(y - popUpOffset));

            mScale = mStartScale + progress * (mMaxScale - mStartScale);
        } else if (age < mPopupDuration + mHoldDuration) {
            // --- HOLD (stay still, full scale)
            mScale = mMaxScale;
            // Keep current Y (no change)
        } else {
            // --- FADE (slow upward float, and opacity fades)
            double fadeStart = mPopupDuration + mHoldDuration;
            double fadeProgress = (age - fadeStart) / mFadeDuration;
            fadeProgress = Math.min(1.0, fadeProgress);

            double opacity = 1.0 - fadeProgress;
            opacity = Math.max(0.0, opacity);

            mScale = mMaxScale;

            // Slow upward drift during fade
            put(Y, getY() - 1);

            mForeground = new Color(mForeground.getRed(), mForeground.getGreen(), mForeground.getBlue(), opacity);
            mBackground = new Color(mBackground.getRed(), mBackground.getGreen(), mBackground.getBlue(), opacity);
        }
    }
//    @Override
//    public void update() {
//        double age = mUtilityTimer.getElapsedSeconds();
//        put(CURRENT_AGE, age);
//
//        // Position stays mostly constant or rises slightly
//        if (age > mPopupDuration + mHoldDuration) {
//            put(Y, getY() - 1);
//        }
//
//        // POP-UP: Scale increases quickly
//        if (age < mPopupDuration) {
//            double progress = age / mPopupDuration;
//            mScale = mStartScale + progress * (mMaxScale - mStartScale);
//        } else if (age < mPopupDuration + mHoldDuration) {
//            // HOLD
//            mScale = mMaxScale;
//        } else {
//            // FADE
//            double fadeStart = mPopupDuration + mHoldDuration;
//            double fadeProgress = (age - fadeStart) / mFadeDuration;
//            fadeProgress = Math.min(1.0, fadeProgress);
//
//            double opacity = 1.0 - fadeProgress;
//            opacity = Math.max(0.0, Math.min(1.0, opacity));
//
//            mScale = mMaxScale; // hold scale
//
//            mForeground = new Color(mForeground.getRed(), mForeground.getGreen(), mForeground.getBlue(), opacity);
//            mBackground = new Color(mBackground.getRed(), mBackground.getGreen(), mBackground.getBlue(), opacity);
//        }
//    }
}