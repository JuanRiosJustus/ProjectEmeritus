package main.ui.huds;

import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.components.OutlineButton;
import main.ui.components.OutlineLabel;
import main.ui.custom.MediaProgressBar;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.JGamePanel;
import main.ui.huds.controls.OutlineMapPanel;
import main.utils.MathUtils;

import javax.swing.*;
import java.awt.*;

public class SelectionPanel extends GameUI {
    private static final String ROW_1 = "row_1",
            ROW_2 = "row_2",
            ROW_3 = "row_3",
            ROW_4 = "row_4",
            ROW_5 = "row_5",
            ROW_6 = "row_6",
            ROW_7 = "row_7";

    private MediaProgressBar mHealthProgressBar = new MediaProgressBar();
    private OutlineButton mTargetDisplay = new OutlineButton();
    private OutlineMapPanel mMainContent = new OutlineMapPanel(1, 1, 1);
    private OutlineMapPanel mTilePanel = null;
    private OutlineMapPanel mUnitPanel = null;
    public SelectionPanel(int width, int height, int x, int y) {
        super(width, height, x, y, SelectionPanel.class.getSimpleName());

        setLayout(new GridBagLayout());

        Color color = ColorPalette.GREEN;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
//        add(mDatasheetPanel, gbc);


        JPanel innerPanel = new JGamePanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));

        mTargetDisplay = new OutlineButton("IMAGE HERE", SwingConstants.CENTER, 0);
        int holderButtonWidth = (int) (width * .35);
        int holderButtonHeight = height;
        mTargetDisplay.setPreferredSize(new Dimension(holderButtonWidth, holderButtonHeight));
        mTargetDisplay.setMinimumSize(new Dimension(holderButtonWidth, holderButtonHeight));
        mTargetDisplay.setMaximumSize(new Dimension(holderButtonWidth, holderButtonHeight));
        innerPanel.add(mTargetDisplay);

        // if border is set, add a little less space
        int scrollableGridWidth = (int) (width * .64);
        int scrollableGridHeight = height;


        mUnitPanel = new OutlineMapPanel(scrollableGridWidth, scrollableGridHeight, 7);
        mTilePanel = new OutlineMapPanel(scrollableGridWidth, scrollableGridHeight, 5);
        mMainContent = new OutlineMapPanel(scrollableGridWidth, scrollableGridHeight, 8);
//        int scrollGridItemWidth = mMainContent.getComponentWidth();
//        int scrollGridItemHeight = mMainContent.getComponentHeight();
//        Font font = FontPool.getInstance().getFontForHeight((int) (scrollGridItemHeight * .8));

//        mNameLabel = new OutlineLabel("Name", SwingConstants.LEFT, 2, true);
//        mUnitLabel = new OutlineLabel("(Unit)", SwingConstants.RIGHT, 2, true);
//        JPanel row1 = generateLeftRightPanel(scrollGridItemWidth, scrollGridItemHeight, mNameLabel, mUnitLabel);
//        mMainContent.putKeyValueLabel("Name").c.setText("ERNREFNNKV");
////        mScrollGridContext.putComponent("row1", row1);
////        mScrollGridContext.putKeyValueLabel("ley");
////        mMainContent.putButton("tttt").setBackground(Color.BLUE);
//
//        mTypeLabel = new OutlineLabel("Types", SwingConstants.LEFT, 2, true);
//        mLevelLabel = new OutlineLabel("Lvl ???", SwingConstants.RIGHT, 2, true);
//        JPanel row2 = generateLeftRightPanel(scrollGridItemWidth, scrollGridItemHeight, mTypeLabel, mLevelLabel);
////        mScrollGridContext.putComponent("row2", row2);
//
//        mHealthLabel = new OutlineLabel("Health:", SwingConstants.LEFT, 2, true);
//        mHealthValue = new OutlineLabel("???", SwingConstants.RIGHT, 2, true);
//        JPanel row3 = generateLeftRightPanel(scrollGridItemWidth, scrollGridItemHeight, mHealthLabel, mHealthValue);
////        mScrollGridContext.putComponent("row3", row3);
//

        mHealthProgressBar.setValue(1);
//        mHealthProgressBar.setPreferredSize(new Dimension(scrollGridItemWidth, scrollGridItemHeight));
//        mHealthProgressBar.setMinimumSize(new Dimension(scrollGridItemWidth, scrollGridItemHeight));
//        mHealthProgressBar.setMaximumSize(new Dimension(scrollGridItemWidth, scrollGridItemHeight));
        mHealthProgressBar.setStringPainted(true);
//        mMainContent.putComponent("row4", mHealthProgressBar);

//        mScrollGridContext.putKeyValueLabel()
//        mMainContent.putComponent("TestButton6", new OutlineLabel(""));

        innerPanel.add(mMainContent);
        add(innerPanel, gbc);

    }

    private Entity mCurrentEntity = null;

    @Override
    public void gameUpdate(GameModel model) {
        Entity tileEntity = model.getGameState().getCurrentlySelectedTileEntity();
        if (tileEntity == null) { return; }
        if (mCurrentEntity == tileEntity) { return; }
        mCurrentEntity = tileEntity;
        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();
        if (unitEntity != null) {
            showUnit(model, unitEntity);
        } else {
            showTile(model, tileEntity);
        }
    }
    public void showUnit(GameModel model, Entity unitEntity) {
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//
//        mMainContent.putKeyValueLabel(ROW_1,
//                unitEntity.get(IdentityComponent.class).getName(),
//                statisticsComponent.getUnit()
//        );


        mMainContent.putKeyValue(
                ROW_1,
                unitEntity.get(IdentityComponent.class).getName(),
                "(" +  statisticsComponent.getUnit() + ")"
        );


        int currentHealth = statisticsComponent.getStatCurrent(StatisticsComponent.HEALTH);
        int maxHealth = statisticsComponent.getStatTotal(StatisticsComponent.HEALTH);
        float currentPercent = (float) ((currentHealth * 1.0) / (maxHealth * 1.0));
        int percentage = (int) MathUtils.map(currentPercent, 0, 1, 0, 100);

        mMainContent.putKeyValue(
                ROW_2,
                "Health:",
                currentHealth + "/" + maxHealth
        );

        mHealthProgressBar.setValue(percentage);
        mMainContent.getOrPutJComponent(ROW_3, mHealthProgressBar);
        mHealthProgressBar.setVisible(true);

        mMainContent.putKeyValue(
                ROW_4,
                "Moved: " + movementComponent.hasMoved(),
                "Acted: " + actionComponent.hasActed()
        );
//        row3Label.setText("Health: " + currentHealth + "/" + maxHealth);
//
//        mMainContent.getOrPutJComponent(
//                ROW_3,
//                mHealthProgressBar
//        ).setVisible(true);
//
//        mMainContent.putKeyValueLabel(
//                ROW_4,
//                "Moved: " + movementComponent.hasMoved(),
//                "Acted: " + actionComponent.hasActed()
//        );


        String id = AssetPool.getInstance().getOrCreateAsset(
                mTargetDisplay.getWidth(),
                mTargetDisplay.getHeight(),
                statisticsComponent.getUnit(),
                AssetPool.STRETCH_Y_ANIMATION,
                0,
                unitEntity + "_SelectionPanel"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        mTargetDisplay.setAnimatedImage(asset.getAnimation().getContent());


        mMainContent.setVisibility(ROW_5, false);
        mMainContent.setVisibility(ROW_6, false);

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
    }

    public void showTile(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);

        mMainContent.putKeyValue(
                ROW_1,
                "Tile: X: " + tile.getColumn() + ", Y: " + tile.getRow(),
                null
        );

        mMainContent.putKeyValue(
                ROW_2,
                "Height " + tile.getHeight(),
                null
        );

        mMainContent.putKeyValue(
                ROW_3,
                "Terrain: " + tile.getTerrain().substring(tile.getTerrain().lastIndexOf("/") + 1),
                null
        );

        mMainContent.getOrPutJComponent(
                ROW_4,
                mHealthProgressBar
        ).setVisible(false);

        mMainContent.putKeyValue(
                ROW_5,
                "Collider: " + (tile.getObstruction() == null ?
                        "None" :
                        tile.getObstruction().substring(tile.getObstruction().lastIndexOf("/") + 1)),
                null
        );

        mMainContent.putKeyValue(
                ROW_6,
                "Liquid: " + (tile.getLiquid() == null ?
                        "None" :
                        tile.getLiquid().substring(tile.getLiquid().lastIndexOf("/") + 1)),
                null
        );
        mMainContent.setVisibility(ROW_5, true);
        mMainContent.setVisibility(ROW_6, true);


        setupTargetImage(tileEntity);

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
    }
    private void setupTargetImage(Entity tileEntity) {
        AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
        Tile tile = tileEntity.get(Tile.class);
        Asset ref = null;
        Asset asset = null;
        if (tile.getUnit() != null) {
            Entity unitEntity = tile.getUnit();
            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
            String id = AssetPool.getInstance().getOrCreateAsset(
                    mTargetDisplay.getWidth(),
                    mTargetDisplay.getHeight(),
                    statisticsComponent.getUnit(),
                    AssetPool.STRETCH_Y_ANIMATION,
                    0,
                    unitEntity + "unit_SelectionPanel" + tile.getRow() + tile.getColumn()
            );
            asset = AssetPool.getInstance().getAsset(id);
        } else if (tile.getLiquid() != null) {
            ref = AssetPool.getInstance().getAsset(assetComponent.getId(AssetComponent.LIQUID_ASSET));
            String id = AssetPool.getInstance().getOrCreateAsset(
                    mTargetDisplay.getWidth(),
                    mTargetDisplay.getHeight(),
                    tile.getLiquid(),
                    AssetPool.FLICKER_ANIMATION,
                    ref.getStartingFrame(),
                    tile + "liquid_SelectionPanel"
            );
            asset = AssetPool.getInstance().getAsset(id);
        } else {

            ref = AssetPool.getInstance().getAsset(assetComponent.getId(AssetComponent.TERRAIN_ASSET));
            String id = AssetPool.getInstance().getOrCreateAsset(
                    mTargetDisplay.getWidth(),
                    mTargetDisplay.getHeight(),
                    tile.getTerrain(),
                    AssetPool.STATIC_ANIMATION,
                    ref.getStartingFrame(),
                    tile + "base_SelectionPanel"
            );
            asset = AssetPool.getInstance().getAsset(id);
        }
        mTargetDisplay.setAnimatedImage(asset.getAnimation().getContent());
    }
}
