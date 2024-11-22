package main.ui.outline;


import main.constants.StateLock;
import main.game.stores.pools.FontPool;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class OutlineLabelToButton extends JPanel {
    private OutlineLabel mLeftLabel = new OutlineLabel();
    private OutlineButton mRightField = new OutlineButton();
    private final StateLock mStateLock = new StateLock();
    public OutlineLabelToButton(String leftText, Color color,  int width, int height) {
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(color);
        setPreferredSize(new Dimension(width, height));

        mLeftLabel = new OutlineLabel(leftText);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
//        mLeftLabel.setBackground(color);

        mRightField = new OutlineButton();
        mRightField.setHorizontalAlignment(JTextField.RIGHT);
        mRightField.setFont(FontPool.getInstance().getFontForHeight(height));

        add(mLeftLabel, BorderLayout.WEST);
        add(mRightField, BorderLayout.CENTER);
    }

    public void setLeftLabel(String str) {
        if (!mStateLock.isUpdated("left", str)) { return; }
        mLeftLabel.setText(str);
    }
    public void setRightText(String str) {
        if (!mStateLock.isUpdated("right", str)) { return; }
        mRightField.setText(str);
    }
    public String getRightText() { return mRightField.getText(); }
    public OutlineButton getButton() { return mRightField; }
    public void setBackground(Color color) {
//        if (mRightField != null) { mRightField.setForeground(color);  }
        if (mLeftLabel != null) { mLeftLabel.setBackground(color);  }
    }
}