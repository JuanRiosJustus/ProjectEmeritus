package main.ui.outline.production;

import main.graphics.GameUI;
import main.ui.outline.OutlineComboBox;
import main.ui.outline.OutlineLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineLabelToDropDownRow extends GameUI {

    private int mOutlineDropDownWidth = 0;
    private int mOutlineDropDownHeight = 0;
    private OutlineComboBox mOutlineDropDown = null;
    private int mOutlineButtonWidth = 0;
    private int mOutlineButtonHeight = 0;
    private OutlineLabel mOutlineLabel = null;
    public OutlineLabelToDropDownRow(int width, int height, Color color) {
        super(width, height);
        setLayout(new BorderLayout());

        mOutlineButtonWidth = (int) (width * .75);
        mOutlineButtonHeight = height;
        mOutlineLabel = new OutlineLabel();
        mOutlineLabel.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineLabel.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineLabel.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineLabel.setBackground(color);
        add(mOutlineLabel, BorderLayout.WEST);


        mOutlineDropDownWidth = width - mOutlineButtonWidth;
        mOutlineDropDownHeight = height;
        mOutlineDropDown = new OutlineComboBox(color, mOutlineDropDownWidth, mOutlineDropDownHeight);
//        mOutlineDropDown.setPreferredSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
//        mOutlineDropDown.setMinimumSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
//        mOutlineDropDown.setMaximumSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
        mOutlineDropDown.setBackground(color);
        add(mOutlineDropDown, BorderLayout.CENTER);
    }

    public OutlineLabel getLabel() { return mOutlineLabel; }
    public OutlineComboBox getDropDown() { return mOutlineDropDown; }
    public void setCheckBoxVisible(boolean visible) {
        mOutlineDropDown.setVisible(visible);
        if (visible) {
            mOutlineButtonWidth = mWidth - mOutlineDropDownWidth;
        } else {
            mOutlineButtonWidth = mWidth;
        }
        mOutlineButtonHeight = mHeight;
        mOutlineLabel.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineLabel.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineLabel.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
    }
}