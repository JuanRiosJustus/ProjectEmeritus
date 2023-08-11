package main.graphics.temporary;

import main.utils.ComponentUtils;

import javax.swing.*;
import java.awt.FlowLayout;

public class JKeyLabel extends JPanel {

    public final JLabel key;
    public final JLabel value;

    public JKeyLabel(String fieldName, String fieldValue) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        key = new JLabel(fieldName);
        ComponentUtils.setTransparent(key);
        add(key);
        value = new JLabel(fieldValue);
        ComponentUtils.setTransparent(value);
        add(value);
        ComponentUtils.setTransparent(this);
    }

    public JKeyLabel(String fieldName, String fieldValue, int layout) {
        setLayout(new BoxLayout(this, layout));
        key = new JLabel(fieldName);
        ComponentUtils.setTransparent(key);
        add(key);
        value = new JLabel(fieldValue);
        ComponentUtils.setTransparent(value);
        add(value);
    }

    public void setValue(String value) { this.value.setText(value); }
    public void setLabelToolTip(String tip) { this.value.setToolTipText(tip); }
}
