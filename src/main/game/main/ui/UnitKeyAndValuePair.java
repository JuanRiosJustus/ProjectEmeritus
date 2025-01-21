package main.game.main.ui;

import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.RandomUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

public class UnitKeyAndValuePair extends GameUI {
    private JPanel mContentPanel = null;
    private int mContentPanelVerticalSpacing = 0;
    public UnitKeyAndValuePair(int width, int height, int rowHeight, Color background) {
        super(width, height);

        mContentPanelVerticalSpacing = (int) (height * .015);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        statsPanel.setPreferredSize(new Dimension(statsPanelWidth, statsPanelHeight));
//        statsPanel.setMinimumSize(new Dimension(statsPanelWidth, statsPanelHeight));
//        statsPanel.setMaximumSize(new Dimension(statsPanelWidth, statsPanelHeight));
        mContentPanel.setBackground(background);
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));

        int rowWidth = width;

        for (int i = 0; i < 10; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setPreferredSize(new Dimension(rowWidth, rowHeight));
            row.setMinimumSize(new Dimension(rowWidth, rowHeight));
            row.setMaximumSize(new Dimension(rowWidth, rowHeight));
            row.setOpaque(false);


            int panelItemIconWidth = (int) (rowWidth * .2);
            int panelItemIconHeight = height;
            JButton panelItemIcon = new OutlineButton(RandomUtils.createRandomName(1, 2));
            panelItemIcon.setPreferredSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
            panelItemIcon.setMinimumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
            panelItemIcon.setMaximumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
//            panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemIconHeight));
            panelItemIcon.setBackground(background);

            int panelItemDataWidth = (int) (rowWidth * .75);
            int panelItemDataHeight = height;
            JButton panelItemData = new OutlineButton(RandomUtils.createRandomName(4, 7));
            panelItemData.setPreferredSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
            panelItemData.setMinimumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
            panelItemData.setMaximumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
//            panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemDataHeight));
            panelItemData.setBackground(background);


            int mContentPanelSpacing = (int) ((rowWidth * .05) / 3);
            row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
            row.add(panelItemIcon);
            row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
            row.add(panelItemData);
            row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));

            mContentPanel.add(row);
            mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));
        }

        setOpaque(false);
        add(new NoScrollBarPane(mContentPanel, width, height, true, 1));
    }
    public UnitKeyAndValuePair(int width, int height, Color background) {
        super(width, height);

        mContentPanelVerticalSpacing = (int) (height * .0125);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        statsPanel.setPreferredSize(new Dimension(statsPanelWidth, statsPanelHeight));
//        statsPanel.setMinimumSize(new Dimension(statsPanelWidth, statsPanelHeight));
//        statsPanel.setMaximumSize(new Dimension(statsPanelWidth, statsPanelHeight));
        mContentPanel.setBackground(background);
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));

        int rowWidth = width;
        int rowHeight = height / 5 - (mContentPanelVerticalSpacing );

        for (int i = 0; i < 10; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setPreferredSize(new Dimension(rowWidth, rowHeight));
            row.setMinimumSize(new Dimension(rowWidth, rowHeight));
            row.setMaximumSize(new Dimension(rowWidth, rowHeight));
            row.setOpaque(false);


            int panelItemIconWidth = (int) (rowWidth * .2);
            int panelItemIconHeight = height;
            JButton panelItemIcon = new OutlineButton(RandomUtils.createRandomName(1, 2));
            panelItemIcon.setPreferredSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
            panelItemIcon.setMinimumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
            panelItemIcon.setMaximumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
//            panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemIconHeight));
            panelItemIcon.setBackground(background);

            int panelItemDataWidth = (int) (rowWidth * .75);
            int panelItemDataHeight = height;
            JButton panelItemData = new OutlineButton(RandomUtils.createRandomName(4, 7));
            panelItemData.setPreferredSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
            panelItemData.setMinimumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
            panelItemData.setMaximumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
//            panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemDataHeight));
            panelItemData.setBackground(background);


            int mContentPanelSpacing = (int) ((rowWidth * .05) / 3);
            row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
            row.add(panelItemIcon);
            row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
            row.add(panelItemData);
            row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));

            mContentPanel.add(row);
            mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));
        }

        setOpaque(false);
        add(new NoScrollBarPane(mContentPanel, width, height, true, 1));
    }
}
