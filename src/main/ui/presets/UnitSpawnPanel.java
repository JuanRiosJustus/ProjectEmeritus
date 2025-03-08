package main.ui.presets;

import main.game.main.GameAPI;
import main.game.stores.pools.FontPoolV1;
import org.json.JSONArray;
import org.json.JSONObject;
import main.game.components.tile.Tile;
import main.game.main.GameControllerV1;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.custom.StringComboBox;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineDropDownRow;
import main.ui.presets.editor.EditorPanel;

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

public class UnitSpawnPanel extends EditorPanel {
    private final Random mRandom = new Random();
    public final StringComboBox mAssetNameDropDown = new StringComboBox();
    public OutlineDropDownRow mSpawnerBrushSizeDropDown = null;
    public OutlineDropDownRow mSpawnerBrushModeDropDown = null;
    public final OutlineDropDownRow mLayerTypeDropDown = null;
    public OutlineDropDownRow mSpawnerBrushTeamDropDown = null;
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();

    public UnitSpawnPanel() { }

    public UnitSpawnPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        super(mainColor, width, collapsedHeight, expandedHeight);
        initialize(mainColor, width, collapsedHeight, expandedHeight);
    }

    public void initialize(Color mainColor, int width, int collapsedHeight, int expandedHeight) {

        simpleToFullAssetNameMap.putAll(AssetPool.getInstance().getBucketV2("floor_tiles"));

        mLayeringPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        mLayeringPanel.setBackground(mainColor);

        // Setting up the image for terrain
        JButton terrainConfigsTileImageButton = new JButton();
        int imageWidth = (int) (mWidth * .5);
        int imageHeight = (int) (mWidth * .5);
        terrainConfigsTileImageButton.setMinimumSize(new Dimension(imageWidth, imageHeight));
        terrainConfigsTileImageButton.setMaximumSize(new Dimension(imageWidth, imageHeight));
        terrainConfigsTileImageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));

        mSpawnerBrushModeDropDown = new OutlineDropDownRow(mainColor, mWidth, mRowHeight);
        mSpawnerBrushModeDropDown.setBackground(mainColor);
        mSpawnerBrushModeDropDown.setLeftLabel("Spawner Mode:");
        mSpawnerBrushModeDropDown.addItem(GameAPI.UPDATE_SPAWNER_OPERATION_ADD); // Adds a spawns
        mSpawnerBrushModeDropDown.addItem(GameAPI.UPDATE_SPAWNER_OPERATION_DELETE); // removes spawns
        mSpawnerBrushModeDropDown.setSelectedIndex(0);

        mSpawnerBrushSizeDropDown = new OutlineDropDownRow(mainColor, mWidth, mRowHeight);
        mSpawnerBrushSizeDropDown.setLeftLabel("Brush Size:");
        mSpawnerBrushSizeDropDown.setBackground(mainColor);
        IntStream.range(0, 5).forEach(i -> mSpawnerBrushSizeDropDown.addItem(String.valueOf(i)));

        mSpawnerBrushTeamDropDown = new OutlineDropDownRow("Spawner:", mainColor, mWidth, mRowHeight);
        AssetPool.getInstance().getMiscellaneous()
                .entrySet()
                .stream()
                .filter(stringStringEntry -> stringStringEntry.getKey().contains("spawn"))
                .forEach(stringStringEntry -> mSpawnerBrushTeamDropDown.addItem(stringStringEntry.getKey()));
        mSpawnerBrushTeamDropDown.getDropDown()
                .addActionListener(e -> EditorPanel.setupDropDownForImage(mSpawnerBrushTeamDropDown.getDropDown(),
                imageWidth, imageHeight, terrainConfigsTileImageButton));


//        mLayeringPanel.setPreferredSize(new Dimension(mWidth, mExpandedHeight));

        JLabel terrainLabel = new OutlineLabel("Terrain Asset");
        terrainLabel.setPreferredSize(new Dimension(mWidth, mRowHeight));
        terrainLabel.setFont(FontPoolV1.getInstance().getFontForHeight(mRowHeight));
        terrainLabel.setBackground(mainColor);

        // Setup dropdown for terrain
        SwingUiUtils.setupPrettyStringComboBox(mAssetNameDropDown, mainColor, mWidth, mRowHeight);
        mAssetNameDropDown.setFont(FontPoolV1.getInstance().getFontForHeight(mRowHeight));
        simpleToFullAssetNameMap.forEach((key, value) -> mAssetNameDropDown.addItem(key));
        mAssetNameDropDown.addActionListener(e -> EditorPanel.setupDropDownForImage(mAssetNameDropDown, imageWidth,
                imageHeight, terrainConfigsTileImageButton));
        mAssetNameDropDown.setSelectedIndex(mRandom.nextInt(mAssetNameDropDown.getItemCount()));

        JLabel terrainConfigsTileImageFullNameLabel = new OutlineLabel("Full Terrain Name");
        terrainConfigsTileImageFullNameLabel.setPreferredSize(new Dimension(mWidth, mRowHeight));
        terrainConfigsTileImageFullNameLabel.setFont(FontPoolV1.getInstance().getFontForHeight(mRowHeight));


        JPanel mainPanel = new GameUI();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

//        mainPanel.add(mTileHeightDropDown);
        mainPanel.add(mSpawnerBrushModeDropDown);
        mainPanel.add(mSpawnerBrushSizeDropDown);
        mainPanel.add(mSpawnerBrushTeamDropDown);
        mainPanel.add(SwingUiUtils.verticalSpacePanel(5));
//        mainPanel.add(mLayerTypeDropDown);
//        mainPanel.add(terrainLabel);
        mainPanel.add(terrainConfigsTileImageButton);
//        mainPanel.add(mAssetNameDropDown);
        mainPanel.add(mLayeringPanel);
        mainPanel.setBackground(mainColor);
        mainPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        setBackground(mainColor);

        add(mainPanel);
    }

//    private void setupDropDownBasedOnTileLayers(JSONArray layers) {
//        // Set the image to the top layer
//        int indexOf = -1;
//        Object topItem = layers.get(0);
//        JSONObject layer = (JSONObject) topItem;
//        String name = (String) layer.get(Tile.LAYER_ASSET);
//        for (int index = 0; index < mAssetNameDropDown.getItemCount(); index++) {
//            String value = mAssetNameDropDown.getItemAt(index);
//            if (!value.equals(name)) { continue; }
//            indexOf = index;
//            break;
//        }
//        mAssetNameDropDown.setSelectedIndex(indexOf);
//    }

//    private void linkDropDownWithImg(StringComboBox dropdown, int width, int height, JButton img) {
//        setupDropDownForImage(dropdown, width, height, img);
////        String dropdownValue = dropdown.getSelectedItem();
////        result.setText(dropdownValue);
//    }
//    private static void setupDropDownForImage(StringComboBox terrainDropDown, int imageWidth, int imageHeight, JButton imageButton) {
//        String assetName = terrainDropDown.getSelectedItem();
//        if (assetName == null || assetName.isEmpty()) { return; }
//        String id = AssetPool.getInstance().getOrCreateAsset(
//                imageWidth,
//                imageHeight,
//                assetName,
//                AssetPool.STATIC_ANIMATION,
//                0,
//                assetName + "_" + imageWidth + "_" + imageHeight + Objects.hash(terrainDropDown) + Objects.hash(imageButton)
//        );
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        imageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
//    }

    public void onEditorGameControllerMouseClicked(GameControllerV1 gameControllerV1, Tile tile) {
        if (!isShowing()) { return; }
        String mode = mSpawnerBrushModeDropDown.getSelectedItem();
        String team = mSpawnerBrushTeamDropDown.getSelectedItem();

        JSONObject request = new JSONObject();
        request.put(GameAPI.UPDATE_SPAWN_MODE, mode);
        request.put(GameAPI.UPDATE_SPAWN_OPERATION_ON_TEAM, team);

        onEditorGameControllerMouseMotion(gameControllerV1, tile);

        gameControllerV1.updateSpawners(request);
    }

    public void onEditorGameControllerMouseMotion(GameControllerV1 gameControllerV1, Tile tile) {
        if (!isShowing()) { return; }

        String value = mSpawnerBrushSizeDropDown.getSelectedItem();
        int brushSize = Integer.parseInt(getOrDefaultString(value, "0"));

        JSONObject request = new JSONObject();
        request.put(GameAPI.GET_TILES_AT_ROW, tile.getRow());
        request.put(GameAPI.GET_TILES_AT_COLUMN, tile.getColumn());
        request.put(GameAPI.GET_TILES_AT_RADIUS, brushSize);

        JSONArray tiles = gameControllerV1.getTilesAtRowColumn(request);
        gameControllerV1.setSelectedTilesV1(tiles);
    }
}
