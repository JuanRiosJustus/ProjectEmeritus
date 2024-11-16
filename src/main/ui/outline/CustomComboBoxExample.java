package main.ui.outline;

import javax.swing.*;
import java.awt.*;

// Custom component for the dropdown items
class CustomComponent extends JPanel {
    public CustomComponent(String labelText) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        add(label);
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(100, 30)); // Example size
    }
}

// Custom renderer for JComboBox
class CustomComboBoxRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof CustomComponent) {
            CustomComponent customComponent = (CustomComponent) value;
            if (isSelected) {
                customComponent.setBackground(Color.LIGHT_GRAY); // Selection color
            } else {
                customComponent.setBackground(Color.DARK_GRAY); // Default color
            }
            return customComponent;
        } else {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(isSelected ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            label.setForeground(Color.WHITE);
            return label;
        }
    }
}

// Usage example for JComboBox with custom components
public class CustomComboBoxExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Custom JComboBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        // Create JComboBox and add custom components
        JComboBox<JComponent> comboBox = new JComboBox<>(new JComponent[]{
                new JLabel("tototo"),
                new JButton("tototoot")

        });
        comboBox.setRenderer(new CustomComboBoxRenderer());

        frame.add(comboBox);
        frame.setVisible(true);
    }
}