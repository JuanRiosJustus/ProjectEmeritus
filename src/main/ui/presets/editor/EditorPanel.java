package main.ui.presets.editor;

import main.constants.StateLock;
import main.game.components.tile.Tile;
import main.game.main.GameController;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.custom.StringComboBox;
import main.ui.outline.*;
import main.ui.outline.production.OutlineLabelToLabelRow;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;

public class EditorPanel extends JPanel {
    protected final JPanel mLayeringPanel = new GameUI();
    protected OutlineLabelToLabelRow mLayerDataHeader = null;
    protected OutlineLabelToLabelRow mLayerDataTotalHeight = null;
    protected OutlineLabelToLabelRow mLayerDataTotalLayers = null;
    protected OutlineLabelToLabelRow mLayerDataTile = null;
    protected OutlineListWithHeaderAndImage mTileInfoPanel = null;
//    protected OutlineListWithHeaderAndImage mTileLayersPanel = null;
    protected OutlineList mTileLayersPanel = null;
    protected GameUI mLayerLevelsPanel = new GameUI();
    protected final JButton mLayerDataHeaderImage = new JButton();
    protected final StateLock mStateLock = new StateLock();
    protected int mWidth = 0;
    protected int mCollapsedHeight = 0;

    public EditorPanel() { }
    public EditorPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        // Set FlowLayout for the main panel with no gaps
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setMinimumSize(new Dimension(width, expandedHeight));
        setMaximumSize(new Dimension(width, expandedHeight));
        setPreferredSize(new Dimension(width, expandedHeight));
        setBackground(mainColor);
        setOpaque(true);

        mCollapsedHeight = collapsedHeight;
        mWidth = width;

        int tileUiWidth = mWidth;
        int tileUiHeight = mCollapsedHeight * 3;

        mLayerDataTile = new OutlineLabelToLabelRow("Tile: ", mWidth, mCollapsedHeight);

        mTileInfoPanel = new OutlineListWithHeaderAndImage(mainColor, tileUiWidth, tileUiHeight, SwingConstants.CENTER);
//        mTileLayersPanel = new OutlineListWithHeaderAndImage(mainColor, tileUiWidth, tileUiHeight, SwingConstants.CENTER);
        mTileLayersPanel = new OutlineList(mainColor, tileUiWidth, mCollapsedHeight * 5, SwingConstants.CENTER);

//        mLayerDataTotalHeight = new OutlineLabelToLabel("Total Height:", mColor, mWidth, mCollapsedHeight);
//        mLayerDataTotalLayers = new OutlineLabelToLabel("Total Layers:", mColor, mWidth, mCollapsedHeight);

    }

    protected static void setupDropDownForImage(StringComboBox terrainDropDown, int imageWidth, int imageHeight, JButton imageButton) {
        String assetName = terrainDropDown.getSelectedItem();
        if (assetName == null || assetName.isEmpty()) { return; }
        String id = AssetPool.getInstance().getOrCreateAsset(
                imageWidth,
                imageHeight,
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_" + imageWidth + "_" + imageHeight + Objects.hash(terrainDropDown.getSelectedItem()) + Objects.hash(imageButton)
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        imageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
    }

    protected static void setupDropDownForImage(StringComboBox terrainDropDown, JButton imageButton) {
        String assetName = terrainDropDown.getSelectedItem();
        if (assetName == null || assetName.isEmpty()) { return; }
        int imageWidth = (int) imageButton.getPreferredSize().getWidth();
        int imageHeight = (int) imageButton.getPreferredSize().getHeight();
        String id = AssetPool.getInstance().getOrCreateAsset(
                imageWidth,
                imageHeight,
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_" + imageWidth + "_" + imageHeight + Objects.hash(terrainDropDown.getSelectedItem()) + Objects.hash(imageButton)
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        imageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
    }


    protected static String getOrDefaultString(String string, String defaultToString) {
        if (string == null || string.trim().isEmpty()) {
            return defaultToString;
        } else {
            return string;
        }
    }


    protected void updateTileStack(Tile tile) {
//        mTileInfoPanel.updateLabelToLabelList("ASSET", "" + tile.getTopLayerAsset(), "");
//        mTileInfoPanel.updateLabelToLabelList("TILE", "Tile:", tile.getRow() + ", " + tile.getColumn() + "");
//        mTileInfoPanel.updateLabelToLabelList("HEIGHT", "Height:", tile.getHeight() + "");
//        mTileInfoPanel.updateLabelToLabelList("LAYERS", "Layers:", tile.getLayersCopy().size() + "");

//        mTileInfoPanel.updateRow("ASSET", tile.getTopLayerAsset());
//        mTileInfoPanel.updateRow("TILE", tile.getRow() + ", " + tile.getColumn() + "");
//        mTileInfoPanel.updateRow("HEIGHT", tile.getHeight() + "");
//        mTileInfoPanel.updateRow("LAYERS", tile.getLayersCopy().size() + "");

        OutlineListWithHeader start = mTileInfoPanel.getList();
        start.updateHeader(tile.getTopLayerAsset());
//        start.updateRowV2("ASSET", "Asset:", tile.getTopLayerAsset());
        start.updateRowV2("TILE", "Tile: ", tile.getRow() + ", " + tile.getColumn());
        start.updateRowV2("HEIGHT", "Height: ", tile.getHeight() + "");
        start.updateRowV2("LAYERS", "Layers:", tile.getLayersCopy().length() + "");



        JButton img = mTileInfoPanel.getImage();
        String layerAsset = tile.getTopLayerAsset();
        String id = AssetPool.getInstance().getOrCreateAsset(
                mTileInfoPanel.getImageWidth(),
                mTileInfoPanel.getImageHeight(),
                layerAsset,
                AssetPool.STATIC_ANIMATION,
                0,
                layerAsset + tile + "_terrain_" + tile.getRow() + tile.getColumn()
        );
        Asset asset1 = AssetPool.getInstance().getAsset(id);
        img.setIcon(new ImageIcon(asset1.getAnimation().toImage()));







        OutlineList list = mTileLayersPanel;
        // Iterate in reverse
//        mTileLayersPanel.updateHeader("Tile Layers");
        list.clear();
        for (int index = tile.getLayerCount() - 1; index >= 0; index--) {
            layerAsset = tile.getLayerAsset(index);
            int layerHeight = tile.getLayerHeight(index);
            String layerType = tile.getLayerType(index);

            OutlineImageToLabelToLabel row = list.updateRowV3(index + "", layerAsset, layerHeight + "");

            id = AssetPool.getInstance().getOrCreateAsset(
                    row.getImageWidth(),
                    row.getImageHeight(),
                    layerAsset,
                    AssetPool.STATIC_ANIMATION,
                    0,
                    layerAsset + tile + "_terrain_"
            );
            asset1 = AssetPool.getInstance().getAsset(id);
            row.getImage().setIcon(new ImageIcon(asset1.getAnimation().toImage()));
        }
//        mTileLayersPanel.getImage().
    }

    public void onEditorGameControllerMouseMotion(GameController mEditorGameController, Tile tile) { }

    public void onEditorGameControllerMouseClicked(GameController mEditorGameController, Tile tile) { }
}
