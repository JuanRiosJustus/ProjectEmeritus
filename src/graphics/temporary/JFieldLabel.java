package graphics.temporary;

import javax.swing.*;

public class JFieldLabel extends JPanel {

    protected JLabel field;
    protected JLabel value;

    public JFieldLabel(String fieldName, String fieldValue) {
        field = new JLabel(fieldName);
        add(field);
        value = new JLabel(fieldValue);
        add(value);
    }

    public void setField(String field) { this.field.setText(field); }
    public void setLabel(String value) { this.value.setText(value); }
}
