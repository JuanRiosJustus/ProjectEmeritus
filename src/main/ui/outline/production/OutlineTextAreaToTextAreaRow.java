package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineTextArea;

import javax.swing.*;
import java.awt.*;

public class OutlineTextAreaToTextAreaRow extends JPanel {
    private OutlineTextArea mLeftTextArea = null;
    private OutlineTextArea mRightTextArea = null;

    public OutlineTextAreaToTextAreaRow(String left, String right, Color color, int width, int height) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBackground(color);

        int fontHeight = height * 9;

        // Left OutlineTextArea
        mLeftTextArea = new OutlineTextArea();
        mLeftTextArea.setText(left);
        mLeftTextArea.setEditable(false);
        mLeftTextArea.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mLeftTextArea.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        mLeftTextArea.setBackground(Color.CYAN);
        mLeftTextArea.setTextAlignment(SwingConstants.LEFT);

        // Right OutlineTextArea
        mRightTextArea = new OutlineTextArea();
        mRightTextArea.setText(right);
        mRightTextArea.setEditable(false);
        mRightTextArea.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightTextArea.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        mRightTextArea.setBackground(color);
        mRightTextArea.setTextAlignment(SwingConstants.RIGHT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch horizontally, not vertically
        gbc.weighty = 0; // No vertical stretching
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment for vertical positioning

        // Add the left OutlineTextArea
        gbc.gridx = 0;
        gbc.weightx = 0.5; // Take half the available space
        gbc.anchor = GridBagConstraints.WEST; // Left alignment for horizontal
        add(mLeftTextArea, gbc);

        // Add the right OutlineTextArea
        gbc.gridx = 1;
        gbc.weightx = 0.5; // Take the other half of the available space
        gbc.anchor = GridBagConstraints.EAST; // Right alignment for horizontal
        add(mRightTextArea, gbc);

        int horizontalPadding = (int) (width * 0.01);
        setBorder(BorderFactory.createEmptyBorder(0, horizontalPadding, 0, horizontalPadding));
    }

    public void setOutlineThickness(int thickness) {
        mLeftTextArea.setOutlineThickness(thickness);
        mRightTextArea.setOutlineThickness(thickness);
    }

    public void setLeftLabelVisible(boolean isVisible) {
        mLeftTextArea.setVisible(isVisible);
        updatePreferredSize();
    }

    public void setLeftLabel(String str) {
        mLeftTextArea.setText(str);
        updatePreferredSize();
    }

    public OutlineTextArea getTextArea() {
        return mRightTextArea;
    }

    public void setRightField(String str) {
        mRightTextArea.setText(str);
        mRightTextArea.revalidate();
        mRightTextArea.repaint();
        updatePreferredSize();
    }

    public int getTextAreaColumns() {
        return mRightTextArea.getColumns();
    }

    public void setFont(Font font) {
        if (mLeftTextArea != null) {
            mLeftTextArea.setFont(font);
        }
        if (mRightTextArea != null) {
            mRightTextArea.setFont(font);
        }
        updatePreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension leftSize = mLeftTextArea.getPreferredSize();
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