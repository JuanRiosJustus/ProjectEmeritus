package main.game.systems.texts;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.constants.Settings;
import main.game.components.SecondTimer;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.SplittableRandom;

public class FloatingText {

    protected static final SplittableRandom random = new SplittableRandom();

    public final String text;
    public Color foreground;
    public Color background;
    public int endY;
    public final Rectangle boundary;
    public final SecondTimer timer;
    public final boolean stationary;
    public final double whenToRemove;
    

    public FloatingText(String value, int x, int y, int width, int height, Color color) {
        this(value, x, y, width, height, color, true);
    }

    public FloatingText(String value, int x, int y, int width, int height, Color color, boolean isStationary) {
        text = value;
        foreground = color;
        background = ColorPalette.TRANSLUCENT_BLACK_V3;
        float spriteSize = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
        x -= (int) spriteSize;
        endY = (int) (y - (spriteSize * 2)); // two tiles
        boundary = new Rectangle(x, y, width, height);
        timer = new SecondTimer();
        stationary = isStationary;
        whenToRemove = 1 + random.nextDouble(0, 2);
    }

    public boolean canRemove() { return timer.elapsed() >= whenToRemove; }
    public void update() {  if (!stationary) { boundary.y -= 1; } }
}
