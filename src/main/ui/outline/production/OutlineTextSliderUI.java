package main.ui.outline.production;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.util.Dictionary;
import java.util.Enumeration;

public class OutlineTextSliderUI extends BasicSliderUI {

    // Outline thickness and colors
    private static final int OUTLINE_THICKNESS = 2;
    private static final Color OUTLINE_COLOR = Color.BLACK;
    private static final Color INLINE_COLOR = Color.WHITE;

    public OutlineTextSliderUI(JSlider b) {
        super(b);
    }

    @Override
    public void paintLabels(Graphics g) {
        // Do NOT call super.paintLabels(g), to prevent default text drawing

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dictionary<?, ?> labelTable = slider.getLabelTable();
        if (labelTable == null) {
            g2.dispose();
            return;
        }

        FontMetrics fm = g2.getFontMetrics();

        @SuppressWarnings("unchecked")
        Enumeration<Integer> keys = (Enumeration<Integer>) labelTable.keys();
        while (keys.hasMoreElements()) {
            Integer key = keys.nextElement();
            JComponent label = (JComponent) labelTable.get(key);
            if (!(label instanceof JLabel)) continue;
            JLabel lbl = (JLabel) label;

            // Compute label position
            Point labelPos = getLabelPosition(key);

            String text = lbl.getText();
            if (text == null || text.isEmpty()) continue;

            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            // Replicate centering logic
            int x = labelPos.x - (textWidth / 2);
            int y = labelPos.y + (textHeight / 2);

            // Draw outline
            g2.setColor(OUTLINE_COLOR);
            for (int xOffset = -OUTLINE_THICKNESS; xOffset <= OUTLINE_THICKNESS; xOffset++) {
                for (int yOffset = -OUTLINE_THICKNESS; yOffset <= OUTLINE_THICKNESS; yOffset++) {
                    if (xOffset != 0 || yOffset != 0) {
                        g2.drawString(text, x + xOffset, y + yOffset);
                    }
                }
            }

            // Draw inline text
            g2.setColor(INLINE_COLOR);
            g2.drawString(text, x, y);
        }

        g2.dispose();
    }

    /**
     * A helper method to find label positions.
     */
    private Point getLabelPosition(int value) {
        // Logic similar to BasicSliderUI
        int labelCenter = xPositionForValue(value);
        int labelY = trackRect.y + trackRect.height + 20; // offset below track

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            return new Point(labelCenter, labelY);
        } else {
            // For vertical slider
            int labelCenterVertical = yPositionForValue(value);
            int labelX = trackRect.x - 20; // offset to the left for vertical slider
            return new Point(labelX, labelCenterVertical);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Outline Text Slider");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JSlider slider = new JSlider(0, 10, 5);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setMajorTickSpacing(2);
            slider.setMinorTickSpacing(1);

            slider.setUI(new OutlineTextSliderUI(slider));

            frame.add(slider, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}