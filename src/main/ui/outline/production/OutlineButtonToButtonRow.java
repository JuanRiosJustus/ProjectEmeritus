package main.ui.outline.production;

import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;

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

        mLeftOutlineButtonWidth = Math.min(width, height);
        mLeftOutlineButtonHeight = height;
        mLeftOutlineButton = new OutlineButton();
        mLeftOutlineButton.setHorizontalAlignment(SwingConstants.LEFT);
        mLeftOutlineButton.setPreferredSize(new Dimension(mLeftOutlineButtonWidth, mLeftOutlineButtonHeight));
        mLeftOutlineButton.setMinimumSize(new Dimension(mLeftOutlineButtonWidth, mLeftOutlineButtonHeight));
        mLeftOutlineButton.setMaximumSize(new Dimension(mLeftOutlineButtonWidth, mLeftOutlineButtonHeight));
        mLeftOutlineButton.setBackground(color);
        add(mLeftOutlineButton, BorderLayout.WEST);


        mRightOutlineButtonWidth = width - mLeftOutlineButtonWidth;
        mRightOutlineButtonHeight = height;
        mRightOutlineButton = new OutlineButton();
        mRightOutlineButton.setHorizontalAlignment(SwingConstants.RIGHT);
        mRightOutlineButton.setPreferredSize(new Dimension(mRightOutlineButtonWidth, mRightOutlineButtonHeight));
        mRightOutlineButton.setMinimumSize(new Dimension(mRightOutlineButtonWidth, mRightOutlineButtonHeight));
        mRightOutlineButton.setMaximumSize(new Dimension(mRightOutlineButtonWidth, mRightOutlineButtonHeight));
        mRightOutlineButton.setBackground(color);
        add(mRightOutlineButton, BorderLayout.CENTER);
    }

    public OutlineButton getLeftButton() { return mLeftOutlineButton; }
    public OutlineButton getRightButton() { return mRightOutlineButton; }
    public void setRightButtonVisible(boolean visible) {
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

    public void setLeftButtonVisible(boolean visible) {
        mLeftOutlineButton.setVisible(visible);
        if (visible) {
            mRightOutlineButtonWidth = mWidth - mRightOutlineButtonWidth;
        } else {
            mRightOutlineButtonWidth = mWidth;
        }
        mRightOutlineButtonHeight = mHeight;
        mRightOutlineButton.setPreferredSize(new Dimension(mRightOutlineButtonWidth, mRightOutlineButtonHeight));
        mRightOutlineButton.setMinimumSize(new Dimension(mRightOutlineButtonWidth, mRightOutlineButtonHeight));
        mRightOutlineButton.setMaximumSize(new Dimension(mRightOutlineButtonWidth, mRightOutlineButtonHeight));
    }
}