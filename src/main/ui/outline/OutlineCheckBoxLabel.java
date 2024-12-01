package main.ui.outline;

import main.graphics.GameUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineCheckBoxLabel extends GameUI {

    private int mCheckBoxWidth = 0;
    private int mCheckBoxHeight = 0;
    private OutlineCheckBox mCheckBox = null;
    private int mOutlineLabelWidth = 0;
    private int mOutlineLabelHeight = 0;
    private OutlineLabel mOutlineLabel = null;
    public OutlineCheckBoxLabel(int width, int height, Color color) {
        super(width, height);
        setLayout(new BorderLayout());

        mCheckBoxWidth = (int) (width * .1);
        mCheckBoxHeight = height;
        mCheckBox = new OutlineCheckBox();
        mCheckBox.setPreferredSize(new Dimension(mCheckBoxWidth, mCheckBoxHeight));
        mCheckBox.setMinimumSize(new Dimension(mCheckBoxWidth, mCheckBoxHeight));
        mCheckBox.setMaximumSize(new Dimension(mCheckBoxWidth, mCheckBoxHeight));
        mCheckBox.setBackground(color);
        add(mCheckBox, BorderLayout.WEST);

        mOutlineLabelWidth = width - mCheckBoxWidth;
        mOutlineLabelHeight = height;
        mOutlineLabel = new OutlineLabel();
        mOutlineLabel.setPreferredSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMinimumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMaximumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setBackground(color);
        add(mOutlineLabel, BorderLayout.CENTER);
    }

    public OutlineLabel getLabel() { return mOutlineLabel; }
    public OutlineCheckBox getCheckBox() { return mCheckBox; }
    public void setCheckBoxVisible(boolean visible) {
        mCheckBox.setVisible(visible);
        if (visible) {
            mOutlineLabelWidth = mWidth - mCheckBoxWidth;
        } else {
            mOutlineLabelWidth = mWidth;
        }
        mOutlineLabelHeight = mHeight;
        mOutlineLabel.setPreferredSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMinimumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMaximumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
    }
}