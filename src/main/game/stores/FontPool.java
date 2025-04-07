package main.game.stores;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.constants.Constants;
import main.logging.EmeritusLogger;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontPool {
    private Font mfont;
    private final Font mDefaultFont;
    private final Map<Double, Font> mCache = new HashMap<>();
    private static FontPool mInstance = null;
    public static FontPool getInstance() {
        if (mInstance == null) {
            mInstance = new FontPool();
        }
        return mInstance;
    }

    private FontPool() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());
        mDefaultFont = Font.getDefault();
        try {
            mfont = Font.loadFont(Objects.requireNonNull(getClass().getResource(Constants.FONT_CLASSPATH)).toExternalForm(), 12);
            if (mfont == null) {
                logger.info("⚠️ Font failed to load! Using default font.");
                mfont = mDefaultFont;
            } else {
                logger.info("✅ Successfully loaded font: " + mfont.getName());
            }
        } catch (Exception e) {
            logger.error("❌ Exception loading font: " + e.getMessage());
            mfont = mDefaultFont;
        }
    }

    public Font getFontForHeight(int height) { return getFont((int) (height * .6)); }
    public Font getBoldFontForHeight(int height) { return getFont((int) (height * .6), false); }
    public Font getFont(double size) { return getFont(size, false); }

    public Font getFont(double size, boolean useBold) {
        Font toUse = mCache.get(size);
        if (toUse != null) {
            return toUse;
        } else {
            Font newFont = !useBold ? Font.font(mfont.getName(), size) :
                    Font.font(mDefaultFont.getName(), FontWeight.BOLD, size);
            mCache.put(size, newFont);
            toUse = newFont;
        }
        return toUse;
    }
}
