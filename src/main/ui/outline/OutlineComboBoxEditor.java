package main.ui.outline;

import javax.swing.ComboBoxEditor;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class OutlineComboBoxEditor implements ComboBoxEditor {
    private final OutlineLabel mField;

    public OutlineComboBoxEditor(Color color) {
        this(color, JLabel.CENTER);
    }
    public OutlineComboBoxEditor(Color color, int horizontalAlignment) {
        mField = new OutlineLabel();
        mField.setEnabled(true);
        mField.setHorizontalAlignment(horizontalAlignment);
        mField.setOpaque(true);
        mField.setBackground(color);
    }

    @Override
    public Component getEditorComponent() {
        return mField;
    }

    @Override
    public void setItem(Object anObject) {
        mField.setText(anObject == null ? null : anObject.toString());
    }

    @Override
    public Object getItem() {
        return mField.getText();
    }

    @Override
    public void selectAll() {
    }

    @Override
    public void addActionListener(ActionListener l) {}

    @Override
    public void removeActionListener(ActionListener l) {}
    public void setPreferredSize(Dimension dimension) {
        mField.setMinimumSize(dimension);
        mField.setMaximumSize(dimension);
        mField.setPreferredSize(dimension);
    }
}
