package main.game.main.ui;

import main.constants.Pair;
import main.constants.SimpleCheckSum;
import main.graphics.GameUI;
// import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class MainControlsStatisticsSubPanel extends GameUI {
    protected final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    protected Map<String, Pair<JLabel, JLabel>> mRows = new HashMap<>();
    protected JButton mBannerBackButton = null;
    protected JTextField mBannerTextField = null;
    protected int mButtonWidth = -1;
    protected int mButtonHeight = -1;
    protected JPanel mContentPanel = null;
    public MainControlsStatisticsSubPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(width, height);

        setBackground(color);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int bannerWidth = width;
        int bannerHeight = (int) (height * .2);
        JPanel bannerRow = new GameUI();
        bannerRow.setLayout(new BoxLayout(bannerRow, BoxLayout.X_AXIS));
        bannerRow.setPreferredSize(new Dimension(bannerWidth, bannerHeight));
        bannerRow.setMinimumSize(new Dimension(bannerWidth, bannerHeight));
        bannerRow.setMaximumSize(new Dimension(bannerWidth, bannerHeight));
        bannerRow.setBackground(color);

        int bannerBackButtonWidth = (int) (bannerWidth * .2);
        int bannerBackButtonHeight = bannerHeight;
        mBannerBackButton = new OutlineButton();
        mBannerBackButton.setFont(getFontForHeight(bannerBackButtonHeight));
        mBannerBackButton.setPreferredSize(new Dimension(bannerBackButtonWidth, bannerBackButtonHeight));
        mBannerBackButton.setMinimumSize(new Dimension(bannerBackButtonWidth, bannerBackButtonHeight));
        mBannerBackButton.setMaximumSize(new Dimension(bannerBackButtonWidth, bannerBackButtonHeight));
        mBannerBackButton.setText("<");
        mBannerBackButton.setBackground(color);

        int bannerTextFieldWidth = bannerWidth - bannerBackButtonWidth;
        int bannerTextFieldHeight = bannerHeight;
        mBannerTextField = new OutlineTextField();
        mBannerTextField.setFont(getFontForHeight(bannerTextFieldHeight));
        mBannerTextField.setPreferredSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMinimumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMaximumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setText("");
        mBannerTextField.setBackground(color);
        mBannerTextField.setHorizontalAlignment(SwingConstants.CENTER);

        bannerRow.add(mBannerBackButton);
        bannerRow.add(mBannerTextField);

        add(bannerRow);



        mButtonWidth = width;
        mButtonHeight = (height - bannerHeight) / visibleRows;



        int contentWidth = width;
        int contentHeight = height - bannerHeight;
        mContentPanel = new GameUI();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        mContentPanel.setPreferredSize(new Dimension(contentWidth, contentHeight));
//        mContentPanel.setMinimumSize(new Dimension(contentWidth, contentHeight));
//        mContentPanel.setMaximumSize(new Dimension(contentWidth, contentHeight));
        mContentPanel.setBackground(color);


        add(new NoScrollBarPane(mContentPanel, contentWidth, contentHeight, true, 1));

        setBounds(x, y, width, height);
    }

    public void setBannerTitleButton(String value) { mBannerTextField.setText(value); }
    public JButton getBannerBackButton() { return mBannerBackButton; }
    protected void clear() { mRows.clear(); mContentPanel.removeAll(); }

    protected Pair<JLabel, JLabel> getOrCreateRow(String name) {
        Pair<JLabel, JLabel> row = mRows.get(name);
        if (row != null) { return row; }

        int containerWidth = (int) (mButtonWidth * .98);
        int containerHeight = mButtonHeight;

        Font rowFontSize = getFontForHeight((int) (containerHeight * .8));
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(containerWidth, containerHeight));
        container.setMinimumSize(new Dimension(containerWidth, containerHeight));
        container.setMaximumSize(new Dimension(containerWidth, containerHeight));
//        container.setBackground(ColorPalette.getRandomColor());
        container.setBackground(getBackground());

        // Create Button (Left Side);
        int leftKeyWidth = (int) (containerWidth * .7);
        int leftKeyHeight = (int) containerHeight;

        JLabel leftKey = new OutlineLabel();
        leftKey.setBackground(getBackground());
        leftKey.setFont(rowFontSize);
        leftKey.setPreferredSize(new Dimension(leftKeyWidth, leftKeyHeight));
        leftKey.setMinimumSize(new Dimension(leftKeyWidth, leftKeyHeight));
        leftKey.setMaximumSize(new Dimension(leftKeyWidth, leftKeyHeight));
        leftKey.setBackground(getBackground());
        leftKey.setHorizontalAlignment(SwingConstants.LEFT);
        leftKey.setText(name);

        int valueButtonWidth = containerWidth - leftKeyWidth;
        int valueButtonHeight = leftKeyHeight;
        JLabel rightValue = new OutlineLabel();
        rightValue.setBackground(getBackground());
        rightValue.setFont(rowFontSize);
        rightValue.setPreferredSize(new Dimension(valueButtonWidth, valueButtonHeight));
        rightValue.setMinimumSize(new Dimension(valueButtonWidth, valueButtonHeight));
        rightValue.setMaximumSize(new Dimension(valueButtonWidth, valueButtonHeight));
        rightValue.setHorizontalAlignment(SwingConstants.RIGHT);
        rightValue.setText("???");


        container.add(leftKey);
        container.add(rightValue);

        mContentPanel.add(container);
        row = new Pair<>(leftKey, rightValue);
        mRows.put(name, row);

        return row;
    }
}
