package game.systems.texts;

import game.systems.texts.FloatingText;

import java.awt.Color;

public class FloatingDialogue extends FloatingText {

    public FloatingDialogue(String value, int x, int y, int width, int height, Color color) {
        super(value, x, y, width, height, color);
    }

    public boolean canRemove() { return timer.elapsed() >= 1; }

    @Override
    public void update() { }
}
