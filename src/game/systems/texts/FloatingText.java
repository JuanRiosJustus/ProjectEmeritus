package game.systems.texts;

import constants.Constants;
import game.components.SecondTimer;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.SplittableRandom;

public abstract class FloatingText {


    protected static final SplittableRandom random = new SplittableRandom();

    public final String text;
    public Color foreground;
    public Color background;
    public int endY;
    public final Rectangle boundary;
    public final SecondTimer timer;

    public FloatingText(String value, int x, int y, int width, int height, Color color) {
        text = value;
        foreground = color;
        background = Color.BLACK;
        x -= Constants.CURRENT_SPRITE_SIZE;
        endY = y - (Constants.CURRENT_SPRITE_SIZE * 2); // two tiles
        boundary = new Rectangle(x, y, width, height);
        timer = new SecondTimer();
    }

//    public boolean canRemove() { return boundary.y <= endY; }
//    public boolean canRemove() { return timer.elapsed() >= 1; },''
    public abstract boolean canRemove();
    public abstract void update();

//    public void update() {
//        if (!still) {
//            boundary.y -= random.nextInt(0, 2);
//        }
//
////        if (boundary.y - 10 <= endY) {
//        if (boundary.y - 20 <= endY) {
////            int alpha = foreground.getAlpha();
////            foreground = new Color(
////                    foreground.getRed(),
////                    foreground.getGreen(),
////                    foreground.getBlue(),
////                    (Math.max(alpha - 20, 0))
////            );
////            background = new Color(
////                    background.getRed(),
////                    background.getGreen(),
////                    background.getBlue(),
////                    (Math.max(alpha - 20, 0))
////            );
//        }

}
