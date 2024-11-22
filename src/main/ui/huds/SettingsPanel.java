package main.ui.huds;


import main.game.main.GameConfigurations;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.graphics.ControllerUI;
import main.ui.outline.OutlineLabel;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.OutlineMapPanel;
import main.utils.StringFormatter;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class SettingsPanel extends ControllerUI {
    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;

    private final String DEBUG_MODE_CHECK_BOX = "DEBUG MODE";
    private JCheckBox mDebugMode = null;
    private final String SHOW_ACTION_CHECK_BOX = "Show Action Ranges";
    private JCheckBox mShowActionRanges = null;
    private final String SHOW_MOVEMENT_CHECK_BOX = "Show Movement Ranges";
    private JCheckBox mShowMovementRanges = null;
    private final String SHOW_HEIGHT_CHECK_BOX = "Show Heights";
    private JCheckBox mShowHeights = null;

    private OutlineMapPanel mMainContent = null;

    public SettingsPanel(int width, int height, int x, int y, JButton enter, JButton exit) {
        super(width, height, x, y, enter, exit);

        int spreadSheetWidth = mMainContentWidth;
        int spreadSheetHeight = mMainContentHeight;

        mMainContent = new OutlineMapPanel(spreadSheetWidth, spreadSheetHeight, 5);

        mDebugMode = new JCheckBox();
        mDebugMode.setPreferredSize(new Dimension(mMainContent.getComponentWidth(), mMainContent.getComponentWidth()));
        mDebugMode.setBorderPaintedFlat(true);
        mDebugMode.setHorizontalAlignment(SwingConstants.RIGHT);
        mMainContent.putPair(DEBUG_MODE_CHECK_BOX, new OutlineLabel(DEBUG_MODE_CHECK_BOX), mDebugMode);

        mShowActionRanges = addCheckBox(SHOW_ACTION_CHECK_BOX);
        mShowMovementRanges = addCheckBox(SHOW_MOVEMENT_CHECK_BOX);
        mShowHeights = addCheckBox(SHOW_HEIGHT_CHECK_BOX);


        add(mMainContent);
        add(getExitButton());
    }
    private JCheckBox addCheckBox(String name) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        checkBox.setBorderPaintedFlat(true);
        mMainContent.putPair(name, new OutlineLabel(name), checkBox);
        return checkBox;
    }

//    @Override
//    public void setBackground(Color color) {
//        if (mDataView == null) { return; }
//        SwingUiUtils.recursivelySetBackgroundV2(color, mDataView.getContainer());
//    }

    public void set(Entity entity) {
        if (entity == null) { return; }
        Animation animation = null;
        String reference = entity.toString();
        if (entity.get(Tile.class) != null) {
            Tile tile = entity.get(Tile.class);
            AssetComponent assetComponent = entity.get(AssetComponent.class);
            String id = null;
            if (tile.getLiquid() != null) {
//                animation = assetComponent.getAnimation(AssetComponent.LIQUID_ASSET);
                id = assetComponent.getId(AssetComponent.LIQUID_ASSET);
            } else if (tile.getTopLayerAsset() != null) {
//                animation = assetComponent.getAnimation(AssetComponent.TERRAIN_ASSET);
                id = assetComponent.getId(AssetComponent.TERRAIN_ASSET);
            }
            Asset asset = AssetPool.getInstance().getAsset(id);
            animation = asset.getAnimation();
//            if (tile.getLiquid() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
//            } else if (tile.getTerrain() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
//            }
            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
        } else if (entity.get(Animation.class) != null) {
            animation = entity.get(Animation.class);
            StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);



//            mDatasheetPanel.addRow("Name", identity.getName());
//            mDatasheetPanel.addRow("Type", statistics.getType().toString());
//            mDatasheetPanel.addRow(Statistics.HEALTH,
//                    statistics.getStatCurrent(Statistics.HEALTH) + "/" + statistics.getStatTotal(Statistics.HEALTH));
//            mDatasheetPanel.addRow(Statistics.MANA,
//                    statistics.getStatCurrent(Statistics.MANA) + "/" + statistics.getStatTotal(Statistics.MANA));
//            mDatasheetPanel.addRow(Statistics.LEVEL, String.valueOf(statistics.getStatBase(Statistics.LEVEL)));
//            mDatasheetPanel.addRow(Statistics.EXPERIENCE,
//                    statistics.getStatModified(Statistics.LEVEL) + "/"
//                            + Statistics.getExperienceNeeded(statistics.getStatBase(Statistics.LEVEL)));

//            String[] stats = new String[]{ Statistics.PHYSICAL_ATTACK, Statistics.PHYSICAL_DEFENSE, Statistics.MAGICAL_ATTACK, Statistics.MAGICAL_DEFENSE };
//            History history = entity.get(History.class);
        }

        if (animation != null && mCurrentImage != animation.getFrame(0)) {
            mCurrentImage = animation.getFrame(0);
//            mUnitTargetFrame.setImage(animation, reference);
        }

        // Setup ui coloring
//        mMainContent.getContents().forEach(component -> {
//            SwingUiUtils.setBackgroundFor(getBackground(), component);
//            SwingUiUtils.setHoverEffect(component);
//            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
//        });

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
        observing = entity;
    }

    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void gameUpdate(GameModel model) {
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
//        currentSelected = (Entity) model.mGameState.getObject(GameState.CURRENTLY_SELECTED_TILES);
//        currentSelected = model.getSelectedTile();
//        if (currentSelected != null) {
//            Tile tile = currentSelected.get(Tile.class);
//            Entity unit = tile.getUnit();
//            set(unit);
//        }
        model.getSettings().put(GameConfigurations.GAMEPLAY_DEBUG_MODE, mDebugMode.isSelected());
//        model.getSettings().put(Settings.SHOW_ACTION_RANGES, mShowActionRanges.isSelected());
        model.getSettings().setShouldShowActionRanges(mShowActionRanges.isSelected());
        model.getSettings().setShouldShowMovementRanges(mShowMovementRanges.isSelected());
//        model.getSettings().setOptionShouldHideGameplayTileHeights(mShowHeights.isSelected());
    }
}
