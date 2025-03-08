package main.ui.outline;

import main.game.stores.pools.ColorPaletteV1;
import main.ui.outline.production.core.OutlineButton;

import javax.swing.*;
import java.awt.*;

public class ScrollableLabelComponentList extends JPanel {
    private final JPanel listPanel;

    public ScrollableLabelComponentList(int width, int height, Color backgroundColor) {
        // Set panel properties
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Create a scrollable panel for the list
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(backgroundColor);

        JScrollPane scrollPane = new JScrollPane(
                listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border around the scroll pane

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Add a labeled component row to the list.
     *
     * @param labelText   The text for the label on the left.
     * @param component   The component to display on the right.
     */
    public void addRow(String labelText, JComponent component) {
        // Create a row panel
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BorderLayout());
        rowPanel.setBackground(listPanel.getBackground());
        rowPanel.setPreferredSize(new Dimension(getPreferredSize().width, component.getPreferredSize().height));

        // Create the label
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.CENTER);

        // Add some padding for better appearance
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Wrap the right component in a JPanel to ensure proper alignment
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false); // Make the panel transparent to inherit the row's background
        rightPanel.add(component);

        // Add the label and right-aligned component to the row
        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(rightPanel, BorderLayout.EAST);

        // Add the row to the list panel
        listPanel.add(rowPanel);
        listPanel.add(Box.createVerticalStrut(5)); // Add spacing between rows

        // Refresh the list
        listPanel.revalidate();
        listPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Scrollable Label Component List");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Color color = ColorPaletteV1.getRandomColor();
            // Create the custom panel
            ScrollableLabelComponentList panel = new ScrollableLabelComponentList(400, 600, color);

            // Add example rows
            for (int i = 1; i <= 10; i++) {
                JButton exampleButton = new OutlineButton("Button " + i);
                exampleButton.setPreferredSize(new Dimension(150, 30)); // Example component size
                panel.addRow("Label " + i, exampleButton);
            }

            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}