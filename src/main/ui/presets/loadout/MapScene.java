package main.ui.presets.loadout;

import main.constants.ColorPalette;
import main.engine.EngineScene;
import main.game.map.base.TileMap;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.presets.editor.EditorTile;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class MapScene extends EngineScene {
    private TileMap mTileMap = null;
    public boolean hasTileMap() { return mTileMap != null; }
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(MapScene.class);
    public void setTileMap(TileMap tileMap, int width, int height) {
        if (tileMap == mTileMap) { return; }
        mTileMap = tileMap;

        // Create interactive tiles
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        removeAll();
        setLayout(new GridBagLayout());
        EditorTile[][] tiles = new EditorTile[mTileMap.getRows()][mTileMap.getColumns()];
        for (int row = 0; row < tiles.length; row++) {
            for (int column = 0; column < tiles[row].length; column++) {
                EditorTile tile = new EditorTile(tileMap.tryFetchingTileAt(row, column));
                tiles[row][column] = tile;
                tile.setOpaque(true);
                tile.setPreferredSize(
                        new Dimension(width / tiles[row].length, height / tiles.length));
                gridBagConstraints.gridx = tile.getTile().column;
                gridBagConstraints.gridy = tile.getTile().row;
                gridBagConstraints.fill = GridBagConstraints.BOTH;
                gridBagConstraints.weighty = 1;
                gridBagConstraints.weightx = 1;
                gridBagConstraints.anchor = GridBagConstraints.CENTER;
                add(tile, gridBagConstraints);
            }
        }
        setBackground(ColorPalette.TRANSPARENT);
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
}
