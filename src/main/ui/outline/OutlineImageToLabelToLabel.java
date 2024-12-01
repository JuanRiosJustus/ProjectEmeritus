package main.ui.outline;

import main.ui.outline.production.OutlineLabelToLabelRow;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class OutlineImageToLabelToLabel extends JPanel {
    protected OutlineLabelToLabelRow mLabelToLabel;
    protected JButton mImage;
    private int mImageHeight;
    private int mImageWidth;
    public OutlineImageToLabelToLabel(int width, int height) { this("", width, height); }
    public OutlineImageToLabelToLabel(String str, int width, int height) { this(str, "", width, height); }
    public OutlineImageToLabelToLabel(String str, Color color, int width, int height) {
        this(str, "", color,  width, height);
    }
    public OutlineImageToLabelToLabel(String left, String right, int width, int height) {
        this(left, right, Color.WHITE, width, height);
    }
    public OutlineImageToLabelToLabel(String left, String right, Color color, int width, int height) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);
        removeAll();

        mImageWidth = height;
        mImageHeight = height;
        mImage = new JButton();
        mImage.setPreferredSize(new Dimension(mImageWidth, mImageHeight));

        int mLabelToLabelWidth = width - mImageWidth;
        int mLabelToLabelHeight = height;
        mLabelToLabel = new OutlineLabelToLabelRow(left, right, color, mLabelToLabelWidth, mLabelToLabelHeight);

        add(mImage, BorderLayout.WEST);
        add(mLabelToLabel, BorderLayout.CENTER);
    }

    public void setLeftLabel(String str) { mLabelToLabel.setLeftLabel(str); }
    public void setRightLabel(String str) {
        mLabelToLabel.setRightLabel(str);
    }
    public int getImageWidth() { return mImageWidth; }
    public int getImageHeight() { return mImageHeight; }
    public JButton getImage() { return mImage; }
    public void setFont(Font font) { if (mLabelToLabel != null) { mLabelToLabel.setFont(font); } }
}
