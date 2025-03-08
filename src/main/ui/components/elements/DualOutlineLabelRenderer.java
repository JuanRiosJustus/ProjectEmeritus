package main.ui.components.elements;

import main.game.stores.pools.ColorPaletteV1;
import main.ui.components.DualOutlineLabel;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class DualOutlineLabelRenderer extends DualOutlineLabel implements ListCellRenderer<String> {

    private Color mPanelColor = Color.DARK_GRAY;
    private final String mSeparator;
    public DualOutlineLabelRenderer(String separator) {
        setOpaque(true);
        mSeparator = separator;
    }

    public void setTexts(String value) {
        if (value == null) { return; }
        if (value.indexOf(mSeparator) != value.lastIndexOf(mSeparator)) { return; }
        // Find out where the separator is.
        // There maybe content on the left only (or None), right only, or both.
        int split = value.indexOf(mSeparator);
        if (split == -1) {
            setLeftLabel(value);
            setRightLabel("");
        } else {
            String left = value.substring(0, split).trim();
            String right = value.substring(split + 1).trim();
            setLeftLabel(left);
            setRightLabel(right);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (!isVisible()) { return this; }

        setTexts(value);

        getRightOutlineLabel().setBackground(index % 2 == 0 ? mPanelColor.darker() : mPanelColor);
        getLeftOutlineLabel().setBackground(index % 2 == 0 ? mPanelColor.darker() : mPanelColor);

        return this;
    }

    public void setPanelColors(Color color) {
        mPanelColor = color;
        getLeftOutlineLabel().setBackground(color);
        getRightOutlineLabel().setBackground(color);
        setBackground(ColorPaletteV1.GREEN);
    }
}
