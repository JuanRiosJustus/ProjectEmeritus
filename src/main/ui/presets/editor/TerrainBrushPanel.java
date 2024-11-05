package main.ui.presets.editor;

import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.ui.components.OutlineLabel;
import main.ui.custom.*;
import main.ui.huds.controls.JGamePanel;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

public class TerrainBrushPanel extends VerticalAccordionPanel {
    private final Random random = new Random();
    public final JTextField terrainConfigsTileTerrain = new JTextField();
    public final StringComboBox mTerrainNameDropDown = new StringComboBox();
    public final OutlineLabelToLabel mCurrentHeightLabel = new OutlineLabelToLabel();
    public final OutlineLabelToLabel mCurrentTerrainLabel = new OutlineLabelToLabel();
    public final OutlineLabelToDropDown mBrushSizeDropDown = new OutlineLabelToDropDown();
    public final OutlineLabelToDropDown mTileHeightDropDown = new OutlineLabelToDropDown();
    public final Map<String, String> simpleToFullTerrainAssetNameMap = new HashMap<>();
    public TerrainBrushPanel() { }

    public TerrainBrushPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super("Terrain Brush", mainColor, width, collapsedHeight, expandedHeight);


        simpleToFullTerrainAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));


        mCurrentHeightLabel.setup(mWidth, mCollapsedHeight);
        mCurrentHeightLabel.setLeftLabel("Current Height:");
        mCurrentHeightLabel.setRightLabel("100");
        mCurrentHeightLabel.setBackground(mainColor);

        mCurrentTerrainLabel.setup(mWidth, mCollapsedHeight);
        mCurrentTerrainLabel.setLeftLabel("Current Terrain:");
        mCurrentTerrainLabel.setRightLabel("???");
        mCurrentTerrainLabel.setBackground(mainColor);

        mBrushSizeDropDown.setup(mainColor, mWidth, mCollapsedHeight);
        mBrushSizeDropDown.setLeftLabel("Brush Size:");
        IntStream.range(1, 11).forEach(i -> mBrushSizeDropDown.addItem(String.valueOf(i)));
        mBrushSizeDropDown.setBackground(mainColor);

        mTileHeightDropDown.setup(mainColor, mWidth, mCollapsedHeight);
        mTileHeightDropDown.setLeftLabel("Tile Height:");
        IntStream.range(0, 11).forEach(i -> mTileHeightDropDown.addItem(String.valueOf(i)));
        mTileHeightDropDown.setBackground(mainColor);

        JLabel terrainLabel = new OutlineLabel("Terrain Asset");
        terrainLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        // Setting up the image for terrain
        JButton terrainConfigsTileImageButton = new JButton();
        int imageWidth = (int) (mWidth * .5);
        int imageHeight = (int) (mWidth * .5);
        terrainConfigsTileImageButton.setMinimumSize(new Dimension(imageWidth, imageHeight));
        terrainConfigsTileImageButton.setMaximumSize(new Dimension(imageWidth, imageHeight));
        terrainConfigsTileImageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));

        // Setup dropdown for terrain
        SwingUiUtils.setupPrettyStringComboBox(mTerrainNameDropDown, mainColor, mWidth, mCollapsedHeight);
        mTerrainNameDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        simpleToFullTerrainAssetNameMap.forEach((key, value) -> mTerrainNameDropDown.addItem(key));

        mTerrainNameDropDown.addActionListener(e -> {
            linkDropDownWithImg(mTerrainNameDropDown, imageWidth, imageHeight, terrainConfigsTileImageButton, terrainConfigsTileTerrain);
        });
        mTerrainNameDropDown.setSelectedIndex(random.nextInt(mTerrainNameDropDown.getItemCount()));

        JLabel terrainConfigsTileImageFullNameLabel = new OutlineLabel("Full Terrain Name");
        terrainConfigsTileImageFullNameLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainConfigsTileImageFullNameLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));

        terrainConfigsTileTerrain.setHorizontalAlignment(JTextField.CENTER);
        terrainConfigsTileTerrain.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainConfigsTileTerrain.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight / 2));
        terrainConfigsTileTerrain.setEditable(false);

        JPanel mainPanel = new JGamePanel(false);
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
//        mainPanel.add(currentTileHeightLabel);
//        mainPanel.add(currentTileHeightField);
//        mainPanel.add(keyValuePanel);
        mainPanel.add(mCurrentHeightLabel);
        mainPanel.add(mCurrentTerrainLabel);
        mainPanel.add(mBrushSizeDropDown);
        mainPanel.add(mTileHeightDropDown);
//        mainPanel.add(mCurrentBrushSizeLTDD);
//        mainPanel.add(mTerrainBrushSizeDropDown);
//        mainPanel.add(terrainConfigsTileHeightLabel);
//        mainPanel.add(mTerrainHeightDropDown);
        mainPanel.add(terrainLabel);
        mainPanel.add(terrainConfigsTileImageButton);
        mainPanel.add(mTerrainNameDropDown);
//        mainPanel.add(terrainConfigsTileImageFullNameLabel);
//        mainPanel.add(terrainConfigsTileTerrain);
        mainPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));

        getContentPanel().add(mainPanel);
    }

    private void linkDropDownWithImg(StringComboBox dropdown, int width, int height, JButton img, JTextField result) {
        setupTerrainImage(dropdown, width, height, img);
        String dropdownValue = dropdown.getSelectedItem();
        result.setText(dropdownValue);
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
}
