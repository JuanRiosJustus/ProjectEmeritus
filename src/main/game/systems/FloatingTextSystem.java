package main.game.systems;

import main.constants.Constants;
import main.game.camera.Camera;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.game.systems.texts.FloatingText;

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

    private final int buffer = 10;
    private final Canvas fontCalculator = new Canvas();
    private final Set<FloatingText> texts = new HashSet<>();
    private final Font font = FontPool.getInstance().getFont(18);
    private final Queue<FloatingText> garbageCollector = new LinkedList<>();
    private final Rectangle temporary = new Rectangle();

    public void stationary(String text, Vector vector, Color color) {
        FontMetrics metrics = fontCalculator.getFontMetrics(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y + 5;

        // Check for collisions, update until none
        texts.add(new FloatingText(text, x, y, width, height, color, true));
    }

    public void floater(String text, Vector vector, Color color) {
        FontMetrics metrics = fontCalculator.getFontMetrics(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y;

                
        temporary.setBounds(x - buffer, y - buffer, width + (buffer), height + (buffer));
        // Check for collisions, update until none
        boolean checkForCollision = true;
        while (checkForCollision) {
            checkForCollision = false;
            for (FloatingText textToCheck : texts) {
                if (textToCheck.stationary) { continue; }
                int toCheckTopY = textToCheck.boundary.y;
                int toCheckBottomY = textToCheck.boundary.y + textToCheck.boundary.height;
                boolean hasOverlapOnYAxis = y >= toCheckTopY && y <= toCheckBottomY;

                // Move every text that needs to be printed print
                if (!hasOverlapOnYAxis) { continue; }

                for (FloatingText toMove : texts) {
                    if (toMove.stationary) { continue; }
                    toMove.endY -= 3;
                    toMove.boundary.y -= 3;
                }
                checkForCollision = true;
            }
        }
        texts.add(new FloatingText(text, x, y, width, height, color, false));
    }

    // TODO Don't draw the floating text offscreen
    public void render(Graphics g) {
        g.setFont(font);
        for (FloatingText floatingText : texts) {
            int x = floatingText.boundary.x + Constants.CURRENT_SPRITE_SIZE;
            x = Camera.getInstance().globalX(x);
            int y = floatingText.boundary.y - (floatingText.boundary.height / 2) - 5;
            y = Camera.getInstance().globalY(y);

            int width = floatingText.boundary.width + 10;
            int height = floatingText.boundary.height + 3;
            g.setColor(floatingText.background);
            g.fillRoundRect(
                    x - 5, y - (floatingText.boundary.height / 2) - 5,
                    width, height,5, 5
            );
            g.setColor(floatingText.foreground);
//            g.drawRoundRect(
//                    x - 5, y - (floatingText.boundary.height / 2) - 5,
//                    width, height,10, 10
//            );
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