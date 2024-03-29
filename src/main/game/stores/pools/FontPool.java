package main.game.stores.pools;

import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FontPool {
    private Font font;
    private final Map<Integer, Font> cache = new HashMap<>();
    private static FontPool instance = null;
    public static FontPool getInstance() {
        if (instance == null) {
            instance = new FontPool();
        }
        return instance;
    }

    private FontPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
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
        cache.put(font.getSize(), font);
    }

    public Font getFont(int size) {
        Font toUse = cache.get(size);
        if (toUse != null) { return toUse; }
        float newSize = (float) size;
        Font newFont = font.deriveFont(newSize);
        cache.put(size, newFont);
        return newFont;
    }
    public Font getBoldFont(int size) {
        Font toUse = cache.get(size);
        if (toUse != null) { return toUse; }
        float newSize = (float) size;
        Font newFont = font.deriveFont(newSize).deriveFont(Font.BOLD);
        cache.put(size, newFont);
        return newFont;
    }
}
