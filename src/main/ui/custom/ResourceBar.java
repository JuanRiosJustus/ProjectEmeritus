package main.ui.custom;

import main.game.stores.pools.ColorPalette;
import main.ui.outline.OutlineLabel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResourceBar extends JPanel {

    private int currentResource;  // Current resource value
    private int maxResource;      // Maximum resource value
    private int startResource;    // Starting resource value for the animation
    private int targetResource;   // Target resource value to animate toward
    private int animationDuration = 30; // Duration of animation in frames
    private int animationFrame = 0; // Current frame of the animation
    private Color resourceColor;  // Color of the resource bar
    private Color backgroundColor; // Background color of the resource bar
    private OutlineLabel resourceLabel; // Label to display resource as text
    private Timer animationTimer; // Timer for resource bar animation
    private int sparkleCooldown = 0; // Cooldown for random sparkle generation
    private final Random random = new Random();
    private final List<Sparkle> sparkles = new ArrayList<>();

    public ResourceBar(int width, int height, Color parentColor, Color resourceColor) {
        this.maxResource = 100;
        this.currentResource = maxResource; // Default to full resource
        this.targetResource = maxResource; // Target resource matches initial value
        this.resourceColor = resourceColor;
        this.backgroundColor = parentColor;

        // Set a black border around the resource bar
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(width, height));
        setBackground(backgroundColor);

        // Set the layout to OverlayLayout to stack components
        setLayout(new OverlayLayout(this));

        // Add the resource label
        resourceLabel = new OutlineLabel(currentResource + " / " + maxResource, SwingConstants.CENTER);
        resourceLabel.setFont(new Font("Arial", Font.BOLD, height / 2)); // Set font size based on height
        resourceLabel.setAlignmentX(0.5f); // Center horizontally
        resourceLabel.setAlignmentY(0.5f); // Center vertically
        resourceLabel.setOpaque(false); // Transparent background
        resourceLabel.setForeground(Color.BLACK); // Text color
        resourceLabel.setMinimumSize(new Dimension(width, height));
        resourceLabel.setMaximumSize(new Dimension(width, height));
        add(resourceLabel);

        // Initialize the animation timer
        animationTimer = new Timer(30, e -> animateResource());
    }

    public void setCurrentResource(int resource) {
        this.targetResource = Math.max(0, Math.min(resource, maxResource)); // Clamp value between 0 and maxResource
        this.startResource = this.currentResource;
        this.animationFrame = 0; // Reset animation frame
        if (!animationTimer.isRunning()) {
            animationTimer.start(); // Start animation if it's not already running
        }
    }

    public void setCurrentResourceNoAnimation(int resource) {
        this.currentResource = resource;
    }

    public void setMaxResource(int maxResource) {
        this.maxResource = maxResource;
        updateResourceLabel();
        repaint();
    }

    private void updateResourceLabel() {
        int currentPercentage = (int) (((currentResource * 1f) / (maxResource * 1f)) * 100);
        resourceLabel.setText(currentResource + " / " + maxResource + " ( " + currentPercentage + "% )"); // Update the label with the new resource values
    }

    private void animateResource() {
        if (animationFrame >= animationDuration) {
            currentResource = targetResource; // Snap to target resource at the end
            updateResourceLabel();
            animationTimer.stop();
            return;
        }

        // Calculate progress (0 to 1) for the current frame
        double progress = (double) animationFrame / animationDuration;

        // Apply easeOutQuart easing
        double easedProgress = 1 - Math.pow(1 - progress, 4);

        // Interpolate resource value based on eased progress
        currentResource = (int) (startResource + (targetResource - startResource) * easedProgress);

        animationFrame++; // Increment the frame
        updateResourceLabel();
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

        // Calculate resource bar width
        int barWidth = (int) ((double) currentResource / maxResource * getWidth());

        // Draw resource bar
        g2.setColor(resourceColor);
        g2.fillRect(0, 0, barWidth, getHeight());

        // Draw sparkles
        drawSparkles(g2, barWidth, getHeight());
    }

    private void drawSparkles(Graphics2D g2, int barWidth, int barHeight) {
        // Generate sparkles at random cadence
        if (sparkleCooldown <= 0 && sparkles.size() < 19) {
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

    // Test the ResourceBar
    public static void main(String[] args) {
        JFrame frame = new JFrame("Resource Bar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        ResourceBar resourceBar = new ResourceBar(200, 50, Color.LIGHT_GRAY, ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3);

        // Simulate resource updates
        Timer damageTimer = new Timer(2000, e -> {
            int newResource = resourceBar.targetResource - 20; // Reduce target resource
            resourceBar.setCurrentResource(newResource);
        });

        frame.add(resourceBar, BorderLayout.CENTER);
        JButton startButton = new JButton("Start Damage");
        startButton.addActionListener(e -> damageTimer.start());
        frame.add(startButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}