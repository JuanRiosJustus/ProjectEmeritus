package main.ui.custom;

import javax.swing.JComboBox;

public class StringComboBox extends JComboBox<String> {

    public StringComboBox() {
        super();
    }

    public StringComboBox(String[] items) {
        super(items);
    }

    // Add items using String as the parameter type
    public void addItem(String item) {
        super.addItem(item);
    }

    @Override
    public String getSelectedItem() {
        return (String) super.getSelectedItem();
    }
}