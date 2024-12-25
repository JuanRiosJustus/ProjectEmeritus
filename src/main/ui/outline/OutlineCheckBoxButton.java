package main.ui.outline;

import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;

import java.awt.Color;
import java.awt.Dimension;

public class OutlineCheckBoxButton extends GameUI {

    private int mCheckBoxWidth = 0;
    private int mCheckBoxHeight = 0;
    private OutlineCheckBox mCheckBox = null;
    private int mOutlineButtonWidth = 0;
    private int mOutlineButtonHeight = 0;
    private OutlineButton mOutlineButton = null;
    public OutlineCheckBoxButton(int width, int height, Color color) {
        super(width, height);

        mCheckBoxWidth = (int) (width * .1);
        mCheckBoxHeight = height;
        mCheckBox = new OutlineCheckBox();
        mCheckBox.setPreferredSize(new Dimension(mCheckBoxWidth, mCheckBoxHeight));
        mCheckBox.setMinimumSize(new Dimension(mCheckBoxWidth, mCheckBoxHeight));
        mCheckBox.setMaximumSize(new Dimension(mCheckBoxWidth, mCheckBoxHeight));
        mCheckBox.setBackground(color);
        add(mCheckBox);

        mOutlineButtonWidth = width - mCheckBoxWidth;
        mOutlineButtonHeight = height;
        mOutlineButton = new OutlineButton();
        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setBackground(color);
        add(mOutlineButton);
    }

    public OutlineButton getButton() { return mOutlineButton; }
    public OutlineCheckBox getCheckBox() { return mCheckBox; }
    public void setCheckBoxVisible(boolean visible) {
        mCheckBox.setVisible(visible);
        if (visible) {
            mOutlineButtonWidth = mWidth - mCheckBoxWidth;
        } else {
            mOutlineButtonWidth = mWidth;
        }
        mOutlineButtonHeight = mHeight;
        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
    }
}