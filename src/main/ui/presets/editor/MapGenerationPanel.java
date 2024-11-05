package main.ui.presets.editor;

import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.components.OutlineLabel;
import main.ui.custom.*;
import main.ui.huds.controls.JGamePanel;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.*;

public class MapGenerationPanel extends VerticalAccordionPanel {
    private final Random random = new Random();
    public final JTextField mDescriptionField = new JTextField();
    public final JCheckBox mUseNoiseGenerationCheckBox = new JCheckBox("Use Noise Generation");
    public final JTextField mNoiseMaxHeightField = new JTextField();
    public final JTextField mNoiseMinHeightField = new JTextField();
    public final JTextField mNoiseZoomField = new JTextField();
    public final JTextField mBaseHeightField = new JTextField();
    public final JTextField mBaseTerrain = new JTextField();
    public final StringComboBox mTerrainSelectionDropDown = new StringComboBox();
    public final StringComboBox mMapSizeDropDown = new StringComboBox();
    public final JButton mGenerateWithNoiseButton = new JButton("Generate Map With Noise");
    public final JButton mGenerateWithoutNoiseButton = new JButton("Generate Map Without Noise");
    public final JButton mGenerateWithCompleteRandomness = new JButton("Generate Completely Randomly");
    public final Map<String, String> simpleToFullTerrainAssetNameMap = new HashMap<>();
    public MapGenerationPanel() { }
    public MapGenerationPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super("Map Generation", mainColor, width, collapsedHeight, expandedHeight);

        simpleToFullTerrainAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));

        // --- Label and text area for map name ---
        JLabel mapNameLabel = new OutlineLabel("Map Name");
        // Set the preferred size and font for the label
        mapNameLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapNameLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // Text area for entering the map name
        JTextArea mapNameField = new JTextArea();
        // Set the preferred size and font for the text area
        mapNameField.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapNameField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // --- Label and text field for map description ---
        JLabel mapDescriptionLabel = new OutlineLabel("Map Description");
        // Set the preferred size and font for the description label
        mapDescriptionLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapDescriptionLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // Configure the text field for the map description
        mDescriptionField.setHorizontalAlignment(JTextField.CENTER);
        mDescriptionField.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mDescriptionField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // --- Label and dropdown for selecting map size ---
        JLabel mapSizeLabel = new OutlineLabel("Map Size");
        // Set the preferred size and font for the map size label
        mapSizeLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapSizeLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // Configure the dropdown for map size selection
        SwingUiUtils.setupPrettyStringComboBox(mMapSizeDropDown, mWidth, mCollapsedHeight);
        mMapSizeDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        // Add default size options to the dropdown
        mMapSizeDropDown.addItem("20x14");
        mMapSizeDropDown.addItem("30x21");
        mMapSizeDropDown.addItem("40x28");
        mMapSizeDropDown.setSelectedIndex(0);

        // --- Label and panel for base terrain selection ---
        JLabel mapGenerationTerrainSelectionLabel = new OutlineLabel("Base Terrain");
        // Set the preferred size and font for the terrain label
        mapGenerationTerrainSelectionLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationTerrainSelectionLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // --- Image button and dropdown for terrain selection ---
        int mapGenerationTerrainSelectionImageWidth = (int) (mWidth * .25);
        int mapGenerationTerrainSelectionImageHeight = (int) (mWidth * .25);
        JButton mapGenerationTerrainSelectionImage = new JButton();
        mapGenerationTerrainSelectionImage.setPreferredSize(new Dimension(
                mapGenerationTerrainSelectionImageWidth, mapGenerationTerrainSelectionImageHeight));

        int mapGenerationTerrainSelectionDropDownWidth = mWidth;
        int mapGenerationTerrainSelectionDropDownHeight = mCollapsedHeight;
        // Configure the dropdown for terrain selection
        SwingUiUtils.setupPrettyStringComboBox(mTerrainSelectionDropDown,
                mapGenerationTerrainSelectionDropDownWidth, mapGenerationTerrainSelectionDropDownHeight);
        mTerrainSelectionDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        // Add terrain options to the dropdown
        simpleToFullTerrainAssetNameMap.forEach((key, value) -> mTerrainSelectionDropDown.addItem(key));

        // Action listener to update the image when a new terrain is selected
        mTerrainSelectionDropDown.addActionListener(e ->
                linkDropDownWithImg(mTerrainSelectionDropDown, mapGenerationTerrainSelectionImageWidth,
                        mapGenerationTerrainSelectionImageHeight, mapGenerationTerrainSelectionImage,
                        mBaseTerrain));
        // Set a random terrain selection as default
        mTerrainSelectionDropDown.setSelectedIndex(
                random.nextInt(mTerrainSelectionDropDown.getItemCount()));

        // --- Checkbox for enabling/disabling noise generation ---
        mUseNoiseGenerationCheckBox.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mUseNoiseGenerationCheckBox.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mUseNoiseGenerationCheckBox.setFocusPainted(false);

        // --- Labels and text fields for noise generation settings ---
        JLabel mapGenerationNoiseMaxHeightLabel = new OutlineLabel("Max Noise Height");
        mapGenerationNoiseMaxHeightLabel.setVisible(false);
        mapGenerationNoiseMaxHeightLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationNoiseMaxHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mNoiseMaxHeightField.setVisible(false);
        mNoiseMaxHeightField.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mNoiseMaxHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mNoiseMaxHeightField.setHorizontalAlignment(JTextField.CENTER);
        PlainDocument doc = (PlainDocument) mNoiseMaxHeightField.getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        JLabel mapGenerationNoiseMinHeightLabel = new OutlineLabel("Min Noise Height");
        mapGenerationNoiseMinHeightLabel.setVisible(false);
        mapGenerationNoiseMinHeightLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationNoiseMinHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mNoiseMinHeightField.setVisible(false);
        mNoiseMinHeightField.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mNoiseMinHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mNoiseMinHeightField.setHorizontalAlignment(JTextField.CENTER);
        doc = (PlainDocument) mNoiseMinHeightField.getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

        JLabel mapGenerationNoiseZoomLabel = new OutlineLabel("Noise Generation Zoom");
        mapGenerationNoiseZoomLabel.setVisible(false);
        mapGenerationNoiseZoomLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationNoiseZoomLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mNoiseZoomField.setVisible(false);
        mNoiseZoomField.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mNoiseZoomField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mNoiseZoomField.setHorizontalAlignment(JTextField.CENTER);
        doc = (PlainDocument) mNoiseZoomField.getDocument();
        doc.setDocumentFilter(new FloatRangeDocumentFilter()); // Filter to allow only floats

        // --- Buttons for map generation ---
        mGenerateWithNoiseButton.setVisible(false);
        mGenerateWithNoiseButton.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mGenerateWithNoiseButton.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        JLabel mapGenerationTileBaseHeightLabel = new OutlineLabel("Map Base Height");
        mapGenerationTileBaseHeightLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mapGenerationTileBaseHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        mBaseHeightField.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mBaseHeightField.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        mBaseHeightField.setHorizontalAlignment(JTextField.CENTER);
        doc = (PlainDocument) mBaseHeightField.getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter()); // Filter to allow only integers

//        JButton mapGenerationWithoutNoiseButton = new JButton("Generate Map Without Noise");
        mGenerateWithoutNoiseButton.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mGenerateWithoutNoiseButton.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

//        JButton mapGenerationCompleteRandomness = new JButton("Generate Completely Random Map");
        mGenerateWithCompleteRandomness.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        mGenerateWithCompleteRandomness.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // --- Action listeners to manage noise generation settings ---
        mUseNoiseGenerationCheckBox.addChangeListener(e -> {
            // Toggle visibility of noise generation fields based on checkbox selection
            mapGenerationNoiseMinHeightLabel.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mNoiseMinHeightField.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mapGenerationNoiseMaxHeightLabel.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mNoiseMaxHeightField.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mapGenerationNoiseZoomLabel.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mNoiseZoomField.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mGenerateWithNoiseButton.setVisible(mUseNoiseGenerationCheckBox.isSelected());
            mapGenerationTileBaseHeightLabel.setVisible(!mUseNoiseGenerationCheckBox.isSelected());
            mBaseHeightField.setVisible(!mUseNoiseGenerationCheckBox.isSelected());
            mGenerateWithoutNoiseButton.setVisible(!mUseNoiseGenerationCheckBox.isSelected());
        });

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
        JPanel mapMetadataPanel = new JGamePanel(false);
        mapMetadataPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mapMetadataPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        // Add all components to the main panel
//        Dictionary dict = new Hashtable();
//        for (int i=0; i<=10000; i += 1000) {
//            dict.put(i, new JLabel(Integer.toString(i / 1000)));
//        }
//
//        JSlider slider = new JSlider();
//        slider.setLabelTable(dict);
//        slider.setMinimum(0);
//        slider.setMaximum(100);
//        mapMetadataPanel.add(slider);


        //Create the slider
//        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
//                0, 60, 5);
////        framesPerSecond.addChangeListener(framesPerSecond);
//        framesPerSecond.setMajorTickSpacing(10);
//        framesPerSecond.setPaintTicks(true);
//
////Create the label table
//        Hashtable labelTable = new Hashtable();
//        labelTable.put( new Integer( 0 ), new JLabel("Stop") );
//        labelTable.put( new Integer( 60/10 ), new JLabel("Slow") );
//        labelTable.put( new Integer( 60 ), new JLabel("Fast") );
//        framesPerSecond.setLabelTable( labelTable );
//        framesPerSecond.setPaintLabels(true);
//        mapMetadataPanel.add(framesPerSecond);

        mapMetadataPanel.add(mapNameLabel);
        mapMetadataPanel.add(mapNameField);
        mapMetadataPanel.add(mapDescriptionLabel);
        mapMetadataPanel.add(mDescriptionField);
        mapMetadataPanel.add(mapSizeLabel);
        mapMetadataPanel.add(mMapSizeDropDown);
        mapMetadataPanel.add(mapGenerationTerrainSelectionLabel);
//        mapMetadataPanel.add(mapGenerationTerrainSelectionPanel);
        mapMetadataPanel.add(mapGenerationTerrainSelectionImage);
        mapMetadataPanel.add(mTerrainSelectionDropDown);
        mapMetadataPanel.add(mUseNoiseGenerationCheckBox);
        mapMetadataPanel.add(mapGenerationNoiseMinHeightLabel);
        mapMetadataPanel.add(mNoiseMinHeightField);
        mapMetadataPanel.add(mapGenerationNoiseMaxHeightLabel);
        mapMetadataPanel.add(mNoiseMaxHeightField);
        mapMetadataPanel.add(mapGenerationNoiseZoomLabel);
        mapMetadataPanel.add(mNoiseZoomField);
        mapMetadataPanel.add(mGenerateWithNoiseButton);
        mapMetadataPanel.add(mapGenerationTileBaseHeightLabel);
        mapMetadataPanel.add(mBaseHeightField);
        mapMetadataPanel.add(mGenerateWithoutNoiseButton);
        mapMetadataPanel.add(mGenerateWithCompleteRandomness);

        getContentPanel().add(mapMetadataPanel);
    }

    private static void setupTerrainImage(StringComboBox terrainDropDown, int imageWidth, int imageHeight, JButton imageButton) {
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

    public void setMapGenerationRandomDefaultsIfEmpty() { setMapGenerationRandomDefaultsIfEmpty(false); }
    public void setMapGenerationRandomDefaultsIfEmpty(boolean forceRandomize) {
        if (mNoiseMinHeightField.getText().isEmpty() || forceRandomize) {
            mNoiseMinHeightField.setText(String.valueOf(random.nextInt(10) * -1));
        }
        if (mNoiseMaxHeightField.getText().isEmpty() || forceRandomize) {
            mNoiseMaxHeightField.setText(String.valueOf(random.nextInt(10)));
        }
        if (mNoiseZoomField.getText().isEmpty() || forceRandomize) {
            mNoiseZoomField.setText(String.valueOf(random.nextFloat(.25f, .75f)));
        }

        if (mBaseHeightField.getText().isEmpty() || forceRandomize) {
            mBaseHeightField.setText(String.valueOf(random.nextInt(0, 10)));
        }

        if (mTerrainSelectionDropDown.getItemCount() > 0 && forceRandomize) {
            mTerrainSelectionDropDown.setSelectedIndex(
                    random.nextInt(mTerrainSelectionDropDown.getItemCount()));
        }

        if (forceRandomize) {
            mUseNoiseGenerationCheckBox.setSelected(random.nextBoolean());
        }
    }
    private void linkDropDownWithImg(StringComboBox dropdown, int width, int height, JButton img, JTextField result) {
        setupTerrainImage(dropdown, width, height, img);
        String dropdownValue = dropdown.getSelectedItem();
        result.setText(dropdownValue);
    }
    private static JButton createTerrainSelectionImage(int mapGenerationTerrainSelectionImageWidth,
                                                       int mapGenerationTerrainSelectionImageHeight) {
        JButton mapGenerationTerrainSelectionImage = new JButton();
        mapGenerationTerrainSelectionImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        mapGenerationTerrainSelectionImage.setMinimumSize(new Dimension(
                mapGenerationTerrainSelectionImageWidth, mapGenerationTerrainSelectionImageHeight));
        mapGenerationTerrainSelectionImage.setMaximumSize( new Dimension(
                mapGenerationTerrainSelectionImageWidth, mapGenerationTerrainSelectionImageHeight));
        mapGenerationTerrainSelectionImage.setPreferredSize(new Dimension(
                mapGenerationTerrainSelectionImageWidth, mapGenerationTerrainSelectionImageHeight));
        return mapGenerationTerrainSelectionImage;
    }
}
