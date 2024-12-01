package main.ui.presets.editor;

import main.game.main.GameAPI;
import main.graphics.GameUI;
import main.ui.outline.*;
import org.json.JSONArray;
import org.json.JSONObject;
import main.game.components.tile.Tile;
import main.game.main.GameController;

import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.ui.custom.*;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.*;

public class MapGenerationPanel extends EditorPanel {
    private final Random random = new Random();
    public OutlineFieldRow mMapNameField = null;
    public OutlineFieldRow mBaseLevelField = null;
    public OutlineDropDownRow mBaseLevelAsset = null;
    public OutlineFieldRow mMaxHeightField = null;
    public OutlineFieldRow mMinHeightField = null;
    public OutlineFieldRow mNoiseZoomField = null;
    public OutlineDropDownRow mTerrainAssetDropDown = null;
    public OutlineFieldRow mWaterLevelField = null;
    public OutlineDropDownRow mWaterLevelAssetDropDown = null;
    public OutlineDropDownRow mBrushSizeDropDown = null;
    public OutlineButtonRow mLoadMapButton = null;
    public OutlineButtonRow mSaveMapButton = null;
    public final JTextField mBaseTerrain = new JTextField();
    public final StringComboBox mTerrainSelectionDropDown = new StringComboBox();
    public OutlineDropDownRow mMapSizeDropDown = null;
    public final JButton mRandomizeMapButton = new OutlineButton("Randomize Map");
    public final JButton mGenerateMapButton = new OutlineButton("Generate Map");
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();
    public MapGenerationPanel() { }
    public MapGenerationPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super(mainColor, width, collapsedHeight, expandedHeight);

        simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));

        mLayeringPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mLayeringPanel.setBackground(mainColor);

        mMapNameField = new OutlineFieldRow("Map Name: ", mainColor, mWidth, mCollapsedHeight);

        mSaveMapButton = new OutlineButtonRow("Load Map: ", mainColor, mWidth, mCollapsedHeight);
        mSaveMapButton.getButton().setHorizontalAlignment(SwingConstants.CENTER);
        mSaveMapButton.getButton().setText("Save Map");
        mSaveMapButton.getButton().setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mSaveMapButton.getButton().setBackground(mainColor);
        SwingUiUtils.setHoverEffect(mSaveMapButton.getButton());

        mLoadMapButton = new OutlineButtonRow("Save Map: ", mainColor, mWidth, mCollapsedHeight);
        mLoadMapButton.getButton().setHorizontalAlignment(SwingConstants.CENTER);
        mLoadMapButton.getButton().setText("Load Map");
        mLoadMapButton.getButton().setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mLoadMapButton.getButton().setBackground(mainColor);
        SwingUiUtils.setHoverEffect(mLoadMapButton.getButton());

        mRandomizeMapButton.setHorizontalAlignment(SwingConstants.CENTER);
        mRandomizeMapButton.setBackground(mainColor);
        SwingUiUtils.setHoverEffect(mRandomizeMapButton);

        mGenerateMapButton.setHorizontalAlignment(SwingConstants.CENTER);
        mGenerateMapButton.setBackground(mainColor);
        SwingUiUtils.setHoverEffect(mGenerateMapButton);

        // Add default size options to the dropdown
        mMapSizeDropDown = new OutlineDropDownRow("Map Size:", mainColor, mWidth, mCollapsedHeight);
        mMapSizeDropDown.addItem("20x14");
        mMapSizeDropDown.addItem("30x21");
        mMapSizeDropDown.addItem("40x28");
        mMapSizeDropDown.setSelectedIndex(0);

        // --- Label and panel for base terrain selection ---
        JLabel mapGenerationTerrainSelectionLabel = new OutlineLabel("Base Terrain");
        // Set the preferred size and font for the terrain label
        mapGenerationTerrainSelectionLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationTerrainSelectionLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mBrushSizeDropDown = new OutlineDropDownRow("Brush Size:", mainColor, mWidth, mCollapsedHeight);
        mBrushSizeDropDown.addItem("1");

        // --- Image button and dropdown for terrain selection ---
        int mapGenerationImageWidth = (int) (mWidth * .2);
        int mapGenerationImageHeight = (int) (mWidth * .2);
        JButton mapGenerationImageButton = new JButton();
        mapGenerationImageButton.setPreferredSize(new Dimension(mapGenerationImageWidth, mapGenerationImageHeight));


        int mapGenerationTerrainSelectionDropDownWidth = mWidth;
        int mapGenerationTerrainSelectionDropDownHeight = mCollapsedHeight;
        // Configure the dropdown for terrain selection
        SwingUiUtils.setupPrettyStringComboBox(mTerrainSelectionDropDown,
                mapGenerationTerrainSelectionDropDownWidth, mapGenerationTerrainSelectionDropDownHeight);
        mTerrainSelectionDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        // Add terrain options to the dropdown
        simpleToFullAssetNameMap.forEach((key, value) -> mTerrainSelectionDropDown.addItem(key));

        // Action listener to update the image when a new terrain is selected
        mTerrainSelectionDropDown.addActionListener(e -> {
//                linkDropDownWithImg(mTerrainSelectionDropDown, mapGenerationImageWidth,
//                        mapGenerationImageHeight, mapGenerationImageButton,
//                        mBaseTerrain)
        });
        // Set a random terrain selection as default
        mTerrainSelectionDropDown.setSelectedIndex(
                random.nextInt(mTerrainSelectionDropDown.getItemCount()));

        mBaseLevelAsset = new OutlineDropDownRow("Base Asset:", mainColor, mWidth, mCollapsedHeight);
        mBaseLevelAsset.setBackground(mainColor);
        simpleToFullAssetNameMap.forEach((key, value) -> mBaseLevelAsset.addItem(key));
        mBaseLevelAsset.addActionListener(e -> {
            setupImageButton(mBaseLevelAsset.getDropDown(), mapGenerationImageWidth, mapGenerationImageHeight,
                    mapGenerationImageButton);
        });

        mBaseLevelField = new OutlineFieldRow("Base Level:", mainColor, mWidth, mCollapsedHeight);
        mBaseLevelField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mBaseLevelField.setRightText("1");
        mBaseLevelField.getTextField().setEditable(false);
        PlainDocument doc = (PlainDocument) mBaseLevelField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mWaterLevelAssetDropDown = new OutlineDropDownRow("Water Asset:", mainColor, width, mCollapsedHeight);
        mWaterLevelAssetDropDown.setBackground(mainColor);
        AssetPool.getInstance().getLiquids().forEach((key, value) -> mWaterLevelAssetDropDown.addItem(key));
        mWaterLevelAssetDropDown.addActionListener(e -> {
            setupImageButton(mWaterLevelAssetDropDown.getDropDown(), mapGenerationImageWidth, mapGenerationImageHeight,
                    mapGenerationImageButton);
        });

        mWaterLevelField = new OutlineFieldRow("Water Level:", mainColor,  mWidth, mCollapsedHeight);
        mWaterLevelField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mWaterLevelField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mTerrainAssetDropDown = new OutlineDropDownRow("Terrain Asset:", mainColor, width, mCollapsedHeight);
        mTerrainAssetDropDown.setBackground(mainColor);
        simpleToFullAssetNameMap.forEach((key, value) -> mTerrainAssetDropDown.addItem(key));
        mTerrainAssetDropDown.addActionListener(e -> {
            setupImageButton(mTerrainAssetDropDown.getDropDown(), mapGenerationImageWidth, mapGenerationImageHeight,
                    mapGenerationImageButton);
        });

        mMaxHeightField = new OutlineFieldRow("Max Height:", mainColor, mWidth, mCollapsedHeight);
        mMaxHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mMaxHeightField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mMinHeightField = new OutlineFieldRow("Min Height:", mainColor, mWidth, mCollapsedHeight);
        mMinHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mMinHeightField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mNoiseZoomField = new OutlineFieldRow("Noise Zoom:", mWidth, mCollapsedHeight);
        mNoiseZoomField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mNoiseZoomField.getTextField().getDocument();
        doc.setDocumentFilter(new FloatRangeDocumentFilter()); // Filter to allow only floats

        mGenerateMapButton.setText("Generate Map");
        mGenerateMapButton.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mGenerateMapButton.setMinimumSize(new Dimension(mWidth, mCollapsedHeight));
        mGenerateMapButton.setMaximumSize(new Dimension(mWidth, mCollapsedHeight));
        mGenerateMapButton.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mRandomizeMapButton.setText("Randomize Map");
        mRandomizeMapButton.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mRandomizeMapButton.setMinimumSize(new Dimension(mWidth, mCollapsedHeight));
        mRandomizeMapButton.setMaximumSize(new Dimension(mWidth, mCollapsedHeight));
        mRandomizeMapButton.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // --- Label and dropdown for map mode selection ---
        JLabel mapModelLabel = new OutlineLabel("Map Mode");
        mapModelLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapModelLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        StringComboBox mapModeDropdown = SwingUiUtils.createJComboBox(mWidth, mCollapsedHeight);
        mapModeDropdown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mapModeDropdown.addItem("Team Deathmatch");
        mapModeDropdown.addItem("Survival");
        mapModeDropdown.addItem("Final Destination");

        // --- Main panel to hold all map metadata components ---
        JPanel mapMetadataPanel = new GameUI();
        mapMetadataPanel.setOpaque(true);
        mapMetadataPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mapMetadataPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mapMetadataPanel.setBackground(mainColor);
        // Add all components to the main panel

        mapMetadataPanel.add(mMapNameField);
        mapMetadataPanel.add(mSaveMapButton);
        mapMetadataPanel.add(mLoadMapButton);

        mapMetadataPanel.add(mMapSizeDropDown);


//        mapMetadataPanel.add(mapGenerationImageButton);
//        mapMetadataPanel.add(new TileMonitorPanel(mainColor, mWidth, mCollapsedHeight * 3));
//        mapMetadataPanel.add(mTileMonitorPanel);
//        mapMetadataPanel.add(mTileInfoPanel);


        mapMetadataPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mapMetadataPanel.add(mBaseLevelAsset);
        mapMetadataPanel.add(mBaseLevelField);
        mapMetadataPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mapMetadataPanel.add(mWaterLevelAssetDropDown);
        mapMetadataPanel.add(mWaterLevelField);
        mapMetadataPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mapMetadataPanel.add(mTerrainAssetDropDown);
        mapMetadataPanel.add(mMinHeightField);
        mapMetadataPanel.add(mMaxHeightField);
        mapMetadataPanel.add(mNoiseZoomField);
        mapMetadataPanel.add(SwingUiUtils.verticalSpacePanel(5));
        mapMetadataPanel.add(mGenerateMapButton);
        mapMetadataPanel.add(mRandomizeMapButton);


//        mapMetadataPanel.add(mTileInfoPanelV2);
        mapMetadataPanel.add(mTileInfoPanel);
        mapMetadataPanel.add(mTileLayersPanel);

//        mTileInfoPanelV2.updateHeader("HEADER V 2");
//        mTileInfoPanelV2.updateRow("1", "ROW 1");
//        mTileInfoPanelV2.updateRow("2", "ROW erttrerwetwert1");
//        mTileInfoPanelV2.updateRow("3", "RO");
//        mTileInfoPanelV2.updateRow("4", "RterwtwetwertO");
//        mapMetadataPanel.add(mLayeringPanel);

//        getContentPanel().add(mapMetadataPanel);
        add(mapMetadataPanel);
//        add(SwingUiUtils.createBonelessScrollingPaneNoVertical(mWidth, expandedHeight, mapMetadataPanel));
    }

    private static void setupImageButton(StringComboBox terrainDropDown, int imageWidth, int imageHeight, JButton imageButton) {
        String assetName = terrainDropDown.getSelectedItem();
        String id = AssetPool.getInstance().getOrCreateAsset(
                imageWidth,
                imageHeight,
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_" + imageWidth + "_" + imageHeight + Objects.hash(terrainDropDown) + Objects.hash(imageButton)
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        imageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
    }

    public void setMapGenerationRandomDefaultsIfEmpty(boolean forceRandomize) {
        if (mMinHeightField.getRightText().isEmpty() || forceRandomize) {
            mMinHeightField.setRightText(String.valueOf(1));
        }
        if (mMaxHeightField.getRightText().isEmpty() || forceRandomize) {
            mMaxHeightField.setRightText(String.valueOf(random.nextInt(5, 10)));
        }

        if (mWaterLevelField.getRightText().isEmpty() || forceRandomize) {
            int min = Integer.parseInt(mMinHeightField.getRightText());
            int max = Integer.parseInt(mMaxHeightField.getRightText());
            int mid = (min + max) / 2;
            mWaterLevelField.setRightText(String.valueOf(mid));
        }

        if (mNoiseZoomField.getRightText().isEmpty() || forceRandomize) {
            mNoiseZoomField.setRightText(String.valueOf(random.nextFloat(.25f, .75f)));
        }

        if (mTerrainSelectionDropDown.getItemCount() > 0 && forceRandomize) {
            mTerrainSelectionDropDown.setSelectedIndex(
                    random.nextInt(mTerrainSelectionDropDown.getItemCount()));
        }
    }
    public void onEditorGameControllerMouseMotion(GameController gameController, Tile tile) {
        if (!isShowing()) { return; }

        updateTileStack(tile);

        JSONObject request = new JSONObject();
        request.put(GameAPI.GET_TILES_AT_ROW, tile.getRow());
        request.put(GameAPI.GET_TILES_AT_COLUMN, tile.getColumn());
        request.put(GameAPI.GET_TILES_AT_RADIUS, 0);

        JSONArray tiles = gameController.getTilesAtRowColumn(request);
        gameController.setSelectedTiles(tiles);
    }
}
