package main.ui.components;

import main.ui.custom.SwingUiUtils;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;

public class OutlineLabel extends JLabel {

    private Color mOutlineColor = Color.BLACK;

    private boolean mIsPaintingOutline = false;
    private boolean mForceTransparent = false;

    private final int mThickness;

    public OutlineLabel() {
        this("", SwingConstants.LEFT, 2, true);

    }

    public OutlineLabel(String text, int horizontalAlignment,
                        int thickness, boolean defaultBordering) {
        super(text, horizontalAlignment);
        mThickness = thickness;
        setOutlineColor(Color.black);
        setForeground(Color.white);
        setOpaque(true);
        if (defaultBordering) {
            defaultBordering(thickness);
        } else {
            setBorder(thickness);
        }
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
    public void paint(Graphics g) {
        String text = getText();
        if (text == null || text.isEmpty()) {
            super.paint(g);
            return;
        }

        // 1 2 3
        // 8 9 4
        // 7 6 5

        if (isOpaque()) {
            super.paint(g);
        }

        mForceTransparent = true;
        mIsPaintingOutline = true;
        g.translate(-mThickness, -mThickness);
        super.paint(g); // 1
        g.translate(mThickness, 0);
        super.paint(g); // 2
        g.translate(mThickness, 0);
        super.paint(g); // 3
        g.translate(0, mThickness);
        super.paint(g); // 4
        g.translate(0, mThickness);
        super.paint(g); // 5
        g.translate(-mThickness, 0);
        super.paint(g); // 6
        g.translate(-mThickness, 0);
        super.paint(g); // 7
        g.translate(0, -mThickness);
        super.paint(g); // 8
        g.translate(mThickness, 0); // 9
        mIsPaintingOutline = false;

        super.paint(g);
        mForceTransparent = false;
    }
}