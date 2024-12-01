package main.ui.presets.editor;

import main.game.main.GameAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import main.game.components.tile.Tile;
import main.game.main.GameController;

import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.custom.*;
import main.ui.outline.*;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.*;
import java.util.stream.IntStream;

public class UpdateTileLayerPanel extends EditorPanel {
    private final Random mRandom = new Random();
    public OutlineDropDownRow mUpdateTileLayersBrushAmountDropDown = null;
    public OutlineDropDownRow mUpdateTileLayersBrushSizeDropDown = null;
    public OutlineDropDownRow mUpdateTileLayersBrushModeDropDown = null;
    public OutlineDropDownRow mUpdateTileLayersBrushTypeDropDown = null;
    public OutlineDropDownRow mUpdateTileLayersBrushTerrainDropDown = null;
    private OutlineListWithHeaderAndImage mSelectedOptionsPane;
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();

    public UpdateTileLayerPanel() { }

    public UpdateTileLayerPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super(mainColor, width, collapsedHeight, expandedHeight);
        initialize(mainColor, width, collapsedHeight, expandedHeight);
    }

    public void initialize(Color mainColor, int width, int collapsedHeight, int expandedHeight) {

        simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));

        mLayeringPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mLayeringPanel.setBackground(mainColor);

        mUpdateTileLayersBrushModeDropDown = new OutlineDropDownRow(mainColor, mWidth, mCollapsedHeight);
        mUpdateTileLayersBrushModeDropDown.setBackground(mainColor);
        mUpdateTileLayersBrushModeDropDown.setLeftLabel("Brush Mode:");
        mUpdateTileLayersBrushModeDropDown.addItem(GameAPI.UPDATE_TILE_LAYERS_OPERATION_ADD_LAYER); // Adds a single layer of height 1 to the tile
        mUpdateTileLayersBrushModeDropDown.addItem(GameAPI.UPDATE_TILE_LAYERS_OPERATION_DELETE_LAYER); // Deletes Layer until another terrain has been detected
        mUpdateTileLayersBrushModeDropDown.addItem(GameAPI.UPDATE_TILE_LAYERS_OPERATION_EXTEND_LAYER); // Adds a single layer of height #N to the tile
        mUpdateTileLayersBrushModeDropDown.addItem(GameAPI.UPDATE_TILE_LAYERS_OPERATION_SHORTEN_LAYER); // Reduces a tiles height by 1
        mUpdateTileLayersBrushModeDropDown.addItem(GameAPI.UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER);
        mUpdateTileLayersBrushModeDropDown.setSelectedIndex(0);

        mUpdateTileLayersBrushSizeDropDown = new OutlineDropDownRow(mainColor, mWidth, mCollapsedHeight);
        mUpdateTileLayersBrushSizeDropDown.setLeftLabel("Brush Size:");
        mUpdateTileLayersBrushSizeDropDown.setBackground(mainColor);
        IntStream.range(0, 5).forEach(i -> mUpdateTileLayersBrushSizeDropDown.addItem(String.valueOf(i)));

        mUpdateTileLayersBrushAmountDropDown = new OutlineDropDownRow(mainColor, mWidth, mCollapsedHeight);
        mUpdateTileLayersBrushAmountDropDown.setLeftLabel("Brush Amount:");
        mUpdateTileLayersBrushAmountDropDown.setBackground(mainColor);
        IntStream.range(1, 11).forEach(i -> mUpdateTileLayersBrushAmountDropDown.addItem(String.valueOf(i)));

//        mLayeringPanel.setPreferredSize(new Dimension(mWidth, mExpandedHeight));

        JLabel terrainLabel = new OutlineLabel("???? Asset");
        terrainLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        terrainLabel.setBackground(mainColor);

        // Setting up the image for terrain
//        mSelectedOptionsPane = new LeftLabelToLabelListWithRightImagerPanel(mWidth, mCollapsedHeight * 3, mainColor);
        mSelectedOptionsPane = new OutlineListWithHeaderAndImage(mWidth, mCollapsedHeight * 3);

//        JButton terrainConfigsTileImageButton = new JButton();
        JButton terrainConfigsTileImageButton = mSelectedOptionsPane.getImage();
        int imageWidth = mSelectedOptionsPane.getImageWidth();
        int imageHeight = mSelectedOptionsPane.getImageHeight();
//        terrainConfigsTileImageButton.setMinimumSize(new Dimension(imageWidth, imageHeight));
//        terrainConfigsTileImageButton.setMaximumSize(new Dimension(imageWidth, imageHeight));
//        terrainConfigsTileImageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));

        // Setup dropdown for terrain
        mUpdateTileLayersBrushTerrainDropDown = new OutlineDropDownRow("Terrain Asset:", mainColor, mWidth, mCollapsedHeight);
        mUpdateTileLayersBrushTerrainDropDown.setBackground(mainColor);
        simpleToFullAssetNameMap.forEach((key, value) -> mUpdateTileLayersBrushTerrainDropDown.addItem(key));
        mUpdateTileLayersBrushTerrainDropDown.addActionListener(e -> {
            EditorPanel.setupDropDownForImage(mUpdateTileLayersBrushTerrainDropDown.getDropDown(), imageWidth,
                    imageHeight, terrainConfigsTileImageButton);
        });
        mUpdateTileLayersBrushTerrainDropDown.setSelectedIndex(0);

//        SwingUiUtils.setupPrettyStringComboBox(mAssetNameDropDown, mainColor, mWidth, mCollapsedHeight);
//        mAssetNameDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
//        simpleToFullAssetNameMap.forEach((key, value) -> mAssetNameDropDown.addItem(key));
//        mAssetNameDropDown.addActionListener(e -> EditorPanel.setupDropDownForImage(mAssetNameDropDown, imageWidth,
//                imageHeight, terrainConfigsTileImageButton));
//        mAssetNameDropDown.setSelectedIndex(mRandom.nextInt(mAssetNameDropDown.getItemCount()));

        JLabel terrainConfigsTileImageFullNameLabel = new OutlineLabel("Full Terrain Name");
        terrainConfigsTileImageFullNameLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainConfigsTileImageFullNameLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mUpdateTileLayersBrushTypeDropDown = new OutlineDropDownRow(mainColor, mWidth, mCollapsedHeight);
        mUpdateTileLayersBrushTypeDropDown.setLeftLabel("Terrain Type");
        mUpdateTileLayersBrushTypeDropDown.addItem(Tile.LAYER_TYPE_SOLID_TERRAIN);
        mUpdateTileLayersBrushTypeDropDown.addItem(Tile.LAYER_TYPE_LIQUID_TERRAIN);
        mUpdateTileLayersBrushTypeDropDown.setBackground(mainColor);
        mUpdateTileLayersBrushTypeDropDown.addActionListener(e -> {
            String selection = mUpdateTileLayersBrushTypeDropDown.getSelectedItem();
            if (selection.equalsIgnoreCase(Tile.LAYER_TYPE_SOLID_TERRAIN)) {
                simpleToFullAssetNameMap.clear();
                simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));
            } else if (selection.equalsIgnoreCase(Tile.LAYER_TYPE_LIQUID_TERRAIN)) {
                simpleToFullAssetNameMap.clear();
                simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("liquids"));
            }
            mUpdateTileLayersBrushTerrainDropDown.getDropDown().removeAllItems();
            simpleToFullAssetNameMap.forEach((key, value) -> mUpdateTileLayersBrushTerrainDropDown.addItem(key));
            mUpdateTileLayersBrushTerrainDropDown.setSelectedIndex(0);
        });

        JPanel mainPanel = new GameUI();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

//        mainPanel.add(mTileHeightDropDown);
        mainPanel.add(mUpdateTileLayersBrushModeDropDown);
        mainPanel.add(mUpdateTileLayersBrushSizeDropDown);
        mainPanel.add(mUpdateTileLayersBrushAmountDropDown);
        mainPanel.add(mUpdateTileLayersBrushTypeDropDown);
        mainPanel.add(mUpdateTileLayersBrushTerrainDropDown);
//        mainPanel.add(terrainLabel);
//        mainPanel.add(terrainConfigsTileImageButton);
//        mainPanel.add(mAssetNameDropDown);

        mainPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mainPanel.add(mSelectedOptionsPane);
        mainPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mainPanel.add(mTileInfoPanel);
        mainPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mainPanel.add(mLayeringPanel);
        mainPanel.setBackground(mainColor);
        mainPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        setBackground(mainColor);

        add(mainPanel);
    }

    public void onEditorGameControllerMouseMotion(GameController gameController, Tile tile) {
        if (!isShowing()) { return; }

        updateTileStack(tile);

        String asset = mUpdateTileLayersBrushTerrainDropDown.getSelectedItem();
        String mode = mUpdateTileLayersBrushModeDropDown.getSelectedItem();
        String amount = mUpdateTileLayersBrushAmountDropDown.getSelectedItem();
        String type = mUpdateTileLayersBrushTypeDropDown.getSelectedItem();
        String terrain = mUpdateTileLayersBrushTerrainDropDown.getSelectedItem();
        String brushSizeStr = mUpdateTileLayersBrushSizeDropDown.getSelectedItem();

        OutlineListWithHeader list = mSelectedOptionsPane.getList();

        list.updateHeader("Tile Details");
        list.updateRowV2("MODE", "Mode:", mode + "");
        list.updateRowV2("TYPE", "Type:", type + "");
        list.updateRowV2("TERRAIN", "Size:", brushSizeStr + "");
        list.updateRowV2("AMOUNT", "Amount:", amount + "");


//        for (int index = 0; index < tile.getLayerCount(); index++) {
//            String layerAsset = tile.getLayerAsset(index);
//            int layerHeight = tile.getLayerHeight(index);
//            OutlineImageToLabelToLabel row = list.updateRowV3(index + "", layerAsset, layerHeight + "");
//
//
//            String id = AssetPool.getInstance().getOrCreateAsset(
//                row.getImageWidth(),
//                row.getImageHeight(),
//                layerAsset,
//                AssetPool.STATIC_ANIMATION,
//                0,
//                layerAsset + tile + "_terrain_"
//            );
//            Asset asset1 = AssetPool.getInstance().getAsset(id);
//            row.setImage(new ImageIcon(asset1.getAnimation().toImage()));
//        }


        String value = mUpdateTileLayersBrushSizeDropDown.getSelectedItem();
        int brushSize = Integer.parseInt(getOrDefaultString(value, "0"));

        JSONObject request = new JSONObject();
        request.put(GameAPI.GET_TILES_AT_ROW, tile.getRow());
        request.put(GameAPI.GET_TILES_AT_COLUMN, tile.getColumn());
        request.put(GameAPI.GET_TILES_AT_RADIUS, brushSize);

        JSONArray tiles = gameController.getTilesAtRowColumn(request);
        gameController.setSelectedTiles(tiles);
    }

    public void onEditorGameControllerMouseClicked(GameController gameController, Tile tile) {
        if (!isShowing()) { return; }

        String asset = mUpdateTileLayersBrushTerrainDropDown.getSelectedItem();
        String mode = mUpdateTileLayersBrushModeDropDown.getSelectedItem();
        String amount = mUpdateTileLayersBrushAmountDropDown.getSelectedItem();
        String type = mUpdateTileLayersBrushTypeDropDown.getSelectedItem();

        JSONObject attributeToUpdate = new JSONObject();

        attributeToUpdate.put(GameAPI.UPDATE_TILE_LAYERS_MODE, mode);
        attributeToUpdate.put(Tile.LAYER_TYPE, type);
        attributeToUpdate.put(Tile.LAYER_HEIGHT, amount);
        attributeToUpdate.put(Tile.LAYER_ASSET, asset);

        onEditorGameControllerMouseMotion(gameController, tile);

        gameController.updateTileLayers(attributeToUpdate);
    }
}
