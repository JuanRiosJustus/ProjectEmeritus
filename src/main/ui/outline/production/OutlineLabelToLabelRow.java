package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.ui.outline.OutlineLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;

public class OutlineLabelToLabelRow extends JPanel {
    private OutlineLabel mLeftLabel = null;
    private OutlineLabel mRightLabel = null;
    public OutlineLabelToLabelRow(int width, int height) { this("", width, height); }
    public OutlineLabelToLabelRow(String str, int width, int height) { this(str, "", width, height); }
    public OutlineLabelToLabelRow(String str, Color color, int width, int height) {
        this(str, "", color,  width, height);
    }
    public OutlineLabelToLabelRow(String left, String right, int width, int height) {
        this(left, right, Color.WHITE, width, height);
    }
    public OutlineLabelToLabelRow(String left, String right, Color color, int width, int height) {
        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);

        int fontHeight = (int)(height * .9);

        mLeftLabel = new OutlineLabel();
        mLeftLabel.setText(left);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);

        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mLeftLabel.setBackground(color);

        mRightLabel = new OutlineLabel();
        mRightLabel.setText(right);
        mRightLabel.setHorizontalAlignment(JLabel.RIGHT);

        mRightLabel.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightLabel.setBackground(color);

        add(mLeftLabel, BorderLayout.WEST);
        add(mRightLabel, BorderLayout.CENTER);
    }
    public void setLeftLabel(String str) {
        mLeftLabel.setText(str);
    }
    public void setRightLabel(String str) {
        mRightLabel.setText(str);
    }
    public void setFont(Font font) {
        if (mLeftLabel != null) {
            mLeftLabel.setFont(font);
        }
        if (mRightLabel != null) {
            mRightLabel.setFont(font);
        }
    }
    public OutlineLabel getRightLabel() { return mRightLabel; }
    public OutlineLabel getLeftLabel() { return mLeftLabel; }
}
