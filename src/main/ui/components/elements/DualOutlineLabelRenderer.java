package main.ui.components.elements;

import main.game.stores.pools.ColorPalette;
import main.ui.components.DualOutlineLabel;
import main.ui.components.OutlineLabel;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

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


//            getLeftOutlineLabel().setOutlineColor(ColorPalette.TRANSPARENT);
//            getRightOutlineLabel().setOutlineColor(ColorPalette.TRANSPARENT);
//            getRightOutlineLabel().setBackground(ColorPalette.TRANSPARENT);
//            getLeftOutlineLabel().setBackground(ColorPalette.TRANSPARENT);

        if (isSelected) {
//            getLeftOutlineLabel().setOutlineColor(ColorPalette.TRANSPARENT);
//            getRightOutlineLabel().setOutlineColor(ColorPalette.TRANSPARENT);
//            getRightOutlineLabel().setBackground(ColorPalette.TRANSPARENT);
//            getLeftOutlineLabel().setBackground(ColorPalette.TRANSPARENT);
        } else {
//            getLeftOutlineLabel().setOutlineColor(Color.BLACK);
//            getLeftOutlineLabel().setForeground(Color.WHITE);
//            getRightOutlineLabel().setOutlineColor(Color.BLACK);
//            getRightOutlineLabel().setForeground(Color.WHITE);

        }

//        if (index == 0) {
////            getRightOutlineLabel().setBackground(mCoverColor);
////            getLeftOutlineLabel().setBackground(mCoverColor);
//            System.out.println("yooo");
//            getRightOutlineLabel().setBackground(mCoverColor);
//            getLeftOutlineLabel().setBackground(mCoverColor);
//        } else {
//            getRightOutlineLabel().setBackground(index % 2 == 0 ? mPanelColor : mPanelColor.darker());
//            getLeftOutlineLabel().setBackground(index % 2 == 0 ? mPanelColor : mPanelColor.darker());
//        }

        getRightOutlineLabel().setBackground(index % 2 == 0 ? mPanelColor.darker() : mPanelColor);
        getLeftOutlineLabel().setBackground(index % 2 == 0 ? mPanelColor.darker() : mPanelColor);
//        getLeftOutlineLabel().setBackground(Color.RED);

//        Color currentColor = mPanelColors;
//        for (int curent = 0; curent < index; curent++) {
//            if (curent % 2 == 0) { continue; }
//            currentColor = currentColor.darker();
//        }

//        mValues.put(getLeftOutlineLabel(), mValues.getOrDefault(getLeftOutlineLabel(),
//                new InnerTuple<>(getLeftOutlineLabel(), getRightOutlineLabel())));
//        getLeftOutlineLabel().setBackground(mValues.get(getLeftOutlineLabel()).value.getBackground());
//        getRightOutlineLabel().setBackground(mValues.get(getLeftOutlineLabel()).value.getBackground());

//        getRightOutlineLabel().setBackground(currentColor);
//        getLeftOutlineLabel().setBackground(currentColor);
//        mValues.put(getLeftOutlineLabel(), mValues.getOrDefault(getLeftOutlineLabel(),
//                new InnerTuple<>(getLeftOutlineLabel(), getRightOutlineLabel())));
        return this;
    }

    public void setPanelColors(Color color) {
        mPanelColor = color;
        getLeftOutlineLabel().setBackground(color);
        getRightOutlineLabel().setBackground(color);
        setBackground(ColorPalette.GREEN);
    }
}
