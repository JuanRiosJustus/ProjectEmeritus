package utils;

import constants.ColorPalette;
import graphics.temporary.JKeyLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ComponentUtils {

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
        panel.setBackground(ColorPalette.TRANSPARENT);
        panel.setOpaque(false);
        return panel;
    }

    public static void setTransparent(JComponent component) {
        component.setBackground(ColorPalette.TRANSPARENT);
        component.setOpaque(false);
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
        button.addActionListener(e -> button.setBackground(new Color(0, 0, 0, 0)));

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
        ComponentUtils.setTransparent(fl.label);
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

    public static void removeActionListeners(JButton component) {
        for(ActionListener al : component.getActionListeners() ) {
            component.removeActionListener( al );
        }
    }
}
