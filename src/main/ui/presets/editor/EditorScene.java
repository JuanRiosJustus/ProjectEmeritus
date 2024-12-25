package main.ui.presets.editor;

import main.game.main.*;
import main.ui.outline.OutlineButtonRow;
import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.StateLock;
import main.engine.EngineScene;
import main.game.components.tile.Tile;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;

import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.SplittableRandom;

public class EditorScene extends EngineScene {

    private GameController mGameController = null; // Game controller to manage game logic
    private final SplittableRandom random = new SplittableRandom(); // Random number generator for map generation
    private final JPanel mGamePanelContainer = new GameUI(); // Panel to hold the game rendering
    private final StateLock mStateLock = new StateLock(); // Lock to prevent UI updates during state changes

    // Dimensions for various UI components
    private final int mGamePanelWidth;
    private final int mGamePanelHeight;
    private final int mSideBarPanelWidth;
    private final int mSideBarPanelHeight;
    private final int mSideBarPanelHeightSize0;
    private final int mSideBarPanelHeightSize1;
    private final int mSideBarPanelHeightSize2;
    private final int mSideBarPanelHeightSize3;
    private final int mSideBarPanelHeightSize4;
    private final int mAccordionContentWidth;
    private final int mAccordionContentHeight;
    private final int mAccordionContentHeight2;
    private boolean mInitializeSideBar = false;

    private final JSONArray mSelectedTiles = new JSONArray(); // Stores selected tiles for editing
    private MapGenerationPanel mMapGenerationPanel = null;
    private UpdateTileLayerPanel mUpdateTileLayerPanel = new UpdateTileLayerPanel();
    private UpdateUnitSpawnPanel mUpdateUnitSpawnPanel = new UpdateUnitSpawnPanel();
    private UpdateStructurePanel mUpdateStructurePanel = new UpdateStructurePanel();

    public OutlineButtonRow mLoadMapButton = null;
    public OutlineButtonRow mSaveMapButton = null;

    private int mSideBarRowHeights = 0;
    private int mInnerSideBarPanelWidth = 0;
    private int mInnerSideBarContentPanelHeight = 0;
    private JPanel mSideBarPanel;
    private JPanel mSideBarTabPanel;
    private JPanel mSideBarContentPanel;

    public EditorScene(int width, int height) {
        super(width, height, "Editor");

        // Set up layout and dimensions for the editor scene
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Calculate dimensions for various panels based on the input width and height
        mGamePanelWidth = (int) (width * .75);
        mGamePanelHeight = (int) (height);
        mSideBarPanelWidth = width - mGamePanelWidth;
        mSideBarPanelHeight = height;
        mSideBarPanelHeightSize0 = (int) (mSideBarPanelHeight * .025);
        mSideBarPanelHeightSize1 = (int) (mSideBarPanelHeight * .033);
        mSideBarPanelHeightSize2 = (int) (mSideBarPanelHeight * .1);
        mSideBarPanelHeightSize3 = (int) (mSideBarPanelHeight * .2);
        mSideBarPanelHeightSize4 = (int) (mSideBarPanelHeight * .01);
        mAccordionContentWidth = (int) mSideBarPanelWidth;
        mAccordionContentHeight = (int) (mSideBarPanelHeight * .5);
        mAccordionContentHeight2 = (int) (mSideBarPanelHeight);


        Color color = ColorPalette.getRandomColor();

        // Initialize the sidebar panel
        mSideBarPanel = new GameUI(mSideBarPanelWidth, mSideBarPanelHeight);
        mSideBarPanel.setBackground(color);
        mSideBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
        mSideBarPanel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeight));
        mSideBarPanel.removeAll();

        // Calculate dimensions for inner panels and other UI components
        mSideBarRowHeights = mSideBarPanelHeightSize1;
        mInnerSideBarPanelWidth = mSideBarPanelWidth;
        mInnerSideBarContentPanelHeight = mSideBarPanelHeight - mSideBarRowHeights;

        mSaveMapButton = new OutlineButtonRow("Load", color, width, mSideBarRowHeights);
        mSaveMapButton.getButton().setHorizontalAlignment(SwingConstants.CENTER);
        mSaveMapButton.getButton().setText("Save");
        mSaveMapButton.getButton().setFont(FontPool.getInstance().getFontForHeight(mSideBarRowHeights));
        mSaveMapButton.getButton().setBackground(color);
//        SwingUiUtils.setBoxLayoutSize(mSaveMapButton, width, rowHeight);
        SwingUiUtils.setHoverEffect(mSaveMapButton.getButton());

        mLoadMapButton = new OutlineButtonRow("Save ", color, width, mSideBarRowHeights);
        mLoadMapButton.getButton().setHorizontalAlignment(SwingConstants.CENTER);
        mLoadMapButton.getButton().setText("Load Map");
        mLoadMapButton.getButton().setFont(FontPool.getInstance().getFontForHeight(mSideBarRowHeights));
        mLoadMapButton.getButton().setBackground(color);
//        SwingUiUtils.setBoxLayoutSize(mLoadMapButton, width, rowHeight);
        SwingUiUtils.setHoverEffect(mLoadMapButton.getButton());

        // Create the tab panel and content panel for the sidebar
        mSideBarTabPanel = new GameUI();
        mSideBarTabPanel.setBackground(color);
        mSideBarTabPanel.setLayout(new BoxLayout(mSideBarTabPanel, BoxLayout.X_AXIS));

        // Create the panel to host all the input
        mSideBarContentPanel = new GameUI();
        mSideBarContentPanel.setLayout(new CardLayout());
        mSideBarContentPanel.setBackground(color);
        mSideBarContentPanel.setPreferredSize(new Dimension(mInnerSideBarPanelWidth, mInnerSideBarContentPanelHeight));

        // Configure the main game panel container
        mGamePanelContainer.setPreferredSize(new Dimension(mGamePanelWidth, mGamePanelHeight));
        mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mGamePanelContainer.removeAll();
        add(mGamePanelContainer);

        // Add "Map Generation" tab and panel
        mMapGenerationPanel = createMapGenerationPanel(color, mInnerSideBarPanelWidth, mSideBarRowHeights, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mMapGenerationPanel, "Map Generation");

        // Add "Tile Layering" tab and panel
        mUpdateTileLayerPanel = new UpdateTileLayerPanel(color, mInnerSideBarPanelWidth, mSideBarRowHeights, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mUpdateTileLayerPanel, "Tile Layering");

        // Add "Unit Spawn" tab and panel
        mUpdateUnitSpawnPanel = new UpdateUnitSpawnPanel(color, mInnerSideBarPanelWidth, mSideBarRowHeights, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mUpdateUnitSpawnPanel, "Unit Spawn Panel");

        // Add "Update Structure" tab and panel
        mUpdateStructurePanel = new UpdateStructurePanel(color, mInnerSideBarPanelWidth, mSideBarRowHeights, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mUpdateStructurePanel, "Structure Panel");

        // Add the main game panel to the container
        mGamePanelContainer.add(generateNewGameController());

        // Add the tab panel and content panel to the sidebar
        mSideBarPanel.add(mSaveMapButton.getButton());
        mSideBarPanel.add(mLoadMapButton.getButton());
        mSideBarPanel.add(new NoScrollBarPane(mSideBarTabPanel, mInnerSideBarPanelWidth, mSideBarRowHeights, false, 5));
        mSideBarPanel.add(mSideBarContentPanel);
        SwingUiUtils.setStylizedRaisedBevelBorder(mSideBarPanel, 1);
//        mSideBarPanel.setBorder(BorderFactory.createLoweredSoftBevelBorder());

        // Add the game panel and sidebar panel to the main editor scene
        add(mGamePanelContainer);
        add(mSideBarPanel);
    }

    private void setupPanelWithTabLink(JPanel content, String tabName) {
        JButton button = new OutlineButton(tabName);
        button.setBackground(content.getBackground());
        SwingUiUtils.setHoverEffect(button);
        int width = (int) (mInnerSideBarPanelWidth * .45);
        button.setMaximumSize(new Dimension(width, mSideBarRowHeights));
        button.setMinimumSize(new Dimension(width, mSideBarRowHeights));
        button.setPreferredSize(new Dimension(width, mSideBarRowHeights));
        button.setFont(FontPool.getInstance().getFontForHeight((int) (mSideBarRowHeights * .9)));
        button.addActionListener(e -> {
            CardLayout cl = (CardLayout)(mSideBarContentPanel.getLayout());
            cl.show(mSideBarContentPanel, button.getText());
        });
        mSideBarTabPanel.add(button);
        mSideBarContentPanel.add(content, button.getText());
    }

    private MapGenerationPanel createMapGenerationPanel(Color color, int width, int rowHeights, int height) {
        mMapGenerationPanel = new MapGenerationPanel(color, width, rowHeights, height);

        mSaveMapButton.getButton().addActionListener(e -> {
            String map = mGameController.getTileMapJson();
            String settings = mGameController.getSettingsJson();
//            String settings = mGameController.\
//            JsonUtils.save("TEST_MAP", map);
//            JsonUtils.save("TEST_SETTINGS", settings);
            mSaveMapButton.setBackground(ColorPalette.GREEN);
        });

        URL location = EditorScene.class.getProtectionDomain().getCodeSource().getLocation();
        JFileChooser jfc = new JFileChooser("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus");
        mLoadMapButton.getButton().addActionListener(e -> {

            try {
                JSONArray mapData = new JSONArray(Files.readString(
                        Paths.get("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/TEST_MAP.json")));
                JSONObject settings = new JSONObject(Files.readString(
                        Paths.get("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/TEST_SETTINGS.json")));

//                mGameController = GameController.create(settings, mapData);
//                mGameController.run();

                JPanel newGamePanel = mGameController.getGamePanel(mGamePanelWidth, mGamePanelHeight);
//                mGameController.setupInput(newGamePanel);

                mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                mGamePanelContainer.removeAll();
                mGamePanelContainer.add(newGamePanel);

                addGamePanelListeners(mGameController, newGamePanel);
            } catch (Exception er) {
                er.printStackTrace();
//                System.out.println("ttttt " + er.toString());
//                System.out.print(er.printStackTrace(););
            }
        });

        mMapGenerationPanel.getGenerateButton().addActionListener(e -> {
//            mMapGenerationPanel.setMapGenerationRandomDefaultsIfEmpty(false);
            generateNewGameController();
        });

        mMapGenerationPanel.getRandomizeButton().addActionListener(e -> {
            mMapGenerationPanel.randomizeMap(5, 15);
            generateNewGameController();
        });


        // Generate map with noise
        mMapGenerationPanel.mMapSizeDropDown.addActionListener(e -> {
            generateNewGameController();
        });

//        mMapGenerationPanel.getToggleButton().setText("Map Generation Configs");

        return mMapGenerationPanel;
    }

    private JPanel generateNewGameController() {
        String mapSize = String.valueOf(mMapGenerationPanel.mMapSizeDropDown.getSelectedItem());
        final int newTileMapColumns = Integer.parseInt(mapSize.split("x")[0]);
        final int newTileMapRows = Integer.parseInt(mapSize.split("x")[1]);
        final int newSpriteWidth = mGamePanelWidth / newTileMapColumns;
        final int newSpriteHeight = mGamePanelHeight / newTileMapRows;

        String terrainAsset = mMapGenerationPanel.getTerrainAsset();
        int minHeight = mMapGenerationPanel.getTerrainHeightMinimum();
        int maxHeight = mMapGenerationPanel.getTerrainHeightMaximum();
        float noiseZoom = mMapGenerationPanel.getTerrainNoiseZoom();

        String liquidAsset = mMapGenerationPanel.getLiquidAsset();
        int liquidLevel = mMapGenerationPanel.getLiquidLevel();

        String foundationAsset = mMapGenerationPanel.getFoundationAsset();
        int foundationDepth = mMapGenerationPanel.getFoundationDepth();

        GameGenerationConfigs settings = GameGenerationConfigs.getDefaults()
                // Required args
                .setRows(newTileMapRows)
                .setColumns(newTileMapColumns)

                .setFoundationAsset(foundationAsset)
                .setFoundationDepth(foundationDepth)

                .setLiquidAsset(liquidAsset)
                .setLiquidLevel(liquidLevel)
                .setTerrainAsset(terrainAsset)
                // Setup randomization
                .setUseNoise(true)
                .setMinimumHeight(minHeight)
                .setMaximumHeight(maxHeight)
                .setNoiseZoom(noiseZoom)

                .setStartingSpriteWidth(64)
                .setStartingViewportHeight(64)

                .setUseNoise(true);
//
//        GameDataStore settings = GameDataStore.getDefaults()
//                // Required args
//                .setViewportWidth(mGamePanelWidth)
//                .setViewportHeight(mGamePanelHeight)
//                .setMapGenerationStep1MapRows(newTileMapRows)
//                .setMapGenerationStep2MapColumns(newTileMapColumns)
//                .setSpriteWidth(newSpriteWidth)
//                .setSpriteHeight(newSpriteHeight)
//                .setMapGenerationStep3BaseAsset(baseAsset)
//                .setMapGenerationStep4BaseLevel(baseLevel)
//                .setMapGenerationStep5WaterAsset(waterAsset)
//                .setMapGenerationStep6WaterLevel(waterLevel)
//                .setMapGenerationStep7TerrainAsset(terrainAsset)
//                // Setup randomization
//                .setOptionHideGameplayHUD(false)
//                .setMapGenerationStep8UseNoise(true)
//                .setMapGenerationStep9MinHeight(minHeight)
//                .setMapGenerationStep10MaxHeight(maxHeight)
//                .setMapGenerationStep11NoiseZoom(noiseZoom)
//
//                .setMapGenerationStep8UseNoise(true);

        mGameController = GameController.create(settings);
        mGameController.run();

        JPanel newGamePanel = mGameController.getGamePanel(mGamePanelWidth, mGamePanelHeight);

        mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mGamePanelContainer.removeAll();
        mGamePanelContainer.add(newGamePanel);

        addGamePanelListeners(mGameController, newGamePanel);

        return newGamePanel;
    }

    private void addGamePanelListeners(GameController gameController, JPanel jp) {
        JSONObject temp = new JSONObject();
        jp.addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent e) {}
            @Override public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                temp.put(GameAPI.GET_TILES_AT_X, x);
                temp.put(GameAPI.GET_TILES_AT_Y, y);
                temp.put(GameAPI.GET_TILES_AT_RADIUS, 0);
                JSONArray tiles = gameController.getTilesAtXY(temp);
                if (tiles == null) { return; }
                Tile tile = (Tile) tiles.get(0);


                EditorPanel panel = null;
                if (mMapGenerationPanel.isShowing()) {
                    panel = mMapGenerationPanel;

                } else if (mUpdateTileLayerPanel.isShowing()) {
                    panel = mUpdateTileLayerPanel;

                } else if (mUpdateUnitSpawnPanel.isShowing()) {
                    panel = mUpdateUnitSpawnPanel;

                } else if (mUpdateStructurePanel.isShowing()) {
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

                temp.put(GameAPI.GET_TILES_AT_Y, y);
                temp.put(GameAPI.GET_TILES_AT_X, x);
                temp.put(GameAPI.GET_TILES_AT_RADIUS, 0);
                JSONArray tiles = gameController.getTilesAtXY(temp);
                if (tiles == null) { return; }
                Tile tile = (Tile) tiles.get(0);


                EditorPanel panel = null;

                if (mMapGenerationPanel.isShowing()) {
                    panel = mMapGenerationPanel;
                } else if (mUpdateTileLayerPanel.isShowing()) {
                    panel = mUpdateTileLayerPanel;
                } else if (mUpdateUnitSpawnPanel.isShowing()) {
                    panel = mUpdateUnitSpawnPanel;
                } else if (mUpdateStructurePanel.isShowing()) {
                    panel = mUpdateStructurePanel;
                }

                if (panel == null) { return; }
                panel.onEditorGameControllerMouseClicked(gameController, tile);
            }
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
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
        // Paint once
        if (!mInitializeSideBar) {
            mSideBarPanel.revalidate();
            mSideBarPanel.repaint();
            mInitializeSideBar = true;
        }
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
