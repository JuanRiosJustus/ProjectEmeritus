package main.ui.presets.editor;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.tile.Tile;
import main.game.main.GameController;
import main.game.main.GameModelAPI;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.ui.custom.*;
import main.ui.huds.controls.JGamePanel;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineLabelToDropDown;
import main.ui.outline.OutlineLabelToField;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.*;

public class MapGenerationPanel extends EditorPanel {
    private final Random random = new Random();
    public OutlineLabelToField mMapNameField = null;
    public OutlineLabelToField mBaseLevelField = null;
    public OutlineLabelToDropDown mBaseLevelAsset = null;
    public OutlineLabelToField mMaxHeightField = null;
    public OutlineLabelToField mMinHeightField = null;
    public OutlineLabelToField mNoiseZoomField = null;
    public OutlineLabelToDropDown mTerrainAssetDropDown = null;
    public OutlineLabelToField mWaterLevelField = null;
    public OutlineLabelToDropDown mWaterLevelAssetDropDown = null;
    public OutlineLabelToDropDown mBrushSizeDropDown = null;
//    private LeftLabelToLabelListWithRightImagerPanel mTileInfoPanel = null;
//    private LeftLabelListWithRightImagerPanel mTileInfoPanel = null;
    public final JTextField mBaseTerrain = new JTextField();
    public final StringComboBox mTerrainSelectionDropDown = new StringComboBox();
//    public FourLabelsAndImagePanel mTileMonitorPanel = new FourLabelsAndImagePanel();
//    public final StringComboBox mMapSizeDropDown = new StringComboBox();
    public OutlineLabelToDropDown mMapSizeDropDown = null;
    public final JButton mRandomizeMapButton = new JButton("Randomize Map");
    public final JButton mGenerateMapButton = new JButton("Generate Map");
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();
    public MapGenerationPanel() { }
    public MapGenerationPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super(mainColor, width, collapsedHeight, expandedHeight);

        simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));

        mLayeringPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mLayeringPanel.setBackground(mainColor);

        mMapNameField = new OutlineLabelToField("Map Name: ", mainColor, mWidth, mCollapsedHeight);

        // Add default size options to the dropdown
        mMapSizeDropDown = new OutlineLabelToDropDown("Map Size:", mainColor, mWidth, mCollapsedHeight);
        mMapSizeDropDown.addItem("20x14");
        mMapSizeDropDown.addItem("30x21");
        mMapSizeDropDown.addItem("40x28");
        mMapSizeDropDown.setSelectedIndex(0);

        // --- Label and panel for base terrain selection ---
        JLabel mapGenerationTerrainSelectionLabel = new OutlineLabel("Base Terrain");
        // Set the preferred size and font for the terrain label
        mapGenerationTerrainSelectionLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationTerrainSelectionLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mBrushSizeDropDown = new OutlineLabelToDropDown("Brush Size:", mainColor, mWidth, mCollapsedHeight);
        mBrushSizeDropDown.addItem("1");

        // --- Image button and dropdown for terrain selection ---
        int mapGenerationImageWidth = (int) (mWidth * .2);
        int mapGenerationImageHeight = (int) (mWidth * .2);
        JButton mapGenerationImageButton = new JButton();
        mapGenerationImageButton.setPreferredSize(new Dimension(mapGenerationImageWidth, mapGenerationImageHeight));




//        mTileMonitorPanel = new FourLabelsAndImagePanel(mainColor, mWidth, mCollapsedHeight * 3);
//        mTileMonitorPanel.addLabel("222222222555555", "Row Value 1");
//        mTileMonitorPanel.addLabel("Key 2", "Row Value");
//        mTileMonitorPanel.addLabel("Row4t55t3 Key 3", "Row Value 3");
//        mTileMonitorPanel.addLabel(" 4", "Row 4");
//        mTileMonitorPanel.setBackground(mainColor);
//        mTileMonitorPanel.addLabel(" 4", "Row 4");


//        int tileUiWidth = mWidth;
//        int tileUiHeight = mCollapsedHeight * 3;
//        mTileInfoPanel = new LeftLabelToLabelListWithRightImagerPanel(tileUiWidth, tileUiHeight, mainColor);
//        mTileInfoPanel = new LeftLabelListWithRightImagerPanel(tileUiWidth, tileUiHeight, mainColor);
//        tileUiPanel.updateLabelToLabelList("HEIGHT", "Height:", "5" );
//        tileUiPanel.addLabelToLeftPanel("Height:", "5" );
//        tileUiPanel.addLabelToLeftPanel("Height:", "5" );
//        tileUiPanel.addLabelToLeftPanel("Height:", "5" );
//        tileUiPanel.addLabelToLeftPanel("tt", "55");
//        tileUiPanel.addLabelToLeftPanel("Height:", "5");
//        tileUiPanel.addLabelToLeftPanel("42344232r42:", "54");



//        int tileUiWidth = mWidth;
//        int tileUiHeight = mCollapsedHeight * 3;
//        GameUI tileUiPanel = new GameUI(tileUiWidth, tileUiHeight);
//
//        int innerLeftPanelWidth = (int) (tileUiWidth * .75);
//        int innerLeftPanelHeight = tileUiHeight;
//        GameUI leftInnerPanel = new GameUI(innerLeftPanelWidth, (int) (innerLeftPanelHeight * 1.1));
//        leftInnerPanel.setLayout(new BoxLayout(leftInnerPanel, BoxLayout.Y_AXIS));
//
//        OutlineLabelToLabel ttt1 = new OutlineLabelToLabel("Height:","8", innerLeftPanelWidth, innerLeftPanelHeight / 4);
//        ttt1.setBackground(mainColor);
//        OutlineLabelToLabel ttt2 = new OutlineLabelToLabel("Spawn:", "lefe", innerLeftPanelWidth, innerLeftPanelHeight / 4);
//        ttt2.setBackground(mainColor);
//        OutlineLabelToLabel ttt3 = new OutlineLabelToLabel("Layers:", "4", innerLeftPanelWidth, innerLeftPanelHeight / 4);
//        ttt3.setBackground(mainColor);
//        OutlineLabelToLabel ttt4 = new OutlineLabelToLabel("TESTER:", "Y", innerLeftPanelWidth, innerLeftPanelHeight / 4);
//        ttt4.setBackground(mainColor);
//
//        leftInnerPanel.add(ttt1);
//        leftInnerPanel.add(ttt2);
//        leftInnerPanel.add(ttt3);
//        leftInnerPanel.add(ttt4);
//        leftInnerPanel.setBackground(mainColor);
//
//        JButton imageButton = new JButton();
//        imageButton.setPreferredSize(new Dimension((int) (tileUiWidth * .25), tileUiHeight));
//
//        tileUiPanel.add(new NoScrollScrollPane(leftInnerPanel, mainColor,  innerLeftPanelWidth, innerLeftPanelHeight));
//        tileUiPanel.add(imageButton);






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

        mBaseLevelAsset = new OutlineLabelToDropDown("Base Asset:", mainColor, mWidth, mCollapsedHeight);
        mBaseLevelAsset.setBackground(mainColor);
        simpleToFullAssetNameMap.forEach((key, value) -> mBaseLevelAsset.addItem(key));
        mBaseLevelAsset.addActionListener(e -> {
            setupImageButton(mBaseLevelAsset.getDropDown(), mapGenerationImageWidth, mapGenerationImageHeight,
                    mapGenerationImageButton);
        });

        mBaseLevelField = new OutlineLabelToField("Base Level:", mainColor, mWidth, mCollapsedHeight);
        mBaseLevelField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mBaseLevelField.setRightText("1");
        mBaseLevelField.getTextField().setEditable(false);
        PlainDocument doc = (PlainDocument) mBaseLevelField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mWaterLevelAssetDropDown = new OutlineLabelToDropDown("Water Asset:", mainColor, width, mCollapsedHeight);
        mWaterLevelAssetDropDown.setBackground(mainColor);
        AssetPool.getInstance().getLiquids().forEach((key, value) -> mWaterLevelAssetDropDown.addItem(key));
        mWaterLevelAssetDropDown.addActionListener(e -> {
            setupImageButton(mWaterLevelAssetDropDown.getDropDown(), mapGenerationImageWidth, mapGenerationImageHeight,
                    mapGenerationImageButton);
        });

        mWaterLevelField = new OutlineLabelToField("Water Level:", mainColor,  mWidth, mCollapsedHeight);
        mWaterLevelField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mWaterLevelField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mTerrainAssetDropDown = new OutlineLabelToDropDown("Terrain Asset:", mainColor, width, mCollapsedHeight);
        mTerrainAssetDropDown.setBackground(mainColor);
        simpleToFullAssetNameMap.forEach((key, value) -> mTerrainAssetDropDown.addItem(key));
        mTerrainAssetDropDown.addActionListener(e -> {
            setupImageButton(mTerrainAssetDropDown.getDropDown(), mapGenerationImageWidth, mapGenerationImageHeight,
                    mapGenerationImageButton);
        });

        mMaxHeightField = new OutlineLabelToField("Max Height:", mainColor, mWidth, mCollapsedHeight);
        mMaxHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mMaxHeightField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mMinHeightField = new OutlineLabelToField("Min Height:", mainColor, mWidth, mCollapsedHeight);
        mMinHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        doc = (PlainDocument) mMinHeightField.getTextField().getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        mNoiseZoomField = new OutlineLabelToField("Noise Zoom:", mWidth, mCollapsedHeight);
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
        JPanel mapMetadataPanel = new JGamePanel(true);
        mapMetadataPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mapMetadataPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mapMetadataPanel.setBackground(mainColor);
        // Add all components to the main panel

        mapMetadataPanel.add(mMapNameField);
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
        getContentPanel().add(SwingUiUtils.createBonelessScrollingPaneNoVertical(mWidth, expandedHeight, mapMetadataPanel));
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
        if (!isOpen()) { return; }

        updateTileStack(tile);

        JsonObject request = new JsonObject();
        request.put(GameModelAPI.GET_TILE_OPERATION, GameModelAPI.GET_TILE_OPERATION_ROW_AND_COLUMN);
        request.put(GameModelAPI.GET_TILE_OPERATION_ROW_OR_Y, tile.getRow());
        request.put(GameModelAPI.GET_TILE_OPERATION_COLUMN_OR_X, tile.getColumn());
        request.put(GameModelAPI.GET_TILE_OPERATION_RADIUS, 0);

        JsonArray tiles = gameController.getTilesAt(request);
        gameController.setSelectedTiles(tiles);
    }
}
