package main.ui.swing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class TranslucentScrollPaneV2 extends JScrollPane {

    public TranslucentScrollPaneV2(JComponent component) {
        super(component);

        // Set custom UI for scrollbars
        getVerticalScrollBar().setUI(new TranslucentScrollBarUI());
        getHorizontalScrollBar().setUI(new TranslucentScrollBarUI());

        // Optional: Hide scrollbars when not needed
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set default opacity for the scroll pane
        setOpaque(false);
        getViewport().setOpaque(false);
    }

    private static class TranslucentScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set translucent color for the thumb
            Color thumbColor = new Color(100, 100, 100, 150); // RGBA: last value is alpha
            g2.setColor(thumbColor);

            // Draw thumb with rounded corners
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);

            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            // Optionally paint the track (leave empty for no track)
            Graphics2D g2 = (Graphics2D) g.create();
            Color trackColor = new Color(200, 200, 200, 50); // Translucent track
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false; // Ensure proper layering of components
    }
}