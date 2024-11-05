package main.ui.presets.editor;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.StateLock;
import main.engine.EngineScene;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameSettings;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.components.OutlineLabel;
import main.ui.custom.*;
import main.ui.huds.controls.JGamePanel;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditorSceneV1 extends EngineScene {

    private GameController mGameController = null;
    private final SplittableRandom random = new SplittableRandom();
    private final JPanel mGamePanelContainer = new JGamePanel();
    private final String CONFIG_BRUSH_SIZE_COMBO_BOX = "Brush_Size";
    private final String CONFIG_TILE_HEIGHT_COMBO_BOX = "Tile_Height";
    private final String CONFIG_TERRAIN = "Terrain";
    private final String MAP_SIZE = "map_size";
    private final Map<String, JComponent> mConfigToComponent = new HashMap<>();
    private final Map<String, String> mConfigMapForStrings = new HashMap<>();
    private final StateLock mStateLock = new StateLock();

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

    public EditorSceneV1(int width, int height) {
        super(width, height, "Editor");

//        height = height - Engine.getInstance().getHeaderSize();

        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        mGamePanelWidth = (int) (width * .75);
        mGamePanelHeight = height;
        mSideBarPanelWidth = width - mGamePanelWidth;
        mSideBarPanelHeight = height;
        mSideBarPanelHeightSize0 = (int) (mSideBarPanelHeight * .025);
        mSideBarPanelHeightSize1 = (int) (mSideBarPanelHeight * .033);
        mSideBarPanelHeightSize2 = (int) (mSideBarPanelHeight * .1);
        mSideBarPanelHeightSize3 = (int) (mSideBarPanelHeight * .2);
        mAccordionContentWidth = mSideBarPanelWidth;
        mAccordionContentHeight = (int) (mSideBarPanelHeight * .5);

        setupAllInputs();

        JPanel sideBarPanel = new GameUI(mSideBarPanelWidth, mSideBarPanelHeight);
        sideBarPanel.setLayout(new FlowLayout());
        sideBarPanel.setBackground(Color.BLUE);
        sideBarPanel.setOpaque(true);

        generateNewGameController();
        JPanel gamePanel = new JGamePanel();
        gamePanel.setBackground(ColorPalette.getRandomColor());
        mGamePanelContainer.setPreferredSize(new Dimension(mGamePanelWidth, mGamePanelHeight));
        mGamePanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mGamePanelContainer.removeAll();
        mGamePanelContainer.add(gamePanel);
        add(mGamePanelContainer);

        addGamePanelListeners(mGameController, gamePanel);

        JLabel mapNameLabel = new OutlineLabel("Map Name");
        mapNameLabel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapNameLabel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapNameLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapNameLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        sideBarPanel.add(mapNameLabel);

        JTextArea mapNameField = new JTextArea();
        mapNameField.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapNameField.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapNameField.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapNameField.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        sideBarPanel.add(mapNameField);

        VerticalAccordionPanel mapMetadataAccordion = createMapMetadataPanel();
        sideBarPanel.add(mapMetadataAccordion);

        VerticalAccordionPanel tileDetailsAccordion = createTileDetailPanel();
        sideBarPanel.add(tileDetailsAccordion);

        VerticalAccordionPanel brushConfigsAccordion = createBrushConfigsPanel();
        sideBarPanel.add(brushConfigsAccordion);

//        AccordionPanelV2 terrainConfigsAccordion = createTerrainConfigsPanel();
//        sideBarPanel.add(terrainConfigsAccordion);

        add(mGamePanelContainer);
        add(sideBarPanel);
    }

    private void setupAllInputs() {
        createAndSaveEditorComponent(GameSettings.MODEL_MAP_DESCRIPTION, new JTextArea());
        createAndSaveEditorComponent(MAP_SIZE, SwingUiUtils.createJComboBox("21x17", mSideBarPanelWidth, mSideBarPanelHeightSize1));
        createAndSaveEditorComponent(GameSettings.MODEL_MAP_GENERATION_USE_NOISE, new JCheckBox("Use Noise Generation"));
        createAndSaveEditorComponent(GameSettings.MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT, new JTextArea(-10 + ""));
        createAndSaveEditorComponent(GameSettings.MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT, new JTextArea(10 + ""));
        createAndSaveEditorComponent(GameSettings.MODEL_MAP_GENERATION_NOISE_ZOOM, new JTextArea(random.nextFloat(.25f, .75f) + ""));

        createAndSaveEditorComponent(GameSettings.MODEL_MAP_GENERATION_TILE_HEIGHT, new JTextArea("0"));
        createAndSaveEditorComponent(CONFIG_BRUSH_SIZE_COMBO_BOX,
                SwingUiUtils.createJComboBox(ColorPalette.getRandomColor(), mSideBarPanelWidth, mSideBarPanelHeightSize1));
        createAndSaveEditorComponent(CONFIG_TILE_HEIGHT_COMBO_BOX,
                SwingUiUtils.createJComboBox(ColorPalette.getRandomColor(), mSideBarPanelWidth, mSideBarPanelHeightSize1));
    }

    private VerticalAccordionPanel createMapMetadataPanel() {
        JLabel mapDescriptionLabel = new OutlineLabel("Map Description");
        mapDescriptionLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapDescriptionLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JTextArea mapDescriptionField = (JTextArea) getEditorComponent(GameSettings.MODEL_MAP_DESCRIPTION);
        mapDescriptionField.setLineWrap(true);
        mapDescriptionField.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize2));
        mapDescriptionField.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize2 / 2));

        JLabel mapSizeLabel = new OutlineLabel("Map Size");
        mapSizeLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapSizeLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        StringComboBox mapSizeDropdown = (StringComboBox) getEditorComponent(MAP_SIZE);
        mapSizeDropdown.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        mapSizeDropdown.addItem("20x14");
        mapSizeDropdown.addItem("30x21");
        mapSizeDropdown.addItem("40x28");
        mapSizeDropdown.setSelectedIndex(0);
        mapSizeDropdown.addActionListener(e -> {
            mConfigMapForStrings.put(MAP_SIZE, String.valueOf(mapSizeDropdown.getSelectedItem()));
        });

        JLabel mapGenerationSpacing1 = new OutlineLabel(" ");
        mapGenerationSpacing1.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationSpacing1.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JCheckBox mapGenerationNoiseGeneration = (JCheckBox) getEditorComponent(GameSettings.MODEL_MAP_GENERATION_USE_NOISE);
        mapGenerationNoiseGeneration.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseGeneration.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        mapGenerationNoiseGeneration.setFocusPainted(false);

        JLabel mapGenerationNoiseMaxHeight = new OutlineLabel("Min Noise Generation Height");
        mapGenerationNoiseMaxHeight.setVisible(false);
        mapGenerationNoiseMaxHeight.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseMaxHeight.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JTextArea mapGenerationNoiseMaxHeightField = (JTextArea) getEditorComponent(GameSettings.MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT);
        mapGenerationNoiseMaxHeightField.setVisible(false);
        mapGenerationNoiseMaxHeightField.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseMaxHeightField.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        PlainDocument doc = (PlainDocument) mapGenerationNoiseMaxHeightField.getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter());

        JLabel mapGenerationNoiseMinHeight = new OutlineLabel("Max Noise Generation Height");
        mapGenerationNoiseMinHeight.setVisible(false);
        mapGenerationNoiseMinHeight.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseMinHeight.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JTextArea mapGenerationNoiseMinHeightField = (JTextArea) getEditorComponent(GameSettings.MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT);
        mapGenerationNoiseMinHeightField.setVisible(false);
        mapGenerationNoiseMinHeightField.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseMinHeightField.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        doc = (PlainDocument) mapGenerationNoiseMaxHeightField.getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter());

        JLabel mapGenerationNoiseZoom = new OutlineLabel("Noise Generation Zoom");
        mapGenerationNoiseZoom.setVisible(false);
        mapGenerationNoiseZoom.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseZoom.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JTextArea mapGenerationNoiseZoomField = (JTextArea) getEditorComponent(GameSettings.MODEL_MAP_GENERATION_NOISE_ZOOM);
        mapGenerationNoiseZoomField.setVisible(false);
        mapGenerationNoiseZoomField.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseZoomField.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        doc = (PlainDocument) mapGenerationNoiseMaxHeightField.getDocument();
        doc.setDocumentFilter(new FloatRangeDocumentFilter());

        JButton mapGenerationNoiseButton = new JButton("Generate Map With Noise");
        mapGenerationNoiseButton.setVisible(false);
        mapGenerationNoiseButton.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationNoiseButton.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JLabel mapGenerationTileBaseHeight = new OutlineLabel("Map Base Height");
        mapGenerationTileBaseHeight.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationTileBaseHeight.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        JTextArea mapGenerationTileBaseHeightField = (JTextArea) getEditorComponent(GameSettings.MODEL_MAP_GENERATION_TILE_HEIGHT);
        mapGenerationTileBaseHeightField.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationTileBaseHeightField.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        doc = (PlainDocument) mapGenerationNoiseMaxHeightField.getDocument();
        doc.setDocumentFilter(new IntegerOnlyDocumentFilter());

        JButton mapGenerationWithoutNoiseButton = new JButton("Generate Map Without Noise");
        mapGenerationWithoutNoiseButton.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapGenerationWithoutNoiseButton.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        mapGenerationNoiseGeneration.addActionListener(e -> {
            mapGenerationNoiseMinHeight.setVisible(mapGenerationNoiseGeneration.isSelected());
            mapGenerationNoiseMinHeightField.setVisible(mapGenerationNoiseGeneration.isSelected());
            mapGenerationNoiseMaxHeight.setVisible(mapGenerationNoiseGeneration.isSelected());
            mapGenerationNoiseMaxHeightField.setVisible(mapGenerationNoiseGeneration.isSelected());
            mapGenerationNoiseZoom.setVisible(mapGenerationNoiseGeneration.isSelected());
            mapGenerationNoiseZoomField.setVisible(mapGenerationNoiseGeneration.isSelected());
            mapGenerationNoiseButton.setVisible(mapGenerationNoiseGeneration.isSelected());


            mapGenerationTileBaseHeight.setVisible(!mapGenerationNoiseGeneration.isSelected());
            mapGenerationTileBaseHeightField.setVisible(!mapGenerationNoiseGeneration.isSelected());
            mapGenerationWithoutNoiseButton.setVisible(!mapGenerationNoiseGeneration.isSelected());
        });

        mapGenerationWithoutNoiseButton.addActionListener(e -> generateNewGameController());
        mapGenerationNoiseButton.addActionListener(e -> generateNewGameController());
        mapSizeDropdown.addActionListener(e -> generateNewGameController());

        JLabel mapModelLabel = new OutlineLabel("Map Mode");
        mapModelLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        mapModelLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        StringComboBox mapModeDropdown = SwingUiUtils.createJComboBox(mSideBarPanelWidth, mSideBarPanelHeightSize1);
        mapModeDropdown.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        mapModeDropdown.addItem("Team Deathmatch");
        mapModeDropdown.addItem("Survival");
        mapModeDropdown.addItem("Final Destination");
        mapModeDropdown.setSelectedIndex(0);

        JPanel mapMetadataPanel = new JGamePanel(false);
        int mapMetadataPanelWidth = mSideBarPanelWidth;
        int mapMetadataPanelHeight = (int) (mSideBarPanelHeight);
        mapMetadataPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mapMetadataPanel.add(mapDescriptionLabel);
        mapMetadataPanel.add(mapDescriptionField);
        mapMetadataPanel.add(mapSizeLabel);
        mapMetadataPanel.add(mapSizeDropdown);
        mapMetadataPanel.add(mapGenerationSpacing1);
        mapMetadataPanel.add(mapGenerationNoiseGeneration);
        mapMetadataPanel.add(mapGenerationNoiseMaxHeight);
        mapMetadataPanel.add(mapGenerationNoiseMaxHeightField);
        mapMetadataPanel.add(mapGenerationNoiseMinHeight);
        mapMetadataPanel.add(mapGenerationNoiseMinHeightField);
        mapMetadataPanel.add(mapGenerationNoiseZoom);
        mapMetadataPanel.add(mapGenerationNoiseZoomField);
        mapMetadataPanel.add(mapGenerationNoiseButton);
        mapMetadataPanel.add(mapGenerationTileBaseHeight);
        mapMetadataPanel.add(mapGenerationTileBaseHeightField);
        mapMetadataPanel.add(mapGenerationWithoutNoiseButton);
        mapMetadataPanel.setPreferredSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));

        VerticalAccordionPanel mapMetadataAccordion = new VerticalAccordionPanel("Map Configurations",
                mapMetadataPanel,
                ColorPalette.getRandomColor(),
                mSideBarPanelWidth,
                mSideBarPanelHeightSize1,
                mAccordionContentHeight
        );
        return mapMetadataAccordion;
    }

    public String getComponentValueOrDefault(String key, Object defValue) {
        String value  = getComponentValue(key);
        if (value == null || value.isEmpty()) {
            value = String.valueOf(defValue);
        }
        return value;
    }
    public String getComponentValue(String key) {
        String text = null;

        for (Map.Entry<String, JComponent> componentEntry : mConfigToComponent.entrySet()) {
            if (!componentEntry.getKey().equals(key)) { continue; }

            if (componentEntry.getValue() instanceof StringComboBox jComboBox) {
                text = String.valueOf(jComboBox.getSelectedItem());
            } else if (componentEntry.getValue() instanceof JTextArea jTextArea) {
                text = String.valueOf(jTextArea.getText());
            } else if (componentEntry.getValue() instanceof JCheckBox jCheckBox) {
                text = String.valueOf(jCheckBox.isSelected());
            } else if (componentEntry.getValue() instanceof JTextField jTextField) {
                text = String.valueOf(jTextField.getText());
            }
        }

        return text;
    }
    public JComponent createAndSaveEditorComponent(String key, JComponent component) {
        return mConfigToComponent.put(key, component);
    }
    public JComponent getEditorComponent(String key) {
        return mConfigToComponent.get(key);
    }

    private void generateNewGameController() {
        String str = getComponentValue(MAP_SIZE);
        final int newTileMapColumns = Integer.parseInt(str.split("x")[0]);
        final int newTileMapRows = Integer.parseInt(str.split("x")[1]);
        final int newSpriteWidth = mGamePanelWidth / newTileMapColumns;
        final int newSpriteHeight = mGamePanelHeight / newTileMapRows;
        boolean useNoiseGeneration = Boolean.parseBoolean(getComponentValue(GameSettings.MODEL_MAP_GENERATION_USE_NOISE));

        GameSettings settings = GameSettings.getDefaults()
                .setViewportWidth(mGamePanelWidth)
                .setViewportHeight(mGamePanelHeight)
                .setTileMapRows(newTileMapRows)
                .setTileMapColumns(newTileMapColumns)
                .setSpriteWidth(newSpriteWidth)
                .setSpriteHeight(newSpriteHeight)
                .setUseNoiseGeneration(useNoiseGeneration);

        if (useNoiseGeneration) {
            int minNoiseHeight = Integer.parseInt(getComponentValue(GameSettings.MODEL_MAP_GENERATION_NOISE_MIN_HEIGHT));
            int maxNoiseHeight =  Integer.parseInt(getComponentValue(GameSettings.MODEL_MAP_GENERATION_NOISE_MAX_HEIGHT));
            float noiseZoom = (float) Double.parseDouble(getComponentValue(GameSettings.MODEL_MAP_GENERATION_NOISE_ZOOM));
            settings.setMinNoiseGenerationHeight(minNoiseHeight)
                    .setMaxNoiseGenerationHeight(maxNoiseHeight)
                    .setNoiseGenerationZoom(noiseZoom);
        }

        mGameController = GameController.create(settings);
        mGameController.getModel().getSettings().setModeAsMapEditorMode();
        mGameController.run();

        JPanel newGamePanel = mGameController.getGamePanel(mGamePanelWidth, mGamePanelHeight);
        mGamePanelContainer.removeAll();
        newGamePanel.setBounds(0, 0, mGamePanelWidth, mGamePanelHeight);
        mGamePanelContainer.add(newGamePanel);

        addGamePanelListeners(mGameController, newGamePanel);
    }

    private final JLabel mTileDetailsAverageHeightLabel = new OutlineLabel("");
    private final JLabel mTileDetailsSelectedTilesCountLabel = new OutlineLabel("");
    private VerticalAccordionPanel createTileDetailPanel() {
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


        JPanel tileDetailsPanel = new JGamePanel(false);
        int mapMetadataPanelWidth = mSideBarPanelWidth;
        int mapMetadataPanelHeight = (int) (mSideBarPanelHeight * .5);
        tileDetailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tileDetailsPanel.add(tileDetailsLabel);
        tileDetailsPanel.add(mTileDetailsSelectedTilesCountLabel);
        tileDetailsPanel.add(mTileDetailsAverageHeightLabel);
        tileDetailsPanel.setMinimumSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));
        tileDetailsPanel.setMaximumSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));
        tileDetailsPanel.setPreferredSize(new Dimension(mapMetadataPanelWidth, mapMetadataPanelHeight));

        VerticalAccordionPanel tileDetailsAccordion = new VerticalAccordionPanel("Tile Details",
                tileDetailsPanel,
                ColorPalette.getRandomColor(),
                mSideBarPanelWidth,
                mSideBarPanelHeightSize1,
                mAccordionContentHeight
        );
        return tileDetailsAccordion;
    }

    private VerticalAccordionPanel createTerrainConfigsPanel() {
        Color color = ColorPalette.getRandomColor();
        JPanel terrainConfigsPanel = new JGamePanel(false);
        int terrainConfigsPanelHeight = (int) (mSideBarPanelHeight * .25);
        int terrainConfigsPanelWidth = mSideBarPanelWidth;

        JButton imageButton = new JButton();
        int imageWidth = (int) (terrainConfigsPanelWidth * .5);
        int imageHeight = (int) (terrainConfigsPanelHeight * .75);
        imageButton.setMinimumSize(new Dimension(imageWidth, imageHeight));
        imageButton.setMaximumSize(new Dimension(imageWidth, imageHeight));
        imageButton.setPreferredSize(new Dimension(imageWidth, imageHeight));

        StringComboBox terrainDropDown = SwingUiUtils.createJComboBox(color, mSideBarPanelWidth, mSideBarPanelHeightSize1);
        terrainDropDown.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        Map<String, String> fullyQualifiedToSimpleTerainNameMap = AssetPool.getInstance()
                .getBucket("floor_tiles")
                .stream()
                .collect(Collectors.toMap(e -> e.substring(e.lastIndexOf('/') + 1, e.lastIndexOf('.')), e -> e));
        Map<String, String> simpleToFullyQualifiedTerrainNameMap = fullyQualifiedToSimpleTerainNameMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        fullyQualifiedToSimpleTerainNameMap.forEach((key, value) -> terrainDropDown.addItem(key));
        terrainDropDown.setSelectedIndex(0);

        // Setup dropdown for terrain
        setupTerrainImage(terrainDropDown, imageWidth, imageHeight, imageButton);
        terrainDropDown.addActionListener(e -> {
            setupTerrainImage(terrainDropDown, imageWidth, imageHeight, imageButton);
            String dropdownValue = (String) terrainDropDown.getSelectedItem();
            String fullyQualifiedAssetName = simpleToFullyQualifiedTerrainNameMap.get(dropdownValue);
            mConfigMapForStrings.put(CONFIG_TERRAIN, fullyQualifiedAssetName);
        });

        terrainConfigsPanel.setLayout(new FlowLayout());
        terrainConfigsPanel.add(imageButton);
        terrainConfigsPanel.add(terrainDropDown);
        terrainConfigsPanel.setMinimumSize(new Dimension(terrainConfigsPanelWidth, terrainConfigsPanelHeight));
        terrainConfigsPanel.setMaximumSize(new Dimension(terrainConfigsPanelWidth, terrainConfigsPanelHeight));
        terrainConfigsPanel.setPreferredSize(new Dimension(terrainConfigsPanelWidth, terrainConfigsPanelHeight));

        VerticalAccordionPanel terrainConfigsAccordion = new VerticalAccordionPanel("Terrain Configurations",
                terrainConfigsPanel,
                ColorPalette.getRandomColor(),
                mSideBarPanelWidth,
                mSideBarPanelHeightSize1,
                mAccordionContentHeight
        );

        return terrainConfigsAccordion;
    }

    private VerticalAccordionPanel createBrushConfigsPanel() {
        Color color = ColorPalette.getRandomColor();
        JLabel brushSizeLabel = new OutlineLabel("Brush Size");
        brushSizeLabel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        brushSizeLabel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        brushSizeLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        brushSizeLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        StringComboBox brushSizeComboBox = (StringComboBox) getEditorComponent(CONFIG_BRUSH_SIZE_COMBO_BOX);
        brushSizeComboBox.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        IntStream.range(1, 11).forEach(i -> brushSizeComboBox.addItem(String.valueOf(i)));
        brushSizeComboBox.setSelectedIndex(0);

        JLabel tileHeightLabel = new OutlineLabel("Tile Height");
        tileHeightLabel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileHeightLabel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileHeightLabel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1));
        tileHeightLabel.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));

        StringComboBox tileHeightDropDown = (StringComboBox) getEditorComponent(CONFIG_TILE_HEIGHT_COMBO_BOX);
        tileHeightDropDown.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        IntStream.range(0, 11).forEach(i -> tileHeightDropDown.addItem(String.valueOf(i)));
        tileHeightDropDown.setSelectedIndex(0);

        // Setting up the image for terrain
        JButton terrainImage = new JButton();
        int imageWidth = (int) (mSideBarPanelWidth * .5);
        int imageHeight = (int) (mSideBarPanelWidth * .5);
        terrainImage.setMinimumSize(new Dimension(imageWidth, imageHeight));
        terrainImage.setMaximumSize(new Dimension(imageWidth, imageHeight));
        terrainImage.setPreferredSize(new Dimension(imageWidth, imageHeight));

        StringComboBox terrainImageComboBox = SwingUiUtils.createJComboBox(color, mSideBarPanelWidth, mSideBarPanelHeightSize1);
        terrainImageComboBox.setFont(FontPool.getInstance().getFontForHeight(mSideBarPanelHeightSize1));
        Map<String, String> fullyQualifiedToSimpleTerainNameMap = AssetPool.getInstance()
                .getBucket("floor_tiles")
                .stream()
                .filter(e -> !e.contains("no_floor"))
                .collect(Collectors.toMap(e -> e.substring(e.lastIndexOf('/') + 1, e.lastIndexOf('.')), e -> e));
        Map<String, String> simpleToFullyQualifiedTerrainNameMap = fullyQualifiedToSimpleTerainNameMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        fullyQualifiedToSimpleTerainNameMap.forEach((key, value) -> terrainImageComboBox.addItem(key));
        terrainImageComboBox.setSelectedIndex(0);

        // Setup dropdown for terrain
        setupTerrainImage(terrainImageComboBox, imageWidth, imageHeight, terrainImage);
        terrainImageComboBox.addActionListener(e -> {
            setupTerrainImage(terrainImageComboBox, imageWidth, imageHeight, terrainImage);
            String dropdownValue = terrainImageComboBox.getSelectedItem();
            String fullyQualifiedAssetName = simpleToFullyQualifiedTerrainNameMap.get(dropdownValue);
            mConfigMapForStrings.put(CONFIG_TERRAIN, fullyQualifiedAssetName);
        });

        JPanel brushConfigsContentPanel = new JGamePanel(false);
        brushConfigsContentPanel.setLayout(new FlowLayout());
        brushConfigsContentPanel.add(brushSizeLabel);
        brushConfigsContentPanel.add(brushSizeComboBox);
        brushConfigsContentPanel.add(tileHeightLabel);
        brushConfigsContentPanel.add(tileHeightDropDown);
        brushConfigsContentPanel.add(terrainImage);
        brushConfigsContentPanel.add(terrainImageComboBox);
        brushConfigsContentPanel.setMinimumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1 * 5));
        brushConfigsContentPanel.setMaximumSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1 * 5));
        brushConfigsContentPanel.setPreferredSize(new Dimension(mSideBarPanelWidth, mSideBarPanelHeightSize1 * 5));

        VerticalAccordionPanel brushConfigsPanel = new VerticalAccordionPanel("Brush Configurations",
                brushConfigsContentPanel,
                color,
                mSideBarPanelWidth,
                mSideBarPanelHeightSize1,
                mAccordionContentHeight
        );
        return brushConfigsPanel;
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

                JsonArray newSelectedTiles = getTilesConsideringBrushSizeV2(gameController, tile);
                gameController.setSelectedTiles(newSelectedTiles);

                List<Entity> selectedTiles = gameController.getSelectedTiles();
                extraMapInteractionData(selectedTiles);
            }
        });
        jp.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Entity entity = gameController.getModel().tryFetchingTileWithXY(x, y);
                if (entity == null) { return; }

                StringComboBox tileHeightComboBox = (StringComboBox) getEditorComponent(CONFIG_TILE_HEIGHT_COMBO_BOX);
                int tileHeight = Integer.parseInt(String.valueOf(tileHeightComboBox.getSelectedItem()));

                String terrain = mConfigMapForStrings.get(CONFIG_TERRAIN);

                JsonObject attributeToUpdate = new JsonObject();
                attributeToUpdate.put(Tile.HEIGHT, tileHeight);
                if (terrain != null) {
                    attributeToUpdate.put(Tile.TERRAIN, terrain);
                }

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
                assetName + "_editor_scene"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        imageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
    }


    private JsonArray getTilesConsideringBrushSizeV2(GameController gameController, Tile tile) {
        Entity entity;
        JsonArray selectedTiles = new JsonArray();

        StringComboBox brushComboBox = (StringComboBox) getEditorComponent(CONFIG_BRUSH_SIZE_COMBO_BOX);
        int brushSize = Integer.parseInt(String.valueOf(brushComboBox.getSelectedItem())) - 1;

        for (int row = tile.getRow() - brushSize; row <= tile.getRow() + brushSize; row++) {
            for (int column = tile.getColumn() - brushSize; column <= tile.getColumn() + brushSize; column++) {
                entity = gameController.getModel().tryFetchingTileAt(row, column);
                if (entity == null) { continue; }
                Tile selectedTile = entity.get(Tile.class);
                JsonArray rowAndColumn = new JsonArray();
                rowAndColumn.add(selectedTile.getRow());
                rowAndColumn.add(selectedTile.getColumn());
                selectedTiles.add(rowAndColumn);
            }
        }
        return selectedTiles;
    }

    @Override
    public void update() {
        mGameController.update();
    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return this;
    }
}
