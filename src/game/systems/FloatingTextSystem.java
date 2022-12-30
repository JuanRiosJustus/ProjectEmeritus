package game.systems;

import constants.Constants;
import game.GameModel;
import game.camera.Camera;
import game.components.Vector;
import game.entity.Entity;
import game.stores.pools.FontPool;
import game.systems.texts.FloatingDialogue;
import game.systems.texts.FloatingScalar;
import game.systems.texts.FloatingText;
import input.InputController;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class FloatingTextSystem extends GameSystem {

    private final int buffer = 40;
    private final Canvas fontCalculator = new Canvas();
    private final Set<FloatingText> texts = new HashSet<>();
    private final Font font = FontPool.instance().getFont(18);
    private final Queue<FloatingText> garbageCollector = new LinkedList<>();
    private final Rectangle temporary = new Rectangle();

    public void dialogue(String text, Vector vector, Color color) {

        FontMetrics metrics = fontCalculator.getFontMetrics(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y + 5;

        // Check for collisions, update until none
        texts.add(new FloatingDialogue(text, x, y, width, height, color));
    }

    public void floater(String text, Vector vector, Color color) {
        FontMetrics metrics = fontCalculator.getFontMetrics(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y;

        // Check for collisions, update until none
        boolean checkForCollision = true;
        while (checkForCollision) {
            checkForCollision = false;
            for (FloatingText textToCheck : texts) {
                temporary.setBounds(x - buffer, y - buffer, width + (buffer * 2), height + (buffer * 2));
                boolean intersects = temporary.intersects(textToCheck.boundary);
                if (intersects && textToCheck instanceof FloatingScalar) {
                    textToCheck.boundary.y -= textToCheck.boundary.height;
                    textToCheck.endY -= textToCheck.boundary.height;
                    checkForCollision = true;
                }
            }
        }
        texts.add(new FloatingScalar(text, x, y, width, height, color));
    }

    // TODO Don't draw the floating text offscreen
    public void render(Graphics g) {
        g.setFont(font);
        for (FloatingText floatingText : texts) {
            int x = floatingText.boundary.x + Constants.CURRENT_SPRITE_SIZE;
            x = Camera.get().globalX(x);
            int y = floatingText.boundary.y - (floatingText.boundary.height / 2) - 5;
            y = Camera.get().globalY(y);

            int width = floatingText.boundary.width + 10;
            int height = floatingText.boundary.height + 3;
            g.setColor(floatingText.background);
            g.fillRoundRect(
                    x - 5, y - (floatingText.boundary.height / 2) - 5,
                    width, height,5, 5
            );
            g.setColor(floatingText.foreground);
            g.drawRoundRect(
                    x - 5, y - (floatingText.boundary.height / 2) - 5,
                    width, height,10, 10
            );
            g.drawString(floatingText.text, x, y + 4);
        }
    }

    @Override
    public void update(GameModel model, Entity unit) {
        // check for floating text that have floated enough
        for (FloatingText floatingText : texts) {
            floatingText.update();
            if (floatingText.canRemove()) {
                garbageCollector.add(floatingText);
            }
        }

        // remove the floating text that have been collected
        while (garbageCollector.size() > 0) {
            texts.remove(garbageCollector.poll());
        }
    }
}