package main.ui.presets.editor;

import org.json.JSONArray;
import org.json.JSONObject;
import main.game.components.tile.Tile;
import main.game.main.GameController;
import main.game.main.GameModelAPI;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.custom.StringComboBox;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineLabelToDropDown;
import main.ui.outline.OutlineListWithHeader;
import main.ui.outline.OutlineListWithHeaderAndImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class UpdateStructurePanel extends EditorPanel {
    private final Random mRandom = new Random();
    public final StringComboBox mAssetNameDropDown = new StringComboBox();
    public OutlineLabelToDropDown mStructureBrushSizeDropDown = null;
    public OutlineLabelToDropDown mStructureBrushModeDropDown = null;
    public OutlineLabelToDropDown mStructureAssetDropDown = null;
    public OutlineListWithHeaderAndImage mListWidthHeaderAndImage = null;
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();

    public UpdateStructurePanel() { }

    public UpdateStructurePanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super(mainColor, width, collapsedHeight, expandedHeight);

        initialize(mainColor, width, collapsedHeight, expandedHeight);
    }

    public void initialize(Color mainColor, int width, int collapsedHeight, int expandedHeight) {

        simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("structures"));

        mLayeringPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mLayeringPanel.setBackground(mainColor);

        mListWidthHeaderAndImage = new OutlineListWithHeaderAndImage(mWidth, mCollapsedHeight * 3);

        // Setting up the image for terrain
        JButton terrainConfigsTileImageButton = new JButton();
        int imageWidth = (int) (mWidth * .5);
        int imageHeight = (int) (mWidth * .5);
        terrainConfigsTileImageButton.setMinimumSize(new Dimension(imageWidth, imageHeight));
        terrainConfigsTileImageButton.setMaximumSize(new Dimension(imageWidth, imageHeight));
        terrainConfigsTileImageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));

        mStructureBrushModeDropDown = new OutlineLabelToDropDown(mainColor, mWidth, mCollapsedHeight);
        mStructureBrushModeDropDown.setBackground(mainColor);
        mStructureBrushModeDropDown.setLeftLabel("Structure Mode:");
        mStructureBrushModeDropDown.addItem(GameModelAPI.UPDATE_STRUCTURE_ADD_MODE); // Adds a spawns
        mStructureBrushModeDropDown.addItem(GameModelAPI.UPDATE_STRUCTURE_DELETE_MODE); // removes spawns
        mStructureBrushModeDropDown.setSelectedIndex(0);

        mStructureBrushSizeDropDown = new OutlineLabelToDropDown(mainColor, mWidth, mCollapsedHeight);
        mStructureBrushSizeDropDown.setLeftLabel("Brush Size:");
        mStructureBrushSizeDropDown.setBackground(mainColor);
        IntStream.range(0, 5).forEach(i -> mStructureBrushSizeDropDown.addItem(String.valueOf(i)));

        mStructureAssetDropDown = new OutlineLabelToDropDown("Structure:", mainColor, mWidth, mCollapsedHeight);
        simpleToFullAssetNameMap.forEach((e1,e2) -> { mStructureAssetDropDown.addItem(e1); });
        mStructureAssetDropDown.getDropDown().addActionListener(e ->
                EditorPanel.setupDropDownForImage(mStructureAssetDropDown.getDropDown(), mListWidthHeaderAndImage.getImage()));
        mStructureAssetDropDown.getDropDown().setSelectedIndex(0);
//        mStructureBrushDropDown.getDropDown()
//                .addActionListener(e -> EditorPanel.setupDropDownForImage(mStructureBrushDropDown.getDropDown(),
//                imageWidth, imageHeight, terrainConfigsTileImageButton));


//        mLayeringPanel.setPreferredSize(new Dimension(mWidth, mExpandedHeight));

        JLabel terrainLabel = new OutlineLabel("Terrain Asset");
        terrainLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        terrainLabel.setBackground(mainColor);

        // Setup dropdown for terrain
        SwingUiUtils.setupPrettyStringComboBox(mAssetNameDropDown, mainColor, mWidth, mCollapsedHeight);
        mAssetNameDropDown.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));
        simpleToFullAssetNameMap.forEach((key, value) -> mAssetNameDropDown.addItem(key));
        mAssetNameDropDown.addActionListener(e -> EditorPanel.setupDropDownForImage(mAssetNameDropDown, imageWidth,
                imageHeight, terrainConfigsTileImageButton));
        mAssetNameDropDown.setSelectedIndex(mRandom.nextInt(mAssetNameDropDown.getItemCount()));

        JLabel terrainConfigsTileImageFullNameLabel = new OutlineLabel("Full Terrain Name");
        terrainConfigsTileImageFullNameLabel.setPreferredSize(new Dimension(mWidth, mCollapsedHeight));
        terrainConfigsTileImageFullNameLabel.setFont(FontPool.getInstance().getFontForHeight(mCollapsedHeight));


        JPanel mainPanel = new GameUI();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

//        mainPanel.add(mTileHeightDropDown);
        mainPanel.add(mStructureBrushModeDropDown);
        mainPanel.add(mStructureBrushSizeDropDown);
        mainPanel.add(mStructureAssetDropDown);
        mainPanel.add(mListWidthHeaderAndImage);
//        mainPanel.add(terrainConfigsTileImageButton);
        mainPanel.add(SwingUiUtils.verticalSpacePanel(5));
//        mainPanel.add(mLayerTypeDropDown);
//        mainPanel.add(terrainLabel);
//        mainPanel.add(terrainConfigsTileImageButton);
//        mainPanel.add(mAssetNameDropDown);

//        mainPanel.add(mTileInfoPanel);
        mainPanel.add(mLayeringPanel);
        mainPanel.setBackground(mainColor);
        mainPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        setBackground(mainColor);

        add(mainPanel);
    }

    public void onEditorGameControllerMouseClicked(GameController gameController, Tile tile) {
        if (!isShowing()) { return; }
        String mode = mStructureBrushModeDropDown.getSelectedItem();
        String asset = mStructureAssetDropDown.getSelectedItem();
        String team = mStructureAssetDropDown.getSelectedItem();

        JSONObject request = new JSONObject();
        request.put(GameModelAPI.UPDATE_STRUCTURE_MODE, mode);
        request.put(GameModelAPI.UPDATE_STRUCTURE_ASSET, asset);
        request.put(GameModelAPI.UPDATE_STRUCTURE_HEALTH, 3);

        onEditorGameControllerMouseMotion(gameController, tile);

        gameController.updateStructures(request);
    }

    public void onEditorGameControllerMouseMotion(GameController gameController, Tile tile) {
        if (!isShowing()) { return; }


        OutlineListWithHeader list = mListWidthHeaderAndImage.getList();
        list.updateHeader(tile.toString());
        list.updateRowV2("0", "Height:", tile.getHeight() + "");
        list.updateRowV2("1", "Layers:", tile.getLayerCount() + "");


        updateTileStack(tile);

        String value = mStructureBrushSizeDropDown.getSelectedItem();
        int brushSize = Integer.parseInt(getOrDefaultString(value, "0"));

        JSONObject request = new JSONObject();
        request.put(GameModelAPI.GET_TILES_AT_ROW, tile.getRow());
        request.put(GameModelAPI.GET_TILES_AT_COLUMN, tile.getColumn());
        request.put(GameModelAPI.GET_TILES_AT_RADIUS, brushSize);

        JSONArray tiles = gameController.getTilesAtRowColumn(request);
        gameController.updateSelectedTiles(tiles);
    }
}