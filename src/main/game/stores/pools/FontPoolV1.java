package main.game.stores.pools;

import main.constants.Constants;
import main.logging.EmeritusLogger;


import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FontPoolV1 {
    private Font mfont;
    private final Font mDefaultFont;
    private final Map<Float, Font> mCache = new HashMap<>();
    private static FontPoolV1 mInstance = null;
    public static FontPoolV1 getInstance() {
        if (mInstance == null) {
            mInstance = new FontPoolV1();
        }
        return mInstance;
    }

    private FontPoolV1() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
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
        mCache.put(mfont.getSize2D(), mfont);
    }

    public Font getFontForHeight(int height) {
        return getFont((int) (height * .7));
    }

    public Font getBoldFontForHeight(int height) {
        return getFont((int) (height * .7));
    }

    public Font getFont(float size) {
        Font toUse = mCache.get(size);
        if (toUse != null) {
            return toUse;
        } else {
            Font newFont = mfont.deriveFont(size);
            mCache.put(size, newFont);
            toUse = newFont;
        }
        return toUse;
    }

    public Font getBoldFont(float size) {
        Font toUse = mCache.get(size);
        if (toUse != null) {
            return toUse;
        } else {
            Font newFont = mfont.deriveFont(size).deriveFont(Font.BOLD);
            mCache.put(size, newFont);
            toUse = newFont;
        }
        return toUse;
    }

    public Font getDefaultFont() { return mDefaultFont; }
}
