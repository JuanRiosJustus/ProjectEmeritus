package main.ui.presets.editor;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.StateLock;
import main.game.components.tile.Tile;
import main.game.main.GameController;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.custom.StringComboBox;
import main.ui.custom.VerticalAccordionPanel;
import main.ui.outline.*;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collections;
import java.util.Objects;

public class EditorPanel extends VerticalAccordionPanel {
    protected final JPanel mLayeringPanel = new GameUI();
    protected OutlineLabelToLabel mLayerDataHeader = null;
    protected OutlineLabelToLabel mLayerDataTotalHeight = null;
    protected OutlineLabelToLabel mLayerDataTotalLayers = null;
    protected OutlineLabelToLabel mLayerDataTile = null;
    protected OutlineListWithHeaderAndImage mTileInfoPanel = null;
//    protected OutlineListWithHeaderAndImage mTileLayersPanel = null;
    protected OutlineList mTileLayersPanel = null;
    protected GameUI mLayerLevelsPanel = new GameUI();
    protected final JButton mLayerDataHeaderImage = new JButton();
    protected final StateLock mStateLock = new StateLock();

    public EditorPanel() { }
    public EditorPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super(mainColor, width, collapsedHeight, expandedHeight);
        int tileUiWidth = mWidth;
        int tileUiHeight = mCollapsedHeight * 3;

        mLayerDataTile = new OutlineLabelToLabel("Tile: ", mWidth, mCollapsedHeight);

        mTileInfoPanel = new OutlineListWithHeaderAndImage(mainColor, tileUiWidth, tileUiHeight, SwingConstants.CENTER);
//        mTileLayersPanel = new OutlineListWithHeaderAndImage(mainColor, tileUiWidth, tileUiHeight, SwingConstants.CENTER);
        mTileLayersPanel = new OutlineList(mainColor, tileUiWidth, mCollapsedHeight * 5, SwingConstants.CENTER);

//        mLayerDataTotalHeight = new OutlineLabelToLabel("Total Height:", mColor, mWidth, mCollapsedHeight);
//        mLayerDataTotalLayers = new OutlineLabelToLabel("Total Layers:", mColor, mWidth, mCollapsedHeight);

    }

    public void setupLayerData(Tile tile) {
        if (!mStateLock.isUpdated("tile_layers_panel", tile.getRow() + tile.getColumn())) { return;  }

        int imageSize = Math.min(mWidth, mCollapsedHeight);
        if (mLayeringPanel.getComponentCount() == 0) {
            mLayeringPanel.setPreferredSize(new Dimension(mWidth, mExpandedHeight));

//            mLayerDataTile = new OutlineLabelToLabel("Tile: ", mWidth, mCollapsedHeight);
            mLayeringPanel.add(mLayerDataTile);

//            mLayerDataTotalHeight = new OutlineLabelToLabel("Total Height:", mColor, mWidth, mCollapsedHeight);
            mLayeringPanel.add(mLayerDataTotalHeight);

//            mLayerDataTotalLayers = new OutlineLabelToLabel("Total Layers:", mColor, mWidth, mCollapsedHeight);
            mLayeringPanel.add(mLayerDataTotalLayers);

            JPanel headerRow = setupTileLayerDataHeaders(mWidth, mCollapsedHeight, imageSize);
            mLayeringPanel.add(headerRow);
            mLayeringPanel.setOpaque(true);

            mLayerLevelsPanel = new GameUI(false, mWidth, mExpandedHeight);
            mLayerLevelsPanel.setBackground(mColor);
            mLayeringPanel.add(mLayerLevelsPanel);
        }
//        mLayerDataTile.setRightLabel(tile.toString());
//        mLayerDataTotalLayers.setRightLabel(tile.getLayersCopy().size() + "");
//        mLayerDataTotalHeight.setRightLabel(tile.getHeight() + "");

        // Create a row for each later of the tile
        int imageWidth = imageSize;
        int imageHeight = imageSize;
        int headerWidth = mWidth - imageWidth;
        int headerHeight = imageHeight;

        GameUI fodderPanel = new GameUI(true, mWidth, mExpandedHeight);
        JsonArray layers = tile.getLayersCopy();
        Collections.reverse(layers);
        for (Object object : layers) {
            JPanel layer = setup(tile, (JsonObject) object, imageSize, headerWidth, headerHeight);
            fodderPanel.add(layer);
        }
        mLayerLevelsPanel.add(fodderPanel);
    }

    private JPanel setupTileLayerDataHeaders(int width, int height, int imageSize) {
        JPanel jPanel = new GameUI(width, height);
//        // Create image holder
        int imageWidth = imageSize;
        int imageHeight = imageSize;
        mLayerDataHeaderImage.setBorderPainted(false);
        mLayerDataHeaderImage.setFocusPainted(false);
        mLayerDataHeaderImage.setBackground(mColor);
        mLayerDataHeaderImage.setPreferredSize(new Dimension(imageWidth, imageHeight));
        // Create text portion
        int headerWidth = width - imageWidth;
        int headerHeight = imageHeight;
        mLayerDataHeader = new OutlineLabelToLabel(headerWidth, headerHeight);
        mLayerDataHeader.setLeftLabel("Layer");
        mLayerDataHeader.setRightLabel("Height");
        mLayerDataHeader.setBackground(mColor);
        // show panel
        jPanel.add(mLayerDataHeaderImage);
        jPanel.add(mLayerDataHeader);
        jPanel.setBackground(mColor);
        return jPanel;
    }

    public JPanel setup(Tile tile, JsonObject layer, int imageSize, int headerWidth, int headerHeight) {
        JButton image;
        JPanel jPanel;
        String assetName = (String) layer.get(Tile.LAYER_ASSET);
        // Create row
        jPanel = new GameUI(mWidth, mCollapsedHeight);
        // Create the image
        image = new JButton();
        image.setPreferredSize(new Dimension(imageSize, imageSize));
        if (assetName == null) {
            System.out.print("ototo");
        }
        String id = AssetPool.getInstance().getOrCreateAsset(
                imageSize,
                imageSize,
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + tile + "_terrain_"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        image.setIcon(new ImageIcon(asset.getAnimation().toImage()));
        // Text portion
        OutlineLabelToLabel row = new OutlineLabelToLabel(headerWidth, headerHeight);
        row.setBackground(mColor);
        row.setLeftLabel((String) layer.get(Tile.LAYER_ASSET));
        row.setRightLabel(String.valueOf(layer.get(Tile.LAYER_HEIGHT)));
        // put everything together
        jPanel.add(image);
        jPanel.add(row);
        return jPanel;
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
        start.updateRowV2("LAYERS", "Layers:", tile.getLayersCopy().size() + "");



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
