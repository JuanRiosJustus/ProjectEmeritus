package main.ui.components.elements;

import main.ui.components.OutlineLabel;

import javax.swing.ComboBoxEditor;
import java.awt.Component;
import java.awt.event.ActionListener;

public class OutlineLabelComboBoxEditor implements ComboBoxEditor {

    private final OutlineLabel mOutlineLabel;

    public OutlineLabelComboBoxEditor() {
        mOutlineLabel = new OutlineLabel();
    }

    @Override
    public Component getEditorComponent() {
        return mOutlineLabel;
    }

    @Override
    public void setItem(Object anObject) {
        mOutlineLabel.setText(anObject == null ? null : anObject.toString());
    }

    @Override
    public Object getItem() {
        return mOutlineLabel.getText();
    }

    @Override
    public void selectAll() {
    }

    @Override
    public void addActionListener(ActionListener l) {
    }

    @Override
    public void removeActionListener(ActionListener l) {
    }

}