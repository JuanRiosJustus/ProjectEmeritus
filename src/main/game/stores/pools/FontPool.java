package main.game.stores.pools;

import javafx.scene.text.Font;
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

    public Font getFontForHeight(int height) {
        return getFont((int) (height * .6));
    }

    public Font getFont(double size) {
        Font toUse = mCache.get(size);
        if (toUse != null) {
            return toUse;
        } else {
            Font newFont = new Font(mfont.getName(), size);
            mCache.put(size, newFont);
            toUse = newFont;
        }
        return toUse;
    }

    public Font getBoldFont(double size) {
        Font toUse = mCache.get(size);
        if (toUse != null) {
            return toUse;
        } else {
            Font newFont = new Font(mDefaultFont.getName(), size);
            mCache.put(size, newFont);
            toUse = newFont;
        }
        return toUse;
    }

    public Font getDefaultFont() { return mDefaultFont; }
}
