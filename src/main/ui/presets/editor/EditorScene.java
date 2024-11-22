package main.ui.presets.editor;

import main.game.main.GameConfigurations;
import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.StateLock;
import main.engine.EngineScene;
import main.game.components.tile.Tile;
import main.game.main.GameController;
import main.game.main.GameModelAPI;
import main.game.main.JsonUtils;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;

import main.ui.outline.OutlineButton;
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
    private final int mAccordionContentWidth;
    private final int mAccordionContentHeight;
    private final int mAccordionContentHeight2;
    private boolean mInitializeSideBar = false;

    private final JSONArray mSelectedTiles = new JSONArray(); // Stores selected tiles for editing
    private MapGenerationPanel mMapGenerationPanel = new MapGenerationPanel();
    private UpdateTileLayerPanel mUpdateTileLayerPanel = new UpdateTileLayerPanel();
    private UpdateUnitSpawnPanel mUpdateUnitSpawnPanel = new UpdateUnitSpawnPanel();
    private UpdateStructurePanel mUpdateStructurePanel = new UpdateStructurePanel();

    private int mInnerSideBarPanelRowHeight = 0;
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
        mInnerSideBarPanelRowHeight = mSideBarPanelHeightSize1;
        mInnerSideBarPanelWidth = mSideBarPanelWidth;
        mInnerSideBarContentPanelHeight = mSideBarPanelHeight - mInnerSideBarPanelRowHeight;

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
        mMapGenerationPanel = createMapGenerationPanel(color, mInnerSideBarPanelWidth, mInnerSideBarPanelRowHeight, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mMapGenerationPanel, "Map Generation");

        // Add "Tile Layering" tab and panel
        mUpdateTileLayerPanel = new UpdateTileLayerPanel(color, mInnerSideBarPanelWidth, mInnerSideBarPanelRowHeight, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mUpdateTileLayerPanel, "Tile Layering");

        // Add "Unit Spawn" tab and panel
        mUpdateUnitSpawnPanel = new UpdateUnitSpawnPanel(color, mInnerSideBarPanelWidth, mInnerSideBarPanelRowHeight, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mUpdateUnitSpawnPanel, "Unit Spawn Panel");

        // Add "Update Structure" tab and panel
        mUpdateStructurePanel = new UpdateStructurePanel(color, mInnerSideBarPanelWidth, mInnerSideBarPanelRowHeight, mInnerSideBarContentPanelHeight);
        setupPanelWithTabLink(mUpdateStructurePanel, "Structure Panel");

        // Add the main game panel to the container
        mGamePanelContainer.add(generateNewGameController());

        // Add the tab panel and content panel to the sidebar
        mSideBarPanel.add(new NoScrollBarPane(mSideBarTabPanel, mInnerSideBarPanelWidth, mInnerSideBarPanelRowHeight, false, 5));
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
        button.setMaximumSize(new Dimension(width, mInnerSideBarPanelRowHeight));
        button.setMinimumSize(new Dimension(width, mInnerSideBarPanelRowHeight));
        button.setPreferredSize(new Dimension(width, mInnerSideBarPanelRowHeight));
        button.setFont(FontPool.getInstance().getFontForHeight((int) (mInnerSideBarPanelRowHeight * .9)));
        button.addActionListener(e -> {
            CardLayout cl = (CardLayout)(mSideBarContentPanel.getLayout());
            cl.show(mSideBarContentPanel, button.getText());
        });
        mSideBarTabPanel.add(button);
        mSideBarContentPanel.add(content, button.getText());
    }

    private MapGenerationPanel createMapGenerationPanel(Color color, int width, int collapsedHeight, int expandedHeight) {
        mMapGenerationPanel = new MapGenerationPanel(color, width, collapsedHeight, expandedHeight);

        mMapGenerationPanel.mSaveMapButton.getButton().addActionListener(e -> {
            String map = mGameController.getTileMapJson();
            String settings = mGameController.getSettingsJson();
//            String settings = mGameController.\
            JsonUtils.save("TEST_MAP", map);
            JsonUtils.save("TEST_SETTINGS", settings);
            mMapGenerationPanel.mSaveMapButton.setBackground(ColorPalette.GREEN);
        });

        URL location = EditorScene.class.getProtectionDomain().getCodeSource().getLocation();
        JFileChooser jfc = new JFileChooser("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus");
        mMapGenerationPanel.mLoadMapButton.getButton().addActionListener(e -> {

            try {
                JSONArray mapData = new JSONArray(Files.readString(
                        Paths.get("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/TEST_MAP.json")));
                JSONObject settings = new JSONObject(Files.readString(
                        Paths.get("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/TEST_SETTINGS.json")));

                mGameController = GameController.create(settings, mapData);
                mGameController.run();

                JPanel newGamePanel = mGameController.getGamePanel(mGamePanelWidth, mGamePanelHeight);
                mGameController.setupInput(newGamePanel);

                mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                mGamePanelContainer.removeAll();
                mGamePanelContainer.add(newGamePanel);

                addGamePanelListeners(mGameController, newGamePanel);
            } catch (Exception er) {
                er.printStackTrace();
                System.out.println("ttttt " + er.toString());
//                System.out.print(er.printStackTrace(););
            }
        });

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

//        mMapGenerationPanel.getToggleButton().setText("Map Generation Configs");

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

        GameConfigurations settings = GameConfigurations.getDefaults()
                // Required args
                .setViewportWidth(mGamePanelWidth)
                .setViewportHeight(mGamePanelHeight)
                .setMapGenerationStep1MapRows(newTileMapRows)
                .setMapGenerationStep2MapColumns(newTileMapColumns)
                .setSpriteWidth(newSpriteWidth)
                .setSpriteHeight(newSpriteHeight)
                .setMapGenerationStep3BaseAsset(baseAsset)
                .setMapGenerationStep4BaseLevel(baseLevel)
                .setMapGenerationStep5WaterAsset(waterAsset)
                .setMapGenerationStep6WaterLevel(waterLevel)
                .setMapGenerationStep7TerrainAsset(terrainAsset)
                // Setup randomization
                .setOptionHideGameplayHUD(false)
                .setMapGenerationStep8UseNoise(true)
                .setMapGenerationStep9MinHeight(minHeight)
                .setMapGenerationStep10MaxHeight(maxHeight)
                .setMapGenerationStep11NoiseZoom(noiseZoom)

                .setMapGenerationStep8UseNoise(true);

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

    private void addGamePanelListeners(GameController gameController, JPanel jp) {
        JSONObject temp = new JSONObject();
        jp.addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent e) {}
            @Override public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                temp.put(GameModelAPI.GET_TILES_AT_X, x);
                temp.put(GameModelAPI.GET_TILES_AT_Y, y);
                temp.put(GameModelAPI.GET_TILES_AT_RADIUS, 0);
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

                temp.put(GameModelAPI.GET_TILES_AT_Y, y);
                temp.put(GameModelAPI.GET_TILES_AT_X, x);
                temp.put(GameModelAPI.GET_TILES_AT_RADIUS, 0);
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
