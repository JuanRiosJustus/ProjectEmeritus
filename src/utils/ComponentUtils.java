package utils;

import constants.ColorPalette;
import graphics.temporary.JFieldLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComponentUtils {

    public static JComponent setSize(JComponent component, int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        component.setMinimumSize(new Dimension(width, height));
        component.setMaximumSize(new Dimension(width, height));
        component.setSize(new Dimension(width, height));
        return component;
    }

    public static JComponent setTransparent(JComponent component) {
        component.setBackground(ColorPalette.TRANSPARENT);
        component.setOpaque(false);
        return component;
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

    public static JFieldLabel createFieldLabel(String field, String label) {
        JFieldLabel fl = new JFieldLabel(field + (field.length() > 0 ? ": " : ""), label);
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
}
