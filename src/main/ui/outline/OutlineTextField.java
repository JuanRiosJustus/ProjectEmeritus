package main.ui.outline;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OutlineTextField extends JTextField {

    private Color mOutlineColor = Color.BLACK;
    private Color mInlineColor = Color.WHITE;
    private int mOutlineThickness;
    private int mLetterSpacing = 3; // Customizable spacing between letters

    public OutlineTextField() {
        this("", SwingConstants.LEFT, 2);
    }

    public OutlineTextField(String text, int horizontalAlignment, int outlineThickness) {
        super(text, horizontalAlignment);
        mOutlineThickness = outlineThickness;
        setOpaque(false);
        setOutlineColor(Color.BLACK);
        setInlineColor(Color.WHITE);
//        defaultBordering(outlineThickness);
        setBorder(BorderFactory.createEmptyBorder());
        setDoubleBuffered(true);
    }

    public void setLetterSpacing(int spacing) {
        mLetterSpacing = spacing;
        repaint(); // Redraw to apply spacing
    }

    public void setOutlineThickness(int thickness) {
        mOutlineThickness = thickness;
    }

    @Override
    public void setText(String str) {
        if (getText() != null && getText().equals(str)) {
            return;
        }
        super.setText(str);
    }

    private void defaultBordering(int thickness) {
        Border border = getBorder();
        Border margin = new EmptyBorder(thickness, thickness + 3, thickness, thickness + 3);
        setBorder(new CompoundBorder(border, margin));
    }

    public Color getOutlineColor() {
        return mOutlineColor;
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
    public void paintComponent(Graphics g) {
        String text = getText();
        if (text == null || text.isEmpty()) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get font metrics for calculating character size
        FontMetrics metrics = g2.getFontMetrics(getFont());
        int textHeight = metrics.getHeight();

        // Calculate the base x position based on alignment
        int totalWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            totalWidth += metrics.charWidth(text.charAt(i)) + mLetterSpacing;
        }
        int x = switch (getHorizontalAlignment()) {
            case JTextField.LEFT -> mOutlineThickness;
            case JTextField.CENTER -> (getWidth() - totalWidth) / 2;
            case JTextField.RIGHT -> getWidth() - totalWidth - mOutlineThickness;
            default -> mOutlineThickness; // Default to left alignment
        };

        // Calculate y position for vertical centering
        int y = (getHeight() - textHeight) / 2 + metrics.getAscent();

        // Draw each character with spacing
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charWidth = metrics.charWidth(c);

            // Draw outline for each character
            g2.setColor(mOutlineColor);
            for (int xOffset = -mOutlineThickness; xOffset <= mOutlineThickness; xOffset++) {
                for (int yOffset = -mOutlineThickness; yOffset <= mOutlineThickness; yOffset++) {
                    if (xOffset != 0 || yOffset != 0) {
                        g2.drawString(charStr, x + xOffset, y + yOffset);
                    }
                }
            }

            // Draw actual character with foreground color
            g2.setColor(mInlineColor);
            g2.drawString(charStr, x, y);

            // Update x position for the next character
            x += charWidth + mLetterSpacing;
        }

        g2.dispose();
    }
}