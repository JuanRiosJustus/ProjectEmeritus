package test.ui;

import javax.swing.*;
import java.awt.*;
import main.game.main.GamePanelHud;

public class GamePanelHudTest {

    public static void main(String[] args) {
        // Set up a test frame
        JFrame frame = new JFrame("GamePanelHud Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Initialize GamePanelHud
        int width = 800;
        int height = 600;
        GamePanelHud hud = new GamePanelHud(width, height, 0, 0);

        // Set debug background color to check visibility
        hud.setBackground(new Color(0, 0, 255, 50)); // Semi-transparent blue background for testing
        hud.setOpaque(true);

        // Verify dimensions and bounds
        hud.setBounds(0, 0, width, height);
        System.out.println("Hud bounds: " + hud.getBounds());

        // Add GamePanelHud to the frame
        frame.setLayout(null);
        frame.add(hud);

        // Validate that the GamePanelHud is visible and rendering correctly
        hud.setVisible(true);

        // Display the test frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Additional tests
        runTests(hud);
    }

    // Method to run basic checks on GamePanelHud
    private static void runTests(GamePanelHud hud) {
        // Test if GamePanelHud is visible
        assert hud.isVisible() : "GamePanelHud should be visible";

        // Test if bounds are set correctly
        Rectangle expectedBounds = new Rectangle(0, 0, hud.getWidth(), hud.getHeight());
        assert hud.getBounds().equals(expectedBounds) : "Bounds should be " + expectedBounds;

        // Simulate updating and check results (you might implement a simple display update to check rendering)
        // Example: hud.updateSomeComponentState();
        System.out.println("GamePanelHud visibility test passed, check visually for rendering issues.");
    }
}