package main.graphics.temporary;

import main.game.stores.pools.ColorPalette;

import javax.swing.*;
import java.awt.FlowLayout;

public class JKeyLabelOld extends JPanel {

    public final JLabel key;
    public final JTextArea value;

    public JKeyLabelOld(String fieldName, String fieldValue) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        key = new JLabel(fieldName);
        key.setBackground(ColorPalette.getRandomColor());
        add(key);
        value = new JTextArea();
        value.setEditable(false);
        add(value);
//        setLayout(new FlowLayout(FlowLayout.LEFT));
//        key = new JLabel(fieldName);
//        key.setBackground(ColorPalette.getRandomColor());
//        add(key);
//        value = new JTextArea();
//        value.setEditable(false);
//        add(value);
    }

    public static JPanel createKeyLabel(String field, String value){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(field);
        panel.add(label);
        JTextArea area = new JTextArea();
        panel.add(area);
        return panel;
    }



    public JKeyLabelOld(String fieldName, String fieldValue, int layout) {
        setLayout(new BoxLayout(this, layout));
        key = new JLabel(fieldName);
//        key.setOpaque(false);
//        key.setBackground(ColorPalette.TRANSPARENT);
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
    public void setTextToolTip(String tip) { this.value.setToolTipText(tip); }
}
