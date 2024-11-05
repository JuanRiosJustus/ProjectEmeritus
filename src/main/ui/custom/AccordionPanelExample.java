package main.ui.custom;

import javax.swing.*;
import java.awt.*;

public class AccordionPanelExample extends JFrame {
    public AccordionPanelExample() {
        setTitle("Accordion Example with FlowLayout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT)); // Align accordion sections to the left

//        // Add sections with custom sizes
//        add(createAccordionPanel("Section 1", "Content of Section 1", 300, 50));
//        add(createAccordionPanel("Section 2", "Content of Section 2", 300, 50));
//        add(createAccordionPanel("Section 3", "Content of Section 3", 300, 50));
        // Add sections with custom sizes
        add(new AccordionPanelV1("Section 1", new JButton("Content of Section 1"), 100, 50));
        add(new AccordionPanelV1("Section 2",  new JButton("Content of Section 2"), 100, 50));
        add(new AccordionPanelV1("Section 3",  new JButton("Content of Section 3"), 100, 50));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createAccordionPanel(String title, String content, int width, int collapsedHeight) {
        // Main panel to hold the button and content panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Set FlowLayout with no gaps
        panel.setPreferredSize(new Dimension(width, collapsedHeight)); // Set initially preferred size

        // Create the button for the accordion header
        JButton toggleButton = new JButton(title);
        toggleButton.setPreferredSize(new Dimension(width, collapsedHeight));
        panel.add(toggleButton);

        // Create the content panel and add content
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPanel.add(new JLabel(content));
        contentPanel.setVisible(false); // Start collapsed
        contentPanel.setPreferredSize(new Dimension(width, 100)); // Fixed size for content
        panel.add(contentPanel);

        // Add action listener to toggle content visibility and adjust panel size
        toggleButton.addActionListener(e -> {
            contentPanel.setVisible(!contentPanel.isVisible());
            // Adjust the panel's preferred size based on the content visibility
            int expandedHeight = collapsedHeight + contentPanel.getPreferredSize().height;
            panel.setPreferredSize(new Dimension(width, contentPanel.isVisible() ? expandedHeight : collapsedHeight));
            panel.revalidate();
            panel.repaint();
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AccordionPanelExample());
    }
}