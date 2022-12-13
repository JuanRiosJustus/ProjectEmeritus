package game.stores.pools;

import constants.Constants;
import logging.Logger;
import logging.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FontPool {

    private Font font;
    private final Map<Integer, Font> map = new HashMap<>();
    private static final FontPool instance = new FontPool();
    public static FontPool instance() { return instance; }

    private FontPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.banner("Started initializing " + getClass().getSimpleName());
        try {
            File f = new File(Constants.FONT_FILEPATH);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(48f);
//            Font customFont = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            font = customFont;
            logger.log("Fonts successfully loaded.");
        } catch (Exception e) {
            font = new Font(Font.MONOSPACED, Font.PLAIN, 48);
            logger.log("Fonts failed to load " + e.getMessage());
        }
        map.put(font.getSize(), font);
        logger.banner("Finished initializing " + getClass().getSimpleName());
    }

    public Font getFont(int size) {
        Font toUse = map.get(size);
        if (toUse != null) { return toUse; }
        float newSize = (float) size;
        Font newFont = font.deriveFont(newSize);
        map.put(size, newFont);
        return newFont;
    }
}
