package main.ui.outline.production;

import main.graphics.GameUI;
import main.ui.outline.OutlineCheckBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineButtonToCheckBoxRow extends GameUI {

    private int mOutlineCheckBoxWidth = 0;
    private int mOutlineCheckBoxHeight = 0;
    private OutlineCheckBox mOutlineCheckBox = null;
    private int mOutlineButtonWidth = 0;
    private int mOutlineButtonHeight = 0;
    private OutlineButton mOutlineButton = null;
    public OutlineButtonToCheckBoxRow(int width, int height, Color color) {
        super(width, height);
        setLayout(new BorderLayout());

        mOutlineButtonWidth = (int) (width * .9);
        mOutlineButtonHeight = height;
        mOutlineButton = new OutlineButton();
//        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
//        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
//        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setBackground(color);
        add(mOutlineButton, BorderLayout.WEST);


        mOutlineCheckBoxWidth = width - mOutlineButtonWidth;
        mOutlineCheckBoxHeight = height;
        mOutlineCheckBox = new OutlineCheckBox();
//        mOutlineCheckBox.setPreferredSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
//        mOutlineCheckBox.setMinimumSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
//        mOutlineCheckBox.setMaximumSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
        mOutlineCheckBox.setBackground(color);
        add(mOutlineCheckBox, BorderLayout.CENTER);
    }

    public OutlineButton getButton() { return mOutlineButton; }
    public OutlineCheckBox getCheckBox() { return mOutlineCheckBox; }
    public void setCheckBoxVisible(boolean visible) {
        mOutlineCheckBox.setVisible(visible);
        if (visible) {
            mOutlineButtonWidth = mWidth - mOutlineCheckBoxWidth;
        } else {
            mOutlineButtonWidth = mWidth;
        }
        mOutlineButtonHeight = mHeight;
        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
    }
}