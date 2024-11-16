package main.ui.components.elements;

import main.ui.outline.OutlineLabel;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class OutlineLabelRenderer extends OutlineLabel implements ListCellRenderer<String> {

    public OutlineLabelRenderer() {
        setOpaque(true);
    }


    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (!isVisible()) { return this; }

        setText(value);

        if (isSelected) {
            setForeground(null);
        } else {
            setOutlineColor(Color.BLACK);
            setForeground(Color.WHITE);
        }

        setBackground(index % 2 == 0 ? Color.WHITE.darker() : Color.WHITE.darker().darker());

        return this;
    }
}