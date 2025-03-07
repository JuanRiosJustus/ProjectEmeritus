package main.ui.outline;

import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;

public class OutlineLabel extends JLabel {

    private Color mOutlineColor = Color.BLACK;
    private Color mInlineColor = Color.WHITE;
    private int mTextOutlineThickness;
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

        SwingUiUtils.setupOutlineLabel(this, outlineThickness);
    }



    public void setTextOutlineThickness(int thickness) {
        mTextOutlineThickness = thickness;
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

        int width = textWidth + (4 * mTextOutlineThickness);
        int height = textHeight + (2 * mTextOutlineThickness);

        Insets insets = getInsets();
        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        String text = getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // FILL BACKGROUND BEFORE DRAWING TEXT
        if (isOpaque()) {
            g2.setColor(getBackground()); // Use the JLabel's background color
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        FontMetrics metrics = g2.getFontMetrics(getFont());
        int textHeight = metrics.getHeight();
        int totalWidth = metrics.stringWidth(text) + (mLetterSpacing * (text.length() - 1));

        int x = switch (getHorizontalAlignment()) {
            case SwingConstants.CENTER -> (getWidth() - totalWidth) / 2;
            case SwingConstants.RIGHT -> getWidth() - totalWidth - mTextOutlineThickness;
            default -> mTextOutlineThickness;
        };

        int y = (getHeight() - textHeight) / 2 + metrics.getAscent();

        // Draw text with outline
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charWidth = metrics.charWidth(c);

            g2.setColor(mOutlineColor);
            for (int xOffset = -mTextOutlineThickness; xOffset <= mTextOutlineThickness; xOffset++) {
                for (int yOffset = -mTextOutlineThickness; yOffset <= mTextOutlineThickness; yOffset++) {
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