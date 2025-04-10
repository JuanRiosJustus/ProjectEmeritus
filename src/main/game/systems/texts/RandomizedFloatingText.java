package main.game.systems.texts;


import javafx.scene.paint.Color;

public class RandomizedFloatingText extends FloatingText {

    public RandomizedFloatingText(String txt, float size, int x, int y, Color color, double lifetime) {
        super(txt, size, x, y, color, lifetime);

        int randomBounds = (int) (size * .5);
        put(X, mRandom.nextInt(x - randomBounds, x + randomBounds));
        put(Y, mRandom.nextInt(y - randomBounds, y + randomBounds));
    }
}