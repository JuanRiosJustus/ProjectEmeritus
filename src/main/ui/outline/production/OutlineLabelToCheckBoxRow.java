package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.ui.outline.OutlineCheckBox;
import main.ui.outline.OutlineLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class OutlineLabelToCheckBoxRow extends JPanel {
    private OutlineLabel mLeftLabel = new OutlineLabel("");
    private OutlineCheckBox mRightCheckBox = new OutlineCheckBox();
//    public OutlineLabelToLabel() { }
    public OutlineLabelToCheckBoxRow(int width, int height) { this("", width, height); }
    public OutlineLabelToCheckBoxRow(int width, int height, Color color) { this("", color, width, height); }
    public OutlineLabelToCheckBoxRow(String str, int width, int height) { this(str, "", width, height); }
    public OutlineLabelToCheckBoxRow(String str, Color color, int width, int height) {
        this(str, "", color,  width, height);
    }
    public OutlineLabelToCheckBoxRow(String left, String right, int width, int height) {
        this(left, right, Color.WHITE, width, height);
    }
    public OutlineLabelToCheckBoxRow(String left, String right, Color color, int width, int height) {
        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);
        removeAll();

        int fontHeight = (int)(height * .9);

        int leftLabelWidth = (int) (width * .8);
        int leftLabelHeight = (int) (height);
        mLeftLabel = new OutlineLabel();
        mLeftLabel.setText(left);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
        mLeftLabel.setPreferredSize(new Dimension(leftLabelWidth, leftLabelHeight));
        mLeftLabel.setMinimumSize(new Dimension(leftLabelWidth, leftLabelHeight));
        mLeftLabel.setMaximumSize(new Dimension(leftLabelWidth, leftLabelHeight));
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mLeftLabel.setBackground(color);

        int rightCheckBoxWidth = width - leftLabelWidth;
        int rightCheckBoxHeight = height;
        mRightCheckBox = new OutlineCheckBox();
        mRightCheckBox.setPreferredSize(new Dimension(rightCheckBoxWidth, rightCheckBoxHeight));
        mRightCheckBox.setMinimumSize(new Dimension(rightCheckBoxWidth, rightCheckBoxHeight));
        mRightCheckBox.setMaximumSize(new Dimension(rightCheckBoxWidth, rightCheckBoxHeight));
        mRightCheckBox.setText(right);
        mRightCheckBox.setHorizontalAlignment(JLabel.RIGHT);

        mRightCheckBox.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightCheckBox.setBackground(color);

        add(mLeftLabel, BorderLayout.CENTER);
        add(mRightCheckBox, BorderLayout.EAST);
    }
    public void setLeftLabel(String str) {
        mLeftLabel.setText(str);
    }
    public void setRightCheckBox(boolean b) { mRightCheckBox.setSelected(b); }
    public void setFont(Font font) {
        if (mLeftLabel != null) {
            mLeftLabel.setFont(font);
        }
        if (mRightCheckBox != null) {
            mRightCheckBox.setFont(font);
        }
    }
    public OutlineCheckBox getCheckBox() { return mRightCheckBox; }
    public OutlineLabel getLeftLabel() { return mLeftLabel; }

}
