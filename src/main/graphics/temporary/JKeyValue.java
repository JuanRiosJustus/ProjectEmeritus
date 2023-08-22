package main.graphics.temporary;

import javax.swing.*;

public class JKeyValue extends JPanel {

    protected JLabel keyLabel;
    protected JTextField valueTextField;

    public JKeyValue(String field, String value) {
        keyLabel = new JLabel(field);
        add(keyLabel);
        valueTextField = new JTextField(value);
        valueTextField.setColumns(15);
        add(valueTextField);
    }

    public void setField(String field) { keyLabel.setText(field); }
    public void setText(String value) { valueTextField.setText(value); }
}
