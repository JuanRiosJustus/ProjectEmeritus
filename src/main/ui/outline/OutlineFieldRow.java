package main.ui.outline;


import main.constants.StateLock;
import main.game.stores.pools.FontPool;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineFieldRow extends JPanel {
    private OutlineLabel mLeftLabel = new OutlineLabel();
    private OutlineTextField mRightField = new OutlineTextField();
    private final StateLock mStateLock = new StateLock();
    public OutlineFieldRow() { }


    public OutlineFieldRow(int width, int height) { this("", width, height); }
    public OutlineFieldRow(Color color, int width, int height) { this("", color, width, height); }
    public OutlineFieldRow(String leftText, int width, int height) { this(leftText, null, width, height); }
    public OutlineFieldRow(String leftText, Color color, int width, int height) {
        removeAll();
//        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(color);
        setPreferredSize(new Dimension(width, height));

        mLeftLabel = new OutlineLabel(leftText);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
        mLeftLabel.setBackground(color);

        mRightField = new OutlineTextField();
        mRightField.setHorizontalAlignment(JTextField.RIGHT);
        mRightField.setFont(FontPool.getInstance().getFontForHeight(height));
//        mRightField.setBackground(color);

        add(mLeftLabel, BorderLayout.WEST);
        add(mRightField, BorderLayout.CENTER);
    }

//    public void setup(int width, int height) { setup("", width, height); }
//    public void setup(Color color, int width, int height) { setup("", color, width, height); }
//    public void setup(String leftText, int width, int height) { setup(leftText, null, width, height); }
//    public void setup(String leftText, Color color,  int width, int height) {
//        removeAll();
////        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        setOpaque(true);
//        setLayout(new BorderLayout());
//        setBackground(color);
//        setPreferredSize(new Dimension(width, height));
//
//        mLeftLabel = new OutlineLabel(leftText);
//        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
//        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
//        mLeftLabel.setBackground(color);
//
//        mRightField = new OutlineTextField();
//        mRightField.setHorizontalAlignment(JTextField.RIGHT);
//        mRightField.setFont(FontPool.getInstance().getFontForHeight(height));
////        mRightField.setBackground(color);
//
//        add(mLeftLabel, BorderLayout.WEST);
//        add(mRightField, BorderLayout.CENTER);
//    }
    public void setLeftLabel(String str) {
        if (!mStateLock.isUpdated("left", str)) { return; }
        mLeftLabel.setText(str);
    }
    public void setRightText(String str) {
        if (!mStateLock.isUpdated("right", str)) { return; }
        mRightField.setText(str);
    }
    public String getRightText() { return mRightField.getText(); }
    public JTextField getTextField() { return mRightField; }
    public void setBackground(Color color) {
//        if (mRightField != null) { mRightField.setForeground(color);  }
        if (mLeftLabel != null) { mLeftLabel.setBackground(color);  }
    }
}