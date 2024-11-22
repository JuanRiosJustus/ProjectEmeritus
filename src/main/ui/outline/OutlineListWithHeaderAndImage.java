package main.ui.outline;

import main.game.stores.pools.ColorPalette;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineListWithHeaderAndImage extends JPanel {
    private final Color mainColor;
    private final int mImageWidth;
    private final int mImageHeight;
    private final JButton mImage;
    private final int mListWidth;
    private final int mListHeight;
    private final int mAlignment;
    private final OutlineListWithHeader mListWithHeader;

    public OutlineListWithHeaderAndImage(int width, int height) {
        this(ColorPalette.getRandomColor(), width, height, SwingConstants.CENTER);
    }

    public OutlineListWithHeaderAndImage(Color mainColor, int width, int height, int alignment) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width, height));
        setOpaque(true);
        this.mainColor = mainColor;

        mAlignment = alignment;

        mImageWidth = height;
        mImageHeight = height;
        mImage = new JButton();
        mImage.setPreferredSize(new Dimension(mImageWidth, mImageHeight));

        mListWidth = width - mImageWidth;
        mListHeight = height;
        mListWithHeader = new OutlineListWithHeader(mainColor, mListWidth, mListHeight, alignment);

        // Add components to the main tile UI panel
        add(mListWithHeader, BorderLayout.WEST);
        add(mImage, BorderLayout.CENTER);
        setBackground(mainColor);
    }

    public OutlineListWithHeader getList() { return mListWithHeader; }
//    public void updateHeader(String value) { mListWithHeader.updateHeader(value); }
//    public OutlineLabel updateRow(String key, String value) {
//        return mListWithHeader.updateRow(key, value);
//    }
//
//    public OutlineLabelToLabel updateRowV2(String key, String left, String right) {
//        return mListWithHeader.updateRowV2(key, left, right);
//    }
//    public OutlineLabelToLabel updateRowV2(String key, String left, String right) {
//        return mListWithHeader.updateRowV2(key, left, right);
//    }
    public JButton getImage() { return mImage; }
    public int getImageWidth() { return mImageWidth; }
    public int getImageHeight() { return mImageHeight; }
    public void updateHeader(String header) { mListWithHeader.updateHeader(header); }
}