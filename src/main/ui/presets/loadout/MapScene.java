package main.ui.presets.loadout;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.pools.ColorPalette;
import main.engine.EngineScene;
import main.game.map.base.TileMap;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.presets.editor.EditorTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MapScene extends EngineScene {
    private TileMap mTileMap = null;
    private Entity mSelectedEntity = null;
    private JPanel mMapLayer = null;
    private JPanel mOverlayer = null;
    private CurrentlyDeployedScene mCurrentlyDeployedScene;
    public boolean hasTileMap() { return mTileMap != null; }
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(MapScene.class);


    public void setup(TileMap tileMap, Rectangle bounds, UnitSelectionListScene unitList) {
        if (tileMap == mTileMap) { return; }

        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        setLayout(new BorderLayout());
        setBackground(ColorPalette.TRANSPARENT);

        JLayeredPane layeredPanel = new JLayeredPane();
        layeredPanel.setBackground(ColorPalette.TRANSPARENT);
        layeredPanel.setBounds(0, 0, bounds.width, bounds.height);
        add(layeredPanel);

        // panel where we put the tiles
        mMapLayer = new JPanel();
        mMapLayer.setBackground(ColorPalette.TRANSPARENT);
        mMapLayer.setLayout(new GridBagLayout());
        mMapLayer.setPreferredSize(new Dimension(bounds.width, bounds.height));
        mMapLayer.setBounds(0, 0, layeredPanel.getWidth(), layeredPanel.getHeight());

        mOverlayer = new JPanel();
        mOverlayer.setLayout(null);
        mOverlayer.setOpaque(false);
        mOverlayer.setBackground(ColorPalette.TRANSPARENT);
        mOverlayer.setBounds(0, 0,  layeredPanel.getWidth(), layeredPanel.getHeight());

        layeredPanel.add(mMapLayer, JLayeredPane.DEFAULT_LAYER);
        layeredPanel.add(mOverlayer, JLayeredPane.MODAL_LAYER);

        mTileMap = tileMap;

        // Create interactive tiles
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        EditorTile[][] tiles = new EditorTile[mTileMap.getRows()][mTileMap.getColumns()];
        for (int row = 0; row < tiles.length; row++) {
            for (int column = 0; column < tiles[row].length; column++) {
                EditorTile tile = new EditorTile(tileMap.tryFetchingTileAt(row, column));
                tiles[row][column] = tile;
                tile.setOpaque(true);
                tile.setPreferredSize(new Dimension(
                        (int) (mMapLayer.getPreferredSize().getWidth() / tiles[row].length),
                        (int) (mMapLayer.getPreferredSize().getHeight() / tiles.length)));
                tile.setCanvas(mOverlayer);
                gridBagConstraints.gridx = tile.getTile().column;
                gridBagConstraints.gridy = tile.getTile().row;
                gridBagConstraints.fill = GridBagConstraints.BOTH;
                gridBagConstraints.weighty = 1;
                gridBagConstraints.weightx = 1;
                gridBagConstraints.ipadx = 0;
                gridBagConstraints.ipady = 0;
                gridBagConstraints.anchor = GridBagConstraints.CENTER;
                tile.addActionListener(e -> {
                    if (mSelectedEntity == null) { return; }
                    if (tile.getTile().isNotNavigable()) { return; }
                    if (mCurrentlyDeployedScene == null) { return; }
                    tile.getTile().setUnit(mSelectedEntity);
                    mCurrentlyDeployedScene.addUnitToDeploymentList(mSelectedEntity, tile, unitList);
                });
                mMapLayer.add(tile, gridBagConstraints);
            }
        }

        mTileMap.placeByAxis(true, new ArrayList<>(), new ArrayList<>(), 4);
//        setOpaque(true);
//        add(layeredPanel);

    }

    @Override
    public void update() {
//        mLogger.warn("MapScene update() was called but not implemented");
    }

    @Override
    public void input() {
        mLogger.warn("MapScene input() was called but not implemented");
    }

    @Override
    public JPanel render() {
        return this;
    }

    public void setSelected(Entity selectedEntity) {
        mSelectedEntity = selectedEntity;
    }

    public void setCurrentlyDeployedPane(CurrentlyDeployedScene currentlyDeployedScene) {
        mCurrentlyDeployedScene = currentlyDeployedScene;
    }

    public TileMap getTileMap() { return mTileMap; }

    public JsonObject getUnitsAndPlacements() {
        JsonObject unitPlacementObject = new JsonObject();
        for (int row = 0; row < mTileMap.getRows(); row++) {
            for (int column = 0; column < mTileMap.getColumns(); column++) {
                Entity entity = mTileMap.tryFetchingTileAt(row, column);
                Tile tile = entity.get(Tile.class);

                if (tile.getUnit() == null) { continue; }
                entity = tile.getUnit();

                Statistics statistics = entity.get(Statistics.class);
                Identity identity = entity.get(Identity.class);
                JsonObject unitData = new JsonObject();
                unitData.put("name", identity.getName());
                unitData.put("uuid", identity.getUuid());
                unitData.put("row", row);
                unitData.put("column", column);

                System.out.println(statistics.toJsonString());

                // Check the spawn region. If its a new spawn region, insert into teamMap
                JsonObject team = (JsonObject) unitPlacementObject.getOrDefault(String.valueOf(tile.getSpawnRegion()), new JsonObject());
                team.put(identity.getUuid(), unitData);
                unitPlacementObject.put(String.valueOf(tile.getSpawnRegion()), team);
            }
        }

        return unitPlacementObject;
    }
}
