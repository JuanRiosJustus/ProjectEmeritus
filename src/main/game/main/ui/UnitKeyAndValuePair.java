package main.game.main.ui;

import main.constants.Quadruple;
import main.game.stores.pools.ColorPaletteV1;
import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineTextArea;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.RandomUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public class UnitKeyAndValuePair extends GameUI {
    private JPanel mContentPanel = null;
//    private Map<String, Quadruple<JPanel, JButton, JButton, JTextArea>> map = new LinkedHashMap<>();
    private Map<String, Quadruple<JPanel, JButton, JLabel, JTextArea>> map = new LinkedHashMap<>();
    private int mContentPanelVerticalSpacing = 0;
    private int mRowWidth = 0;
    private int mRowHeight = 0;
    private int mKeyWidth = 0;
    private static final int ROWS_TO_SHOW_AT_ALL_TIMES = 5;
    public UnitKeyAndValuePair(int width, int height, Color background) {
        this(width, height, (int) (width * .3), ROWS_TO_SHOW_AT_ALL_TIMES, background);
    }

    public UnitKeyAndValuePair(int width, int height, int visibleRows, Color background) {
        this(width, height, (int) (width * .3), visibleRows, background);
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

    public Quadruple<JPanel, JButton, JLabel, JTextArea> createTextAreaRow(String name) {
        Quadruple<JPanel, JButton, JLabel, JTextArea> components = map.get(name);

        if (components != null) {
            return components;
        } else {
            components = addTextAreaRow(name);
            map.put(name, components);
        }


        mContentPanel.add(components.getFirst());
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));

        return components;
    }

//    public Tuple<JPanel, JButton, JButton> createRow(String name) {
//        Tuple<JPanel, JButton, JButton> components = map.get(name);
//
//        if (components != null) {
//            return components;
//        } else {
//            components = addRow(name);
//            map.put(name, components);
//        }
//
//
//        mContentPanel.add(components.getFirst());
//        mContentPanel.add(Box.createRigidArea(new Dimension(0, mContentPanelVerticalSpacing)));
//
//        return components;
//    }

    private Quadruple<JPanel, JButton, JLabel, JTextArea> addTextAreaRow(String rowId) {
        Quadruple<JPanel, JButton, JLabel, JTextArea> result = null;

        int totalRowWidthForContent = (int) (mRowWidth * .95);
        int totalRowHeightForContent = (int) (mRowHeight * .9);
//        int totalRowHeightPadding = mRowHeight = totalRowHeightForContent;
//        int totalRowWidthForPadding = mRowWidth - totalRowWidthForContent;

        int expandedRowHeight = mRowHeight * 3;
        int expandedRowWidth = mRowWidth;

        int closedRowWidth = mRowWidth;
        int closedRowHeight = mRowHeight;

        JPanel expandablePanel = new JPanel();
        expandablePanel.setLayout(new BoxLayout(expandablePanel, BoxLayout.Y_AXIS));
        expandablePanel.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
        expandablePanel.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
        expandablePanel.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
        expandablePanel.setOpaque(false);
        expandablePanel.setBackground(ColorPaletteV1.getRandomColor());

        JPanel panelRow = new JPanel();
        panelRow.setLayout(new BoxLayout(panelRow, BoxLayout.X_AXIS));
        panelRow.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
        panelRow.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
        panelRow.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
        panelRow.setBackground(ColorPaletteV1.getRandomColor());
        panelRow.setOpaque(false);

        int panelItemIconWidth = mKeyWidth;
        int panelItemIconHeight = totalRowHeightForContent;
        JButton panelItemIcon = new OutlineButton(RandomUtils.createRandomName(1, 2));
        panelItemIcon.setPreferredSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setMinimumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setMaximumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setFont(FontPoolV1.getInstance().getFontForHeight(panelItemIconHeight));
        panelItemIcon.setBackground(getBackground());
        SwingUiUtils.setHoverEffect(panelItemIcon);

        int panelItemDataWidth = totalRowWidthForContent - panelItemIconWidth;
        int panelItemDataHeight = totalRowHeightForContent;
        JLabel panelItemData = new OutlineLabel(rowId + "_ITEM_DATA");
        panelItemData.setPreferredSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setMinimumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setMaximumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
//        panelItemData.setBorderPainted(false);
//        panelItemData.setFocusPainted(false);
//        panelItemData.setFont(FontPool.getInstance().getFontForHeight(panelItemDataHeight));
        panelItemData.setBackground(getBackground());


        int panelItemRowSpacing = (int) ((mRowWidth - totalRowWidthForContent) / 3);
        panelRow.add(Box.createRigidArea(new Dimension(panelItemRowSpacing, 0)));
        panelRow.add(panelItemIcon);
        panelRow.add(Box.createRigidArea(new Dimension(panelItemRowSpacing, 0)));
        panelRow.add(panelItemData);
        panelRow.add(Box.createRigidArea(new Dimension(panelItemRowSpacing, 0)));



        expandablePanel.add(panelRow);


        JTextArea textArea = new OutlineTextArea();
        textArea.setText("This is some text and i wonder how this is going to look");
//        textArea.setPreferredSize(new Dimension(totalRowWidthForContent, totalRowHeightForContent));
//        textArea.setMinimumSize(new Dimension(totalRowWidthForContent, totalRowHeightForContent));
//        textArea.setMaximumSize(new Dimension(totalRowWidthForContent, totalRowHeightForContent));
        textArea.setFont(FontPoolV1.getInstance().getFontForHeight((int) (totalRowHeightForContent * .8)));
        textArea.setEditable(false);
        textArea.setOpaque(true);
        textArea.setBackground(getBackground());

        int bottomRowHeight = expandedRowHeight - closedRowHeight;
        int bottomRowWidth = totalRowWidthForContent;
        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        bottomRow.setPreferredSize(new Dimension(bottomRowWidth, bottomRowHeight));
        bottomRow.setMinimumSize(new Dimension(bottomRowWidth, bottomRowHeight));
        bottomRow.setMaximumSize(new Dimension(bottomRowWidth, bottomRowHeight));
        bottomRow.setOpaque(false);

        int bottomRowSpacingWidth =((mRowWidth - totalRowWidthForContent) / 2);
        bottomRow.add(Box.createRigidArea(new Dimension(bottomRowSpacingWidth, 0)));
//        bottomRow.add(new NoScrollBarPane(textArea, bottomRowWidth, bottomRowHeight, true, 1));
        bottomRow.add(textArea);
        bottomRow.add(Box.createRigidArea(new Dimension(bottomRowSpacingWidth, 0)));

        expandablePanel.add(bottomRow);


        bottomRow.setVisible(false);
        panelItemIcon.addActionListener(e -> {
            if (!bottomRow.isVisible()) {
                bottomRow.setVisible(true);
                expandablePanel.setPreferredSize(new Dimension(expandedRowWidth, expandedRowHeight));
                expandablePanel.setMinimumSize(new Dimension(expandedRowWidth, expandedRowHeight));
                expandablePanel.setMaximumSize(new Dimension(expandedRowWidth, expandedRowHeight));
            } else {
                bottomRow.setVisible(false);
                expandablePanel.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
                expandablePanel.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
                expandablePanel.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
            }
        });

//        panelItemData.addActionListener(e -> {
//            bottomRow.setVisible(!bottomRow.isVisible());
//            if (bottomRow.isVisible()) {
//                expandablePanel.setPreferredSize(new Dimension(expandedRowWidth, expandedRowHeight));
//                expandablePanel.setMinimumSize(new Dimension(expandedRowWidth, expandedRowHeight));
//                expandablePanel.setMaximumSize(new Dimension(expandedRowWidth, expandedRowHeight));
//            } else {
//                expandablePanel.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
//                expandablePanel.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
//                expandablePanel.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
//            }
//        });



        result = new Quadruple<>(expandablePanel, panelItemIcon, panelItemData, textArea);

        return result;
    }

    public int getRowHeight() { return mRowHeight; }
    public int getRowWidth() { return mRowWidth; }

    private Quadruple<JPanel, JButton, JButton, JTextArea> addRowWithButtonsValue(String rowId) {
        Quadruple<JPanel, JButton, JButton, JTextArea> result = null;

        int totalRowWidthForContent = (int) (mRowWidth * .95);
        int totalRowHeightForContent = (int) (mRowHeight * .9);
//        int totalRowHeightPadding = mRowHeight = totalRowHeightForContent;
//        int totalRowWidthForPadding = mRowWidth - totalRowWidthForContent;

        int expandedRowHeight = mRowHeight * 2;
        int expandedRowWidth = mRowWidth;

        int closedRowWidth = mRowWidth;
        int closedRowHeight = mRowHeight;

        JPanel expandablePanel = new JPanel();
        expandablePanel.setLayout(new BoxLayout(expandablePanel, BoxLayout.Y_AXIS));
        expandablePanel.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
        expandablePanel.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
        expandablePanel.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
        expandablePanel.setOpaque(false);
        expandablePanel.setBackground(ColorPaletteV1.getRandomColor());

        JPanel panelRow = new JPanel();
        panelRow.setLayout(new BoxLayout(panelRow, BoxLayout.X_AXIS));
        panelRow.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
        panelRow.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
        panelRow.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
        panelRow.setBackground(ColorPaletteV1.getRandomColor());
        panelRow.setOpaque(false);

        int panelItemIconWidth = mKeyWidth;
        int panelItemIconHeight = totalRowHeightForContent;
        JButton panelItemIcon = new OutlineButton(RandomUtils.createRandomName(1, 2));
        panelItemIcon.setPreferredSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setMinimumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setMaximumSize(new Dimension(panelItemIconWidth, panelItemIconHeight));
        panelItemIcon.setFont(FontPoolV1.getInstance().getFontForHeight(panelItemIconHeight));
        panelItemIcon.setBackground(getBackground());

        int panelItemDataWidth = totalRowWidthForContent - panelItemIconWidth;
        int panelItemDataHeight = totalRowHeightForContent;
        JButton panelItemData = new OutlineButton(rowId + "_ITEM_DATA");
        panelItemData.setPreferredSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setMinimumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setMaximumSize(new Dimension(panelItemDataWidth, panelItemDataHeight));
        panelItemData.setBorderPainted(false);
        panelItemData.setFocusPainted(false);
//        panelItemData.setFont(FontPool.getInstance().getFontForHeight(panelItemDataHeight));
        panelItemData.setBackground(getBackground());


        int panelItemRowSpacing = (int) ((mRowWidth - totalRowWidthForContent) / 3);
        panelRow.add(Box.createRigidArea(new Dimension(panelItemRowSpacing, 0)));
        panelRow.add(panelItemIcon);
        panelRow.add(Box.createRigidArea(new Dimension(panelItemRowSpacing, 0)));
        panelRow.add(panelItemData);
        panelRow.add(Box.createRigidArea(new Dimension(panelItemRowSpacing, 0)));



        expandablePanel.add(panelRow);


        JTextArea bigButton = new OutlineTextArea();
        bigButton.setText("This is some text and i wonder how this is going to look");
        bigButton.setPreferredSize(new Dimension(totalRowWidthForContent, totalRowHeightForContent));
        bigButton.setMinimumSize(new Dimension(totalRowWidthForContent, totalRowHeightForContent));
        bigButton.setMaximumSize(new Dimension(totalRowWidthForContent, totalRowHeightForContent));
        bigButton.setFont(FontPoolV1.getInstance().getFontForHeight((int) (totalRowHeightForContent * .9)));
        bigButton.setBackground(getBackground());

        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        bottomRow.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
        bottomRow.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
        bottomRow.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
        bottomRow.setOpaque(false);

        int bottomRowSpacingWidth = ((mRowWidth - totalRowWidthForContent) / 2);
        bottomRow.add(Box.createRigidArea(new Dimension(bottomRowSpacingWidth, 0)));
        bottomRow.add(new NoScrollBarPane(bigButton, closedRowWidth, closedRowHeight, true, 1));
        bottomRow.add(Box.createRigidArea(new Dimension(bottomRowSpacingWidth, 0)));

        expandablePanel.add(bottomRow);


        panelItemIcon.addActionListener(e -> {
            bottomRow.setVisible(!bottomRow.isVisible());
            if (bottomRow.isVisible()) {
                expandablePanel.setPreferredSize(new Dimension(expandedRowWidth, expandedRowHeight));
                expandablePanel.setMinimumSize(new Dimension(expandedRowWidth, expandedRowHeight));
                expandablePanel.setMaximumSize(new Dimension(expandedRowWidth, expandedRowHeight));
            } else {
                expandablePanel.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
                expandablePanel.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
                expandablePanel.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
            }
        });

//        panelItemData.addActionListener(e -> {
//            bottomRow.setVisible(!bottomRow.isVisible());
//            if (bottomRow.isVisible()) {
//                expandablePanel.setPreferredSize(new Dimension(expandedRowWidth, expandedRowHeight));
//                expandablePanel.setMinimumSize(new Dimension(expandedRowWidth, expandedRowHeight));
//                expandablePanel.setMaximumSize(new Dimension(expandedRowWidth, expandedRowHeight));
//            } else {
//                expandablePanel.setPreferredSize(new Dimension(closedRowWidth, closedRowHeight));
//                expandablePanel.setMinimumSize(new Dimension(closedRowWidth, closedRowHeight));
//                expandablePanel.setMaximumSize(new Dimension(closedRowWidth, closedRowHeight));
//            }
//        });



        result = new Quadruple<>(expandablePanel, panelItemIcon, panelItemData, bigButton);

        return result;
    }
}
