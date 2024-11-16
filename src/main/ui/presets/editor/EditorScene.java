package main.ui.presets.editor;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.StateLock;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModelAPI;
import main.game.main.GameSettings;
import main.game.stores.pools.ColorPalette;
import main.engine.EngineScene;
import main.game.components.tile.Tile;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.*;
import main.ui.huds.controls.JGamePanel;
import main.ui.outline.OutlineLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EditorScene extends EngineScene {

    private GameController mGameController = null; // Game controller to manage game logic
    private final SplittableRandom random = new SplittableRandom(); // Random number generator for map generation
    private final JPanel mGamePanelContainer = new JGamePanel(); // Panel to hold the game rendering
    private final StateLock mStateLock = new StateLock(); // Lock to prevent UI updates during state changes
    private final JTextField mMapName = new JTextField(); // Input field for map name

    // Dimensions for various UI components
    private final int mGamePanelWidth;
    private final int mGamePanelHeight;
    private final int mSideBarPanelWidth;
    private final int mSideBarPanelHeight;
    private final int mSideBarPanelHeightSize0;
    private final int mSideBarPanelHeightSize1;
    private final int mSideBarPanelHeightSize2;
    private final int mSideBarPanelHeightSize3;
    private final int mAccordionContentWidth;
    private final int mAccordionContentHeight;
    private final int mAccordionContentHeight2;

    private final JsonArray mSelectedTiles = new JsonArray(); // Stores selected tiles for editing


    private MapGenerationPanel mMapGenerationPanel = new MapGenerationPanel();
    private UpdateTileLayerPanel mUpdateTileLayerPanel = new UpdateTileLayerPanel();
    private UpdateUnitSpawnPanel mUpdateUnitSpawnPanel = new UpdateUnitSpawnPanel();
    private UpdateStructurePanel mUpdateStructurePanel = new UpdateStructurePanel();
    private LiquidBrushPanel mLiquidBrushPanel = new LiquidBrushPanel();

    public EditorScene(int width, int height) {
        super(width, height, "Editor");

//        height = height - Engine.getInstance().getHeaderSize();

        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        mGamePanelWidth = (int) (width * .75);
        mGamePanelHeight = (int) (height);
        mSideBarPanelWidth = width - mGamePanelWidth;
        mSideBarPanelHeight = height;
        mSideBarPanelHeightSize0 = (int) (mSideBarPanelHeight * .025);
        mSideBarPanelHeightSize1 = (int) (mSideBarPanelHeight * .033);
        mSideBarPanelHeightSize2 = (int) (mSideBarPanelHeight * .1);
        mSideBarPanelHeightSize3 = (int) (mSideBarPanelHeight * .2);
        mAccordionContentWidth = (int) mSideBarPanelWidth;
        mAccordionContentHeight = (int) (mSideBarPanelHeight * .5);
        mAccordionContentHeight2 = (int) (mSideBarPanelHeight);

        JPanel sideBarPanel = new GameUI(mSideBarPanelWidth, mSideBarPanelHeight);
        sideBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
        sideBarPanel.setBackground(Color.BLUE);
        sideBarPanel.setOpaque(true);

        mGamePanelContainer.setPreferredSize(new Dimension(mGamePanelWidth, mGamePanelHeight));
        mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mGamePanelContainer.removeAll();
        add(mGamePanelContainer);

        int panelWidth = mSideBarPanelWidth;
        int collapsedHeight = mSideBarPanelHeightSize1;
        int expandedHeight = mAccordionContentHeight2;
        Color color = ColorPalette.getRandomColor();

        JPanel tileDetailsAccordion = createTileDetailPanel();
        sideBarPanel.add(tileDetailsAccordion);

        JPanel panel = createMapGenerationPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(panel);

        panel = createTileBrushPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(panel);

        panel = createSpawnPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(panel);

        panel = createObstructionPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(panel);

//        createSpawnPanel
//        JPanel liquidConfigsAccordion = createLiquidConfigsPanel(color, panelWidth, collapsedHeight, expandedHeight);
//        sideBarPanel.add(liquidConfigsAccordion);

//        AccordionPanel terrainConfigsAccordion = createTerrainConfigsPanel();
//        sideBarPanel.add(terrainConfigsAccordion);

        mGamePanelContainer.add(generateNewGameController());

        add(mGamePanelContainer);
        add(sideBarPanel);
    }

    private VerticalAccordionPanel createMapGenerationPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mMapGenerationPanel = new MapGenerationPanel(color, width, collapsedHeight, expandedHeight);


        mMapGenerationPanel.mGenerateMapButton.addActionListener(e -> {
            mMapGenerationPanel.setMapGenerationRandomDefaultsIfEmpty(false);
            generateNewGameController();
        });

        mMapGenerationPanel.mRandomizeMapButton.addActionListener(e -> {
            mMapGenerationPanel.setMapGenerationRandomDefaultsIfEmpty(true);
            generateNewGameController();
        });


        // Generate map with noise
        mMapGenerationPanel.mMapSizeDropDown.addActionListener(e -> {
            generateNewGameController();
        });

        mMapGenerationPanel.getToggleButton().setText("Map Generation Configs");

        return mMapGenerationPanel;
    }

    private JPanel generateNewGameController() {
        String mapSize = String.valueOf(mMapGenerationPanel.mMapSizeDropDown.getSelectedItem());
        final int newTileMapColumns = Integer.parseInt(mapSize.split("x")[0]);
        final int newTileMapRows = Integer.parseInt(mapSize.split("x")[1]);
        final int newSpriteWidth = mGamePanelWidth / newTileMapColumns;
        final int newSpriteHeight = mGamePanelHeight / newTileMapRows;

        String terrainAsset = mMapGenerationPanel.mBaseTerrain.getText();
        int minHeight = Integer.parseInt(getOrDefault(mMapGenerationPanel.mMinHeightField.getRightText(), "1"));
        int maxHeight =  Integer.parseInt(getOrDefault(mMapGenerationPanel.mMaxHeightField.getRightText(), "10"));
        float noiseZoom = (float) Double.parseDouble(getOrDefault(mMapGenerationPanel.mNoiseZoomField.getRightText(), ".5f"));

        String waterAsset = mMapGenerationPanel.mWaterLevelAssetDropDown.getSelectedItem();
        int waterLevel = Integer.parseInt(getOrDefault(mMapGenerationPanel.mWaterLevelField.getRightText(), "0"));

        String baseAsset = mMapGenerationPanel.mBaseLevelAsset.getSelectedItem();
        int baseLevel = Integer.parseInt(getOrDefault(mMapGenerationPanel.mBaseLevelField.getRightText(), "1"));

        GameSettings settings = GameSettings.getDefaults()
                // Required args
                .setViewportWidth(mGamePanelWidth)
                .setViewportHeight(mGamePanelHeight)
                .setTileMapRows(newTileMapRows)
                .setTileMapColumns(newTileMapColumns)
                .setSpriteWidth(newSpriteWidth)
                .setSpriteHeight(newSpriteHeight)
                .setMapGenerationBaseAsset(baseAsset)
                .setMapGenerationBaseLevel(baseLevel)
                .setMapGenerationWaterAsset(waterAsset)
                .setMapGenerationWaterLevel(waterLevel)
                .setMapGenerationTerrainAsset(terrainAsset)
                // Setup randomization
                .setShowGameplayUI(false)
                .setUseNoiseGeneration(true)
                .setMinNoiseGenerationHeight(minHeight)
                .setMaxNoiseGenerationHeight(maxHeight)
                .setNoiseGenerationZoom(noiseZoom)

                .setUseNoiseGeneration(true);
//        if (true) {
//            settings.setMinNoiseGenerationHeight(minHeight)
//                    .setMaxNoiseGenerationHeight(maxHeight)
//                    .setNoiseGenerationZoom(noiseZoom);
//        }

        mGameController = GameController.create(settings);
        mGameController.run();

        JPanel newGamePanel = mGameController.getGamePanel(mGamePanelWidth, mGamePanelHeight);
        mGameController.setupInput(newGamePanel);

        mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mGamePanelContainer.removeAll();
        mGamePanelContainer.add(newGamePanel);

        addGamePanelListeners(mGameController, newGamePanel);

        return newGamePanel;
    }

    private final JLabel mTileDetailsAverageHeightLabel = new OutlineLabel("");
    private final JLabel mTileDetailsSelectedTilesCountLabel = new OutlineLabel("");
    private JPanel createTileDetailPanel() {
        JTextField tileMapName = mMapName;
        tileMapName.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileMapName.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileMapName.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileMapName.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JLabel tileDetailsLabel = new OutlineLabel("Tile Details");
        tileDetailsLabel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileDetailsLabel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileDetailsLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileDetailsLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        mTileDetailsAverageHeightLabel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mTileDetailsAverageHeightLabel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mTileDetailsAverageHeightLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mTileDetailsAverageHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        mTileDetailsSelectedTilesCountLabel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mTileDetailsSelectedTilesCountLabel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mTileDetailsSelectedTilesCountLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mTileDetailsSelectedTilesCountLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JButton saveTileMap = new JButton("Save Tile Map");
        saveTileMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveTileMap.setMaximumSize(new Dimension(mSideBarPanelWidth * 2, mSideBarPanelHeightSize1));
//        saveTileMap.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        saveTileMap.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        saveTileMap.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            int userOption = jFileChooser.showDialog(null, "Save Map");
            if(userOption == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to save this file: ");
            } else {

            }
        });

        JButton loadTileMap = new JButton("Load Tile Map");
        loadTileMap.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadTileMap.setMaximumSize(new Dimension(mSideBarPanelWidth * 2, mSideBarPanelHeightSize1));
//        loadTileMap.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        loadTileMap.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));


        JPanel tileDetailsPanel = new JGamePanel(false);
        tileDetailsPanel.setBackground(Color.RED);
        int mapMetadataPanelWidth = mSideBarPanelWidth;
        int mapMetadataPanelHeight = (int) (mSideBarPanelHeightSize1 * 3.25);
//        tileDetailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tileDetailsPanel.setLayout(new BoxLayout(tileDetailsPanel, BoxLayout.Y_AXIS));
//        tileDetailsPanel.add(tileDetailsLabel);
        tileDetailsPanel.add(tileMapName);
        tileDetailsPanel.add(saveTileMap);
        tileDetailsPanel.add(loadTileMap);
        tileDetailsPanel.setPreferredSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));



        VerticalAccordionPanel tileDetailsAccordion = new VerticalAccordionPanel();
        tileDetailsAccordion.initialize(tileDetailsPanel,
                ColorPalette.getRandomColor(),
                mSideBarPanelWidth,
                mSideBarPanelHeightSize1,
                mAccordionContentHeight
        );
        tileDetailsAccordion.getToggleButton().setText("Tile Details");
        return tileDetailsPanel;
    }

    private VerticalAccordionPanel createLiquidConfigsPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mLiquidBrushPanel = new LiquidBrushPanel(color, width, collapsedHeight, expandedHeight);
        return mLiquidBrushPanel;
    }

    private VerticalAccordionPanel createTileBrushPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mUpdateTileLayerPanel = new UpdateTileLayerPanel(color, width, collapsedHeight, expandedHeight);
        mUpdateTileLayerPanel.getToggleButton().setText("Tile Brush Panel");
        return mUpdateTileLayerPanel;
    }

    private VerticalAccordionPanel createSpawnPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mUpdateUnitSpawnPanel = new UpdateUnitSpawnPanel(color, width, collapsedHeight, expandedHeight);
        mUpdateUnitSpawnPanel.getToggleButton().setText("Unit Spawn Panel");
        return mUpdateUnitSpawnPanel;
    }

    private VerticalAccordionPanel createObstructionPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mUpdateStructurePanel = new UpdateStructurePanel(color, width, collapsedHeight, expandedHeight);
        mUpdateStructurePanel.getToggleButton().setText("Obstruction Panel");
        return mUpdateStructurePanel;
    }

    private void addGamePanelListeners(GameController gameController, JPanel jp) {
        JsonObject temp = new JsonObject();
        temp.put(GameModelAPI.GET_TILE_OPERATION, GameModelAPI.GET_TILE_OPERATION_X_AND_Y);
        jp.addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent e) {}
            @Override public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                temp.put(GameModelAPI.GET_TILE_OPERATION_ROW_OR_Y, y);
                temp.put(GameModelAPI.GET_TILE_OPERATION_COLUMN_OR_X, x);
                temp.put(GameModelAPI.GET_TILE_OPERATION_RADIUS, 0);
                JsonArray tiles = gameController.getTilesAt(temp);
                if (tiles == null) { return; }
                Tile tile = (Tile) tiles.get(0);


                EditorPanel panel = null;
                if (mMapGenerationPanel.isOpen()) {
                    panel = mMapGenerationPanel;
                } else if (mUpdateTileLayerPanel.isOpen()) {
                    panel = mUpdateTileLayerPanel;
                } else if (mUpdateUnitSpawnPanel.isOpen()) {
                    panel = mUpdateUnitSpawnPanel;
                } else if (mUpdateStructurePanel.isOpen()) {
                    panel = mUpdateStructurePanel;
                }

                if (panel != null) {
                    panel.onEditorGameControllerMouseMotion(gameController, tile);
                }
            }
        });
        jp.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                temp.put(GameModelAPI.GET_TILE_OPERATION_ROW_OR_Y, y);
                temp.put(GameModelAPI.GET_TILE_OPERATION_COLUMN_OR_X, x);
                temp.put(GameModelAPI.GET_TILE_OPERATION_RADIUS, 0);
                JsonArray tiles = gameController.getTilesAt(temp);
                if (tiles == null) { return; }
                Tile tile = (Tile) tiles.get(0);

                if (mMapGenerationPanel.isOpen()) {
                    mMapGenerationPanel.onEditorGameControllerMouseClicked(gameController, tile);
                } else if (mUpdateTileLayerPanel.isOpen()) {
                    mUpdateTileLayerPanel.onEditorGameControllerMouseClicked(gameController, tile);
                } else if (mUpdateUnitSpawnPanel.isOpen()) {
                    mUpdateUnitSpawnPanel.onEditorGameControllerMouseClicked(gameController, tile);
                }
            }
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
    }

    private int getBrushSize() {
        String dropdownContext = mUpdateTileLayerPanel.mUpdateTileLayersBrushSizeDropDown.getSelectedItem();
        if (mLiquidBrushPanel.mFillMode.isShowing()) {
//            dropdownContext = mLiquidBrushPanel.mFillMode.getSelectedItem();
        }
        int brushSize = Integer.parseInt(getOrDefault(dropdownContext, "1")) - 1;
        return brushSize;
    }

    AtomicReference<Float> mTileAverageHeights = new AtomicReference<>(4f);
    private void extraMapInteractionData(List<Entity> selectedTiles) {
        mTileAverageHeights.set(0f);
        selectedTiles.forEach(entity1 -> {
            Tile selectedTile = entity1.get(Tile.class);
            mTileAverageHeights.set(mTileAverageHeights.get() + selectedTile.getHeight());
        });
        mTileAverageHeights.set(mTileAverageHeights.get() / selectedTiles.size());


        // tile data
        if (mStateLock.isUpdated("TileDetailsHeights", mTileAverageHeights.get())) {
            mTileDetailsAverageHeightLabel.setText("AVG Heights: " + mTileAverageHeights.get());
        }
        if (mStateLock.isUpdated("TileDetailsCounts", selectedTiles.size())) {
            mTileDetailsSelectedTilesCountLabel.setText("Tile Counts: " + selectedTiles.size());
        }
    }


    private JsonArray getTilesConsideringBrushSize(GameController gameController, Tile tile, int brushSize) {
        Entity entity;
        mSelectedTiles.clear();

        for (int row = tile.getRow() - brushSize; row <= tile.getRow() + brushSize; row++) {
            for (int column = tile.getColumn() - brushSize; column <= tile.getColumn() + brushSize; column++) {
                entity = gameController.getModel().tryFetchingTileAt(row, column);
                if (entity == null) { continue; }
                Tile selectedTile = entity.get(Tile.class);
                mSelectedTiles.add(selectedTile);
            }
        }

        return mSelectedTiles;
    }

    private static String getOrDefault(String str, String defaultToStr) {
        if (str == null || str.trim().isEmpty()) {
            return defaultToStr;
        } else {
            return str;
        }
    }

    @Override
    public void update() {
        mGameController.update();
    }

    @Override
    public void input() {
        mGameController.input();
    }

    @Override
    public JPanel render() {
        return this;
    }
}
