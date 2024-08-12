package main.game.stores.pools;

import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FontPool {
    private Font mfont;
    private Font mDefaultFont;
    private final Map<Integer, Font> mCache = new HashMap<>();
    private static FontPool mInstance = null;
    public static FontPool getInstance() {
        if (mInstance == null) {
            mInstance = new FontPool();
        }
        return mInstance;
    }

    private FontPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        mDefaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
        try {
            File f = new File(Constants.FONT_FILEPATH);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(48f);
//            Font customFont = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            mfont = customFont;
            logger.info("Finished initializing {}", getClass().getSimpleName());
        } catch (Exception e) {
            mfont = mDefaultFont;
            logger.error("Failed initializing {} because {}", getClass().getSimpleName(), e.getMessage());
        }
        mCache.put(mfont.getSize(), mfont);
    }

    public Font getFontForHeight(int height) {
        return getFont((int) (height * .5));
    }

    public Font getFont(int size) {
        Font toUse = mCache.get(size);
        if (toUse != null) { return toUse; }
        float newSize = (float) size;
        Font newFont = mfont.deriveFont(newSize);
        mCache.put(size, newFont);
        return newFont;
    }
    public Font getBoldFont(int size) {
        Font toUse = mCache.get(size);
        if (toUse != null) { return toUse; }
        float newSize = (float) size;
        Font newFont = mfont.deriveFont(newSize).deriveFont(Font.BOLD);
        mCache.put(size, newFont);
        return newFont;
    }

    public Font getDefaultFont() { return mDefaultFont; }
}
