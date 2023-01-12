package graphics.temporary;

import javax.swing.*;

public class JKeyValueLabel extends JPanel {

    public final JLabel key;
    public final JLabel value;

    public JKeyValueLabel(String fieldName, String fieldValue) {
        this(fieldName, fieldValue, BoxLayout.X_AXIS);
    }

    public JKeyValueLabel(String fieldName, String fieldValue, int layout) {
        setLayout(new BoxLayout(this, layout));
        key = new JLabel(fieldName);
        add(key);
        value = new JLabel(fieldValue);
        add(value);
    }

    public void setLabel(String value) { this.value.setText(value); }
}
