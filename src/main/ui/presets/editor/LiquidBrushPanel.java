package main.ui.presets.editor;

import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.ui.custom.StringComboBox;

import main.ui.outline.OutlineDropDownRow;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LiquidBrushPanel extends EditorPanel {
    public final StringComboBox mLiquidNameDropDown = new StringComboBox();
    private final Map<String, String> simpleToFullLiquidAssetNameMap = new HashMap<>();
    public final OutlineDropDownRow mFillMode = null;
    public final OutlineDropDownRow mLiquidHeightDropDown = null;
    public LiquidBrushPanel() { }
    public LiquidBrushPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
//        super(mainColor, width, collapsedHeight, expandedHeight);
//
//        simpleToFullLiquidAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("liquids"));
//
//        mLayeringPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
//        mLayeringPanel.setBackground(mainColor);
//
//        mFillMode.setup(mainColor, mWidth, mCollapsedHeight);
//        mFillMode.setLeftLabel("Fill Mode:");
//        mFillMode.setBackground(mainColor);
//        mFillMode.addItem(GameAPI.UPDATE_TILE_LAYERS_OPERATION_FILL_TO_LAYER);
//        IntStream.range(1, 11).forEach(i -> mFillMode.addItem(String.valueOf(i)));
//
//        mLiquidHeightDropDown.setup(mainColor, mWidth, mCollapsedHeight);
//        mLiquidHeightDropDown.setLeftLabel("Liquid Height");
//        IntStream.range(1, 11).forEach(i -> mLiquidHeightDropDown.addItem(String.valueOf(i)));
//        mLiquidHeightDropDown.setBackground(mainColor);
//
//
//        // Setting up the image for liquid
//        JButton liquidConfigsTileImageButton = new JButton();
//        int imageWidth = (int) (mWidth * .5);
//        int imageHeight = (int) (mWidth * .5);
//        liquidConfigsTileImageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));
//        liquidConfigsTileImageButton.setBackground(mainColor);
//
//        // Setup dropdown for terrain
//        SwingUiUtils.setupPrettyStringComboBox(mLiquidNameDropDown, mainColor, mWidth, mCollapsedHeight);
//        mLiquidNameDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
//        simpleToFullLiquidAssetNameMap.forEach((key, value) -> mLiquidNameDropDown.addItem(key));
//        mLiquidNameDropDown.addActionListener(e -> {
//            linkDropDownWithImgV2(mLiquidNameDropDown, imageWidth, imageHeight, liquidConfigsTileImageButton);
//        });
//        mLiquidNameDropDown.setSelectedIndex(0);
//
////
////        JLabel terrainConfigsTileHeightLabel = new OutlineLabel("Tile Height");
////        terrainConfigsTileHeightLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
////        terrainConfigsTileHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
////
////        SwingUiUtils.setupPrettyStringComboBox(terrainConfigsTileHeightDropDown, ColorPalette.getRandomColor(), mSideBarPanelWidth, mSideBarPanelHeightSize1);
////        terrainConfigsTileHeightDropDown.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
////        IntStream.range(0, 11).forEach(i -> terrainConfigsTileHeightDropDown.addItem(String.valueOf(i)));
////        terrainConfigsTileHeightDropDown.setSelectedIndex(0);
//
//        JPanel liquidConfigsContentPanel = new GameUI(false);
//        liquidConfigsContentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
//        liquidConfigsContentPanel.add(mFillMode);
//        liquidConfigsContentPanel.add(mLiquidHeightDropDown);
//        liquidConfigsContentPanel.add(liquidConfigsTileImageButton);
//        liquidConfigsContentPanel.add(mLiquidNameDropDown);
//        liquidConfigsContentPanel.add(mLayeringPanel);
//
//        liquidConfigsContentPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
//
//        getContentPanel().add(liquidConfigsContentPanel);
    }

//    public void setupLayerData(Tile tile) {
//        mLayeringPanel.removeAll();
//
//        mCurrentHeightLabel.setRightLabel(String.valueOf(tile.getHeight()));
////        mCurrentTerrainLabel.setRightLabel(String.valueOf(tile.getLayersCopy().size()));
//
//        JPanel jPanel = new GameUI(mWidth, mCollapsedHeight);
//        // Create image holder
//        int imageSize = Math.min(mWidth, mCollapsedHeight);
//        int imageWidth = imageSize;
//        int imageHeight = imageSize;
//        JButton image = new JButton();
//        image.setBorderPainted(false);
//        image.setFocusPainted(false);
//        image.setBackground(mColor);
//        image.setPreferredSize(new Dimension(imageWidth, imageHeight));
//        // Create text portion
//        int headerWidth = mWidth - imageWidth;
//        int headerHeight = imageHeight;
//        OutlineLabelToLabel header = new OutlineLabelToLabel(headerWidth, headerHeight);
//        header.setLeftLabel("Layer");
//        header.setRightLabel("Height");
//        header.setBackground(mColor);
//        // show panel
//        jPanel.add(image);
//        jPanel.add(header);
//        jPanel.setBackground(mColor);
//        mLayeringPanel.add(jPanel);
//
//        // Create a row for each later of the tile
//        JPanel layerPanel = new GameUI(mWidth, mExpandedHeight);
//        layerPanel.setBackground(mColor);
//        JSONArray layers = tile.getLayersCopy();
//        Collections.reverse(layers);
//        for (Object object : layers) {
//            JPanel layer = setup(tile, (JSONObject) object, imageSize, headerWidth, headerHeight);
//            layerPanel.add(layer);
//        }
//        mLayeringPanel.add(layerPanel);
//
//    }


    private static void setupLiquidImage(StringComboBox terrainDropDown, int imageWidth, int imageHeight, JButton imageButton) {
        String assetName = terrainDropDown.getSelectedItem();
        String id = AssetPool.getInstance().getOrCreateAsset(
                imageWidth,
                imageHeight,
                assetName,
                AssetPool.FLICKER_ANIMATION,
                0,
                assetName + "_" + imageWidth + "_" + imageHeight + Objects.hash(terrainDropDown) + Objects.hash(imageButton)
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        imageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
    }
    private void linkDropDownWithImgV2(StringComboBox dropdown, int width, int height, JButton img) {
        setupLiquidImage(dropdown, width, height, img);
        String dropdownValue = dropdown.getSelectedItem();
//        result.setText(dropdownValue);
    }
}
