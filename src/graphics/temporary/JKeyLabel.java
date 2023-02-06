package graphics.temporary;

import utils.ComponentUtils;

import javax.swing.*;
import java.awt.FlowLayout;

public class JKeyLabel extends JPanel {

    public final JLabel key;
    public final JLabel label;

//    public JKeyValueLabel(String fieldName, String fieldValue) {
////        this(fieldName, fieldValue, BoxLayout.X_AXIS);
//    }

    public JKeyLabel(String fieldName, String fieldValue) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        key = new JLabel(fieldName);
        ComponentUtils.setTransparent(key);
        add(key);
        label = new JLabel(fieldValue);
        ComponentUtils.setTransparent(label);
        add(label);
    }

    public JKeyLabel(String fieldName, String fieldValue, int layout) {
        setLayout(new BoxLayout(this, layout));
        key = new JLabel(fieldName);
        ComponentUtils.setTransparent(key);
        add(key);
        label = new JLabel(fieldValue);
        ComponentUtils.setTransparent(label);
        add(label);
    }

    public void setLabel(String value) { this.label.setText(value); }
}
