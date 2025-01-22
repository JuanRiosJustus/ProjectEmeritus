package main.game.main.ui;

import main.constants.Quadruple;
import main.constants.Tuple;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.ResourceBar;
import main.ui.outline.OutlineLabel;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.RandomUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class UnitKeyAndValuePair extends GameUI {
    private JPanel mContentPanel = null;
    private Map<String, Tuple<JPanel, JButton, JButton>> map = new LinkedHashMap<>();
    private int mContentPanelVerticalSpacing = 0;
    private int mRowWidth = 0;
    private int mRowHeight = 0;
    private int mKeyWidth = 0;
    private static final int ROWS_TO_SHOW_AT_ALL_TIMES = 5;
    public UnitKeyAndValuePair(int width, int height, Color background) {
        this(width, height, (int) (width * .3), ROWS_TO_SHOW_AT_ALL_TIMES, background);
    }

    public UnitKeyAndValuePair(int width, int height, int keyWidth, int visibleRows, Color background) {
        super(width, height);

        setBackground(background);
        mContentPanelVerticalSpacing = (int) (height * .0125);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        statsPanel.setPreferredSize(new Dimension(statsPanelWidth, statsPanelHeight));
//        statsPanel.setMinimumSize(new Dimension(statsPanelWidth, statsPanelHeight));
//        statsPanel.setMaximumSize(new Dimension(statsPanelWidth, statsPanelHeight));
        mContentPanel.setBackground(background);
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));

        mRowWidth = width;
        mRowHeight = (height / visibleRows) - (mContentPanelVerticalSpacing * 1);
        mKeyWidth = keyWidth;

        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));

        setOpaque(false);
        add(new NoScrollBarPane(mContentPanel, width, height, true, 1));
    }

    public void clear() {
        map.clear();
        mContentPanel.removeAll();
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));
    }

    public Tuple<JPanel, JButton, JButton> createRow(String name) {
        Tuple<JPanel, JButton, JButton> components = map.get(name);

        if (components != null) {
            return components;
        } else {
            components = addRow(name);
            map.put(name, components);
        }


        mContentPanel.add(components.getFirst());
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));

        return components;
    }
//    private Tuple<JPanel, JButton, JButton> addRow(String rowId) {
//        Tuple<JPanel, JButton, JButton> result = null;
//
//        int totalRowSpace = (int) (mRowWidth * .95);
//
//        JPanel row = new JPanel();
//        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
//        row.setPreferredSize(new Dimension(mRowWidth, mRowHeight));
//        row.setMinimumSize(new Dimension(mRowWidth, mRowHeight));
//        row.setMaximumSize(new Dimension(mRowWidth, mRowHeight));
////        row.setBackground(ColorPalette.getRandomColor());
//        row.setOpaque(false);
//
//        int panelItemIconWidth = (int) mKeyWidth;
//        int panelItemIconHeight = mRowHeight;
//        JButton panelItemIcon = new OutlineButton(RandomUtils.createRandomName(1, 2));
//        panelItemIcon.setPreferredSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
//        panelItemIcon.setMinimumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
//        panelItemIcon.setMaximumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
//        panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemIconHeight));
//        panelItemIcon.setBackground(getBackground());
//
//        int panelItemDataWidth = totalRowSpace - panelItemIconWidth;
//        int panelItemDataHeight = mRowHeight;
//        JButton panelItemData = new OutlineButton(rowId + "_ITEM_DATA");
//        panelItemData.setPreferredSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
//        panelItemData.setMinimumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
//        panelItemData.setMaximumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
//        panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemDataHeight));
//        panelItemData.setBackground(getBackground());
//
//
//        int mContentPanelSpacing = (int) ((mRowWidth * .05) / 3);
//        row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
//        row.add(panelItemIcon);
//        row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
//        row.add(panelItemData);
//        row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
//
//        result = new Tuple<>(row, panelItemIcon, panelItemData);
//
//        return result;
//    }

    private Tuple<JPanel, JButton, JButton> addRow(String rowId) {
        Tuple<JPanel, JButton, JButton> result = null;

        int totalRowSpace = (int) (mRowWidth * .95);

        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setPreferredSize(new Dimension(mRowWidth, mRowHeight * 2));
        block.setMinimumSize(new Dimension(mRowWidth, mRowHeight * 2));
        block.setMaximumSize(new Dimension(mRowWidth, mRowHeight * 2));
        block.setOpaque(false);
        block.setBackground(ColorPalette.getRandomColor());

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setPreferredSize(new Dimension(mRowWidth, mRowHeight));
        row.setMinimumSize(new Dimension(mRowWidth, mRowHeight));
        row.setMaximumSize(new Dimension(mRowWidth, mRowHeight));
        row.setBackground(ColorPalette.getRandomColor());
        row.setOpaque(false);

        int panelItemIconWidth = (int) mKeyWidth;
        int panelItemIconHeight = mRowHeight;
        JButton panelItemIcon = new OutlineButton(RandomUtils.createRandomName(1, 2));
        panelItemIcon.setPreferredSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setMinimumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setMaximumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemIconHeight));
        panelItemIcon.setBackground(getBackground());

        int panelItemDataWidth = totalRowSpace - panelItemIconWidth;
        int panelItemDataHeight = mRowHeight;
        JButton panelItemData = new OutlineButton(rowId + "_ITEM_DATA");
        panelItemData.setPreferredSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setMinimumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setMaximumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemIcon.setFont(FontPool.getInstance().getFontForHeight(panelItemDataHeight));
        panelItemData.setBackground(getBackground());



        int mContentPanelSpacing = (int) ((mRowWidth * .05) / 3);
        row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        row.add(panelItemIcon);
        row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        row.add(panelItemData);
        row.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));



        block.add(row);


        int rowWidth = panelItemIconWidth + mContentPanelSpacing + panelItemDataWidth;
        int rowHeight = (int) (panelItemIconHeight * .9);
        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        bottomRow.setPreferredSize(new Dimension(rowWidth, rowHeight));
        bottomRow.setMinimumSize(new Dimension(rowWidth, rowHeight));
        bottomRow.setMaximumSize(new Dimension(rowWidth, rowHeight));
        bottomRow.setOpaque(false);

        JButton bigButton = new OutlineButton("TTTT");
        bigButton.setPreferredSize(new Dimension(totalRowSpace, (int) (mRowHeight * .9)));
        bigButton.setMinimumSize(new Dimension(totalRowSpace, (int) (mRowHeight * .9)));
        bigButton.setMaximumSize(new Dimension(totalRowSpace, (int) (mRowHeight * .9)));
        bigButton.setBackground(getBackground());

//        bottomRow.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        bottomRow.add(bigButton);
        bottomRow.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));


        block.add(bottomRow);




        panelItemData.addActionListener(e -> {
            bottomRow.setVisible(false);
            block.setPreferredSize(new Dimension(mRowWidth, mRowHeight));
            block.setMinimumSize(new Dimension(mRowWidth, mRowHeight));
            block.setMaximumSize(new Dimension(mRowWidth, mRowHeight));
        });



        result = new Tuple<>(block, panelItemIcon, panelItemData);

        return result;
    }
}
