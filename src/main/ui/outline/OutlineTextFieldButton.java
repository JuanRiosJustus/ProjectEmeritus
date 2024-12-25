package main.ui.outline;

import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;

import java.awt.Color;
import java.awt.Dimension;

public class OutlineTextFieldButton extends GameUI {

    private int mTextFieldWidth = 0;
    private int mTextFieldHeight = 0;
    private OutlineTextField mTextField = null;
    private int mOutlineButtonWidth = 0;
    private int mOutlineButtonHeight = 0;
    private OutlineButton mOutlineButton = null;
    public OutlineTextFieldButton(int width, int height, Color color) {
        super(width, height);

        mTextFieldWidth = (int) (width * .1);
        mTextFieldHeight = height;
        mTextField = new OutlineTextField();
        mTextField.setPreferredSize(new Dimension(mTextFieldWidth, mTextFieldHeight));
        mTextField.setMinimumSize(new Dimension(mTextFieldWidth, mTextFieldHeight));
        mTextField.setMaximumSize(new Dimension(mTextFieldWidth, mTextFieldHeight));
        mTextField.setBackground(color);
        add(mTextField);

        mOutlineButtonWidth = width - mTextFieldWidth;
        mOutlineButtonHeight = height;
        mOutlineButton = new OutlineButton();
        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setBackground(color);
        add(mOutlineButton);
    }

    public OutlineButton getButton() { return mOutlineButton; }
    public OutlineTextField getCheckBox() { return mTextField; }
    public void setCheckBoxVisible(boolean visible) {
        mTextField.setVisible(visible);
        if (visible) {
            mOutlineButtonWidth = mWidth - mTextFieldWidth;
        } else {
            mOutlineButtonWidth = mWidth;
        }
        mOutlineButtonHeight = mHeight;
        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
    }
}