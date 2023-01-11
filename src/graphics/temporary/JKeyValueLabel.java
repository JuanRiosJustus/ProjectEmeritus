package graphics.temporary;

import javax.swing.*;

public class JFieldLabel extends JPanel {

    public final JLabel field;
    public final JLabel value;

    public JFieldLabel(String fieldName, String fieldValue) {
        this(fieldName, fieldValue, BoxLayout.X_AXIS);
    }

    public JFieldLabel(String fieldName, String fieldValue, int layout) {
        setLayout(new BoxLayout(this, layout));
        field = new JLabel(fieldName);
        add(field);
        value = new JLabel(fieldValue);
        add(value);
    }

    public void setLabel(String value) { this.value.setText(value); }
}
