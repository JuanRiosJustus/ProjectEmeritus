package main.ui.presets.editor;

import main.ui.components.OutlineLabel;

import javax.swing.ComboBoxEditor;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class OutlineComboBoxEditor implements ComboBoxEditor {
    private final OutlineLabel mField;

    public OutlineComboBoxEditor(int width, int height) {
        this(null, width, height);
    }
    public OutlineComboBoxEditor(Color color, int width, int height) {
        mField = new OutlineLabel();
        mField.setMinimumSize(new Dimension(width, height));
        mField.setMaximumSize(new Dimension(width, height));
        mField.setEnabled(true);
        mField.setPreferredSize(new Dimension(width, height));
        mField.setHorizontalAlignment(JLabel.RIGHT);
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
    public void addActionListener(ActionListener l) {

    }

    @Override
    public void removeActionListener(ActionListener l) {}
}
