package main.ui.presets.editor;

import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.ui.custom.StringComboBox;
import main.ui.custom.SwingUiUtils;
import main.ui.custom.VerticalAccordionPanel;
import main.ui.huds.controls.JGamePanel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class LiquidBrushPanel extends VerticalAccordionPanel {
    public final StringComboBox mLiquidNameDropDown = new StringComboBox();
    private final Map<String, String> simpleToFullLiquidAssetNameMap = new HashMap<>();
    public final OutlineLabelToLabel mCurrentHeightLabel = new OutlineLabelToLabel();
    public final OutlineLabelToLabel mCurrentLiquidLabel = new OutlineLabelToLabel();
    public final OutlineLabelToDropDown mBrushSizeDropDown = new OutlineLabelToDropDown();
    public final OutlineLabelToDropDown mLiquidHeightDropDown = new OutlineLabelToDropDown();
    public LiquidBrushPanel() { }
    public LiquidBrushPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super("Liquid Brush", mainColor, width, collapsedHeight, expandedHeight);

        simpleToFullLiquidAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("liquids"));

        mCurrentHeightLabel.setup(mWidth, mCollapsedHeight);
        mCurrentHeightLabel.setLeftLabel("Current Height:");
        mCurrentHeightLabel.setRightLabel("100");
        mCurrentHeightLabel.setBackground(mainColor);

        mCurrentLiquidLabel.setup(mWidth, mCollapsedHeight);
        mCurrentLiquidLabel.setLeftLabel("Current Liquid:");
        mCurrentLiquidLabel.setRightLabel("???");
        mCurrentLiquidLabel.setBackground(mainColor);

        mBrushSizeDropDown.setup(mainColor, mWidth, mCollapsedHeight);
        mBrushSizeDropDown.setLeftLabel("Brush Size:");
        IntStream.range(1, 11).forEach(i -> mBrushSizeDropDown.addItem(String.valueOf(i)));
        mBrushSizeDropDown.setBackground(mainColor);

        mLiquidHeightDropDown.setup(mainColor, mWidth, mCollapsedHeight);
        mLiquidHeightDropDown.setLeftLabel("Liquid Height");
        IntStream.range(1, 11).forEach(i -> mLiquidHeightDropDown.addItem(String.valueOf(i)));
        mLiquidHeightDropDown.setBackground(mainColor);


        // Setting up the image for liquid
        JButton liquidConfigsTileImageButton = new JButton();
        int imageWidth = (int) (mWidth * .5);
        int imageHeight = (int) (mWidth * .5);
        liquidConfigsTileImageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));

        // Setup dropdown for terrain
        SwingUiUtils.setupPrettyStringComboBox(mLiquidNameDropDown, mainColor, mWidth, mCollapsedHeight);
        mLiquidNameDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        simpleToFullLiquidAssetNameMap.forEach((key, value) -> mLiquidNameDropDown.addItem(key));
        mLiquidNameDropDown.addActionListener(e -> {
            linkDropDownWithImgV2(mLiquidNameDropDown, imageWidth, imageHeight, liquidConfigsTileImageButton);
        });
        mLiquidNameDropDown.setSelectedIndex(0);

//
//        JLabel terrainConfigsTileHeightLabel = new OutlineLabel("Tile Height");
//        terrainConfigsTileHeightLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
//        terrainConfigsTileHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
//
//        SwingUiUtils.setupPrettyStringComboBox(terrainConfigsTileHeightDropDown, ColorPalette.getRandomColor(), mSideBarPanelWidth, mSideBarPanelHeightSize1);
//        terrainConfigsTileHeightDropDown.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
//        IntStream.range(0, 11).forEach(i -> terrainConfigsTileHeightDropDown.addItem(String.valueOf(i)));
//        terrainConfigsTileHeightDropDown.setSelectedIndex(0);

        JPanel liquidConfigsContentPanel = new JGamePanel(false);
        liquidConfigsContentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        liquidConfigsContentPanel.add(mCurrentHeightLabel);
        liquidConfigsContentPanel.add(mCurrentLiquidLabel);
        liquidConfigsContentPanel.add(mBrushSizeDropDown);
        liquidConfigsContentPanel.add(mLiquidHeightDropDown);
        liquidConfigsContentPanel.add(liquidConfigsTileImageButton);
        liquidConfigsContentPanel.add(mLiquidNameDropDown);

        liquidConfigsContentPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));

        getContentPanel().add(liquidConfigsContentPanel);
    }
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
