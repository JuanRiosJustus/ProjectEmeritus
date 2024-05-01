package main.ui.presets.editor;

import main.game.stores.pools.ColorPalette;
import main.constants.Constants;
import main.constants.Direction;
import main.constants.Settings;
import main.engine.Engine;
import main.engine.EngineScene;
import main.game.components.tile.Tile;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapBuilder;
import main.game.map.builders.utils.TileMapOperations;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.SpriteSheetRow;
import main.graphics.SpriteSheet;
import main.ui.panels.ExpandingPanels;
import main.utils.MathUtils;
import main.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class EditorScene extends EngineScene {
    private ExpandingPanels controlPane = new ExpandingPanels();
    private final JPanel gridPanel = new JPanel();
    private final JButton selectedTileImageButton = new JButton();
    private final String NOT_AVAILABLE = "NONE";
    private String selectedTileImageString = "";
    private BufferedImage selectedTileImage;

    private final Map<String, JComboBox<String>> mapSettingsConfigs = new HashMap<>();
    private final static String SIZE_CONFIG = "Size",
            ROUGH_TERRAIN = Tile.OBSTRUCTION_ROUGH_TERRAIN, DESTROYABLE_BLOCKER = Tile.OBSTRUCTION_DESTROYABLE_BLOCKER;
    public JComboBox<String> getAndOrCreateConfig(String config) {
        JComboBox<String> dropdown = mapSettingsConfigs.getOrDefault(config, new JComboBox<>());
        mapSettingsConfigs.put(config, dropdown);
        return dropdown;
    }
    private final JSlider mapSettingsZoomSlider = new JSlider();
    private JButton mapSettingsGeneratorButton = new JButton("Generate");

    private final JTextField tileDetailsRowColumnField = new JTextField();
    private final JTextField tileDetailsShadowsField = new JTextField();
    private final JTextField tileDetailsHeightTextField = new JTextField();

    private final JComboBox<String> brushSettingsModeComboBox = new JComboBox<>();
    private final JComboBox<String> tileDetailsComboBox = new JComboBox<>();
    private final SplittableRandom random = new SplittableRandom();


    private final JTextField brushSettingsSizeField = new JTextField("Enter Brush Size");
    private int tileMapRows = 25, tileMapColumns = 40;
    private final JCheckBox tileDetailsCausesCollisionCheckbox = new JCheckBox();
    private int selectedTileHeight = -1;
    private int controlPaneWidth;
    private int controlPaneHeight;
    private TileMap tileMap;

    public EditorScene(int width, int height) {
//        super(width, height - Engine.getInstance().getHeaderSize(), EditorScene.class.getSimpleName());
        setLayout(null);

        height = height - Engine.getInstance().getHeaderSize();
        controlPaneWidth = width / 5;
        controlPaneHeight = height;

        JPanel gridPane = setupGrid(11, 20, width - controlPaneWidth, height, false);
        gridPane.setOpaque(true);
        gridPane.setLocation(0, 0);
        add(gridPane);


        controlPane = setupControlPanel();
        controlPane.setLocation(width - controlPaneWidth, 0);
        controlPane.setSize(controlPaneWidth, controlPaneHeight);
        controlPane.setOpaque(true);
        controlPane.setBackground(ColorPalette.getRandomColor());
//        add(controlPane);
        add(controlPane);
//        controlPane.setBackground(ColorPalette.getRandomColor());


//        add(controlPane);
//        setBackground(ColorPalette.getRandomColor());

//        add(new JButton("Yest"));
//        init(InputController.instance());
//        setLayout(new BorderLayout());
//
//        m_board = new GameEditorScreen(getWidth() - Constants.SIDE_BAR_WIDTH, getHeight());
//        add(m_board, BorderLayout.CENTER);
//
//        m_panel = new GameEditorPanel(Constants.SIDE_BAR_WIDTH, getHeight(), getEscapeButton());
//        add(m_panel, BorderLayout.EAST);
//
//        m_board.linkToScreen(controls, m_panel);
    }

//    private JPanel setupGridPanel(int width, int height) {
//        gridPanel.setLayout(new GridBagLayout());
//        gridPanel.setPreferredSize(new Dimension(width, height));
//        return setupGrid(11, 20, false);
//    }

    private JPanel setupGrid(int rows, int columns) {
        return setupGrid(rows, columns, -1, -1, false);
    }
    private JPanel setupGrid(int rows, int columns, boolean isCustom) {
        return setupGrid(rows, columns, -1, -1, isCustom);
    }
    private JPanel setupGrid(int rows, int columns, int width, int height, boolean isCustom) {
        if (rows <= 0 || columns <= 0) { return gridPanel; }
        if (width > 0 || height > 0) {
            gridPanel.setLayout(new GridBagLayout());
            gridPanel.setPreferredSize(new Dimension(width, height));
            gridPanel.setSize(new Dimension(width, height));
        }

        GridBagConstraints constraints = new GridBagConstraints();
        gridPanel.removeAll();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;

        final boolean[] isBeingPressed = {false};
        width = (int) gridPanel.getPreferredSize().getWidth();
        height = (int) gridPanel.getPreferredSize().getHeight();
        ArrayList<EditorTile> editorTiles = new ArrayList<>();

        boolean shouldUseTileMap = isCustom && tileMap != null;
        if (shouldUseTileMap) {
            rows = tileMap.getRows();
            columns = tileMap.getColumns();
        }

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
//                EditorTile newEditorTile = new EditorTile(row, column);
                EditorTile newEditorTile = new EditorTile(null);
                if (shouldUseTileMap) {
                    newEditorTile = new EditorTile(tileMap.tryFetchingTileAt(row, column));
                    newEditorTile.revalidate();
                    newEditorTile.repaint();
                }
                EditorTile finalNewEditorTile = newEditorTile;
                newEditorTile.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) { }
                    @Override
                    public void mousePressed(MouseEvent e) { isBeingPressed[0] = true; setIconAndRemoveText(); }
                    @Override
                    public void mouseReleased(MouseEvent e) { isBeingPressed[0] = false; }
                    @Override
                    public void mouseEntered(MouseEvent e) { }
                    @Override
                    public void mouseExited(MouseEvent e) { }
                    public void setIconAndRemoveText() {
                        if (selectedTileImage == null) { return; }
                        if (!isBeingPressed[0] || selectedTileImageButton.getIcon() == null) { return; }

                        // get the current brush mode
                        String selectedMode = (String) brushSettingsModeComboBox.getSelectedItem();
                        if (selectedMode == null) { return; }

                        // setup the tile
                        Tile tile = finalNewEditorTile.getTile();
                        int isPath = tileDetailsCausesCollisionCheckbox.isSelected() ? 0 : 1;

                        // get tile height
                        int tileHeight = selectedTileHeight;
                        if (tileHeight == -1) { tileHeight = tile.getHeight(); }

                        // get terrain index
                        SpriteSheet map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
                        if (map == null) { return; }
                        int terrainIndex = map.indexOf(selectedTileImageString);

                        int liquidIndex = (selectedTileImageString.contains("liquid") ? terrainIndex : -1);

                        int structureIndex = (selectedTileImageString.contains("structure") ? terrainIndex : -1);

                        int size = Integer.parseInt(brushSettingsSizeField.getText());
                        for (int depth = 0; depth < size; depth++) {
                            for (Direction direction : Direction.values()) {
                                // TODO add depth for tile size
                            }
                        }

                        if (selectedMode.equalsIgnoreCase("Add")) {
                            if (selectedTileImageString.contains("floor")) {
//                                tile.encode(isPath, tileHeight, terrainIndex, tile.getLiquid());
                            } else if (selectedTileImageString.contains("wall")) {
//                                tile.encode(isPath, tileHeight, terrainIndex, tile.getLiquid());
                            } else if (selectedTileImageString.contains("liquid")) {
//                                tile.encode(isPath, tileHeight, tile.getTerrain(), liquidIndex);
                            } else if (selectedTileImageString.contains("structure")) {
//                                tile.encode(isPath, tileHeight, tile.getTerrain(), tile.getLiquid());
                            }
                        } else if (selectedMode.equalsIgnoreCase("Remove")) {
                            finalNewEditorTile.reset();
                        } else if (selectedMode.equalsIgnoreCase("Fill")) {
                            for (EditorTile editorTile : editorTiles) {
                                tile = editorTile.getTile();
//                                tile.encode(isPath, tileHeight, terrainIndex, liquidIndex);
                            }
                        } else if (selectedMode.equalsIgnoreCase("Inspect")) {
                            tileDetailsHeightTextField.setText(tile.getHeight() + "");
                            tileDetailsShadowsField.setText(tile.getAssets(Tile.CARDINAL_SHADOW).size() + "");
                            tileDetailsRowColumnField.setText(tile.toString());
                        }
                    }
                });
                newEditorTile.setOpaque(true);
                newEditorTile.setBackground(ColorPalette.getRandomColor());
//                et.setOpaque(true);
//                et.setBackground(ColorPalette.getRandomColor());
                newEditorTile.setPreferredSize(new Dimension(width / columns, height / rows));
                constraints.gridy = row;
                constraints.gridx = column;
                editorTiles.add(newEditorTile);

                gridPanel.add(newEditorTile, constraints);
            }
        }
        return gridPanel;
    }

    private ExpandingPanels setupControlPanel() {

        ExpandingPanels panels = new ExpandingPanels();
//        panels.addPanel("Tile", setupTileDetails());
//        panels.addPanel("Brush", setupBrushSettings());
        panels.addPanel("Map", setupMapSettings());

        return panels;

//        JPanel panel = new JPanel();
//        panel.setLayout(new GridBagLayout());
//        panel.setPreferredSize(new Dimension(width, height));
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.gridy = 0;
//        constraints.weightx = 1;
//
//        panel.add(setupTileDetails(), constraints);
//
//        constraints.gridy = 1;
//        panel.add(setupBrushSettings(), constraints);
//
//
//        constraints.gridy = 2;
//        panel.add(setupMapSettings(), constraints);
//
//        return panel;
    }


//
//    private JPanel setupTileDetails() {
//        JPanel panel = new JPanel();
//        panel.setLayout(new GridBagLayout());
//
//        // ROW 1
//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.gridx = 0;
//        constraints.gridy = 0;
//        constraints.weighty = 1;
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.anchor = GridBagConstraints.WEST;
//        constraints.gridwidth = 2;
//        JButton label = new JButton("Tile Details");
//        label.setBorderPainted(false);
//        label.setFocusPainted(false);
//        panel.add(label, constraints);
//
//        // Row 2 and 3
//        constraints.gridx = 2;
//        constraints.gridy = 2;
//        constraints.weighty = 0;
//        constraints.gridheight = 1;
//        constraints.gridwidth = 1;
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.anchor = GridBagConstraints.WEST;
//        tileDetailsComboBox.addItem(NOT_AVAILABLE);
//        SpriteMap map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
//        for (String sprite : map.getKeys()) { tileDetailsComboBox.addItem(sprite); }
//        panel.add(tileDetailsComboBox, constraints);
//
//        constraints.gridy = 2;
//        tileDetailsCausesCollisionCheckbox.setText("causes Collisions?");
//        panel.add(tileDetailsCausesCollisionCheckbox, constraints);
//
//        selectedTileImageButton.setIcon(new ImageIcon(new BufferedImage(
//                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
//                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
//                BufferedImage.TYPE_INT_ARGB)));
//        constraints.gridy = 1;
//        constraints.gridx = 0;
//        constraints.weightx = 1;
//        constraints.gridheight = 2;
//        panel.add(selectedTileImageButton, constraints);
//        tileDetailsComboBox.addActionListener(e -> {
//            selectedTileImageString = tileDetailsComboBox.getItemAt(tileDetailsComboBox.getSelectedIndex());
//            if (selectedTileImageString == null) { return; }
//            SpriteSheet spriteSheet = map.get(selectedTileImageString);
//            if (spriteSheet == null) { return; }
//            selectedTileImage = spriteSheet.getSprite(0, 0);
//            selectedTileImageButton.setIcon(new ImageIcon(selectedTileImage));
//        });
//
//
//        // Row 4
//        constraints.gridy = 3;
//        constraints.gridx = 0;
//        constraints.weighty = 0;
//        constraints.weightx = 1;
//        constraints.gridheight = 1;
//        constraints.anchor = GridBagConstraints.WEST;
//        JButton heightLabel = new JButton("Height");
//        panel.add(heightLabel, constraints);
//
//        constraints.gridy = 3;
//        constraints.gridx = 1;
//        constraints.weightx = 0;
//        constraints.anchor = GridBagConstraints.EAST;
//        tileDetailsHeightTextField.addActionListener(e -> {
//            String data = e.getActionCommand();
//            if (StringUtils.containsNonDigits(data)) { return; }
//            selectedTileHeight = Integer.parseInt(data);
//        });
//        panel.add(tileDetailsHeightTextField, constraints);
//
//
//        // Row 5
//        constraints.gridy = 4;
//        constraints.gridx = 0;
//        constraints.weighty = 0;
//        constraints.weightx = 0;
//        constraints.anchor = GridBagConstraints.WEST;
//        JButton rowColumnLabel = new JButton("Location");
//        panel.add(rowColumnLabel, constraints);
//
//        constraints.gridy = 4;
//        constraints.gridx = 1;
//        constraints.weightx = 1;
//        constraints.anchor = GridBagConstraints.EAST;
//        panel.add(tileDetailsRowColumnField, constraints);
//
//        // Row 6
//        constraints.gridy = 5;
//        constraints.gridx = 0;
//        constraints.weighty = 0;
//        constraints.weightx = 0;
//        constraints.anchor = GridBagConstraints.WEST;
//        label = new JButton("Shadows");
//        panel.add(label, constraints);
//
//        constraints.gridy = 5;
//        constraints.gridx = 1;
//        constraints.weightx = 1;
//        constraints.anchor = GridBagConstraints.EAST;
//        panel.add(tileDetailsShadowsField, constraints);
//
//
//        return panel;
//    }

    private JPanel setupBrushSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // ROW 1
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 2;
        JButton label = new JButton("Brush Settings");
        label.setBorderPainted(false);
        label.setFocusPainted(false);
        panel.add(label, constraints);

        // ROW 2
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        JButton nameLabel = new JButton("Size");
        panel.add(nameLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        brushSettingsSizeField.addFocusListener(
                createTextFieldTemplate(brushSettingsSizeField, brushSettingsSizeField.getText()));
        brushSettingsSizeField.setText("1");
        panel.add(brushSettingsSizeField, constraints);


        // ROW 3
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        nameLabel = new JButton("Mode");
        panel.add(nameLabel, constraints);

        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        brushSettingsModeComboBox.addItem("Add");
        brushSettingsModeComboBox.addItem("Remove");
        brushSettingsModeComboBox.addItem("Fill");
        brushSettingsModeComboBox.addItem("Inspect");
        panel.add(brushSettingsModeComboBox, constraints);









        // ROW 4
//        constraints.gridx = 0;
//        constraints.gridy = 3;
//        constraints.weighty = 3;
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.anchor = GridBagConstraints.WEST;
//        constraints.gridwidth = 2;
//        label = new JButton("Tile Details");
//        label.setBorderPainted(false);
//        label.setFocusPainted(false);
//        panel.add(label, constraints);

        // Row 4
        constraints.gridy = 3;
        constraints.gridx = 1;
        constraints.weighty = 0;
        constraints.gridheight = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        tileDetailsComboBox.addItem(NOT_AVAILABLE);
        SpriteSheet map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
        List<String> keys = map.getKeys().stream().filter(e -> !e.contains(".png")).toList();
        for (String sprite : keys) { tileDetailsComboBox.addItem(sprite); }
        panel.add(tileDetailsComboBox, constraints);

        selectedTileImageButton.setIcon(new ImageIcon(new BufferedImage(
                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
                BufferedImage.TYPE_INT_ARGB)));
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.gridheight = 2;
        constraints.gridwidth = 1;
        panel.add(selectedTileImageButton, constraints);
        tileDetailsComboBox.addActionListener(e -> {
            selectedTileImageString = tileDetailsComboBox.getItemAt(tileDetailsComboBox.getSelectedIndex());
            if (selectedTileImageString == null) { return; }
            SpriteSheetRow spriteSheetRow = map.get(selectedTileImageString);
            if (spriteSheetRow == null) { return; }
            selectedTileImage = spriteSheetRow.getSprite(0, 0);
            selectedTileImageButton.setIcon(new ImageIcon(selectedTileImage));
        });



        // ROW 4
        constraints.gridy = 4;
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.gridheight = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        tileDetailsCausesCollisionCheckbox.setText("causes Collisions?");
        panel.add(tileDetailsCausesCollisionCheckbox, constraints);



        // Row 5
        constraints.gridy = 5;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
//        constraints.gridheight = 0;
        constraints.anchor = GridBagConstraints.WEST;
        JButton heightLabel = new JButton("Height");
        panel.add(heightLabel, constraints);

        constraints.gridy = 5;
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.EAST;
        tileDetailsHeightTextField.addActionListener(e -> {
            String data = e.getActionCommand();
            if (StringUtils.containsNonDigits(data)) { return; }
            selectedTileHeight = Integer.parseInt(data);
        });
        panel.add(tileDetailsHeightTextField, constraints);
//
//
        // Row 5
        constraints.gridy = 6;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        JButton rowColumnLabel = new JButton("Location");
        panel.add(rowColumnLabel, constraints);

        constraints.gridy = 6;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(tileDetailsRowColumnField, constraints);
//
        // Row 6
        constraints.gridy = 7;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Shadows");
        panel.add(label, constraints);

        constraints.gridy = 7;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(tileDetailsShadowsField, constraints);
//



        panel.setBackground(ColorPalette.getRandomColor());
        return panel;
    }



    private ExpandingPanels setupMapSettings() {
        ExpandingPanels panel = new ExpandingPanels();

        SpriteSheet map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);
        String[] labels = new String[]{ "Info", "Brush", "Floor", "Wall", "Liquid", "Obstacle", "Zoom" };

        for (String str : labels) {
            JPanel expandedPanelItem = new JPanel();
            expandedPanelItem.setLayout(new GridBagLayout());

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            JButton contextButton = new JButton();
            constraints.gridy = 0;
            constraints.gridx = 0;
            constraints.gridheight = 2;
            constraints.gridwidth = 2;
            expandedPanelItem.add(contextButton, constraints);

            constraints.gridx = 2;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            constraints.weightx = 1;
            constraints.weighty = 1;
            JButton label = new JButton("Randomize");
            expandedPanelItem.add(label, constraints);

            JComponent component = null;
            switch (str) {
                case "Info" -> {
                    contextButton.setVisible(false);
                    expandedPanelItem = new JPanel(new GridBagLayout());

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridwidth = 2;
                    gbc.gridheight = 2;
                    gbc.weightx = 1;
                    gbc.weighty = 1;
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    expandedPanelItem.add(label, gbc);

                    JTextField textField = new JTextField("Map Name");
                    textField.addFocusListener(createTextFieldTemplate(textField, textField.getText()));
                    gbc.gridy = 2;
                    expandedPanelItem.add(textField, gbc);

                    JComboBox<String> comboBox = getAndOrCreateConfig(SIZE_CONFIG);
                    comboBox.setToolTipText("Map Size");
                    comboBox.addItem(NOT_AVAILABLE);
                    comboBox.addItem("Small (8x16)");
                    comboBox.addItem("Medium (16x24)");
                    comboBox.addItem("Large (24x32)");
                    comboBox.addItem("Extra Large (32x40)");
                    comboBox.addActionListener(e -> {
                        String selection = (String) comboBox.getSelectedItem();
                        if (selection == null || selection.equals(NOT_AVAILABLE)) { return; }
                        String[] tokens = selection
                                .substring(selection.indexOf("(") + 1, selection.indexOf(")"))
                                .split("x");
                        tileMapRows = Integer.parseInt(tokens[0]);
                        tileMapColumns = Integer.parseInt(tokens[1]);
//
//                        getAndOrCreateConfig(TileMapBuilder.ROWS).addItem(String.valueOf(tileMapRows));
//                        getAndOrCreateConfig(TileMapBuilder.COLUMNS).addItem(String.valueOf(tileMapColumns));
                        setupGrid(tileMapRows, tileMapColumns);

                    });
                    gbc.gridy = 4;
                    expandedPanelItem.add(comboBox, gbc);

                    JComboBox<String> comboBox2 = getAndOrCreateConfig(TileMapBuilder.ALGORITHM);
                    comboBox2.addItem(NOT_AVAILABLE);
                    Map<String, TileMapOperations> operationsMap = TileMapBuilder.getTileMapBuilderMapping();
                    for (Map.Entry<String, TileMapOperations> entry : operationsMap.entrySet()) {
                        comboBox2.addItem(entry.getKey());
                    }
                    comboBox2.addActionListener(e -> actionListenerCheckToEnableGeneratorButton());
                    gbc.gridy = 6;
                    expandedPanelItem.add(comboBox2, gbc);

                    gbc.gridy = 8;
                    JButton create = new JButton("Generate Map");
                    create.addActionListener(e -> actionListenerGenerateGrid());
                    expandedPanelItem.add(create, gbc);

                    gbc.gridy = 10;
                    JButton openAllConfigs = new JButton("Open All Configs");
                    openAllConfigs.addActionListener(e -> panel.openAll());
                    expandedPanelItem.add(openAllConfigs, gbc);

                    label.addActionListener(e -> {
                        actionListenerRandomizeConfigs();
                        actionListenerCheckToEnableGeneratorButton();
                    });

                    mapSettingsGeneratorButton = create;
                    component = new JButton();
                    component.setVisible(false);
                }
                case "Brush" -> {
                    label.setVisible(false);
                    component = setupBrushSettings();
                    contextButton.setVisible(false);
                }
                case "Zoom" -> {
                    contextButton.setVisible(false);
                    JComboBox<String> comboBox = getAndOrCreateConfig(TileMapBuilder.ZOOM);
                    for (int i = 0; i < 101; i+= 20) { comboBox.addItem(String.valueOf(i)); }
                    label.addActionListener(e ->
                            comboBox.setSelectedIndex(random.nextInt(1, comboBox.getItemCount())));
                    component = comboBox;
                }
                case "Floor" -> {
                    JComboBox<String> comboBox = getAndOrCreateConfig(TileMapBuilder.FLOOR);
                    linkComboBoxAndLabel(map, TileMapBuilder.FLOOR, comboBox, label);
                    linkComboBoxAndImage(comboBox, map, contextButton);
                    component = comboBox;
                }
                case "Wall" -> {
                    JComboBox<String> comboBox = getAndOrCreateConfig(TileMapBuilder.WALL);
                    linkComboBoxAndLabel(map, TileMapBuilder.WALL, comboBox, label);
                    linkComboBoxAndImage(comboBox, map, contextButton);
                    component = comboBox;
                }
                case "Liquid" -> {
                    JComboBox<String> comboBox = getAndOrCreateConfig(TileMapBuilder.LIQUID);
                    linkComboBoxAndLabel(map, TileMapBuilder.LIQUID, comboBox, label);
                    linkComboBoxAndImage(comboBox, map, contextButton);
                    component = comboBox;
                }
                case "Obstacle" -> {

                    expandedPanelItem = new JPanel(new GridBagLayout());
                    String[] obstacles = new String[]{  ROUGH_TERRAIN, DESTROYABLE_BLOCKER };
                    GridBagConstraints gbc = new GridBagConstraints();

                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridwidth = 2;
                    gbc.gridheight = 2;
                    gbc.weightx = 1;
                    gbc.weighty = 1;
                    gbc.gridx = 0;
                    contextButton.setIcon(new ImageIcon(new BufferedImage(
                            Settings.getInstance().getSpriteSize(),
                            Settings.getInstance().getSpriteSize(),
                            BufferedImage.TYPE_INT_ARGB
                    )));
                    contextButton.setPreferredSize(new Dimension(controlPaneWidth / 2,
                            (int) contextButton.getPreferredSize().getHeight()));
                    expandedPanelItem.add(contextButton, gbc);
                    gbc.gridy++;
                    gbc.gridy++;


                    for (String obstacleType : obstacles) {

                        JButton comboboxLabel = new JButton(obstacleType);
                        comboboxLabel.setBorderPainted(false);
                        comboboxLabel.setFocusPainted(false);
                        gbc.gridy++;
                        gbc.fill = GridBagConstraints.HORIZONTAL;
                        gbc.gridwidth = 2;
                        gbc.gridheight = 1;
                        gbc.weightx = 1;
                        gbc.weighty = 1;
                        gbc.gridx = 0;
                        expandedPanelItem.add(comboboxLabel, gbc);

                        JComboBox<String> comboBox = getAndOrCreateConfig(obstacleType);
                        adjustWidth(comboBox, controlPaneWidth / 2);
                        gbc.gridy++;
                        gbc.fill = GridBagConstraints.HORIZONTAL;
                        gbc.gridwidth = 2;
                        gbc.gridheight = 1;
                        gbc.weightx = 1;
                        gbc.weighty = 1;
                        gbc.gridx = 0;
                        linkComboBoxAndLabel(map, obstacleType, comboBox, new JButton());
                        linkComboBoxAndImage(comboBox, map, contextButton);
                        expandedPanelItem.add(comboBox, gbc);
                    }
                    component = new JButton();
                    component.setVisible(false);
                }
                default -> {
                    continue;
                }
            }

            constraints.gridx = 2;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            constraints.weightx = 1;
            constraints.weighty = 1;

            expandedPanelItem.add(component, constraints);
            expandedPanelItem.setBackground(ColorPalette.getRandomColor());

            adjustWidth(component, controlPaneWidth / 2);
            adjustWidth(expandedPanelItem, controlPaneWidth / 2);

            panel.addPanel(str, expandedPanelItem);
        }

        return panel;
    }

    private void actionListenerGenerateGrid() {
        if (tileMapRows <= 0 || tileMapColumns <= 0) {
            return;
        }

        String toGenerate = (String) getAndOrCreateConfig(TileMapBuilder.ALGORITHM).getSelectedItem();
        if (toGenerate == null) {
            return;
        }

        Map<String, Object> generalConfigs = new HashMap<>();
        Map<String, Object> obstructConfigs = new HashMap<>();
        SpriteSheet map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITEMAP_FILEPATH);

        for (Map.Entry<String, JComboBox<String>> entry : mapSettingsConfigs.entrySet()) {
            String config = entry.getKey();
            JComboBox<String> comboBox = entry.getValue();
            int index = comboBox.getSelectedIndex();
            if (index <= 0) { continue; }

            String str = (String) comboBox.getSelectedItem();
            if (str == null) { continue; }
            if (StringUtils.isNumber(str)) {
                int val = Integer.parseInt(str);
                if (config.endsWith("obstruct")) {
                    obstructConfigs.put(config, val);
                } else {
                    generalConfigs.put(config, val);
                }
            } else {
                int spriteSheetIndex = map.indexOf(str);
                generalConfigs.put(config, spriteSheetIndex);

                if (config.endsWith("obstruct")) {
                    obstructConfigs.put(config, spriteSheetIndex);
                } else {
                    generalConfigs.put(config, spriteSheetIndex);
                }
            }
        }


        if (random.nextBoolean()) {
            List<String> list = map.endingWith(TileMapBuilder.EXIT_STRUCTURE);
            int exit = map.indexOf(list.get(random.nextInt(list.size())));
            generalConfigs.put(TileMapBuilder.EXIT_STRUCTURE, exit);
        }

        if (random.nextBoolean()) {
            List<String> list = map.endingWith(TileMapBuilder.ENTRANCE_STRUCTURE);
            int exit = map.indexOf(list.get(random.nextInt(list.size())));
            generalConfigs.put(TileMapBuilder.ENTRANCE_STRUCTURE, exit);
        }

        int zoomSliderValue = mapSettingsZoomSlider.getValue();
        float zoom = MathUtils.map(zoomSliderValue, 0, 100, 0, 1);
        generalConfigs.put(TileMapBuilder.ZOOM, zoom);
        generalConfigs.put(TileMapBuilder.ALGORITHM, toGenerate);
        generalConfigs.put(TileMapBuilder.ROWS, tileMapRows);
        generalConfigs.put(TileMapBuilder.COLUMNS, tileMapColumns);
        generalConfigs.put(TileMapBuilder.STRUCTURES, obstructConfigs);


        tileMap = TileMap.create(generalConfigs);
        setupGrid(tileMapRows, tileMapColumns,true);
        System.out.println("Created Tile Map");
    }

    private void linkComboBoxAndImage(JComboBox<String> comboBox, SpriteSheet map, JButton imager) {
        imager.setFocusPainted(false);
        imager.setBorderPainted(false);
        comboBox.addActionListener(e -> {
            String selected = comboBox.getItemAt(comboBox.getSelectedIndex());
            if (selected == null || selected.equals(NOT_AVAILABLE)) {
                imager.setIcon(new ImageIcon(new BufferedImage(Settings.getInstance().getSpriteSize(),
                        Settings.getInstance().getSpriteSize(), BufferedImage.TYPE_INT_ARGB)));
                return;
            }
            SpriteSheetRow spriteSheetRow = map.get(selected);
            if (spriteSheetRow == null) {
                imager.setIcon(new ImageIcon(new BufferedImage(Settings.getInstance().getSpriteSize(),
                        Settings.getInstance().getSpriteSize(), BufferedImage.TYPE_INT_ARGB)));
                return;
            }
            BufferedImage image = spriteSheetRow.getSprite(0, 0);
            imager.setIcon(new ImageIcon(image));
        });
    }

    private void linkComboBoxAndLabel(SpriteSheet map, String spritesLike, JComboBox<String> comboBox, JButton label) {
        List<String> list = map.endingWith(spritesLike);
        setupComboBox(list, comboBox);
        label.addActionListener(e -> comboBox.setSelectedIndex(random.nextInt(comboBox.getItemCount())));
    }

    private void adjustWidth(JComponent component, int width) {
        component.setPreferredSize(new Dimension(width, (int) component.getPreferredSize().getHeight()));
    }

    private void setupComboBox(List<String> list, JComboBox<String> mapSettingsComboBox) {
        mapSettingsComboBox.addItem(NOT_AVAILABLE);
        for (String sprite : list) {
            mapSettingsComboBox.addItem(sprite);
        }
        mapSettingsComboBox.addActionListener(e -> {
            String selectedItem = (String) mapSettingsComboBox.getSelectedItem();
            for (int i = 0; i < tileDetailsComboBox.getItemCount(); i++) {
                String tileDetailItem = tileDetailsComboBox.getItemAt(i);
                if (!tileDetailItem.equalsIgnoreCase(selectedItem)) {
                    continue;
                }
                tileDetailsComboBox.setSelectedIndex(i);
            }
            actionListenerCheckToEnableGeneratorButton();
        });
    }


    private FocusListener createTextFieldTemplate(JTextField field, String template) {
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(template)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(template);
                }
            }
        };
    }

    private void actionListenerRandomizeConfigs() {
        for (Map.Entry<String, JComboBox<String>> entry : mapSettingsConfigs.entrySet()) {
            entry.getValue().setSelectedIndex(random.nextInt(1, entry.getValue().getItemCount()));
        }
    }
    private void actionListenerCheckToEnableGeneratorButton() {
        mapSettingsGeneratorButton.setEnabled(false);
//        if (mapSettingsAlgorithmComboBox.getSelectedIndex() == 0) { return; }
//        if (mapSettingsWallComboBox.getSelectedIndex() == 0) { return; }
//        if (mapSettingsFloorComboBox.getSelectedIndex() == 0) { return; }
        Set<String> allowedNone = new HashSet<>(List.of(new String[]{ TileMapBuilder.ZOOM }));
        for (Map.Entry<String, JComboBox<String>> entry : mapSettingsConfigs.entrySet()) {
            if (entry.getKey().contains("obstruct")) { continue; }
            if (allowedNone.contains(entry.getKey())) { continue; }
            if (entry.getValue().getSelectedIndex() == 0) { return; }
        }

        mapSettingsGeneratorButton.setEnabled(true);
    }

    @Override
    public void update() {

    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return this;
    }
}
