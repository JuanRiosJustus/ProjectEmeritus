package main.ui.huds;

import main.constants.StateLock;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.components.OutlineButton;
import main.ui.custom.MediaProgressBar;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.JGamePanel;
import main.ui.huds.controls.OutlineMapPanel;
import main.utils.MathUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Objects;

public class SelectionPanel extends GameUI {
    private ELogger mLogger = ELoggerFactory.getInstance().getELogger(SelectionPanel.class);
    private MediaProgressBar mHealthProgressBar = new MediaProgressBar();
    private MediaProgressBar mManaProgressBar = new MediaProgressBar();
    private MediaProgressBar mStaminaProgressBar = new MediaProgressBar();
    private OutlineButton mTargetDisplay = new OutlineButton();
    private OutlineMapPanel mTilePanel = null;
    private OutlineMapPanel mUnitPanel = null;
    private JPanel mContainerPanel = null;
    private final String TILE_PANEL = "tile_panel";
    private final String UNIT_PANEL = "unit_panel";
    private final StateLock mStateLock = new StateLock();
    public SelectionPanel(int width, int height, int x, int y) {
        super(width, height);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;


        JPanel innerPanel = new JGamePanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        innerPanel.setOpaque(true);

        mTargetDisplay = new OutlineButton("", SwingConstants.CENTER, 0);
        int holderButtonWidth = (int) (width * .35);
        int holderButtonHeight = height;
        mTargetDisplay.setPreferredSize(new Dimension(holderButtonWidth, holderButtonHeight));
        mTargetDisplay.setMinimumSize(new Dimension(holderButtonWidth, holderButtonHeight));
        mTargetDisplay.setMaximumSize(new Dimension(holderButtonWidth, holderButtonHeight));
        innerPanel.add(mTargetDisplay);

        // if border is set, add a little less space
        int scrollableGridWidth = (int) (width * .64);
        int scrollableGridHeight = height;

        mUnitPanel = new OutlineMapPanel(scrollableGridWidth, scrollableGridHeight, 8);
        mTilePanel = new OutlineMapPanel(scrollableGridWidth, scrollableGridHeight, 7);

        mContainerPanel = new JGamePanel();
        mContainerPanel.setLayout(new CardLayout());
        mContainerPanel.setBorder(new MatteBorder(1, 1, 1, 1, ColorPalette.TRANSPARENT));

        mHealthProgressBar = new MediaProgressBar(Color.GREEN);
        mManaProgressBar = new MediaProgressBar(Color.BLUE);
        mStaminaProgressBar = new MediaProgressBar(Color.ORANGE);

        innerPanel.add(mContainerPanel);
        add(innerPanel, gbc);

    }

    @Override
    public void gameUpdate(GameModel model) {
        var tileEntity = model.getSelectedTiles()
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        Entity entity = tileEntity;
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();
        if (unitEntity != null) {
            showUnit(model, unitEntity);
        } else {
            showTile(model, tileEntity);
        }
    }
//    @Override
//    public void gameUpdate(GameModel model) {
//        Entity tileEntity = model.getGameState().getCurrentlySelectedTileEntity();
//        if (tileEntity == null) { return; }
//        Tile tile = tileEntity.get(Tile.class);
//        Entity unitEntity = tile.getUnit();
//        if (unitEntity != null) {
//            showUnit(model, unitEntity);
//        } else {
//            showTile(model, tileEntity);
//        }
//    }

    public boolean hasSameUnitState(GameModel model, Entity unitEntity) {
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        boolean isNewState = mStateLock.isUpdated(
                "unit",
                statisticsComponent.getUnit(),
                statisticsComponent.getCurrent(StatisticsComponent.HEALTH),
                statisticsComponent.getCurrent(StatisticsComponent.STAMINA),
                statisticsComponent.getCurrent(StatisticsComponent.MANA),
                actionComponent.hasActed(),
                movementComponent.hasMoved()
        );
        return !isNewState;
    }

    public boolean hasSameTileState(GameModel model, Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);

         boolean isNewState = mStateLock.isUpdated(
                 "tile",
                tile.getRow(),
                tile.getColumn(),
                tile.getTerrain(),
                tile.getLiquid(),
                tile.getCollider()
        );
         return !isNewState;
    }

    //    public Tuple<Integer, Integer, Integer> get
    public void showUnit(GameModel model, Entity unitEntity) {
        // Show the correct panel
        mContainerPanel.add(mUnitPanel, UNIT_PANEL);
        CardLayout cardLayout = (CardLayout)mContainerPanel.getLayout();
        cardLayout.show(mContainerPanel, UNIT_PANEL);

        if (hasSameUnitState(model, unitEntity)) { return; }
        mLogger.info("Updated Selection panel");

        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);

        mUnitPanel.putKeyValue("ROW_1", unitEntity.get(IdentityComponent.class).getName(),
                "(" +  statisticsComponent.getUnit() + ")");

        int current = statisticsComponent.getCurrent(StatisticsComponent.HEALTH);
        int max = statisticsComponent.getTotal(StatisticsComponent.HEALTH);
        int percentage = (int) MathUtils.map(current, 0, max, 0, 100);
        mUnitPanel.putKeyValue("ROW_2", "Health:", current + "/" + max);
        mHealthProgressBar.setValue(percentage);
        mHealthProgressBar.setToolTipText("Test");
        mUnitPanel.getOrPut("ROW_3", mHealthProgressBar);

        current = statisticsComponent.getCurrent(StatisticsComponent.STAMINA);
        max = statisticsComponent.getTotal(StatisticsComponent.STAMINA);
        percentage = (int) MathUtils.map(current, 0, max, 0, 100);
        mUnitPanel.putKeyValue("ROW_4", "Stamina:", current + "/" + max);
        mStaminaProgressBar.setValue(percentage);
        mUnitPanel.getOrPut("ROW_5", mStaminaProgressBar);

        current = statisticsComponent.getCurrent(StatisticsComponent.MANA);
        max = statisticsComponent.getTotal(StatisticsComponent.MANA);
        percentage = (int) MathUtils.map(current, 0, max, 0, 100);
        mUnitPanel.putKeyValue("ROW_6", "Mana:", current + "/" + max);
        mManaProgressBar.setValue(percentage);
        mManaProgressBar.setString("ttt");
        mUnitPanel.getOrPut("ROW_7", mManaProgressBar);

        mUnitPanel.putKeyValue("ROW_8", "Moved: " + movementComponent.hasMoved(),
                "Acted: " + actionComponent.hasActed());

        String id = AssetPool.getInstance().getOrCreateAsset(
                (int) mTargetDisplay.getPreferredSize().getWidth(),
                (int) mTargetDisplay.getPreferredSize().getHeight(),
                statisticsComponent.getUnit(),
                AssetPool.STRETCH_Y_ANIMATION,
                0,
                unitEntity + "_SelectionPanel"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        mTargetDisplay.setAnimatedImage(asset.getAnimation().getContent());

        // Setup ui coloring
        mUnitPanel.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mUnitPanel.getComponentHeight()));
        });
        SwingUiUtils.setBackgroundFor(getBackground(), mContainerPanel);
        mLogger.info("Updated Selection Panel");
    }

    public void showTile(GameModel model, Entity tileEntity) {
        // Show the correct panel
        mContainerPanel.add(mTilePanel, TILE_PANEL);
        CardLayout cardLayout = (CardLayout)mContainerPanel.getLayout();
        cardLayout.show(mContainerPanel, TILE_PANEL);

        if (hasSameTileState(model, tileEntity)) { return; }
        mLogger.info("Updated Selection panel");

        Tile tile = tileEntity.get(Tile.class);

        mTilePanel.putKeyValue(
                "ROW_1",
                "Tile: X: " + tile.getColumn() + ", Y: " + tile.getRow(),
                null
        );

        mTilePanel.putKeyValue(
                "ROW_2",
                "Height " + tile.getHeight(),
                null
        );

        mTilePanel.putKeyValue(
                "ROW_3",
                "Terrain: " + tile.getTerrain().substring(tile.getTerrain().lastIndexOf("/") + 1),
                null
        );

        mTilePanel.putKeyValue(
                "ROW_5",
                "Collider: " + (tile.getObstruction() == null ?
                        "None" :
                        tile.getObstruction().substring(tile.getObstruction().lastIndexOf("/") + 1)),
                null
        );

        mTilePanel.putKeyValue(
                "ROW_6",
                "Liquid: " + (tile.getLiquid() == null ?
                        "None" :
                        tile.getLiquid().substring(tile.getLiquid().lastIndexOf("/") + 1)),
                null
        );

        mTilePanel.putKeyValue("ROW_7", "Spawn Region: " + tile.getSpawnRegion(), null);
        mTilePanel.putKeyValue("ROW_8", "Vector: " + tile.getLocalVector(model), null);


        setupTargetImage(tileEntity);

        // Setup ui coloring
        mTilePanel.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mTilePanel.getComponentHeight()));
        });
        SwingUiUtils.setBackgroundFor(getBackground(), mContainerPanel);
        mLogger.info("Updated Selection Panel");
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
                    (int) mTargetDisplay.getPreferredSize().getWidth(),
                    (int) mTargetDisplay.getPreferredSize().getHeight(),
                    statisticsComponent.getUnit(),
                    AssetPool.STRETCH_Y_ANIMATION,
                    0,
                    unitEntity + "unit_SelectionPanel" + tile.getRow() + tile.getColumn()
            );
            asset = AssetPool.getInstance().getAsset(id);
        } else if (tile.getLiquid() != null) {
            ref = AssetPool.getInstance().getAsset(assetComponent.getId(AssetComponent.LIQUID_ASSET));
            String id = AssetPool.getInstance().getOrCreateAsset(
                    (int) mTargetDisplay.getPreferredSize().getWidth(),
                    (int) mTargetDisplay.getPreferredSize().getHeight(),
                    tile.getLiquid(),
                    AssetPool.FLICKER_ANIMATION,
                    ref.getStartingFrame(),
                    tile + "liquid_SelectionPanel"
            );
            asset = AssetPool.getInstance().getAsset(id);
        } else {

            ref = AssetPool.getInstance().getAsset(assetComponent.getId(AssetComponent.TERRAIN_ASSET));
            String id = AssetPool.getInstance().getOrCreateAsset(
                    (int) mTargetDisplay.getPreferredSize().getWidth(),
                    (int) mTargetDisplay.getPreferredSize().getHeight(),
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
