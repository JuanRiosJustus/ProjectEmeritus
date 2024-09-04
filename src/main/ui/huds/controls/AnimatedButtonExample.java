package main.ui.huds.controls;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class AnimatedButtonExample {
    private JButton button;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private Timer animationTimer;

    public AnimatedButtonExample(BufferedImage[] frames) {
        this.frames = frames;
        this.button = new JButton();

        // Set the initial icon
        button.setIcon(new ImageIcon(frames[0]));

        // Create and start the animation timer
        // Change 100 to adjust the animation speed (milliseconds)
        animationTimer = new Timer(100, e -> animateButtonIcon());
        animationTimer.start();
    }

    private void animateButtonIcon() {
        // Update the current frame index
        currentFrame = (currentFrame + 1) % frames.length;

        // Set the new icon on the button
        button.setIcon(new ImageIcon(frames[currentFrame]));
    }

    public JButton getButton() {
        return button;
    }

    public static void main(String[] args) {
        // Example setup for testing the AnimatedButtonExample
        JFrame frame = new JFrame("Animated JButton Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Create an array of BufferedImages for the animation frames
        BufferedImage[] images = new BufferedImage[3];
        for (int i = 0; i < images.length; i++) {
            images[i] = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = images[i].createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, 100, 100);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Frame " + (i + 1), 25, 50);
            g2d.dispose();
        }

        // Create the animated button
        AnimatedButtonExample animatedButton = new AnimatedButtonExample(images);
        frame.add(animatedButton.getButton());

        // Show the frame
        frame.pack();
        frame.setVisible(true);
    }
}