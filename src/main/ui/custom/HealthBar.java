package main.ui.custom;

import javax.swing.*;
import java.awt.*;

public class HealthBar extends JPanel {

    private int currentHealth;  // Current health value
    private int maxHealth;      // Maximum health value
    private Color healthColor;  // Color of the health bar
    private Color backgroundColor; // Background color of the health bar

    public HealthBar(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth; // Default to full health
        this.healthColor = Color.GREEN;
        this.backgroundColor = Color.GRAY;
    }

    public void setCurrentHealth(int health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth)); // Clamp value between 0 and maxHealth
        repaint();
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        repaint();
    }

    public void setHealthColor(Color healthColor) {
        this.healthColor = healthColor;
        repaint();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Calculate health bar width
        int barWidth = (int) ((double) currentHealth / maxHealth * getWidth());

        // Draw health bar
        g2.setColor(healthColor);
        g2.fillRect(0, 0, barWidth, getHeight());

        // Draw border
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Draw text (optional)
        String healthText = currentHealth + " / " + maxHealth;
        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(healthText);
        int textHeight = metrics.getHeight();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() + textHeight) / 2 - metrics.getDescent();
        g2.setColor(Color.BLACK);
        g2.drawString(healthText, textX, textY);
    }

    // Test the HealthBar
    public static void main(String[] args) {
        JFrame frame = new JFrame("Health Bar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        HealthBar healthBar = new HealthBar(100);

        // Simulate health updates
        Timer timer = new Timer(500, e -> {
            int newHealth = healthBar.currentHealth - 10;
            healthBar.setCurrentHealth(newHealth);
        });

        frame.add(healthBar, BorderLayout.CENTER);
        JButton startButton = new JButton("Start Damage");
        startButton.addActionListener(e -> timer.start());
        frame.add(startButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}