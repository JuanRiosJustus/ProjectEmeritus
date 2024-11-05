package main.ui.components;

import main.game.stores.pools.FontPool;
import main.ui.custom.SwingUiUtils;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OutlineLabel extends JLabel {

    private Color mOutlineColor = Color.BLACK;

    private boolean mIsPaintingOutline = false;
    private boolean mForceTransparent = false;

    private final int mOutlineThickness;

    public OutlineLabel() {
        this("", SwingConstants.LEFT, 2, true);

    }

    public OutlineLabel(int outlineThickness) {
        this("", SwingConstants.LEFT, outlineThickness, true);
    }
    public OutlineLabel(String label) {
        this(label, SwingConstants.LEFT, 2, true);
    }

    public OutlineLabel(String text, int horizontalAlignment,
                        int outlineThickness, boolean defaultBordering) {
        super(text, horizontalAlignment);
        mOutlineThickness = outlineThickness;
        setOutlineColor(Color.black);
        setForeground(Color.white);
        if (defaultBordering) {
            defaultBordering(outlineThickness);
        } else {
            setBorder(outlineThickness);
        }
        setDoubleBuffered(true);
    }

    public void setBordering(int thickness, boolean defaultBordering) {
        if (defaultBordering) {
            defaultBordering(thickness);
        } else {
            setBorder(thickness);
        }
    }

    @Override
    public void setText(String str) {
        if (getText() != null && getText().equalsIgnoreCase(str)) { return; }
        super.setText(str);
    }

    private void setBorder(int thickness) {
        SwingUiUtils.setStylizedRaisedBevelBorder(this, thickness);
    }

    private void defaultBordering(int thickness) {
        Border border = getBorder();
        Border margin = new EmptyBorder(thickness, thickness + 3,
                thickness, thickness + 3);
        setBorder(new CompoundBorder(border, margin));
    }

    public Color getOutlineColor() {
        return mOutlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        mOutlineColor = outlineColor;
        this.invalidate();
    }

    @Override
    public Color getForeground() {
        if (mIsPaintingOutline) {
            return mOutlineColor;
        } else {
            return super.getForeground();
        }
    }

    @Override
    public boolean isOpaque() {
        if (mForceTransparent) {
            return false;
        } else {
            return super.isOpaque();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        String text = getText();

//        Color outlineColor = Color.white;
//        Color fillColor = Color.black;
//        BasicStroke outlineStroke = new BasicStroke(2.0f);
//        int height = (int) getPreferredSize().getHeight();
//        int width = (int) getPreferredSize().getWidth();
////
//        if (g != null) {
//            Graphics2D g2 = (Graphics2D) g;
//
//            // remember original settings
//            Color originalColor = g2.getColor();
//            Stroke originalStroke = g2.getStroke();
//            RenderingHints originalHints = g2.getRenderingHints();
//
//
//            // create a glyph vector from your text
////            g2.setFont(getFont());
//            GlyphVector glyphVector = getFont().createGlyphVector(g2.getFontRenderContext(), text);
//            // get the shape object
//            Shape textShape = glyphVector.getOutline();
//
//            // activate anti aliasing for text rendering (if you want it to look nice)
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON);
//            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
//                    RenderingHints.VALUE_RENDER_QUALITY);
//
//            g2.setColor(outlineColor);
//            g2.setStroke(outlineStroke);
//            g2.translate(width * .5, height * .75);
//            g2.draw(textShape); // draw outline
//
//            g2.setColor(fillColor);
//            g2.fill(textShape); // fill the shape
//
//            // reset to original settings after painting
//            g2.setColor(originalColor);
//            g2.setStroke(originalStroke);
//            g2.setRenderingHints(originalHints);
//        }

//        Color fillColor = Color.RED;
//        g.setColor(fillColor);
//        g.fillRect(0, 0, getWidth(), getHeight());

        if (text == null || text.isEmpty()) {
            super.paintComponent(g);
            return;
        }

        // 1 2 3
        // 8 9 4
        // 7 6 5

        if (isOpaque()) {
            super.paintComponent(g);
        }

        mForceTransparent = true;
        mIsPaintingOutline = true;
        g.translate(-mOutlineThickness, -mOutlineThickness);
        super.paintComponent(g); // 1
        g.translate(mOutlineThickness, 0);
        super.paintComponent(g); // 2
        g.translate(mOutlineThickness, 0);
        super.paintComponent(g); // 3
        g.translate(0, mOutlineThickness);
        super.paintComponent(g); // 4
        g.translate(0, mOutlineThickness);
        super.paintComponent(g); // 5
        g.translate(-mOutlineThickness, 0);
        super.paintComponent(g); // 6
        g.translate(-mOutlineThickness, 0);
        super.paintComponent(g); // 7
        g.translate(0, -mOutlineThickness);
        super.paintComponent(g); // 8
        g.translate(mOutlineThickness, 0); // 9
        mIsPaintingOutline = false;

        super.paintComponent(g);
        mForceTransparent = false;
    }

    @Override
    public void update(Graphics g) {
        paintComponent(g);
    }
}