package main.ui.outline.production;

import main.graphics.GameUI;

import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineButtonToButtonRow extends GameUI {

    private int mRightOutlineButtonWidth = 0;
    private int mRightOutlineButtonHeight = 0;
    private OutlineButton mRightOutlineButton = null;
    private int mLeftOutlineButtonWidth = 0;
    private int mLeftOutlineButtonHeight = 0;
    private OutlineButton mLeftOutlineButton = null;
    public OutlineButtonToButtonRow(int width, int height, Color color) {
        super(width, height);
        setLayout(new BorderLayout());

        mLeftOutlineButtonWidth = (int) (width * .9);
        mLeftOutlineButtonHeight = height;
        mLeftOutlineButton = new OutlineButton();
        mLeftOutlineButton.setHorizontalAlignment(SwingConstants.LEFT);
//        mOutlineButton.setPreferredSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
//        mOutlineButton.setMinimumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
//        mOutlineButton.setMaximumSize(new Dimension(mOutlineButtonWidth, mOutlineButtonHeight));
        mLeftOutlineButton.setBackground(color);
        add(mLeftOutlineButton, BorderLayout.WEST);


        mRightOutlineButtonWidth = width - mLeftOutlineButtonWidth;
        mRightOutlineButtonHeight = height;
        mRightOutlineButton = new OutlineButton();
        mRightOutlineButton.setHorizontalAlignment(SwingConstants.RIGHT);
//        mOutlineCheckBox.setPreferredSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
//        mOutlineCheckBox.setMinimumSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
//        mOutlineCheckBox.setMaximumSize(new Dimension(mOutlineCheckBoxWidth, mOutlineCheckBoxHeight));
        mRightOutlineButton.setBackground(color);
        add(mRightOutlineButton, BorderLayout.CENTER);
    }

    public OutlineButton getLeftButton() { return mLeftOutlineButton; }
    public OutlineButton getRightButton() { return mRightOutlineButton; }
    public void setCheckBoxVisible(boolean visible) {
        mRightOutlineButton.setVisible(visible);
        if (visible) {
            mLeftOutlineButtonWidth = mWidth - mRightOutlineButtonWidth;
        } else {
            mLeftOutlineButtonWidth = mWidth;
        }
        mLeftOutlineButtonHeight = mHeight;
        mLeftOutlineButton.setPreferredSize(new Dimension(mLeftOutlineButtonWidth, mLeftOutlineButtonHeight));
        mLeftOutlineButton.setMinimumSize(new Dimension(mLeftOutlineButtonWidth, mLeftOutlineButtonHeight));
        mLeftOutlineButton.setMaximumSize(new Dimension(mLeftOutlineButtonWidth, mLeftOutlineButtonHeight));
    }
}