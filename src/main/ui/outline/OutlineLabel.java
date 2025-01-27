package main.ui.outline;

import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OutlineLabel extends JLabel {

    private Color mOutlineColor = Color.BLACK;
    private Color mInlineColor = Color.WHITE;
    private int mOutlineThickness;
    private int mLetterSpacing = 1; // Customizable spacing between letters

    public OutlineLabel() { this("", SwingConstants.LEFT, 1); }

    public OutlineLabel(int outlineThickness) {
        this("", SwingConstants.LEFT, outlineThickness);
    }

    public OutlineLabel(String label) {
        this(label, SwingConstants.LEFT, 1);
    }

    public OutlineLabel(String label, int horizontalAlignment) {
        this(label, horizontalAlignment, 1);
    }

    public OutlineLabel(String text, int horizontalAlignment, int outlineThickness) {
        super(text, horizontalAlignment);
        mOutlineThickness = outlineThickness;
        setOpaque(false);
        setOutlineColor(Color.BLACK);
        setInlineColor(Color.WHITE);
//        setBorder(new CompoundBorder(getBorder(), new EmptyBorder(mOutlineThickness, mOutlineThickness,
//                mOutlineThickness, mOutlineThickness)));
        setDoubleBuffered(true);
    }
    public void setOutlineThickness(int thickness) {
        mOutlineThickness = thickness;
    }

    public void setOutlineColor(Color outlineColor) {
        mOutlineColor = outlineColor;
        invalidate();
    }

    public void setInlineColor(Color inlineColor) {
        mInlineColor = inlineColor;
        invalidate();
    }

    @Override
    public void setText(String str) {
        if (getText() != null && getText().equals(str)) {
            return;
        }
        super.setText(str);
    }

    @Override
    public Dimension getPreferredSize() {
        String text = getText();

        // Return zero width if there's no text
        if (text == null || text.isEmpty()) {
            return new Dimension(0, 0);
        }

        FontMetrics metrics = getFontMetrics(getFont());
        int textWidth = metrics.stringWidth(text) + (mLetterSpacing * (text.length() - 1));
        int textHeight = metrics.getHeight();

        int width = textWidth + (4 * mOutlineThickness);
        int height = textHeight + (2 * mOutlineThickness);

        Insets insets = getInsets();
        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        String text = getText();

        // Skip painting if there's no text
        if (text == null || text.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics metrics = g2.getFontMetrics(getFont());
        int textHeight = metrics.getHeight();

        int totalWidth = metrics.stringWidth(text) + (mLetterSpacing * (text.length() - 1));

        int x = switch (getHorizontalAlignment()) {
            case SwingConstants.CENTER -> (getWidth() - totalWidth) / 2;
            case SwingConstants.RIGHT -> getWidth() - totalWidth - mOutlineThickness;
            default -> mOutlineThickness;
        };

        int y = (getHeight() - textHeight) / 2 + metrics.getAscent();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charWidth = metrics.charWidth(c);

            g2.setColor(mOutlineColor);
            for (int xOffset = -mOutlineThickness; xOffset <= mOutlineThickness; xOffset++) {
                for (int yOffset = -mOutlineThickness; yOffset <= mOutlineThickness; yOffset++) {
                    if (xOffset != 0 || yOffset != 0) {
                        g2.drawString(charStr, x + xOffset, y + yOffset);
                    }
                }
            }

            g2.setColor(mInlineColor);
            g2.drawString(charStr, x, y);

            x += charWidth + mLetterSpacing;
        }

        g2.dispose();
    }

    @Override
    public void update(Graphics g) {
        paintComponent(g);
    }
}