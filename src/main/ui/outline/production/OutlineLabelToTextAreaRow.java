package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineTextArea;

import javax.swing.*;
import java.awt.*;

public class OutlineLabelToTextAreaRow extends JPanel {
    private OutlineLabel mLeftLabel = null;
    private OutlineTextArea mRightTextArea = null;

    public OutlineLabelToTextAreaRow(String left, String right, Color color, int width, int height) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // Avoid interfering with child backgrounds
        setOpaque(false);

        setBackground(color);

        int fontHeight = height * 9;

        // Left label
        mLeftLabel = new OutlineLabel();
        mLeftLabel.setText(left);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mLeftLabel.setBackground(color);

        // Right text area
        mRightTextArea = new OutlineTextArea();
        mRightTextArea.setText(right);
        mRightTextArea.setBackground(color);
        mRightTextArea.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
        mRightTextArea.setEditable(false);

        add(mLeftLabel);
        add(mRightTextArea);

        int horizontalPadding = (int) (width * 0.01);
        setBorder(BorderFactory.createEmptyBorder(0, horizontalPadding, 0, horizontalPadding));
    }

    public void setOutlineThickness(int thickness) {
        mLeftLabel.setOutlineThickness(thickness);
        mRightTextArea.setOutlineThickness(thickness);
    }

    public void setLeftLabelVisible(boolean isVisible) {
        mLeftLabel.setVisible(isVisible);
        updatePreferredSize();
    }

    public void setLeftLabel(String str) {
        mLeftLabel.setText(str);
        updatePreferredSize();
    }

    public OutlineTextArea getTextArea() { return mRightTextArea; }

    public void setRightField(String str) {
        mRightTextArea.setText(str);
//        mRightLabel.setWrapStyleWord(true);

        // Force preferred size recalculation
        mRightTextArea.revalidate();
        mRightTextArea.repaint();

        updatePreferredSize();
    }

    public int getTextAreaColumns() {
        return mRightTextArea.getColumns();
    }

    public void setFont(Font font) {
        if (mLeftLabel != null) {
            mLeftLabel.setFont(font);
        }
        if (mRightTextArea != null) {
            mRightTextArea.setFont(font);
        }
        updatePreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension leftSize = mLeftLabel.getPreferredSize();
        Dimension rightSize = mRightTextArea.getPreferredSize();

        Insets insets = getInsets();
        int width = leftSize.width + rightSize.width + insets.left + insets.right;
        int height = Math.max(leftSize.height, rightSize.height) + insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    private void updatePreferredSize() {
        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }
}