package main.constants;

import main.game.stores.pools.ability.Ability;

import java.awt.Color;
import java.util.Random;

public class ColorPalette {

    public static final Random random = new Random();
    
    public static final Color LIGHT_TYPE = new Color(225, 198, 153);
    public static final Color AIR_TYPE = new Color(245, 245, 171);
    public static final Color WATER_TYPE = new Color(165, 206, 255);
    public static final Color DARK_TYPE = new Color(201, 201, 201);
    public static final Color FIRE_TYPE = new Color(255, 174, 173);
    public static final Color EARTH_TYPE = new Color(179, 255, 172);
    
    
    public static final Color BEIGE = new Color(245, 245, 220);
    public static final Color TRANSPARENT_BEIGE = new Color(245, 245, 220, 100);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color TRANSPARENT_RED = new Color(221, 107, 49, 100);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color TRANSPARENT_BLUE = new Color(107, 198, 239, 100);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color TRANSLUCENT_GREEN_V1 = new Color(0, 200, 0, 100);
    public static final Color TRANSLUCENT_GREEN_V2 = new Color(0, 200, 0, 100);
    public static final Color TRANSLUCENT_YELLOW_V1 = new Color(253, 218, 13, 100);
    public static final Color TRANSLUCENT_YELLOW_V2 = new Color(253, 218, 13, 200);
    public static final Color TRANSLUCENT_GREY_V1 = new Color(200, 200, 200, 100);
    public static final Color TRANSLUCENT_GREY_V2 = new Color(200, 200, 200, 200);
    public static final Color TRANSLUCENT_RED_V1 = new Color(200, 0, 0, 100);
    public static final Color TRANSLUCENT_RED_V2 = new Color(200, 0, 0, 200);
    public static final Color PURPLE = new Color(122, 102, 142);
    public static final Color TRANSPARENT_PURPLE = new Color(122, 102, 142, 100);
    public static final Color GOLD = new Color(205, 165, 0);
    public static final Color TRANSPARENT_GOLD = new Color(205, 165, 0, 100);
    public static final Color GREY = new Color(150, 150, 150);
    public static final Color TRANSPARENT_GREY = new Color(150, 150, 150, 100);

//    public static final Color GREY = new Color (188,189,193);


    public static final Color TRANSLUCENT_BLACK_V1 = new Color(0, 0, 0, 64);
    public static final Color TRANSLUCENT_BLACK_V2 = new Color(0, 0, 0, 128);
    public static final Color TRANSLUCENT_BLACK_V3 = new Color(0, 0, 0, 192);
    public static final Color BLACK = new Color(0, 0, 0, 255);

    public static final Color TRANSLUCENT_WHITE_V1 = new Color(255, 255, 255,64);
    public static final Color TRANSLUCENT_WHITE_V2 = new Color(255, 255, 255,128);
    public static final Color TRANSLUCENT_WHITE_V3 = new Color(255, 255, 255,192);
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
        return "<font color=\"" + colorHexCode+  "\">" + text + "</font>";
    }
    public static String getHtmlColor(int num, String colorHexCode) {
        return getHtmlColor(String.valueOf(num), colorHexCode);
    }

    public static Color getColorOfAbility(Ability ability) {
        Color color = ColorPalette.GOLD;

        if (ability.getTypes().size() > 1) {
            return ColorPalette.GOLD;
        } else {
            String type = ability.getTypes()
                    .iterator()
                    .next();

            switch (type) {
                case Constants.LIGHT -> color = ColorPalette.LIGHT_TYPE;
                case Constants.AIR -> color = ColorPalette.AIR_TYPE;
                case Constants.WATER -> color = ColorPalette.WATER_TYPE;
                case Constants.DARK -> color = ColorPalette.DARK_TYPE;
                case Constants.FIRE -> color = ColorPalette.FIRE_TYPE;
                case Constants.EARTH -> color = ColorPalette.EARTH_TYPE;
                default -> color = ColorPalette.GREY; // case Constants.NORMAL, Constants.BLUNT, Constants.PIERCE, Constants.SLASH -> 
            }

            return color;
        }
    }

    public static Color getRandomColor() {
        return new Color(
                random.nextInt(0, 255), 
                random.nextInt(0, 255), 
                random.nextInt(0, 255)
        );
    }
}
