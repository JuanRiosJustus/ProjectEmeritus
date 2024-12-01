package main.ui.outline;

import main.graphics.GameUI;

import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineTextFieldLabel extends GameUI {

    private int mTextFieldWidth = 0;
    private int mTextFieldHeight = 0;
    private OutlineTextField mTextField = null;
    private int mOutlineLabelWidth = 0;
    private int mOutlineLabelHeight = 0;
    private OutlineLabel mOutlineLabel = null;
    public OutlineTextFieldLabel(int width, int height, Color color) {
        super(width, height);

        setLayout(new BorderLayout());

        mTextFieldWidth = (int) (width * .1);
        mTextFieldHeight = height;
        mTextField = new OutlineTextField();
        mTextField.setAlignmentX(SwingConstants.CENTER);
        mTextField.setPreferredSize(new Dimension(mTextFieldWidth, mTextFieldHeight));
        mTextField.setMinimumSize(new Dimension(mTextFieldWidth, mTextFieldHeight));
        mTextField.setMaximumSize(new Dimension(mTextFieldWidth, mTextFieldHeight));
        mTextField.setBackground(color);
        add(mTextField, BorderLayout.WEST);

        mOutlineLabelWidth = width - mTextFieldWidth;
        mOutlineLabelHeight = height;
        mOutlineLabel = new OutlineLabel();
        mOutlineLabel.setPreferredSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMinimumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMaximumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setBackground(color);
        add(mOutlineLabel, BorderLayout.CENTER);
    }

    public OutlineLabel getLabel() { return mOutlineLabel; }
    public OutlineTextField getCheckBox() { return mTextField; }
    public void setCheckBoxVisible(boolean visible) {
        mTextField.setVisible(visible);
        if (visible) {
            mOutlineLabelWidth = mWidth - mTextFieldWidth;
        } else {
            mOutlineLabelWidth = mWidth;
        }
        mOutlineLabelHeight = mHeight;
        mOutlineLabel.setPreferredSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMinimumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
        mOutlineLabel.setMaximumSize(new Dimension(mOutlineLabelWidth, mOutlineLabelHeight));
    }
}