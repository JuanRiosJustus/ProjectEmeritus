package main.ui.outline;


import main.constants.StateLock;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public class OutlineRowsToImageRow extends GameUI {

    private OutlineButton mLeftButton = null;
    private OutlineButton mRightImage = null;
    private StateLock mStateLock = new StateLock();
    private JPanel mDropDownFields = null;
    private OutlineLabel mHeaderField = null;
    private int mDropDownWidths = 0;
    private int mDropDownHeights = 0;
    private int mRightImageWidth = 0;
    private int mRightImageHeight = 0;
    //    public OutlineLabelToLabel() { }
//    public OutlineDropDownsToImageRow(int width, int height) { this("", width, height); }
//    public OutlineDropDownsToImageRow(String str, int width, int height) { this(str, "", width, height); }
//    public OutlineDropDownsToImageRow(String left, String right, int width, int height) {
////        this(left, right, Color.WHITE, width, height);
//    }

    private int mRowHeights = 0;
    protected Color mColor = null;
    public OutlineRowsToImageRow(Color color, int width, int height, int rowHeights) {
        super(width, height);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

//        setLayout(new BorderLayout());
//        setLayout(new FlowLayout());
//        setLayout(new FlowLayout(FlowLayout.L));
        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);
        removeAll();

        mColor = color;
        int fontHeight = (int)(height * .9);
        mRowHeights = rowHeights;



        mRightImageWidth = height;
        mRightImageHeight = height;
        mRightImage = new OutlineButton();
        mRightImage.setOpaque(true);
        mRightImage.setHorizontalAlignment(JLabel.RIGHT);
        mRightImage.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightImage.setPreferredSize(new Dimension(mRightImageWidth, mRightImageHeight));
        mRightImage.setMinimumSize(new Dimension(mRightImageWidth, mRightImageHeight));
        mRightImage.setMaximumSize(new Dimension(mRightImageWidth, mRightImageHeight));
        mRightImage.setBackground(color);
//        currentTileHeightField.setBackground(keyValuePanel.getBackground());


        int contentsPanelWidth = width - mRightImageWidth;
        int contentsPanelHeight = height;
        JPanel rowPanels = new GameUI(contentsPanelWidth, contentsPanelHeight);
        rowPanels.setBackground(color);
        rowPanels.setLayout(new BoxLayout(rowPanels, BoxLayout.Y_AXIS));


        int contentsHeaderWidth = contentsPanelWidth;
        int contentsHeaderHeight = rowHeights;
        mHeaderField = new OutlineLabel();
        mHeaderField.setHorizontalAlignment(SwingConstants.CENTER);
        mHeaderField.setText("HEADER");
        mHeaderField.setOpaque(true);
        mHeaderField.setBackground(color);
        mHeaderField.setPreferredSize(new Dimension(contentsHeaderWidth, contentsHeaderHeight));
        mHeaderField.setMinimumSize(new Dimension(contentsHeaderWidth, contentsHeaderHeight));
        mHeaderField.setMaximumSize(new Dimension(contentsHeaderWidth, contentsHeaderHeight));
        mHeaderField.setFont(FontPool.getInstance().getFontForHeight(contentsHeaderHeight));



        int rowsWidth = contentsPanelWidth;
        int rowsHeight = contentsPanelHeight - contentsHeaderHeight;

        mDropDownWidths = contentsHeaderWidth;
        mDropDownHeights = contentsHeaderHeight;

        mDropDownFields = new JPanel();
        mDropDownFields.setBackground(color);
        mDropDownFields.setLayout(new BoxLayout(mDropDownFields, BoxLayout.Y_AXIS));

        rowPanels.add(mHeaderField);
        rowPanels.add(new NoScrollBarPane(mDropDownFields, rowsWidth, rowsHeight, true, 1));

//        addDropDownField("Value");
//        addDropDownField("Value2");
//        addDropDownField("Value3");
//        addDropDownField("Value3");
//        addDropDownField("Value3");


//        mLeftButton = new OutlineButton();
//        mLeftButton.setText(left);
//        mLeftButton.setHorizontalAlignment(JLabel.LEFT);
//        mLeftButton.setPreferredSize(new Dimension(labelWidth, labelHeight));
////        leftLabel.setPreferredSize(new Dimension((int) (width * .75), height));
////        leftLabel.setMinimumSize(leftLabel.getPreferredSize());
////        leftLabel.setMaximumSize(leftLabel.getPreferredSize());
//        mLeftButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
//        mLeftButton.setBackground(color);
//        leftLabel.setBackground(ColorPalette.getRandomColor());
//        currentTileHeightLabel.setBackground(ColorPalette.getRandomColor());


        add(rowPanels);
        add(mRightImage);
    }

    public int getImageWidth() { return mRightImageWidth; }
    public int getImageHeight() { return mRightImageHeight; }
    public JButton getImageContainer() { return mRightImage; }
    public void setHeaderField(String value) { mHeaderField.setText(value); }
//    public OutlineDropDownsToImageRow(Color color, int width, int height, int rowHeights) {
//        super(width, height);
//
//        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//
////        setLayout(new BorderLayout());
////        setLayout(new FlowLayout());
////        setLayout(new FlowLayout(FlowLayout.L));
//        setPreferredSize(new Dimension(width, height));
//        setBackground(color);
//        setOpaque(true);
//        removeAll();
//
//        mColor = color;
//
//        int fontHeight = (int)(height * .9);
//        mRowHeights = rowHeights;
//        int imageSize = Math.min(width, height);
//
////        leftLabel = new OutlineLabel();
//
////        rightLabel = new OutlineLabel();
//        int imageWidth = height;
//        int imageHeight = height;
//        mRightButton = new OutlineButton();
//        mRightButton.setHorizontalAlignment(JLabel.RIGHT);
////        rightLabel.setPreferredSize(new Dimension((int) (width  * .25), height));
//        mRightButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
//        mRightButton.setPreferredSize(new Dimension(imageWidth, imageHeight));
//        mRightButton.setMinimumSize(new Dimension(imageWidth, imageHeight));
//        mRightButton.setMaximumSize(new Dimension(imageWidth, imageHeight));
//        mRightButton.setBackground(color);
//        mRightButton.setFocusPainted(false);
//        mRightButton.setBorderPainted(false);
////        currentTileHeightField.setBackground(keyValuePanel.getBackground());
//
//        int rowsWidth = width - imageWidth;
//        int rowsHeight = (height);
//
//        mLeftRows = new JPanel();
//        mLeftRows.setBackground(color);
//        mLeftRows.setLayout(new BoxLayout(mLeftRows, BoxLayout.Y_AXIS));
//
//        addDropDown("Value");
//        addDropDown("Value2");
//        addDropDown("Value3");
//        addDropDown("Value3");
//        addDropDown("Value3");
////        mLeftButton = new OutlineButton();
////        mLeftButton.setText(left);
////        mLeftButton.setHorizontalAlignment(JLabel.LEFT);
////        mLeftButton.setPreferredSize(new Dimension(labelWidth, labelHeight));
//////        leftLabel.setPreferredSize(new Dimension((int) (width * .75), height));
//////        leftLabel.setMinimumSize(leftLabel.getPreferredSize());
//////        leftLabel.setMaximumSize(leftLabel.getPreferredSize());
////        mLeftButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
////        mLeftButton.setBackground(color);
////        leftLabel.setBackground(ColorPalette.getRandomColor());
////        currentTileHeightLabel.setBackground(ColorPalette.getRandomColor());
//
//
//        add(new NoScrollBarPane(mLeftRows, rowsWidth, rowsHeight, true, 1));
//        add(mRightButton);
//    }

    private Map<String, JComponent> mDropDownRows = new LinkedHashMap<>();

    public JComponent getDropDownField(String key) {
        JComponent row = mDropDownRows.get(key);
        return row;
    }

    public JComponent addDropDownField(String key) {
        return addDropDownField(key, null);
    }
    public JComponent addDropDownField(String key, JComponent row) {
        row.setPreferredSize(new Dimension(mDropDownWidths, mDropDownHeights));
        row.setMinimumSize(new Dimension(mDropDownWidths, mDropDownHeights));
        row.setMaximumSize(new Dimension(mDropDownWidths, mDropDownHeights));

        mDropDownFields.add(row);
        mDropDownRows.put(key, row);

        mDropDownFields.revalidate();
        mDropDownFields.repaint();
        return row;
    }



//    public OutlineDropDownsToImageRow(Color color, int width, int height, int rowHeights) {
//        setLayout(new BorderLayout());
////        setLayout(new FlowLayout());
////        setLayout(new FlowLayout(FlowLayout.L));
//        setPreferredSize(new Dimension(width, height));
//        setBackground(color);
//        setOpaque(true);
//        removeAll();
//
//        int fontHeight = (int)(height * .9);
//        int imageSize = Math.min(width, height);
//
////        leftLabel = new OutlineLabel();
//
////        rightLabel = new OutlineLabel();
//        int imageWidth = height;
//        int imageHeight = height;
//        mRightButton = new OutlineButton();
//        mRightButton.setHorizontalAlignment(JLabel.RIGHT);
////        rightLabel.setPreferredSize(new Dimension((int) (width  * .25), height));
//        mRightButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
//        mRightButton.setPreferredSize(new Dimension(imageWidth, imageHeight));
//        mRightButton.setBackground(color);
//        mRightButton.setFocusPainted(false);
//        mRightButton.setBorderPainted(false);
////        currentTileHeightField.setBackground(keyValuePanel.getBackground());
//
//        int labelWidth = width - imageWidth;
//        int labelHeight = (height);
//
//        mLeftButton = new OutlineButton();
//        mLeftButton.setText(left);
//        mLeftButton.setHorizontalAlignment(JLabel.LEFT);
//        mLeftButton.setPreferredSize(new Dimension(labelWidth, labelHeight));
////        leftLabel.setPreferredSize(new Dimension((int) (width * .75), height));
////        leftLabel.setMinimumSize(leftLabel.getPreferredSize());
////        leftLabel.setMaximumSize(leftLabel.getPreferredSize());
//        mLeftButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
//        mLeftButton.setBackground(color);
////        leftLabel.setBackground(ColorPalette.getRandomColor());
////        currentTileHeightLabel.setBackground(ColorPalette.getRandomColor());
//
//
//        add(mLeftButton, BorderLayout.WEST);
//        add(mRightButton, BorderLayout.CENTER);
//    }
//
//

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
        mRightImage.setText(str);
    }
    public String getRightText() { return mRightImage.getText(); }
    public OutlineButton getButton() { return mRightImage; }
    public void setBackground(Color color) {
        if (mRightImage != null) { mRightImage.setBackground(color);  }
        if (mLeftButton != null) { mLeftButton.setBackground(color);  }
    }
}