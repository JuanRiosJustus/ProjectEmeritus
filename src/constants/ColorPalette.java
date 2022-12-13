package constants;

import game.stores.pools.ability.Ability;

import java.awt.Color;

public class ColorPalette {
    public static final Color BEIGE = new Color(245, 245, 220);
    public static final Color TRANSPARENT_BEIGE = new Color(245, 245, 220, 100);
    public static final Color RED = new Color(221, 107, 49);
    public static final Color TRANSPARENT_RED = new Color(221, 107, 49, 100);
    public static final Color BLUE = new Color(107, 198, 239);
    public static final Color TRANSPARENT_BLUE = new Color(107, 198, 239, 100);
    public static final Color GREEN = new Color(102, 190, 99);
    public static final Color PURPLE = new Color(122, 102, 142);
    public static final Color GOLD = new Color(205, 165, 0);
    public static final Color TRANSPARENT_GOLD = new Color(205, 165, 0, 100);
    public static final Color GREY = new Color(150, 150, 150);
    public static final Color TRANSPARENT_GREY = new Color(150, 150, 150, 100);

//    public static final Color GREY = new Color (188,189,193);


    public static final Color TRANSPARENT_BLACK = new Color(0, 0, 0, 100);

    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color TRANSPARENT_WHITE = new Color(255, 255, 255, 100);
//    public static final Color TRANSPARENT_GREY = new Color(150, 150, 150, 150);

    public static Color getColorBasedOnAbility(Ability ability) {
        Color color = ColorPalette.WHITE;

        if (ability.types.size() > 1) {
            return ColorPalette.GOLD;
        } else {
            String type = ability.types.stream().iterator().next();

            switch (type) {
                case Constants.LIGHT -> color = ColorPalette.BEIGE;
                case Constants.WATER -> color = ColorPalette.BLUE;
                case Constants.DARK -> color = ColorPalette.PURPLE;
                case Constants.FIRE -> color = ColorPalette.RED;
                case Constants.EARTH -> color = ColorPalette.GREEN;
                case Constants.NORMAL, Constants.BLUNT, Constants.PIERCE, Constants.SLASH -> color = ColorPalette.GREY;
            }

            return color;
        }
    }
}
