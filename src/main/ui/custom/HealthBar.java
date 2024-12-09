package main.ui.custom;

import main.ui.outline.OutlineLabel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealthBar extends JPanel {

    private int currentHealth;  // Current health value
    private int maxHealth;      // Maximum health value
    private int startHealth;    // Starting health value for the animation
    private int targetHealth;   // Target health value to animate toward
    private int animationDuration = 30; // Duration of animation in frames
    private int animationFrame = 0; // Current frame of the animation
    private Color healthColor;  // Color of the health bar
    private Color backgroundColor; // Background color of the health bar
    private OutlineLabel healthLabel; // Label to display health as text
    private Timer animationTimer; // Timer for health bar animation
    private int sparkleCooldown = 0; // Cooldown for random sparkle generation
    private final Random random = new Random();
    private final List<Sparkle> sparkles = new ArrayList<>();

    public HealthBar(int width, int height, Color color) {
        this.maxHealth = 100;
        this.currentHealth = maxHealth; // Default to full health
        this.targetHealth = maxHealth; // Target health matches initial value
        this.healthColor = Color.GREEN;
        this.backgroundColor = color;

        // Set a black border around the health bar
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(width, height));
        setBackground(backgroundColor);

        // Set the layout to OverlayLayout to stack components
        setLayout(new OverlayLayout(this));

        // Add the health label
        healthLabel = new OutlineLabel(currentHealth + " / " + maxHealth, SwingConstants.CENTER);
        healthLabel.setFont(new Font("Arial", Font.BOLD, height / 2)); // Set font size based on height
        healthLabel.setAlignmentX(0.5f); // Center horizontally
        healthLabel.setAlignmentY(0.5f); // Center vertically
        healthLabel.setOpaque(false); // Transparent background
        healthLabel.setForeground(Color.BLACK); // Text color
        healthLabel.setMinimumSize(new Dimension(width, height));
        healthLabel.setMaximumSize(new Dimension(width, height));
        add(healthLabel);

        // Initialize the animation timer
        animationTimer = new Timer(30, e -> animateHealth());
    }

    public void setCurrentHealth(int health) {
        this.targetHealth = Math.max(0, Math.min(health, maxHealth)); // Clamp value between 0 and maxHealth
        this.startHealth = this.currentHealth;
        this.animationFrame = 0; // Reset animation frame
        if (!animationTimer.isRunning()) {
            animationTimer.start(); // Start animation if it's not already running
        }
    }

    public void setCurrentHealthNoAnimation(int health) {
        this.currentHealth = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        updateHealthLabel();
        repaint();
    }

    private void updateHealthLabel() {
        healthLabel.setText(currentHealth + " / " + maxHealth); // Update the label with the new health values
    }

    private void animateHealth() {
        if (animationFrame >= animationDuration) {
            currentHealth = targetHealth; // Snap to target health at the end
            updateHealthLabel();
            animationTimer.stop();
            return;
        }

        // Calculate progress (0 to 1) for the current frame
        double progress = (double) animationFrame / animationDuration;

        // Apply easeOutQuart easing
        double easedProgress = 1 - Math.pow(1 - progress, 4);

        // Interpolate health value based on eased progress
        currentHealth = (int) (startHealth + (targetHealth - startHealth) * easedProgress);

        animationFrame++; // Increment the frame
        updateHealthLabel();
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

        // Draw sparkles
        drawSparkles(g2, barWidth, getHeight());

        // Draw health text
//        drawHealthText(g2);
    }

    private void drawHealthText(Graphics2D g2) {
        String healthText = currentHealth + " / " + maxHealth;
        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(healthText);
        int textHeight = metrics.getHeight();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() + textHeight) / 2 - metrics.getDescent();
        g2.setColor(Color.BLACK);
        g2.drawString(healthText, textX, textY);
    }

    private void drawSparkles(Graphics2D g2, int barWidth, int barHeight) {
        // Generate sparkles at random cadence
        if (sparkleCooldown <= 0 && sparkles.size() < 5) {
            int x = random.nextInt(barWidth);
            int y = random.nextInt(barHeight);
            sparkles.add(new Sparkle(x, y, 4 + random.nextInt(3))); // Smaller sparkles
            sparkleCooldown = 10 + random.nextInt(50); // Random cooldown before the next sparkle
        }

        // Reduce cooldown
        sparkleCooldown--;

        // Update and draw sparkles
        List<Sparkle> toRemove = new ArrayList<>();
        for (Sparkle sparkle : sparkles) {
            sparkle.age++;
            sparkle.x += 0.5; // Slower horizontal movement
            sparkle.y += 0.5; // Slower vertical movement
            int alpha = Math.max(0, 255 - (sparkle.age * 5)); // Even slower fade-out
            if (alpha == 0 || sparkle.x > barWidth || sparkle.y > barHeight) {
                toRemove.add(sparkle);
                continue;
            }

            g2.setColor(new Color(255, 255, 255, alpha));

            // Draw cross-shaped sparkle
            int size = sparkle.size;
            g2.fillRect((int) (sparkle.x - size / 2), (int) (sparkle.y - 1), size, 2); // Horizontal line
            g2.fillRect((int) (sparkle.x - 1), (int) (sparkle.y - size / 2), 2, size); // Vertical line
        }

        // Remove faded sparkles
        sparkles.removeAll(toRemove);
    }

    static class Sparkle {
        double x, y; // Use double for smooth movement
        int size, age;

        Sparkle(double x, double y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.age = 0;
        }
    }

    // Test the HealthBar
    public static void main(String[] args) {
        JFrame frame = new JFrame("Health Bar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        HealthBar healthBar = new HealthBar(200, 50, Color.LIGHT_GRAY);

        // Simulate health updates
        Timer damageTimer = new Timer(2000, e -> {
            int newHealth = healthBar.targetHealth - 20; // Reduce target health
            healthBar.setCurrentHealth(newHealth);
        });

        frame.add(healthBar, BorderLayout.CENTER);
        JButton startButton = new JButton("Start Damage");
        startButton.addActionListener(e -> damageTimer.start());
        frame.add(startButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}