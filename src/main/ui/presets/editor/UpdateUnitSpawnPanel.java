package main.ui.presets.editor;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
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

public class UpdateUnitSpawnPanel extends EditorPanel {
    private final Random mRandom = new Random();
    public final StringComboBox mAssetNameDropDown = new StringComboBox();
    public OutlineLabelToDropDown mSpawnerBrushSizeDropDown = null;
    public OutlineLabelToDropDown mSpawnerBrushModeDropDown = null;
    public OutlineLabelToDropDown mSpawnerBrushTeamDropDown = null;
    public final Map<String, String> simpleToFullAssetNameMap = new HashMap<>();

    public UpdateUnitSpawnPanel() { }

    public UpdateUnitSpawnPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
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

        mSpawnerBrushModeDropDown = new OutlineLabelToDropDown(mainColor, mWidth, mCollapsedHeight);
        mSpawnerBrushModeDropDown.setBackground(mainColor);
        mSpawnerBrushModeDropDown.setLeftLabel("Spawner Mode:");
        mSpawnerBrushModeDropDown.addItem(GameModelAPI.UPDATE_SPAWNER_OPERATION_ADD); // Adds a spawns
        mSpawnerBrushModeDropDown.addItem(GameModelAPI.UPDATE_SPAWNER_OPERATION_DELETE); // removes spawns
        mSpawnerBrushModeDropDown.setSelectedIndex(0);

        mSpawnerBrushSizeDropDown = new OutlineLabelToDropDown(mainColor, mWidth, mCollapsedHeight);
        mSpawnerBrushSizeDropDown.setLeftLabel("Brush Size:");
        mSpawnerBrushSizeDropDown.setBackground(mainColor);
        IntStream.range(0, 5).forEach(i -> mSpawnerBrushSizeDropDown.addItem(String.valueOf(i)));

        mSpawnerBrushTeamDropDown = new OutlineLabelToDropDown("Spawner:", mainColor, mWidth, mCollapsedHeight);
        AssetPool.getInstance().getMiscellaneous()
                .entrySet()
                .stream()
                .filter(stringStringEntry -> stringStringEntry.getKey().contains("spawn"))
                .forEach(stringStringEntry -> mSpawnerBrushTeamDropDown.addItem(stringStringEntry.getKey()));
        mSpawnerBrushTeamDropDown.getDropDown()
                .addActionListener(e -> EditorPanel.setupDropDownForImage(mSpawnerBrushTeamDropDown.getDropDown(),
                imageWidth, imageHeight, terrainConfigsTileImageButton));


        mLayeringPanel.setPreferredSize(new Dimension(mWidth, mExpandedHeight));

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
        mainPanel.add(mSpawnerBrushModeDropDown);
        mainPanel.add(mSpawnerBrushSizeDropDown);
        mainPanel.add(mSpawnerBrushTeamDropDown);
        mainPanel.add(SwingUiUtils.verticalSpacePanel(5));
//        mainPanel.add(mLayerTypeDropDown);
//        mainPanel.add(terrainLabel);
//        mainPanel.add(terrainConfigsTileImageButton);
//        mainPanel.add(mAssetNameDropDown);
        mainPanel.add(mTileInfoPanel);
        mainPanel.add(mLayeringPanel);
        mainPanel.setBackground(mainColor);
        mainPanel.setPreferredSize(new Dimension(mWidth, expandedHeight));
        setBackground(mainColor);

        getContentPanel().add(mainPanel);
    }

    public void onEditorGameControllerMouseClicked(GameController gameController, Tile tile) {
        if (!isOpen()) { return; }
        String mode = mSpawnerBrushModeDropDown.getSelectedItem();
        String team = mSpawnerBrushTeamDropDown.getSelectedItem();

        JsonObject request = new JsonObject();
        request.put(GameModelAPI.UPDATE_SPAWN_OPERATION, mode);
        request.put(GameModelAPI.UPDATE_SPAWN_OPERATION_ON_TEAM, team);

        onEditorGameControllerMouseMotion(gameController, tile);

        gameController.updateSpawners(request);
    }

    public void onEditorGameControllerMouseMotion(GameController gameController, Tile tile) {
        if (!isOpen()) { return; }

        updateTileStack(tile);

        String value = mSpawnerBrushSizeDropDown.getSelectedItem();
        int brushSize = Integer.parseInt(getOrDefaultString(value, "0"));

        JsonObject request = new JsonObject();
        request.put(GameModelAPI.GET_TILE_OPERATION, GameModelAPI.GET_TILE_OPERATION_ROW_AND_COLUMN);
        request.put(GameModelAPI.GET_TILE_OPERATION_ROW_OR_Y, tile.getRow());
        request.put(GameModelAPI.GET_TILE_OPERATION_COLUMN_OR_X, tile.getColumn());
        request.put(GameModelAPI.GET_TILE_OPERATION_RADIUS, brushSize);

        JsonArray tiles = gameController.getTilesAt(request);
        gameController.setSelectedTiles(tiles);
    }
}
