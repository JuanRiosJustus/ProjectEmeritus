package main.game.main.ui;

import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class AnimationPanel extends JComponent {

    private BufferedImage[] animationFrames;
    private int currentFrameIndex = 0;
    private Timer animationTimer;
    private boolean scaleToFit;
    private float xAlign = 0.5f; // 0.0 = left, 0.5 = center, 1.0 = right
    private float yAlign = 0.5f; // 0.0 = top, 0.5 = center, 1.0 = bottom

    public AnimationPanel() {

//        setPreferredSize(new Dimension(width, heu));
    }

    public void setup(BufferedImage[] animationFrames, int frameDelay, boolean scaleToFit) {
        this.animationFrames = animationFrames;
        this.scaleToFit = scaleToFit;

        if (animationFrames == null || animationFrames.length == 0) {
            throw new IllegalArgumentException("Animation frames cannot be null or empty.");
        }

        // Set preferred size based on the first frame
        setPreferredSize(new Dimension(animationFrames[0].getWidth(), animationFrames[0].getHeight()));

        // Create and start the animation timer
        animationTimer = new Timer(frameDelay, e -> {
            currentFrameIndex = (currentFrameIndex + 1) % animationFrames.length;
            repaint();
        });
        animationTimer.start();

        // Add a click listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick(e);
            }
        });
        SwingUiUtils.setStylizedRaisedBevelBorder(this, 1);
    }

    /**
     * Handles click events on the animation.
     */
    private void onClick(MouseEvent e) {
        System.out.println("Animation clicked at: " + e.getPoint());
        // Custom behavior can be added here
    }

    /**
     * Sets alignment for the animation rendering.
     * @param xAlign Horizontal alignment (0.0 = left, 0.5 = center, 1.0 = right)
     * @param yAlign Vertical alignment (0.0 = top, 0.5 = center, 1.0 = bottom)
     */
    public void setAlignment(float xAlign, float yAlign) {
        this.xAlign = xAlign;
        this.yAlign = yAlign;
        repaint();
    }

    /**
     * Stops the animation.
     */
    public void stopAnimation() {
        if (animationTimer == null) { return; }
        animationTimer.stop();
    }

    /**
     * Starts the animation.
     */
    public void startAnimation() {
        if (animationTimer == null) { return; }
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (animationFrames == null || animationFrames.length == 0) {
            return;
        }

        if (currentFrameIndex >= animationFrames.length) { return; }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage currentFrame = animationFrames[currentFrameIndex];

        int frameWidth = currentFrame.getWidth();
        int frameHeight = currentFrame.getHeight();
        int compWidth = getWidth();
        int compHeight = getHeight();

        int drawX, drawY, drawWidth = frameWidth, drawHeight = frameHeight;

        if (scaleToFit) {
            // Scale to fit component
            drawWidth = compWidth;
            drawHeight = compHeight;
        }

        // Aligning the animation frame within the component
        drawX = (int) ((compWidth - drawWidth) * xAlign);
        drawY = (int) ((compHeight - drawHeight) * yAlign);

        g2d.drawImage(currentFrame, 0, 0, drawWidth, drawHeight, null);
    }
}