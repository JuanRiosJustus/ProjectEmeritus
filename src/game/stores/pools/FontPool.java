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
    private static FontPool instance = null;
    public static FontPool instance() {
        if (instance == null) {
            instance = new FontPool();
        }
        return instance;
    }

    private FontPool() {
        Logger logger = LoggerFactory.instance().logger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            File f = new File(Constants.FONT_FILEPATH);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(48f);
//            Font customFont = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            font = customFont;
            logger.info("Finished initializing {}", getClass().getSimpleName());
        } catch (Exception e) {
            font = new Font(Font.MONOSPACED, Font.PLAIN, 48);
            logger.error("Failed initializing {} because {}", getClass().getSimpleName(), e.getMessage());
        }
        map.put(font.getSize(), font);
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
