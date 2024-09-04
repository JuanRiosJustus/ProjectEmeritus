package main.ui.components;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class LabelWithTextBorderExample {
    public static void main(String[] args) {
        // Create a new JFrame
        JFrame frame = new JFrame("JLabel Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create a JLabel with white text
        JLabel label = new JLabel("Your Text Here") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Get the FontMetrics for the label's font
                FontMetrics metrics = g.getFontMetrics(getFont());

                // Get the size of the text
                int textWidth = metrics.stringWidth(getText());
                int textHeight = metrics.getHeight();

                // Get the position of the text within the label
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + metrics.getAscent();

                // Draw a black rectangle around the text
                g.setColor(Color.BLACK);
                g.drawRect(x - 2, y - metrics.getAscent() - 2, textWidth + 4, textHeight + 4);
            }
        };

        // Set label text color to white
        label.setForeground(Color.WHITE);

        // Optional: Set background color and make the label opaque
        label.setBackground(Color.GRAY);  // Example background color
        label.setOpaque(true);

        // Add the label to the frame
        frame.add(label);

        // Set the frame to be visible
        frame.setVisible(true);
    }
}