package main.graphics.temporary;

import main.constants.ColorPalette;
import main.utils.ComponentUtils;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class JKeyLabel extends JPanel {

    public final JLabel key;
//    public final JLabel value;
    public final JTextArea value;

//    public JKeyLabel(String fieldName, String fieldValue) {
//        setLayout(new GridBagLayout());
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.gridx = 0;
//        constraints.gridy = 0;
//        constraints.weightx = .3;
//
//        key = new JLabel(fieldName);
//        key.setBackground(ColorPalette.getRandomColor());
//        add(key, constraints);
//
//        constraints.gridx = 1;
//        constraints.weightx = .7;
//        value = new JTextArea();
//        value.setEditable(false);
//        add(value, constraints);
//    }


    public JKeyLabel(String fieldName, String fieldValue) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        key = new JLabel(fieldName);
        key.setBackground(ColorPalette.getRandomColor());

        ComponentUtils.setTransparent(key);
        add(key);
        value = new JTextArea();
        value.setEditable(false);
        add(value);
    }

    public JKeyLabel(String fieldName, String fieldValue, int layout) {
        setLayout(new BoxLayout(this, layout));
        key = new JLabel(fieldName);
//        key.setOpaque(false);
        key.setBackground(ColorPalette.TRANSPARENT);
//        ComponentUtils.setTransparent(key);
        add(key);
//        value = new JLabel(fieldValue);
        value = new JTextArea();
        value.setOpaque(false);
        value.setEditable(false);
//        ComponentUtils.setTransparent(value);
//        value.setBackground(ColorPalette.TRANSPARENT);
        add(value);
    }

    public void setValue(String value) { this.value.setText(value); }
    public void setLabelToolTip(String tip) { this.value.setToolTipText(tip); }
}
