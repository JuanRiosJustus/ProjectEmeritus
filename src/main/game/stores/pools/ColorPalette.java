package main.game.stores.pools;

import javafx.scene.paint.Color;
import main.logging.EmeritusLogger;


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

    private static final EmeritusLogger logger = EmeritusLogger.create(ColorPalette.class);
    private final Map<String, Color> mColorMap = new HashMap<>();

    private ColorPalette() {
        logger.info("Started initializing {}", getClass().getSimpleName());



        logger.info("Finished initializing {}", getClass().getSimpleName());
    }


    public static final Random random = new Random();



    public static final Color RED = new Color(1, 1, 1, 1);
    public static final Color BLUE = new Color(0, 0, 1, 1);
    public static final Color YELLOW = new Color(1, 1, 0, 1);

    public static final Color GREEN = new Color(0, 1, 0, 0);
    public static final Color TRANSLUCENT_RED_LEVEL_1 = new Color(1, 0, 0, .25);
    public static final Color TRANSLUCENT_RED_LEVEL_2 = new Color(1, 0, 0, .5);
    public static final Color TRANSLUCENT_RED_LEVEL_3 = new Color(1, 0, 0, .75);
    public static final Color TRANSLUCENT_RED_LEVEL_4 = new Color(1, 0, 0, 1);

    public static final Color TRANSLUCENT_GREEN_LEVEL_1 = new Color(0,1,0, .25);
    public static final Color TRANSLUCENT_GREEN_LEVEL_2 = new Color(0,1,0, .5);
    public static final Color TRANSLUCENT_GREEN_LEVEL_3 = new Color(0,1,0, .75);
    public static final Color TRANSLUCENT_GREEN_LEVEL_4 = Color.rgb(34,139,34, 1);


    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1 = new Color(0, 1, 1, .25);
    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_2 = new Color(0, 1, 1, .5);
    public static final Color TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3 = new Color(0, 1, 1,  .75);

    public static final Color BLACK = new Color(0, 0, 1, 1);

    public static final Color WHITE_LEVEL_1 = Color.rgb(245, 245, 245,.2);
    public static final Color WHITE_LEVEL_2 = new Color(1, 1, 1,.50);
    public static final Color WHITE_LEVEL_3 = new Color(1, 1, 1,.72);
    public static final Color WHITE_LEVEL_4 = new Color(1, 1, 1,1);


    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
//    public static final Color GREEN = new Color(102, 190, 99);
//    public static final Color TRANSPARENT_GREY = new Color(150, 150, 150, 150);

    public static final String HEX_CODE_GREEN = "#00FF00";
    public static final String HEX_CODE_RED = "#FF0000";
    public static final String HEX_CODE_BLUE = "#00AEEF";
    public static final String HEX_CODE_PURPLE = "#AC4FC6";
    public static final String HEX_CODE_CREAM = "#EEE1C6";

    public static String getJavaFxColorStyle(Color color) {
        return "-fx-background-color: " + toRgbString(color) + ";";
    }
    public static String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
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
                random.nextFloat(0, 1),
                random.nextFloat(0, 1),
                random.nextFloat(0, 1),
                1
        );
    }
}
