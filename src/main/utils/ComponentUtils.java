package main.utils;

import main.constants.ColorPalette;
import main.graphics.temporary.JKeyLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

public class ComponentUtils {

//     public static JPanel createJPanelColumn(Map<String, JKeyLabel> container, String[] values, int width, int height) {
//         JPanel column = new JPanel();
//         column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

//         for (String value : values) {
//             JKeyLabel label = new JKeyLabel(value, " ");
//             label.setPreferredSize(new Dimension(width, (int) (height / values.length)));
// //            ComponentUtils.setSize( label, width, (int) (height * .));
//             ComponentUtils.setTransparent(label);
//             ComponentUtils.setTransparent(label.key);
//             ComponentUtils.setTransparent(label.label);
//             label.key.setFont(label.key.getFont().deriveFont(Font.BOLD));
//             column.add(label);
//             container.put(value, label);
//         }

//         ComponentUtils.setTransparent(column);
//         column.setBorder(new EmptyBorder(5, 5, 5,5));
//         return column;
//     }


    public static JPanel createJPanelColumn(Map<String, JKeyLabel> container, String[] values, int width, int height) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        for (String value : values) {
            JKeyLabel label = new JKeyLabel(value, " ");
            label.setPreferredSize(new Dimension(width, (int) (height / values.length)));
//            ComponentUtils.setSize( label, width, (int) (height * .));
            // ComponentUtils.setTransparent(label);
            // ComponentUtils.setTransparent(label.key);
            /**
             * 
             * 
             *         JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
             */
            // ComponentUtils.setTransparent(label.label);
            label.key.setFont(label.key.getFont().deriveFont(Font.BOLD));
            column.add(label);
            container.put(value, label);
        }

        // ComponentUtils.setTransparent(column);
        column.setBorder(new EmptyBorder(5, 5, 5,5));
        return column;
    }

    public static JComponent setSize(JComponent component, int width, int height) {
//        component.setMinimumSize(new Dimension(width - 1, height - 1));
//        component.setMaximumSize(new Dimension(width + 1, height + 1));
        component.setPreferredSize(new Dimension(width, height));
        return component;
    }

    public static void setMinMaxThenPreferredSize(JComponent component, int width, int height) {
        component.setMinimumSize(new Dimension(width - 1, height - 1));
        component.setMaximumSize(new Dimension(width + 1, height + 1));
        component.setPreferredSize(new Dimension(width, height));
    }

    public static JPanel createTransparentPanel(LayoutManager layout, int width, int height) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(ColorPalette.TRANSPARENT);
        panel.setOpaque(false);
        ComponentUtils.setSize(panel, width, height);
        return panel;
    }

    public static JPanel createTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        // panel.setBackground(ColorPalette.TRANSPARENT);
        panel.setOpaque(false);
        return panel;
    }

    public static void setTransparent(JComponent component) {
        component.setBackground(ColorPalette.TRANSPARENT);
        component.setOpaque(true);
    }
    public static JButton createJButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 0, 0, 0));

//        Border line = new LineBorder(Color.BLACK);
//        Border margin = new EmptyBorder(5, 15, 5, 15);
//        Border compound = new CompoundBorder(line, margin);
//        button.setBorder(compound);
//
//        button.setFocusPainted(false);;
//        button.setBorderPainted(false);
        // button.addActionListener(e -> button.setBackground(new Color(0, 0, 0, 0)));

        return button;
    }

    public static JLabel embolden(JLabel toBold) {
        toBold.setFont(toBold.getFont().deriveFont(toBold.getFont().getStyle() | Font.BOLD));
        return toBold;
    }

    public static JPanel wrap(JComponent component) {
        JPanel jPanel = new JPanel();
        jPanel.setOpaque(false);
        jPanel.setBackground(new Color(0, 0, 0, 0));
        jPanel.add(component);
        return jPanel;
    }

    public static JKeyLabel createFieldLabel(String field, String value) {
        return createFieldLabel(field, value, BoxLayout.X_AXIS);
    }


    public static JKeyLabel createFieldLabel(String field, String label, int layout) {
        JKeyLabel fl = new JKeyLabel(field + (field.length() > 0 ? ": " : ""), label, layout);
        ComponentUtils.setTransparent(fl.value);
        ComponentUtils.setTransparent(fl.key);
        fl.setName(field);
        return fl;
    }

    public static GridBagConstraints verticalGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;
        return gbc;
    }

    public static GridBagConstraints horizontalGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public static JButton[] createJButtonGrid(int size) {
        JButton[] buttons = new JButton[size];
        for (int index = 0; index < buttons.length; index++) {
            buttons[index] = new JButton("Button " + index);
        }
        return buttons;
    }

    public static JToggleButton[] createSingleOnJToggleButtonGrid(int size) {
        JToggleButton[] buttons = new JToggleButton[size];
        for (int index = 0; index < buttons.length; index++) {
            buttons[index] = new JToggleButton("Button " + index);
        }

        for (JToggleButton button : buttons) {
            button.addActionListener(e -> {
                for (JToggleButton sibling : buttons) {
                    sibling.setSelected(false);
                }
                button.setSelected(true);
            });
        }
        return buttons;
    }

//    public static void removeActionListeners(JToggleButton component) {
//        for(ActionListener al : component.getActionListeners() ) {
//            component.removeActionListener( al );
//        }
//    }

    public static void removeActionListeners(AbstractButton component) {
        for(ActionListener al : component.getActionListeners() ) {
            component.removeActionListener( al );
        }
    }
}
