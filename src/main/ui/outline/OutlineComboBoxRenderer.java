package main.ui.outline;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;

public class OutlineComboBoxRenderer extends DefaultListCellRenderer {
    private final Color mMainColor;
    private final Color mHighlightColor;
    private final int mHorizontalAlignment;
    public OutlineComboBoxRenderer(Color color) {
        this(color, SwingConstants.CENTER);
    }
    public OutlineComboBoxRenderer(Color color, int horizontalAlignment) {
        mMainColor = color;
        mHighlightColor = mMainColor.brighter().brighter();
        mHorizontalAlignment = horizontalAlignment;
    }
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Center-align the text
        label.setHorizontalAlignment(mHorizontalAlignment);

//        // Set background color for the selected item
//        if (isSelected) {
//            label.setBackground(mHighlightColor); // Dropdown selection color
//            label.setForeground(Color.BLACK); // Text color for contrast
//        } else {
//            label.setBackground(mMainColor); // Default background color
//            label.setForeground(Color.BLACK); // Default text color
//        }
        // Set background color for the selected item
        if (isSelected) {
            label.setBackground(Color.WHITE); // Dropdown selection color
            label.setForeground(Color.BLACK); // Text color for contrast
        } else {
            label.setBackground(Color.BLACK); // Default background color
            label.setForeground(Color.WHITE); // Default text color
        }
        // Ensure the background color is visible
        label.setOpaque(true);
        return label;
    }
}
