package main.ui.outline;


import main.constants.StateLock;
import main.game.stores.pools.FontPool;
import main.ui.outline.production.OutlineButton;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineButtonRow extends JPanel {

    private OutlineButton mLeftButton = null;
    private OutlineButton mRightButton = null;
    private StateLock mStateLock = new StateLock();
    //    public OutlineLabelToLabel() { }
    public OutlineButtonRow(int width, int height) { this("", width, height); }
    public OutlineButtonRow(String str, int width, int height) { this(str, "", width, height); }
    public OutlineButtonRow(String str, Color color, int width, int height) {
        this(str, "", color,  width, height);
    }
    public OutlineButtonRow(String left, String right, int width, int height) {
        this(left, right, Color.WHITE, width, height);
    }
    public OutlineButtonRow(String left, String right, Color color, int width, int height) {
        setLayout(new BorderLayout());
//        setLayout(new FlowLayout(FlowLayout.L));
        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);
        removeAll();

        int fontHeight = (int)(height * .9);

//        leftLabel = new OutlineLabel();
        mLeftButton = new OutlineButton();
        mLeftButton.setText(left);
        mLeftButton.setHorizontalAlignment(JLabel.LEFT);
//        leftLabel.setPreferredSize(new Dimension((int) (width * .75), height));
//        leftLabel.setMinimumSize(leftLabel.getPreferredSize());
//        leftLabel.setMaximumSize(leftLabel.getPreferredSize());
        mLeftButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mLeftButton.setBackground(color);
//        leftLabel.setBackground(ColorPalette.getRandomColor());
//        currentTileHeightLabel.setBackground(ColorPalette.getRandomColor());

//        rightLabel = new OutlineLabel();
        mRightButton = new OutlineButton();
        mRightButton.setText(right);
        mRightButton.setHorizontalAlignment(JLabel.RIGHT);
//        rightLabel.setPreferredSize(new Dimension((int) (width  * .25), height));
        mRightButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightButton.setBackground(color);
//        currentTileHeightField.setBackground(keyValuePanel.getBackground());


        add(mLeftButton, BorderLayout.WEST);
        add(mRightButton, BorderLayout.CENTER);
    }
//    private OutlineLabel mLeftLabel = new OutlineLabel();
//    private OutlineButton mRightField = new OutlineButton();
//    private final StateLock mStateLock = new StateLock();
//
//    public OutlineButtonRow(int width, int height) {
//        removeAll();
//        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        setOpaque(true);
//        setLayout(new BorderLayout());
//        setPreferredSize(new Dimension(width, height));
//
//        mLeftLabel = new OutlineLabel();
//        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
//        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
////        mLeftLabel.setBackground(color);
//
//        mRightField = new OutlineButton();
//        mRightField.setHorizontalAlignment(JTextField.RIGHT);
//        mRightField.setFont(FontPool.getInstance().getFontForHeight(height));
//
//        add(mLeftLabel, BorderLayout.WEST);
//        add(mRightField, BorderLayout.CENTER);
//    }
//
//    public OutlineButtonRow(String leftText, Color color, int width, int height) {
//        this(width, height);
//        setBackground(color);
//        setLeftLabel(leftText);
//    }


    public void setLeftLabel(String str) {
        if (!mStateLock.isUpdated("left", str)) { return; }
        mLeftButton.setText(str);
    }
    public void setRightText(String str) {
        if (!mStateLock.isUpdated("right", str)) { return; }
        mRightButton.setText(str);
    }
    public String getRightText() { return mRightButton.getText(); }
    public OutlineButton getButton() { return mRightButton; }
    public void setBackground(Color color) {
        if (mRightButton != null) { mRightButton.setBackground(color);  }
        if (mLeftButton != null) { mLeftButton.setBackground(color);  }
    }
}