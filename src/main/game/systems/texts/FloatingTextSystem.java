package main.game.systems.texts;

import main.game.camera.Camera;
import main.game.components.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.game.systems.GameSystem;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class FloatingTextSystem extends GameSystem {

    private final int mSpaceBuffer = 10;
    private final Canvas mFontCalculator = new Canvas();
    private final Set<FloatingText> mFloatingText = new HashSet<>();
    private final Font mFont = FontPool.getInstance().getBoldFont(20);
    private final Queue<FloatingText> mGarbageCalculator = new LinkedList<>();
    private final Rectangle mTemporary = new Rectangle();
    private final BasicStroke mOutlineStroke = new BasicStroke(5f);

    public void stationary(String text, Vector3f vector, Color color) {
        FontMetrics metrics = mFontCalculator.getFontMetrics(mFont);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y + 5;

        // Check for collisions, update until none
        mFloatingText.add(new FloatingText(text, x, y, width, height, color, true));
    }

    public void floater(String text, Vector3f vector, Color color) {
        FontMetrics metrics = mFontCalculator.getFontMetrics(mFont);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y;
                
        mTemporary.setBounds(x - mSpaceBuffer, y - mSpaceBuffer,
                width + (mSpaceBuffer), height + (mSpaceBuffer));
        mFloatingText.add(new FloatingText(text, x, y, width, height, color, false));
    }

//    // TODO Don't draw the floating text offscreen
//    public void render(Graphics gg) {
//        Graphics2D g = (Graphics2D) gg;
//
//        for (FloatingText floatingText : mFloatingText) {
//            g.setFont(mFont);
//            int x = Camera.getInstance().globalX(floatingText.getX());
//            int y = Camera.getInstance().globalY(floatingText.getY() - (floatingText.getHeight() / 2));
//
////            floatingText.debug(g);
//
//            // remember the original settings
//            Color originalColor = g.getColor();
//            Stroke originalStroke = g.getStroke();
//            RenderingHints originalHints = g.getRenderingHints();
//            AffineTransform originalTransform = g.getTransform();
//
//            // create a glyph vector from your text, then get the shape object
//            GlyphVector glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), floatingText.getValue());
//            Shape textShape = glyphVector.getOutline();
//
//            g.setColor(floatingText.getBackground());
//            g.setStroke(mOutlineStroke);
//            g.translate(x, y);
//            g.draw(textShape); // draw outline
//
//            g.setColor(floatingText.getForeground());
//            g.fill(textShape); // fill the shape
//
//            // reset to original settings after painting
//            g.setColor(originalColor);
//            g.setStroke(originalStroke);
//            g.setRenderingHints(originalHints);
//            g.setTransform(originalTransform);
//        }
//    }

    public Set<FloatingText> getFloatingText() { return mFloatingText; }
    public Font getFont() { return mFont; }
    public BasicStroke getOutlineStroke() { return mOutlineStroke; }

    @Override
    public void update(GameModel model, Entity unit) {
        // check for floating text that have floated enough
        for (FloatingText floatingText : mFloatingText) {
            floatingText.update();
            if (floatingText.canRemove()) {
                mGarbageCalculator.add(floatingText);
            }
        }

        // remove the floating text that have been collected
        while (!mGarbageCalculator.isEmpty()) {
            mFloatingText.remove(mGarbageCalculator.poll());
        }
    }
}