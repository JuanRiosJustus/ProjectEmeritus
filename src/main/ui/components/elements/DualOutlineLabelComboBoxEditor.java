package main.ui.components.elements;

import main.ui.components.DualOutlineLabel;

import javax.swing.ComboBoxEditor;
import java.awt.Component;
import java.awt.event.ActionListener;

public class DualOutlineLabelComboBoxEditor implements ComboBoxEditor {

    private final DualOutlineLabel mOutlineLabel;
    private final String mSeparator;

    public DualOutlineLabelComboBoxEditor(String separator) {
        mOutlineLabel = new DualOutlineLabel();
        mOutlineLabel.setLeftLabel("");
        mOutlineLabel.setRightLabel("");
        mSeparator = separator;
    }

    @Override
    public Component getEditorComponent() {
        return mOutlineLabel;
    }

    @Override
    public void setItem(Object anObject) {
        if (anObject == null) { return; }
        String value = anObject.toString();
        int split = value.indexOf(mSeparator);
        if (split == -1) {
            mOutlineLabel.setLeftLabel(value);
            mOutlineLabel.setRightLabel("");
        } else {
            String left = value.substring(0, split).trim();
            String right = value.substring(split + 1).trim();
            mOutlineLabel.setLeftLabel(left);
            mOutlineLabel.setRightLabel(right);
        }
    }

    @Override
    public Object getItem() {
        return mOutlineLabel.getRightLabelText();
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