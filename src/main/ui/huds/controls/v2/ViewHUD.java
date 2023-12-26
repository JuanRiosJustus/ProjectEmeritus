package main.ui.huds.controls.v2;

import main.constants.Constants;
import main.game.components.Summary;
import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.game.stores.pools.Asset;
import main.game.stores.pools.AssetPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JKeyValueMap;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.HUD;
import main.ui.custom.ImagePanel;
import main.utils.StringFormatter;
import main.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

public class ViewHUD extends HUD {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JTextArea description;
    private final JKeyValueMap mStatsKeyValueMap;
    private static final String IS_WALL = "Wall";
    private static final String HAS_STRUCTURE = "Structure";
    private static final String SHADOW_COUNT = "Shadows";
    private static final String SHADOWS_LIST = "Shadow List";
    private static final String TERRAIN_ID = "Terrain";
    private static final String TERRAIN_ASSET = "Terrain Asset";
    private static final String LIQUID_ID = "Liquid";
    private static final String LIQUID_ASSET = "Liquid Asset";
    private static final String STRUCTURE_ID = "Structure";
    private static final String STRUCTURE_ASSET = "Structure Asset";

    public ViewHUD(int width, int height) {
        super(width, height);

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
//        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        // Image
        mImagePanel = new ImagePanel(width, (int) (height * .25));
        add(mImagePanel, constraints);

        constraints.gridy = 1;
        mStatsKeyValueMap =  new JKeyValueMap(
                width,
                (int) (height * .65),
                new Object[][]{
                        new Object[] { Constants.MOVE, new JLabel() },
                        new Object[] { Constants.CLIMB, new JLabel() },
                        new Object[] { Constants.SPEED, new JLabel() },
                        new Object[] { Constants.ELEVATION, new JLabel() },
                        new Object[] { Constants.TILE, new JLabel() },


                        new Object[] {SHADOW_COUNT, new JLabel() },
                        new Object[] {SHADOWS_LIST, SwingUiUtils.getComboBox() },

                        new Object[] {TERRAIN_ID, new JLabel() },
                        new Object[] {TERRAIN_ASSET, new JLabel() },

                        new Object[] {LIQUID_ID, new JLabel() },
                        new Object[] {LIQUID_ASSET, new JLabel() },

                        new Object[] {STRUCTURE_ID, new JLabel() },
                        new Object[] {STRUCTURE_ASSET, new JLabel() }
                }
        );
        mStatsKeyValueMap.get(STRUCTURE_ASSET).fill();
        mStatsKeyValueMap.get(TERRAIN_ASSET).fill();
        mStatsKeyValueMap.get(LIQUID_ASSET).fill();
        mStatsKeyValueMap.get(SHADOWS_LIST).fill();

        add(mStatsKeyValueMap, constraints);

        constraints.gridy = 2;
        description = new JTextArea();
        description.setPreferredSize(new Dimension(width, (int) (height * .1)));
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(description, constraints);
    }

    private String setLabel(Tile tile, String assetId) {
        String id = tile.getAsset(assetId);
        Asset asset = AssetPool.getInstance().getAsset(id);
        if (asset == null) { return ""; }
        return asset.getName().substring(asset.getName().lastIndexOf("/") + 1);
    }
    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (gameModel == null) { return; }
        if (mCurrentTile != null && mCurrentTile != mPreviousTile) {
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.MOVE).setText("");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.SPEED).setText("");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.CLIMB).setText("");

            if (mCurrentUnit == null) { mImagePanel.set(mCurrentTile); }
            Tile tile = mCurrentTile.get(Tile.class);

            Set<String> list = tile.getAssets(Tile.SHADOW);
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, SHADOW_COUNT).setText(list.size() + "");

            JComboBox comboBox = JKeyValueMap.getJComboBoxComponent(mStatsKeyValueMap, SHADOWS_LIST);
            comboBox.removeAllItems();
            for (String shadowKey : list) {
                comboBox.addItem(StringUtils.spaceByCapitalization(shadowKey));
            }

            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, STRUCTURE_ID).setText(tile.getObstruction() + "");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, STRUCTURE_ASSET).setText(
                    setLabel(tile, Tile.OBSTRUCTION)
            );
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, LIQUID_ID).setText(tile.getLiquid() + "");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, LIQUID_ASSET).setText(
                    setLabel(tile, Tile.LIQUID)
            );
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, TERRAIN_ID).setText(tile.getTerrain() + "");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, TERRAIN_ASSET).setText(
                    setLabel(tile, Tile.TERRAIN)
            );

            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.ELEVATION).setText(tile.getHeight() + "");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.TILE).setText(
                    StringFormatter.format("Row: {}, Column: {}", tile.row, tile.column)
            );
        }
        if (mCurrentUnit != null) {
            Summary summary = mCurrentUnit.get(Summary.class);
            mImagePanel.set(mCurrentUnit);

            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.MOVE).setText(summary.getStatTotal(Constants.MOVE) + "");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.SPEED).setText(summary.getStatTotal(Constants.CLIMB) + "");
            JKeyValueMap.getJLabelComponent(mStatsKeyValueMap, Constants.CLIMB).setText(summary.getStatTotal(Constants.SPEED) + "");
        }
    }
}