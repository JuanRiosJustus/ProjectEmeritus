package main.game.stores.pools;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ColorPalette {

    private static ColorPalette mInstance = null;
    public static ColorPalette getInstance() {
        if (mInstance == null) {
            mInstance = new ColorPalette();
        }
        return mInstance;
    }

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(ColorPalette.class);
    private final Map<String, Color> mColorMap = new HashMap<>();

    private ColorPalette() {
        logger.info("Started initializing {}", getClass().getSimpleName());



        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public static final Color LIGHT_TYPE = new Color(255,240,219);
    public static final Color WATER_TYPE = new Color(200, 200, 255);
    public static final Color DARK_TYPE = new Color(173, 158, 170);
    public static final Color FIRE_TYPE = new Color(255, 200, 200);
    public static final Color NATURE_TYPE = new Color(200, 255, 200);
    public static final Color NORMAL_TYPE = new Color(150, 150, 150);

    public Color get(String color) {
        Color c = mColorMap.get(color.toLowerCase());
        if (c != null) {
            return c;
        }

        c = Color.getColor(color);
        if (c != null) {
            mColorMap.put(color, c);
            return c;
        }

        try {
            c = Color.decode(color);
            mColorMap.put(color, c);
            return c;
        } catch (Exception ex) {
            logger.error("Unknown color");
        }
        return null;
    }


    public static final Random random = new Random();


    public static final Color BEIGE = new Color(245, 245, 220);
    public static final Color DARK_BEIGE = new Color(245, 245, 220, 150);
    public static final Color DARKEST_BEIGE = new Color(245, 245, 220, 50);
    public static final Color TRANSPARENT_BEIGE = new Color(245, 245, 220, 100);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color TRANSPARENT_RED = new Color(255, 100, 100, 100);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color TRANSPARENT_BABY_BLUE = new Color(200, 200, 255, 155);
    public static final Color TILES_FOR_MOVEMENT_SELECTION_IN_RANGE = new Color(50, 155, 50, 150);
    public static final Color TILES_FOR_MOVEMENT_SELECTION_IN_RANGE_PRIME = new Color(50, 255, 50, 150);
    public static final Color TILES_FOR_MOVEMENT_SELECTION_IN_PATH = new Color(155, 155, 155, 250);
    public static final Color TILES_FOR_MOVEMENT_SELECTION_IN_PATH_PRIME = new Color(255, 255, 255, 250);
    public static final Color TRANSLUCENT_YELLOW_V1 = new Color(253, 218, 13, 100);
    public static final Color TRANSLUCENT_YELLOW_V2 = new Color(253, 218, 13, 200);
    public static final Color TRANSLUCENT_RED_V1 = new Color(200, 0, 0, 100);
    public static final Color TRANSLUCENT_RED_V2 = new Color(200, 0, 0, 200);
    public static final Color PURPLE = new Color(122, 102, 142);
    public static final Color GOLD = new Color(205, 165, 0);
    public static final Color TRANSPARENT_GOLD = new Color(205, 165, 0, 100);
    public static final Color TRANSPARENT_GREY = new Color(150, 150, 150, 100);
    public static final Color TRANSPARENT_DARK_GREY = new Color(50, 50, 50, 100);

//    public static final Color GREY = new Color (188,189,193);

    public static final Color DARK_GREEN_V1 = new Color(20, 100, 20);
    public static final Color DARK_RED_V1 = new Color(100, 20, 20);


    public static final Color TRANSLUCENT_GREEN_V1 = new Color(0, 200, 0, 64);
    public static final Color TRANSLUCENT_GREEN_V2 = new Color(0, 200, 0, 128);
    public static final Color TRANSLUCENT_GREEN_V3 = new Color(0, 200, 0, 192);
    public static final Color GREEN = new Color(0, 200, 0);




    public static final Color LIGHT_COLOR_SCHEME = new Color(255,240,219,255);

    public static final Color TRANSLUCENT_SUNSET_ORANGE = new Color(250, 95, 85, 255);

    public static final Color TRANSLUCENT_BLACK_LEVEL_1 = new Color(0, 0, 0, 64);
    public static final Color TRANSLUCENT_BLACK_LEVEL_2 = new Color(0, 0, 0, 128);
    public static final Color TRANSLUCENT_BLACK_LEVEL_3 = new Color(0, 0, 0, 192);
    public static final Color TRANSLUCENT_BLACK_LEVEL_4 = new Color(0, 0, 0, 255);

    public static final Color TRANSLUCENT_WHITE_LEVEL_1 = new Color(255, 255, 255, 64);
    public static final Color TRANSLUCENT_WHITE_LEVEL_2 = new Color(255, 255, 255,128);
    public static final Color TRANSLUCENT_WHITE_LEVEL_3 = new Color(255, 255, 255, 192);
    public static final Color TRANSLUCENT_WHITE_LEVEL_4 = new Color(255, 255, 255, 255);

    public static final Color TRANSLUCENT_PURPLE_LEVEL_1 = new Color(128, 0, 128, 64);
    public static final Color TRANSLUCENT_PURPLE_LEVEL_2 = new Color(128, 0, 128, 128);
    public static final Color TRANSLUCENT_PURPLE_LEVEL_3 = new Color(128, 0, 128, 192);
    public static final Color TRANSLUCENT_PURPLE_LEVEL_4 = new Color(128, 0, 128, 255);

    public static final Color TRANSLUCENT_RED_LEVEL_1 = new Color(255, 0, 0, 64);
    public static final Color TRANSLUCENT_RED_LEVEL_2 = new Color(255, 0, 0, 128);
    public static final Color TRANSLUCENT_RED_LEVEL_3 = new Color(255, 0, 0, 192);
    public static final Color TRANSLUCENT_RED_LEVEL_4 = new Color(255, 0, 0, 255);

    public static final Color TRANSLUCENT_GREEN_LEVEL_1 = new Color(0,255,0, 64);
    public static final Color TRANSLUCENT_GREEN_LEVEL_2 = new Color(0,255,0, 128);
    public static final Color TRANSLUCENT_GREEN_LEVEL_3 = new Color(0,255,0, 192);
    public static final Color TRANSLUCENT_GREEN_LEVEL_4 = new Color(0,255,0, 255);

    public static final Color TRANSLUCENT_FOREST_GREEN_LEVEL_1 = new Color(34,139,34, 64);
    public static final Color TRANSLUCENT_FOREST_GREEN_LEVEL_2 = new Color(34,139,34, 128);
    public static final Color TRANSLUCENT_FOREST_GREEN_LEVEL_3 = new Color(34,139,34, 192);
    public static final Color TRANSLUCENT_FOREST_GREEN_LEVEL_4 = new Color(34,139,34, 255);


    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1 = new Color(0, 191, 255, 64);
    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_2 = new Color(0, 191, 255, 128);
    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3 = new Color(0, 191, 255,  192);
    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_4 = new Color(0, 191, 255, 255);

    public static final Color TRANSLUCENT_AMBER_LEVEL_1 = new Color(255, 191, 0, 64);
    public static final Color TRANSLUCENT_AMBER_LEVEL_2 = new Color(255, 191, 0, 128);
    public static final Color TRANSLUCENT_AMBER_LEVEL_3 = new Color(255, 191, 0, 192);
    public static final Color TRANSLUCENT_AMBER_LEVEL_4 = new Color(255, 191, 0, 255);

    public static final Color TRANSLUCENT_BLUE_LEVEL_1 = new Color(0, 0, 255, 64);
    public static final Color TRANSLUCENT_BLUE_LEVEL_2 = new Color(0, 0, 255, 128);
    public static final Color TRANSLUCENT_BLUE_LEVEL_3 = new Color(0, 0, 255,  192);
    public static final Color TRANSLUCENT_BLUE_LEVEL_4 = new Color(0, 0, 255, 255);





    public static final Color BLACK = new Color(0, 0, 0, 255);
    public static final Color CONTROLLER_BUTTON_HIGHLIGHT = new Color(255, 255, 255,60);
    public static final Color CONTROLLER_BUTTON_HIGHLIGHT_V2 = new Color(255, 255, 255,200);

    public static final Color WHITE = new Color(255, 255, 255,255);


    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
//    public static final Color GREEN = new Color(102, 190, 99);
//    public static final Color TRANSPARENT_GREY = new Color(150, 150, 150, 150);

    public static final String HEX_CODE_GREEN = "#00FF00";
    public static final String HEX_CODE_RED = "#FF0000";
    public static final String HEX_CODE_BLUE = "#00AEEF";
    public static final String HEX_CODE_PURPLE = "#AC4FC6";
    public static final String HEX_CODE_CREAM = "#EEE1C6";

    public static String getHtmlColor(String text, String colorHexCode) {
//        return "<font color=\"" + colorHexCode+  "\">" + text + "</font>";
        return text;
    }
    public static String getHtmlColor(int num, String colorHexCode) {
        return getHtmlColor(String.valueOf(num), colorHexCode);
    }

//    public static Color getColorOfAbility(CsvRow action) {
//        Color color = ColorPalette.GOLD;
//
//        List<String> types = action.getList(ActionPool.TYPE_COLUMN);
//        if (types.size() > 1) {
//            return ColorPalette.GOLD;
//        } else {
//            String type = types
//                    .iterator()
//                    .next();
//
//            switch (type) {
//                case Constants.LIGHT -> color = ColorPalette.LIGHT_TYPE;
//                case Constants.WATER -> color = ColorPalette.WATER_TYPE;
//                case Constants.DARK -> color = ColorPalette.DARK_TYPE;
//                case Constants.FIRE -> color = ColorPalette.FIRE_TYPE;
//                case Constants.GROUND -> color = ColorPalette.NATURE_TYPE;
//                default -> color = ColorPalette.NORMAL_TYPE; // case Constants.NORMAL, Constants.BLUNT, Constants.PIERCE, Constants.SLASH ->
//            }
//
//            return color;
//        }
//    }

    public static Color getRandomColor() { return getRandomColor(255); }
    public static Color getRandomColor(int alpha) {
        return new Color(
                random.nextInt(0, 255),
                random.nextInt(0, 255),
                random.nextInt(0, 255),
                alpha
        );
    }
}
