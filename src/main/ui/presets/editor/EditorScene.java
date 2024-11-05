package main.ui.presets.editor;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.StateLock;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameSettings;
import main.game.stores.pools.ColorPalette;
import main.engine.EngineScene;
import main.game.components.tile.Tile;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.components.OutlineLabel;
import main.ui.custom.*;
import main.ui.huds.controls.JGamePanel;

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

    // UI components for map generation
    private final StringComboBox liquidConfigsBrushSizeDropDown = new StringComboBox();
    private final StringComboBox liquidConfigsSelectionDropDown = new StringComboBox();
    private final JTextField terrainConfigsTileTerrain = new JTextField();

    // Maps to handle asset name translations
    private final Map<String, String> simpleToFullTerrainAssetNameMap;
    private final Map<String, String> simpleToFullLiquidAssetNameMap;

    private final JsonArray mSelectedTiles = new JsonArray(); // Stores selected tiles for editing


    private MapGenerationPanel mMapGenerationPanel = new MapGenerationPanel();
    private TerrainBrushPanel mTerrainBrushPanel = new TerrainBrushPanel();
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

        // Loading the assets
        simpleToFullTerrainAssetNameMap = AssetPool.getInstance().getBucketV2("floor_tiles");
        simpleToFullLiquidAssetNameMap = AssetPool.getInstance().getBucketV2("liquids");


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

        JPanel baseMapConfigsAccordion = createMapGenerationPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(baseMapConfigsAccordion);

        JPanel terrainBrushConfigsAccordion = createTerrainBrushConfigsPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(terrainBrushConfigsAccordion);

        JPanel liquidConfigsAccordion = createLiquidConfigsPanel(color, panelWidth, collapsedHeight, expandedHeight);
        sideBarPanel.add(liquidConfigsAccordion);

//        AccordionPanel terrainConfigsAccordion = createTerrainConfigsPanel();
//        sideBarPanel.add(terrainConfigsAccordion);

        mGamePanelContainer.add(generateNewGameController());

        add(mGamePanelContainer);
        add(sideBarPanel);
    }

    private VerticalAccordionPanel createMapGenerationPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mMapGenerationPanel = new MapGenerationPanel(color, width, collapsedHeight, expandedHeight);

        // Generate map without noise
        mMapGenerationPanel.mGenerateWithoutNoiseButton.addActionListener(e -> {
            mMapGenerationPanel.setMapGenerationRandomDefaultsIfEmpty();
            generateNewGameController();
        });
        // Generate map with noise
        mMapGenerationPanel.mGenerateWithNoiseButton.addActionListener(e -> {
            mMapGenerationPanel.setMapGenerationRandomDefaultsIfEmpty();
            generateNewGameController();
        });
        // Generate with complete randomness
        mMapGenerationPanel.mGenerateWithCompleteRandomness.addActionListener(e -> {
            mMapGenerationPanel.setMapGenerationRandomDefaultsIfEmpty(true);
            generateNewGameController();
        });
        // Generate map with noise
        mMapGenerationPanel.mMapSizeDropDown.addActionListener(e -> {
            generateNewGameController();
        });

        return mMapGenerationPanel;
    }

    private JPanel generateNewGameController() {
        String mapSize = String.valueOf(mMapGenerationPanel.mMapSizeDropDown.getSelectedItem());
        final int newTileMapColumns = Integer.parseInt(mapSize.split("x")[0]);
        final int newTileMapRows = Integer.parseInt(mapSize.split("x")[1]);
        final int newSpriteWidth = mGamePanelWidth / newTileMapColumns;
        final int newSpriteHeight = mGamePanelHeight / newTileMapRows;
        boolean useNoiseGeneration = mMapGenerationPanel.mUseNoiseGenerationCheckBox.isSelected();
        int minNoiseHeight = Integer.parseInt(getOrDefault(mMapGenerationPanel.mNoiseMinHeightField.getText(), "-10"));
        int maxNoiseHeight =  Integer.parseInt(getOrDefault(mMapGenerationPanel.mNoiseMaxHeightField.getText(), "10"));
        float noiseZoom = (float) Double.parseDouble(getOrDefault(mMapGenerationPanel.mNoiseZoomField.getText(), ".5f"));
        int baseHeight = Integer.parseInt(getOrDefault(mMapGenerationPanel.mBaseHeightField.getText(), "0"));
        String terrainAsset = mMapGenerationPanel.mBaseTerrain.getText();

        GameSettings settings = GameSettings.getDefaults()
                .setViewportWidth(mGamePanelWidth)
                .setViewportHeight(mGamePanelHeight)
                .setTileMapRows(newTileMapRows)
                .setTileMapColumns(newTileMapColumns)
                .setSpriteWidth(newSpriteWidth)
                .setSpriteHeight(newSpriteHeight)
                .setMapGenerationTerrainAsset(terrainAsset)
                // Below are unnecessary
                .setShowGameplayUI(false)
                .setMapGenerationTileHeight(baseHeight)
                .setUseNoiseGeneration(useNoiseGeneration);
        if (useNoiseGeneration) {
            settings.setMinNoiseGenerationHeight(minNoiseHeight)
                    .setMaxNoiseGenerationHeight(maxNoiseHeight)
                    .setNoiseGenerationZoom(noiseZoom);
        }

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
//        tileDetailsPanel.setMinimumSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));
//        tileDetailsPanel.setMaximumSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));
        tileDetailsPanel.setPreferredSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));



        VerticalAccordionPanel tileDetailsAccordion = new VerticalAccordionPanel("Tile Details",
                tileDetailsPanel,
                ColorPalette.getRandomColor(),
                mSideBarPanelWidth,
                mSideBarPanelHeightSize1,
                mAccordionContentHeight
        );
        return tileDetailsPanel;
    }

    private VerticalAccordionPanel createLiquidConfigsPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mLiquidBrushPanel = new LiquidBrushPanel(color, width, collapsedHeight, expandedHeight);
        return mLiquidBrushPanel;
    }

    private VerticalAccordionPanel createTerrainBrushConfigsPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mTerrainBrushPanel = new TerrainBrushPanel(color, width, collapsedHeight, expandedHeight);
        return mTerrainBrushPanel;
    }


    private void addGamePanelListeners(GameController gameController, JPanel jp) {
        jp.addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent e) {}
            @Override public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Entity entity = gameController.getModel().tryFetchingTileWithXY(x, y);
                if (entity == null) { return; }
                Tile tile = entity.get(Tile.class);
                String tileHeight = tile.getHeight() + "";
                String tileTerrainOrLiquid = tile.getTerrain();
                mTerrainBrushPanel.mCurrentHeightLabel.setRightLabel(tileHeight);
                mTerrainBrushPanel.mCurrentTerrainLabel.setRightLabel(tileTerrainOrLiquid);
                mLiquidBrushPanel.mCurrentHeightLabel.setRightLabel(tileHeight);
                mLiquidBrushPanel.mCurrentLiquidLabel.setRightLabel(tileTerrainOrLiquid);


                String dropdownContext = mTerrainBrushPanel.mBrushSizeDropDown.getSelectedItem();
                if (mLiquidBrushPanel.mBrushSizeDropDown.isShowing()) {
                    dropdownContext = mLiquidBrushPanel.mBrushSizeDropDown.getSelectedItem();
                }

                int brushSize = Integer.parseInt(getOrDefault(dropdownContext, "1")) - 1;


                JsonArray newSelectedTiles = getTilesConsideringBrushSize(gameController, tile, brushSize);
                gameController.setSelectedTiles(newSelectedTiles);

//                List<Entity> selectedTiles = gameController.getSelectedTiles();

//                System.out.println(newSelectedTiles);
//                extraMapInteractionData(selectedTiles);
            }
        });
        jp.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Entity entity = gameController.getModel().tryFetchingTileWithXY(x, y);
                if (entity == null) { return; }

                String value = getOrDefault(mTerrainBrushPanel.mTileHeightDropDown.getSelectedItem(), "0");
                if (mLiquidBrushPanel.mLiquidHeightDropDown.isShowing()) {
                    value = getOrDefault(mLiquidBrushPanel.mLiquidHeightDropDown.getSelectedItem(), "0");
                }
                int tileHeight = Integer.parseInt(value);



                String terrainOrLiquid = getOrDefault(mTerrainBrushPanel.mTerrainNameDropDown.getSelectedItem(), "");
                if (mLiquidBrushPanel.mLiquidNameDropDown.isShowing()) {
                    terrainOrLiquid = getOrDefault(mLiquidBrushPanel.mLiquidNameDropDown.getSelectedItem(), "");
                }
                if (terrainOrLiquid.isEmpty()) { return; }




//
//
//                // Make sure the brush configs panel is being used before editing map
//                if (!mTerrainBrushPanel.mTerrainNameDropDown.isShowing()) { return; }

                JsonObject attributeToUpdate = new JsonObject();
                attributeToUpdate.put(Tile.HEIGHT, tileHeight);


                if (mTerrainBrushPanel.mTerrainNameDropDown.isShowing()) {
                    attributeToUpdate.put(Tile.TERRAIN, terrainOrLiquid);
                } else if (mLiquidBrushPanel.mLiquidNameDropDown.isShowing()) {
                    attributeToUpdate.put(Tile.LIQUID, terrainOrLiquid);
                }

//                attributeToUpdate.put(Tile.TERRAIN, terrainOrLiquid);
//                if (mLiquidBrushPanel.mLiquidNameDropDown.isShowing()) {
//                    attributeToUpdate.put(Tile.LIQUID, mLiquidBrushPanel.mLiquidNameDropDown.getSelectedItem());
//                }

                gameController.updateSelectedTiles(attributeToUpdate);

                List<Entity> selectedTiles = gameController.getSelectedTiles();
                extraMapInteractionData(selectedTiles);
            }
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
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
