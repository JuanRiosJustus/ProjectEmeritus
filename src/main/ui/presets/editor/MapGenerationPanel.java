package main.ui.presets.editor;

import main.game.main.GameAPI;
import main.graphics.GameUI;
import main.ui.outline.*;
import main.ui.outline.production.core.OutlineButton;
import main.ui.outline.production.labels.OutlineButtonBackgroundWithWestLabel;
import main.ui.outline.production.labels.OutlineComboBoxWithNorthWestLabel;
import main.ui.outline.production.labels.OutlineSliderWithNorthWestLabel;
import main.ui.swing.NoScrollBarPane;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import main.game.components.tile.Tile;
import main.game.main.GameController;

import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.ui.custom.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.PlainDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.util.stream.IntStream;

public class MapGenerationPanel extends EditorPanel {
    private final Random random = new Random();
    public OutlineFieldRow mMapNameField = null;
    public OutlineDropDownRow mBrushSizeDropDown = null;
    public OutlineDropDownRow mMapSizeDropDown = null;
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();
    private final int DEFAULT_FOUNDATION_MINIMUM_DEPTH = 1;
    private final int DEFAULT_FOUNDATION_MAXIMUM_DEPTH = 4;
    private OutlineButtonBackgroundWithWestLabel mFoundationHeader = null;
    private OutlineComboBoxWithNorthWestLabel mFoundationAsset = null;
    private OutlineSliderWithNorthWestLabel mFoundationDepth = null;

    private final int DEFAULT_TERRAIN_MINIMUM_HEIGHT = 5;
    private final int DEFAULT_TERRAIN_MAXIMUM_HEIGHT = 15;
    private final float DEFAULT_TERRAIN_ZOOM = .5f;
    private OutlineButtonBackgroundWithWestLabel mTerrainHeader = null;
    private OutlineComboBoxWithNorthWestLabel mTerrainAsset = null;
    private OutlineSliderWithNorthWestLabel mTerrainMinimumHeight = null;
    private OutlineSliderWithNorthWestLabel mTerrainMaximumHeight = null;
    private OutlineSliderWithNorthWestLabel mTerrainNoiseZoom = null;

    private OutlineButtonBackgroundWithWestLabel mLiquidHeader = null;
    private OutlineComboBoxWithNorthWestLabel mLiquidAsset = null;
    private OutlineSliderWithNorthWestLabel mLiquidLevel = null;

    public JButton mLoadButton = null;
    public JButton mSaveButton = null;
    public JButton mRandomizeButton = null;
    public JButton mGenerateButton = null;
    public MapGenerationPanel(Color mainColor, int width, int rowHeight, int height) {
        super(mainColor, width, rowHeight, height);

        simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));

        mLayeringPanel.setPreferredSize(new Dimension(mWidth, height));
        mLayeringPanel.setBackground(mainColor);

        mMapNameField = new OutlineFieldRow("Map Name: ", mainColor, width, rowHeight);
        SwingUiUtils.setBoxLayoutSize(mMapNameField, width, rowHeight);

        // Add default size options to the dropdown
        mMapSizeDropDown = new OutlineDropDownRow("Map Size:", mainColor, width, rowHeight);
        mMapSizeDropDown.addItem("20x14");
        mMapSizeDropDown.addItem("30x21");
        mMapSizeDropDown.addItem("40x28");
        mMapSizeDropDown.setSelectedIndex(0);
        SwingUiUtils.setBoxLayoutSize(mMapSizeDropDown, width, rowHeight);

        // --- Label and panel for base terrain selection ---
        JLabel mapGenerationTerrainSelectionLabel = new OutlineLabel("Base Terrain");
        // Set the preferred size and font for the terrain label
        mapGenerationTerrainSelectionLabel.setPreferredSize(new Dimension(mWidth, mRowHeight));
        mapGenerationTerrainSelectionLabel.setFont(FontPool.getInstance().getFontForHeight(mRowHeight));

        mBrushSizeDropDown = new OutlineDropDownRow("Brush Size:", mainColor, mWidth, mRowHeight);
        mBrushSizeDropDown.addItem("1");

        // --- Image button and dropdown for terrain selection ---
        int mapGenerationImageWidth = (int) (mWidth * .2);
        int mapGenerationImageHeight = (int) (mWidth * .2);
        JButton mapGenerationImageButton = new JButton();
        mapGenerationImageButton.setPreferredSize(new Dimension(mapGenerationImageWidth, mapGenerationImageHeight));


        int rowHeightDoubled = (int) (rowHeight * 2);

        mFoundationHeader = new OutlineButtonBackgroundWithWestLabel(mainColor, width, rowHeightDoubled, "Foundation");
        mFoundationAsset = new OutlineComboBoxWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Foundation Asset");
        mFoundationDepth = new OutlineSliderWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Foundation Depth");
        SwingUiUtils.setBoxLayoutSize(mFoundationHeader, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mFoundationAsset, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mFoundationDepth, width, rowHeightDoubled);

        AssetPool.getInstance().getFloors().forEach((key, value) -> mFoundationAsset.addItem(key));
        mFoundationAsset.addActionListener(e -> {
            setupImageButton(
                    mFoundationAsset.getDropDown(),
                    mFoundationHeader.getImageWidth(),
                    mFoundationHeader.getImageHeight(),
                    mFoundationHeader.getImageContainer());
            mFoundationAsset.addAdditionalContext(mFoundationAsset.getDropDown().getSelectedItem());
        });
        mFoundationAsset.getDropDown().setSelectedIndex(0);
        mFoundationDepth.getSlider().addChangeListener(e ->
                mFoundationDepth.addAdditionalContext(String.valueOf(mFoundationDepth.getValue())));


        mTerrainHeader = new OutlineButtonBackgroundWithWestLabel(mainColor, width, rowHeightDoubled, "Terrain");
        mTerrainAsset = new OutlineComboBoxWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Terrain Asset");
        mTerrainMinimumHeight = new OutlineSliderWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Terrain Minimum Height");
        mTerrainMaximumHeight = new OutlineSliderWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Terrain Maximum Height");
        mTerrainNoiseZoom = new OutlineSliderWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Terrain Height Deviation");

        SwingUiUtils.setBoxLayoutSize(mTerrainHeader, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mTerrainAsset, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mTerrainMinimumHeight, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mTerrainMaximumHeight, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mTerrainNoiseZoom, width, rowHeightDoubled);

        AssetPool.getInstance().getFloors().forEach((key, value) -> mTerrainAsset.addItem(key));
        mTerrainAsset.addActionListener(e -> {
            setupImageButton(
                    mTerrainAsset.getDropDown(),
                    mTerrainHeader.getImageWidth(),
                    mTerrainHeader.getImageHeight(),
                    mTerrainHeader.getImageContainer());
            mTerrainAsset.addAdditionalContext(mTerrainAsset.getDropDown().getSelectedItem());

        });
        mTerrainAsset.getDropDown().setSelectedIndex(1);
        mTerrainMinimumHeight.getSlider().addChangeListener(e ->
                mTerrainMinimumHeight.addAdditionalContext(String.valueOf(mTerrainMinimumHeight.getValue())));
        mTerrainMaximumHeight.getSlider().addChangeListener(e ->
                mTerrainMaximumHeight.addAdditionalContext(String.valueOf(mTerrainMaximumHeight.getValue())));
        mTerrainNoiseZoom.getSlider().setMinimum(0);
        mTerrainNoiseZoom.getSlider().setMaximum(100);
        mTerrainNoiseZoom.getSlider().addChangeListener(e ->
                mTerrainNoiseZoom.addAdditionalContext(StringUtils.floatToPercentage(mTerrainNoiseZoom.getValue() / 100f)));

        mLiquidHeader = new OutlineButtonBackgroundWithWestLabel(mainColor, width, rowHeightDoubled, "Liquid");
        mLiquidAsset = new OutlineComboBoxWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Liquid Asset");
        mLiquidLevel = new OutlineSliderWithNorthWestLabel(mainColor, width, rowHeightDoubled, "Liquid Level");
        SwingUiUtils.setBoxLayoutSize(mLiquidHeader, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mLiquidAsset, width, rowHeightDoubled);
        SwingUiUtils.setBoxLayoutSize(mLiquidLevel, width, rowHeightDoubled);

        AssetPool.getInstance().getLiquids().forEach((key, value) -> mLiquidAsset.addItem(key));
        mLiquidAsset.addActionListener(e -> {
            setupImageButton(
                    mLiquidAsset.getDropDown(),
                    mLiquidHeader.getImageWidth(),
                    mLiquidHeader.getImageHeight(),
                    mLiquidHeader.getImageContainer());
            mLiquidAsset.addAdditionalContext(mLiquidAsset.getDropDown().getSelectedItem());
        });

        mLiquidAsset.getDropDown().setSelectedIndex(0);
        mLiquidLevel.getSlider().addChangeListener(e ->
                mLiquidLevel.addAdditionalContext(String.valueOf(mLiquidLevel.getValue())));



//        Map.Entry<JPanel, JButton> loadMapRow = createButtonPanel("Load Map", mainColor, width, rowHeight);
//        mLoadButton = loadMapRow.getValue();
//
//        Map.Entry<JPanel, JButton> saveMapRow = createButtonPanel("Save Map", mainColor, width, rowHeight);
//        mSaveButton = saveMapRow.getValue();

        Map.Entry<JPanel, JButton> randomizeMapRow = createButtonPanel("Randomize Map", mainColor, width, rowHeight);
        mRandomizeButton = randomizeMapRow.getValue();

        Map.Entry<JPanel, JButton> generateMapRow = createButtonPanel("Generate Map", mainColor, width, rowHeight);
        mGenerateButton = generateMapRow.getValue();


        // --- Label and dropdown for map mode selection ---
        JLabel mapModelLabel = new OutlineLabel("Map Mode");
        mapModelLabel.setPreferredSize(new Dimension(mWidth, mRowHeight));
        mapModelLabel.setFont(FontPool.getInstance().getFontForHeight(mRowHeight));

        StringComboBox mapModeDropdown = SwingUiUtils.createJComboBox(mWidth, mRowHeight);
        mapModeDropdown.setFont(FontPool.getInstance().getFontForHeight(mRowHeight));
        mapModeDropdown.addItem("Team Deathmatch");
        mapModeDropdown.addItem("Survival");
        mapModeDropdown.addItem("Final Destination");

        // --- Main panel to hold all map metadata components ---
        JPanel mapMetadataPanel = new GameUI();
        mapMetadataPanel.setPreferredSize(new Dimension(width, height * 3)); // or any value larger than 'height'
//        mapMetadataPanel.revalidate();
//        mapMetadataPanel.repaint();
        mapMetadataPanel.setOpaque(true);
//        mapMetadataPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
//        mapMetadataPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mapMetadataPanel.setLayout(new BoxLayout(mapMetadataPanel, BoxLayout.Y_AXIS));
        mapMetadataPanel.setBackground(mainColor);
        // Add all components to the main panel

//        mapMetadataPanel.add(mSaveMapButton);
//        mapMetadataPanel.add(mLoadMapButton);
//        mapMetadataPanel.add(saveMapRow.getKey());
//        mapMetadataPanel.add(loadMapRow.getKey());

        mapMetadataPanel.add(mMapNameField);
        mapMetadataPanel.add(mMapSizeDropDown);

//        mapMetadataPanel.add(mapGenerationImageButton);
//        mapMetadataPanel.add(new TileMonitorPanel(mainColor, mWidth, mCollapsedHeight * 3));
//        mapMetadataPanel.add(mTileMonitorPanel);
//        mapMetadataPanel.add(mTileInfoPanel);


//        mapMetadataPanel.add(SwingUiUtils.verticalSpacePanel(5));
//        mapMetadataPanel.add(mFoundationRow);
//        mapMetadataPanel.add(mFoundationAssetField);
//        mapMetadataPanel.add(mFoundationDepthField);
//        mapMetadataPanel.add(SwingUiUtils.verticalSpacePanel(5));
//        mapMetadataPanel.add(mWaterLevelAssetDropDown);
//        mapMetadataPanel.add(mWaterLevelField);

        mapMetadataPanel.add(mFoundationHeader);
        mapMetadataPanel.add(mFoundationAsset);
        mapMetadataPanel.add(mFoundationDepth);

        mapMetadataPanel.add(mTerrainHeader);
        mapMetadataPanel.add(mTerrainAsset);
        mapMetadataPanel.add(mTerrainMinimumHeight);
        mapMetadataPanel.add(mTerrainMaximumHeight);
        mapMetadataPanel.add(mTerrainNoiseZoom);

        mapMetadataPanel.add(mLiquidHeader);
        mapMetadataPanel.add(mLiquidAsset);
        mapMetadataPanel.add(mLiquidLevel);

        mapMetadataPanel.add(randomizeMapRow.getKey());
        mapMetadataPanel.add(generateMapRow.getKey());


        add(new NoScrollBarPane(mapMetadataPanel, width, height, true, 1));

        randomizeMap(10, 20);
    }

    private Map.Entry<JPanel, JButton> createButtonPanel(String name, Color color, int width, int height) {
        JPanel containerPanel = new JPanel();
        containerPanel.setBackground(color);
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBorder(new EmptyBorder(0, 0, 0, 5));
        SwingUiUtils.setBoxLayoutSize(containerPanel, width, height);

        JButton button = new OutlineButton();
        button.setText(name);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setFont(FontPool.getInstance().getFontForHeight(height));
        SwingUiUtils.setHoverEffect(button);

        containerPanel.add(button, BorderLayout.CENTER);
        return Map.entry(containerPanel, button);
    }

    public JButton getGenerateButton() { return mGenerateButton; }
    public JButton getRandomizeButton() { return mRandomizeButton; }
    public String getFoundationAsset() { return mFoundationAsset.getDropDown().getSelectedItem(); }
    public int getFoundationDepth() { return mFoundationDepth.getValue(); }
    public String getTerrainAsset() { return mTerrainAsset.getDropDown().getSelectedItem(); }
    public int getTerrainHeightMinimum() { return mTerrainMinimumHeight.getValue(); }
    public int getTerrainHeightMaximum() { return mTerrainMaximumHeight.getValue(); }
    public float getTerrainNoiseZoom() { return mTerrainNoiseZoom.getValue() / 100f; }
    public String getLiquidAsset() { return mLiquidAsset.getDropDown().getSelectedItem(); }
    public int getLiquidLevel() { return mLiquidLevel.getValue(); }


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

    public void randomizeMap(int minimumHeight, int maximumHeight) {
        if (minimumHeight > maximumHeight) { return; }

        // Select random foundation
        mFoundationAsset.getDropDown().setSelectedIndex(random.nextInt(mFoundationAsset.getDropDown().getItemCount()));
        mFoundationDepth.getSlider().setMinimum(DEFAULT_FOUNDATION_MINIMUM_DEPTH);
        mFoundationDepth.getSlider().setMaximum(DEFAULT_FOUNDATION_MAXIMUM_DEPTH);
        mFoundationDepth.getSlider().setValue(random.nextInt(DEFAULT_FOUNDATION_MINIMUM_DEPTH, DEFAULT_FOUNDATION_MAXIMUM_DEPTH));

        mTerrainMinimumHeight.getSlider().setMinimum(1);
        mTerrainMinimumHeight.getSlider().setMaximum(minimumHeight);
        mTerrainMinimumHeight.setValue(random.nextInt(1, minimumHeight));

        mTerrainMaximumHeight.getSlider().setMinimum(minimumHeight);
        mTerrainMaximumHeight.getSlider().setMaximum(maximumHeight);
        mTerrainMaximumHeight.setValue(random.nextInt(minimumHeight, maximumHeight));

        mTerrainNoiseZoom.getSlider().setValue(random.nextInt(25, 75)); // always between 100

        mTerrainAsset.getDropDown().setSelectedIndex(random.nextInt(mTerrainAsset.getDropDown().getItemCount()));
        int liquidHeight = (mTerrainMinimumHeight.getValue() + mTerrainMaximumHeight.getValue()) / 2;
        mLiquidLevel.setValue(liquidHeight);
        mLiquidAsset.getDropDown().setSelectedIndex(random.nextInt(mLiquidAsset.getDropDown().getItemCount()));


//        mTerrainMinimumHeight.
//        if (mMinHeightField.getRightText().isEmpty() || forceRandomize) {
//            mMinHeightField.setRightText(String.valueOf(1));
//        }
//        if (mMaxHeightField.getRightText().isEmpty() || forceRandomize) {
//            mMaxHeightField.setRightText(String.valueOf(random.nextInt(5, 10)));
//        }
//
//        if (mWaterLevelField.getRightText().isEmpty() || forceRandomize) {
//            int min = Integer.parseInt(mMinHeightField.getRightText());
//            int max = Integer.parseInt(mMaxHeightField.getRightText());
//            int mid = (min + max) / 2;
//            mWaterLevelField.setRightText(String.valueOf(mid));
//        }
//
//        if (mNoiseZoomField.getRightText().isEmpty() || forceRandomize) {
//            mNoiseZoomField.setRightText(String.valueOf(random.nextFloat(.25f, .75f)));
//        }
//
//        if (mTerrainSelectionDropDown.getItemCount() > 0 && forceRandomize) {
//            mTerrainSelectionDropDown.setSelectedIndex(
//                    random.nextInt(mTerrainSelectionDropDown.getItemCount()));
//        }
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
