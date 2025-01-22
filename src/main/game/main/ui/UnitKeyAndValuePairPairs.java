package main.game.main.ui;

import main.graphics.GameUI;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

public class UnitKeyAndValuePairPairs extends GameUI {
    private JPanel mContentPanel = null;

    private UnitKeyAndValuePair mLeftArray = null;
    private UnitKeyAndValuePair mRightArray = null;
    private int mContentPanelHorizontalSpacing = 0;
    public UnitKeyAndValuePairPairs(int width, int height, Color background) {
        super(width, height);

        int arrayWidth = (int) (width * .5);
        int arrayHeight = height;
        mContentPanelHorizontalSpacing = (width - (arrayWidth * 2)) / 3;
        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.X_AXIS));
        mContentPanel.setBackground(background);


        mLeftArray = new UnitKeyAndValuePair(arrayWidth, arrayHeight, background);
        mRightArray = new UnitKeyAndValuePair(arrayWidth, arrayHeight, background);

        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelHorizontalSpacing, 0)));
        mContentPanel.add(mLeftArray);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelHorizontalSpacing, 0)));
        mContentPanel.add(mRightArray);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelHorizontalSpacing, 0)));

        setOpaque(false);
        add(mContentPanel);
    }

    public UnitKeyAndValuePairPairs(int width, int height, int keyWidth, int visibleRows, Color background) {
        super(width, height);

        int arrayWidth = (int) (width * .5);
        int arrayHeight = height;
        mContentPanelHorizontalSpacing = (width - (arrayWidth * 2)) / 3;
        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.X_AXIS));
        mContentPanel.setBackground(background);


        mLeftArray = new UnitKeyAndValuePair(arrayWidth, arrayHeight, keyWidth, visibleRows, background);
        mRightArray = new UnitKeyAndValuePair(arrayWidth, arrayHeight, keyWidth, visibleRows, background);

        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelHorizontalSpacing, 0)));
        mContentPanel.add(mLeftArray);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelHorizontalSpacing, 0)));
        mContentPanel.add(mRightArray);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelHorizontalSpacing, 0)));

        setOpaque(false);
        add(mContentPanel);
    }

    public UnitKeyAndValuePair getLeftArray() { return mLeftArray; }
    public UnitKeyAndValuePair getRightArray() { return mRightArray; }
}
