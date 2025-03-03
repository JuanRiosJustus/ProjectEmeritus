package main.game.main.ui;

import main.constants.Quadruple;
import main.graphics.GameUI;
import main.ui.custom.ResourceBar;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.core.OutlineButton;
import main.utils.RandomUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;

public class UnitPortraitAndResourcesRowPanel extends GameUI {

    private UnitResourcesRowsPanel mUnitResourcesRowsPanel = null;
    private JPanel mContentPanel;
    private JButton mUnitPortrait;
    private int mContentPanelSpacing = 0;
    private int mUnitPortraitWidth = 0;
    private int mUnitPortraitHeight = 0;

    public UnitPortraitAndResourcesRowPanel(int width, int height, int portraitWidth, Color background) {
        super(width, height);

//        mContentPanelSpacing = (int) (width * .05);
        mContentPanelSpacing = (int) ((width * .05) / 3);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.X_AXIS));
        mContentPanel.setPreferredSize(new Dimension(width, height));
        mContentPanel.setMinimumSize(new Dimension(width, height));
        mContentPanel.setMaximumSize(new Dimension(width, height));
        mContentPanel.setBackground(background);


        mUnitPortraitWidth = portraitWidth < width ? portraitWidth : (int) (width * .4);
        mUnitPortraitHeight = (int) (height * .9);
        mUnitPortrait = new OutlineButton();
        mUnitPortrait.setPreferredSize(new Dimension(mUnitPortraitWidth, mUnitPortraitHeight));
        mUnitPortrait.setMinimumSize(new Dimension(mUnitPortraitWidth, mUnitPortraitHeight));
        mUnitPortrait.setMaximumSize(new Dimension(mUnitPortraitWidth, mUnitPortraitHeight));
        mUnitPortrait.setBackground(background);
        SwingUiUtils.setHoverEffect(mUnitPortrait);


//        int unitResourcesWidth = (int) (width - unitPortraitWidth - (width * .028));
        int unitResourcesWidth = (int) (width - mUnitPortraitWidth - (mContentPanelSpacing * 3));
        int unitResourceHeight = (int) (height * .95);
        JPanel unitResources = new JPanel();
        unitResources.setLayout(new BoxLayout(unitResources, BoxLayout.Y_AXIS));
        unitResources.setBackground(background);
//        unitResources.setPreferredSize(new Dimension(unitResourcesWidth, unitResourceHeight));
//        unitResources.setMinimumSize(new Dimension(unitResourcesWidth, unitResourceHeight));
//        unitResources.setMaximumSize(new Dimension(unitResourcesWidth, unitResourceHeight));
        for (int i = 0; i < 10; i++) {
            unitResources.add(new JButton(RandomUtils.createRandomName(3, 6)));
        }


        mUnitResourcesRowsPanel = new UnitResourcesRowsPanel(
                unitResourcesWidth,
                unitResourceHeight,
                background
        );



        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        mContentPanel.add(mUnitPortrait);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        mContentPanel.add(mUnitResourcesRowsPanel);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));

        setOpaque(false);
        add(mContentPanel);
    }

    public int getUnitPortraitWidth() { return mUnitPortraitWidth; }
    public int getUnitPortraitHeight() { return mUnitPortraitHeight; }
    public void setPortraitIcon(Icon icon) { mUnitPortrait.setIcon(icon); }
//    public Quadruple<JPanel, OutlineLabel, OutlineLabel, ResourceBar> createResourceRow(String name) {
//        return mUnitResourcesRowsPanel.createResourceRow(name);
//    }
}
