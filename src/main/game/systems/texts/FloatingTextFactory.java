package main.game.systems.texts;

import javafx.scene.paint.Color;
import main.game.stores.ColorPalette;

import java.util.Random;

/**
 * Factory for creating different styles of FloatingText based on game rules.
 */
public class FloatingTextFactory {

    public enum Style {
        BASIC,
        SHRINKING,
        RANDOMIZED, // Can be expanded later,
        POP_UP
    }

    private final Random mRandom = new Random();
    /**
     * Creates a FloatingText of the given style.
     *
     * @param text the message
     * @param x screen x
     * @param y screen y
     * @param style one of the enum styles
     * @return a new FloatingText
     */
    public FloatingText create(String text, int x, int y, Style style, Color color, float fontSize) {
        float variedFontSize = fontSize + mRandom.nextInt((int) (fontSize * 0.25f));
        double lifeTime = mRandom.nextDouble(2.0, 4.0);

        return switch (style) {
            case BASIC -> new FloatingText(text, variedFontSize, x, y, color, lifeTime);
            case SHRINKING -> new ShrinkingFloatingText(text, variedFontSize, x, y, color, lifeTime);
            case POP_UP -> new PopUpFloatingText(text, variedFontSize, x, y, color, lifeTime);
            case RANDOMIZED -> new ShrinkingFloatingText(text, variedFontSize * mRandom.nextFloat(0.75f, 1.25f), x, y, color, lifeTime);
        };
    }

    public FloatingText createBasic(String text, int x, int y, Color color, float fontSize) {
        return create(text, x, y, Style.BASIC, color, fontSize);
    }

    public FloatingText createShrinking(String text, int x, int y, Color color, float fontSize) {
        return create(text, x, y, Style.SHRINKING, color, fontSize);
    }

    public FloatingText createRandomized(String text, int x, int y, Color color, float fontSize) {
        return create(text, x, y, Style.RANDOMIZED, color, fontSize);
    }

    public FloatingText createPopUp(String text, int x, int y, Color color, float fontSize) {
        return create(text, x, y, Style.POP_UP, color, fontSize);
    }
}