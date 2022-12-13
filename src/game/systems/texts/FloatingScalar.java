package game.systems.texts;

import game.systems.texts.FloatingText;

import java.awt.Color;

public class FloatingScalar extends FloatingText {

    public FloatingScalar(String value, int x, int y, int width, int height, Color color) {
        super(value, x, y, width, height, color);
    }

    @Override
    public boolean canRemove() {
        return boundary.y <= endY;
    }

    @Override
    public void update() { boundary.y -= 1; }
}
