package main.game.main.ui;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.RandomUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class UnitStatusEffectRowPanel extends GameUI {

    private int mStatusEffectRowItemWidth = 0;
    private int mStatusEffectRowItemHeight = 0;
    private int mStatusEffectWidthSpacing = 0;
    private Map<String, Integer> mStatusEffectToQuantityMap = new LinkedHashMap<>();
    private Map<String, JButton> mStatusEffectToJbuttonMap = new LinkedHashMap<>();
    private JPanel mContentPanel;

    public UnitStatusEffectRowPanel(int width, int height, Color color) { this(width, height, false, color); }
    public UnitStatusEffectRowPanel(int width, int height, boolean isTestComponent, Color background) {
        super(width, height);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.X_AXIS));
//        statusBarPanel.setPreferredSize(new Dimension(statusBarWidth, statusBarHeight));
//        statusBarPanel.setMinimumSize(new Dimension(statusBarWidth, statusBarHeight));
//        statusBarPanel.setMaximumSize(new Dimension(statusBarWidth, statusBarHeight));
//        contentPanel.setBackground(Color.BLUE);
        mContentPanel.setBackground(background);
        mContentPanel.setOpaque(true);

        // Height should be shortest dimension
//        mStatusEffectRowItemWidth = (int) (height * 1.5);
        mStatusEffectWidthSpacing = (int) ((width * .05) / 4);
        mStatusEffectRowItemWidth = (int) (width / 7) - mStatusEffectWidthSpacing;
        mStatusEffectRowItemHeight = (int) (height * .9);
//        mStatusEffectWidthSpacing = 2;
//        mStatusEffectWidthSpacing = (int) ((width * .05) / 4);

        mContentPanel.add(Box.createRigidArea(new Dimension(mStatusEffectWidthSpacing, 0)));
        if (isTestComponent) {
            for (int i = 0; i < 15; i++) {
                JButton statusBarItem = new OutlineButton(RandomUtils.createRandomName(1,2));
                statusBarItem.setPreferredSize(new Dimension(mStatusEffectRowItemWidth, mStatusEffectRowItemHeight));
                statusBarItem.setMinimumSize(new Dimension(mStatusEffectRowItemWidth, mStatusEffectRowItemHeight));
                statusBarItem.setMaximumSize(new Dimension(mStatusEffectRowItemWidth, mStatusEffectRowItemHeight));
                statusBarItem.setFont(FontPoolV1.getInstance().getFontForHeight(mStatusEffectRowItemHeight));
                statusBarItem.setFocusPainted(false);
                statusBarItem.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

                mContentPanel.add(statusBarItem);
                mContentPanel.add(Box.createRigidArea(new Dimension(mStatusEffectWidthSpacing, 0)));
            }
        }


//        setBackground();
        setOpaque(true);
        setBackground(background);
        add(new NoScrollBarPane(mContentPanel, width, height, false, 1));
    }

    public JButton putStatusEffect(String status) {
        JButton statusBarItem = new OutlineButton(status.substring(0, Math.min(2, status.length())).toUpperCase(Locale.ROOT));
        statusBarItem.setPreferredSize(new Dimension(mStatusEffectRowItemWidth, mStatusEffectRowItemHeight));
        statusBarItem.setMinimumSize(new Dimension(mStatusEffectRowItemWidth, mStatusEffectRowItemHeight));
        statusBarItem.setMaximumSize(new Dimension(mStatusEffectRowItemWidth, mStatusEffectRowItemHeight));
        statusBarItem.setFont(FontPoolV1.getInstance().getFontForHeight(mStatusEffectRowItemHeight));
        statusBarItem.setBackground(getBackground());

        mContentPanel.add(statusBarItem);
        mContentPanel.add(Box.createRigidArea(new Dimension(mStatusEffectWidthSpacing, 0)));

        mStatusEffectToQuantityMap.put(status, mStatusEffectToQuantityMap.getOrDefault(status, 0) + 1);
        mStatusEffectToJbuttonMap.put(status, statusBarItem);
        return statusBarItem;
    }

    public void clear() {
        mContentPanel.removeAll();
        mContentPanel.add(Box.createRigidArea(new Dimension(mStatusEffectWidthSpacing, 0)));
        mStatusEffectToQuantityMap.clear();
        mStatusEffectToJbuttonMap.clear();
    }
}
