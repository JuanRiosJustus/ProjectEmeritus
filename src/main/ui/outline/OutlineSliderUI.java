package main.ui.outline;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class OutlineSliderUI extends BasicSliderUI {

    public OutlineSliderUI(JSlider b) {
        super(b);
    }

    @Override
    public void paintTrack(Graphics g) {
        super.paintTrack(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get track bounds
        Rectangle trackBounds = trackRect;
        // Draw a white inline
        g2.setColor(Color.WHITE);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        // Draw a black outline
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        g2.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        super.paintThumb(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // After the default thumb is painted, overlay outline
        Rectangle knobBounds = thumbRect;
        // Fill white
        g2.setColor(Color.WHITE);
        g2.fillOval(knobBounds.x, knobBounds.y, knobBounds.width, knobBounds.height);

        // Outline black
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(knobBounds.x, knobBounds.y, knobBounds.width, knobBounds.height);

        g2.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Outline Slider");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JSlider slider = new JSlider(0, 100, 50);
            slider.setUI(new OutlineSliderUI(slider));
            slider.setOpaque(false); // Let us handle background if needed

            frame.add(slider, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

