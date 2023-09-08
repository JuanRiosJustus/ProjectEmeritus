package main.ui.presets;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.constants.Settings;
import main.engine.Engine;
import main.game.components.Tile;
import main.game.main.GameModel;
import main.game.map.TileMap;
import main.game.map.TileMapFactory;
import main.game.map.builders.*;
import main.game.stores.pools.AssetPool;
import main.graphics.JScene;
import main.graphics.SpriteSheet;
import main.graphics.SpriteSheetMap;
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

public class EditorScene extends JScene {
    private final JPanel gridPanel = new JPanel();
    private final JButton selectedTileImageButton = new JButton();
    private final String NOT_AVAILABLE = "N/A";
    private String selectedTileImageString = "";
    private BufferedImage selectedTileImage;

    private final JComboBox<String> mapSettingsFloorComboBox = new JComboBox<>();
    private final JComboBox<String> mapSettingsWallComboBox = new JComboBox<>();
    private final JComboBox<String> mapSettingsLiquidComboBox = new JComboBox<>();
    private final JComboBox<String> mapSettingsGreaterObstructComboBox = new JComboBox<>();
    private final JComboBox<String> mapSettingsAlgorithmComboBox = new JComboBox<>();
    private final JSlider mapSettingsZoomSlider = new JSlider();
    private final JButton mapSettingsGeneratorButton = new JButton("Generate");

    private final JTextField tileDetailsRowColumnField = new JTextField();
    private final JTextField tileDetailsShadowsField = new JTextField();
    private final JTextField tileDetailsHeightTextField = new JTextField();

    private final JComboBox<String> brushSettingsModeComboBox = new JComboBox<>();
    private final JComboBox<String> tileDetailsComboBox = new JComboBox<>();
    private final SplittableRandom random = new SplittableRandom();


    private final JTextField brushSettingsTextField = new JTextField("Enter Brush Size");
    private int tileMapRows = 25, tileMapColumns = 40;
    private final JCheckBox tileDetailsCausesCollisionCheckbox = new JCheckBox();
    private int selectedTileHeight = -1;
    private TileMap tileMap;

    public EditorScene(int width, int height) {
        super(width, height - Engine.getInstance().getHeaderSize(), EditorScene.class.getSimpleName());
        setLayout(null);

        height = height - Engine.getInstance().getHeaderSize();


        int controlPaneWidth = width / 5, controlPaneHeight = height;
        JPanel controlPane = setupControlPanel(controlPaneWidth, controlPaneHeight);
        controlPane.setBounds(width - controlPaneWidth, 0, controlPaneWidth, controlPaneHeight);
        controlPane.setOpaque(true);
        controlPane.setBackground(ColorPalette.getRandomColor());
        add(controlPane);

        JPanel gridPane = setupGridPanel(width - controlPaneWidth, height);
        gridPane.setOpaque(true);
        gridPane.setBounds(0, 0, width - controlPaneWidth, height);
        gridPane.setBackground(ColorPalette.getRandomColor());
        add(gridPane);
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

    private JPanel setupGridPanel(int width, int height) {
        gridPanel.setLayout(new GridBagLayout());
        gridPanel.setPreferredSize(new Dimension(width, height));
        return setupGrid(11, 20, false);
    }

    private JPanel setupGrid(int rows, int columns) {
        return setupGrid(rows, columns, false);
    }
    private JPanel setupGrid(int rows, int columns, boolean isCustom) {
        if (rows <= 0 || columns <= 0) { return gridPanel; }

        GridBagConstraints constraints = new GridBagConstraints();
        gridPanel.removeAll();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;

        final boolean[] isBeingPressed = {false};
        int width = (int) gridPanel.getPreferredSize().getWidth();
        int height = (int) gridPanel.getPreferredSize().getHeight();
        ArrayList<EditorTile> editorTiles = new ArrayList<>();

        boolean shouldUseTileMap = isCustom && tileMap != null;
        if (shouldUseTileMap) {
            rows = tileMap.getRows();
            columns = tileMap.getColumns();
        }

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                EditorTile newEditorTile = new EditorTile(row, column);
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
                        SpriteSheetMap map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITESHEET_FILEPATH);
                        if (map == null) { return; }
                        int terrainIndex = map.indexOf(selectedTileImageString);

                        int liquidIndex = (selectedTileImageString.contains("liquid") ? terrainIndex : -1);

                        int structureIndex = (selectedTileImageString.contains("structure") ? terrainIndex : -1);

                        if (selectedMode.equalsIgnoreCase("Add")) {
                            if (selectedTileImageString.contains("floor")) {
                                tile.encode(isPath, tileHeight, terrainIndex, tile.getLiquid(), tile.getGreaterStructure(), -1);
                            } else if (selectedTileImageString.contains("wall")) {
                                tile.encode(isPath, tileHeight, terrainIndex, tile.getLiquid(), tile.getGreaterStructure(), -1);
                            } else if (selectedTileImageString.contains("liquid")) {
                                tile.encode(isPath, tileHeight, tile.getTerrain(), liquidIndex, tile.getGreaterStructure(), -1);
                            } else if (selectedTileImageString.contains("structure")) {
                                tile.encode(isPath, tileHeight, tile.getTerrain(), tile.getLiquid(), terrainIndex, -1);
                            }
                        } else if (selectedMode.equalsIgnoreCase("Remove")) {
                            finalNewEditorTile.reset();
                        } else if (selectedMode.equalsIgnoreCase("Fill")) {
                            for (EditorTile editorTile : editorTiles) {
                                tile = editorTile.getTile();
                                tile.encode(isPath, tileHeight, terrainIndex, liquidIndex, structureIndex, -1);
                            }
                        } else if (selectedMode.equalsIgnoreCase("Inspect")) {
                            tileDetailsHeightTextField.setText(tile.getHeight() + "");
                            tileDetailsShadowsField.setText(tile.shadowIds.size() + "");
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

    private JPanel setupControlPanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(width, height));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1;

        panel.add(setupTileDetails(), constraints);

        constraints.gridy = 1;
        panel.add(setupBrushSettings(), constraints);


        constraints.gridy = 2;
        panel.add(setupMapSettings(), constraints);

        return panel;
    }



    private JPanel setupTileDetails() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // ROW 1
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 2;
        JButton label = new JButton("Tile Details");
        label.setBorderPainted(false);
        label.setFocusPainted(false);
        panel.add(label, constraints);

        // Row 2 and 3
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        tileDetailsComboBox.addItem(NOT_AVAILABLE);
        SpriteSheetMap map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITESHEET_FILEPATH);
        for (String sprite : map.getKeys()) { tileDetailsComboBox.addItem(sprite); }
        panel.add(tileDetailsComboBox, constraints);

        constraints.gridy = 2;
        tileDetailsCausesCollisionCheckbox.setText("causes Collisions?");
        panel.add(tileDetailsCausesCollisionCheckbox, constraints);

        selectedTileImageButton.setIcon(new ImageIcon(new BufferedImage(
                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
                BufferedImage.TYPE_INT_ARGB)));
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.gridheight = 2;
        panel.add(selectedTileImageButton, constraints);
        tileDetailsComboBox.addActionListener(e -> {
            selectedTileImageString = tileDetailsComboBox.getItemAt(tileDetailsComboBox.getSelectedIndex());
            if (selectedTileImageString == null) { return; }
            SpriteSheet spriteSheet = map.get(selectedTileImageString);
            if (spriteSheet == null) { return; }
            selectedTileImage = spriteSheet.getSprite(0, 0);
            selectedTileImageButton.setIcon(new ImageIcon(selectedTileImage));
        });


        // Row 4
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        JButton heightLabel = new JButton("Height");
        panel.add(heightLabel, constraints);

        constraints.gridy = 3;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        tileDetailsHeightTextField.addActionListener(e -> {
            String data = e.getActionCommand();
            if (StringUtils.containsNonDigits(data)) { return; }
            selectedTileHeight = Integer.parseInt(data);
        });
        panel.add(tileDetailsHeightTextField, constraints);


        // Row 5
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        JButton rowColumnLabel = new JButton("Location");
        panel.add(rowColumnLabel, constraints);

        constraints.gridy = 4;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(tileDetailsRowColumnField, constraints);

        // Row 6
        constraints.gridy = 5;
        constraints.gridx = 0;
        constraints.weighty = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Shadows");
        panel.add(label, constraints);

        constraints.gridy = 5;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(tileDetailsShadowsField, constraints);


        return panel;
    }

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
        brushSettingsTextField.addFocusListener(
                createTextFieldTemplate(brushSettingsTextField, brushSettingsTextField.getText()));
        brushSettingsTextField.setText("1");
        panel.add(brushSettingsTextField, constraints);


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

        panel.setBackground(ColorPalette.getRandomColor());
        return panel;
    }

    private JPanel setupMapSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // ROW 1
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 2;
        JButton label = new JButton("Map Settings");
        label.setBorderPainted(false);
        label.setFocusPainted(false);
        panel.add(label, constraints);


        // ROW 2
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Name");
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        JTextField textField = new JTextField("Enter Map Name");
        textField.addFocusListener(createTextFieldTemplate(textField, textField.getText()));
        panel.add(textField, constraints);

        // ROW 3
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Size");
        panel.add(label, constraints);

        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        textField = new JTextField("Enter two integers");
        textField.addFocusListener(createTextFieldTemplate(textField, textField.getText()));
        textField.addActionListener(e -> {
            String[] data = e.getActionCommand().split(" ");
            if (data.length != 2) { return; }
            if (StringUtils.containsNonDigits(data[0]) || StringUtils.containsNonDigits(data[1])) { return; }

            tileMapRows = Integer.parseInt(data[0]);
            tileMapColumns = Integer.parseInt(data[1]);

            setupGrid(tileMapRows, tileMapColumns);
        });
        panel.add(textField, constraints);


        // ROW 4
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        JButton mapSettingsAlgorithmButton = new JButton("Generator");
        panel.add(mapSettingsAlgorithmButton, constraints);
        mapSettingsAlgorithmButton.addActionListener(e -> {
            mapSettingsAlgorithmComboBox.setSelectedIndex(random.nextInt(1, mapSettingsAlgorithmComboBox.getItemCount()));
            mapSettingsFloorComboBox.setSelectedIndex(random.nextInt(1, mapSettingsFloorComboBox.getItemCount()));
            mapSettingsWallComboBox.setSelectedIndex(random.nextInt(1, mapSettingsWallComboBox.getItemCount()));
            mapSettingsLiquidComboBox.setSelectedIndex(random.nextInt(mapSettingsLiquidComboBox.getItemCount()));
            mapSettingsGreaterObstructComboBox.setSelectedIndex(random.nextInt(mapSettingsGreaterObstructComboBox.getItemCount()));
            mapSettingsZoomSlider.setValue(random.nextInt(100));
            checkToEnableGeneratorButton();
        });

        constraints.gridy = 4;
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        mapSettingsAlgorithmComboBox.setPrototypeDisplayValue("THIS IS THE LARGEST");
        mapSettingsAlgorithmComboBox.addItem(NOT_AVAILABLE);
        for (TileMapFactory.Algorithm algorithm : TileMapFactory.Algorithm.values()) {
            mapSettingsAlgorithmComboBox.addItem(algorithm.name());
        }
        mapSettingsAlgorithmComboBox.addActionListener(e -> {
            String txt = (String) mapSettingsAlgorithmComboBox.getSelectedItem();
            if (txt == null) { return; }
            mapSettingsFloorComboBox.setEnabled(!txt.equalsIgnoreCase(NOT_AVAILABLE));
            mapSettingsWallComboBox.setEnabled(!txt.equalsIgnoreCase(NOT_AVAILABLE));
            mapSettingsLiquidComboBox.setEnabled(!txt.equalsIgnoreCase(NOT_AVAILABLE));
            mapSettingsGreaterObstructComboBox.setEnabled(!txt.equalsIgnoreCase(NOT_AVAILABLE));
            mapSettingsZoomSlider.setEnabled(!txt.equalsIgnoreCase(NOT_AVAILABLE));

            checkToEnableGeneratorButton();
        });
        panel.add(mapSettingsAlgorithmComboBox, constraints);

        // ROW 5
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Floor");
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;

        SpriteSheetMap map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITESHEET_FILEPATH);
        List<String> list = map.getKeysEndingWith("floor");
        setupComboBox(list, mapSettingsFloorComboBox);
        mapSettingsFloorComboBox.setEnabled(false);
        panel.add(mapSettingsFloorComboBox, constraints);

        label.addActionListener(e ->
                mapSettingsFloorComboBox.setSelectedIndex(random.nextInt(mapSettingsFloorComboBox.getItemCount())));


        // ROW 6
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Wall");
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;

        list = map.getKeysEndingWith("wall");
        setupComboBox(list, mapSettingsWallComboBox);
        mapSettingsWallComboBox.setEnabled(false);
        panel.add(mapSettingsWallComboBox, constraints);


        label.addActionListener(e ->
                mapSettingsWallComboBox.setSelectedIndex(random.nextInt(mapSettingsWallComboBox.getItemCount())));


        // ROW 7 liquid settings
        constraints.gridy = 7;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Liquid");
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;

        list = map.getKeysEndingWith(Tile.LIQUID);
        setupComboBox(list, mapSettingsLiquidComboBox);
        mapSettingsLiquidComboBox.setEnabled(false);
        panel.add(mapSettingsLiquidComboBox, constraints);

        label.addActionListener(e ->
                mapSettingsLiquidComboBox.setSelectedIndex(random.nextInt(mapSettingsLiquidComboBox.getItemCount())));

        // ROW 8 structure settings
        constraints.gridy = 8;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Greater Obstructs");
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;

        list = map.getKeysEndingWith(Tile.GREATER_STRUCTURE);
        setupComboBox(list, mapSettingsGreaterObstructComboBox);
        mapSettingsGreaterObstructComboBox.setEnabled(false);
        panel.add(mapSettingsGreaterObstructComboBox, constraints);


        label.addActionListener(e ->
                mapSettingsGreaterObstructComboBox.setSelectedIndex(
                        random.nextInt(mapSettingsGreaterObstructComboBox.getItemCount())));

        // ROW 9 height Settings
        constraints.gridy = 9;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("Zoom");
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        mapSettingsZoomSlider.setEnabled(false);
        panel.add(mapSettingsZoomSlider, constraints);

        // ROW 9
        constraints.gridy = 10;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        mapSettingsGeneratorButton.setEnabled(false);
        mapSettingsGeneratorButton.addActionListener(e -> {
            if (tileMapRows <= 0 || tileMapColumns <= 0) { return; }

            TileMapBuilder builder;
            String toGenerate = (String) mapSettingsAlgorithmComboBox.getSelectedItem();
            if (toGenerate == null) { return; }

            TileMapFactory.TileMapFactoryConfigs configs = new TileMapFactory.TileMapFactoryConfigs();

            int zoomSliderValue = mapSettingsZoomSlider.getValue();
            float zoom = MathUtils.map(zoomSliderValue, 0, 100, 0, 1);

            int wall = -1, floor = -1, liquid = -1, structure = -1;
            if (mapSettingsWallComboBox.getSelectedIndex() > 0) {
                wall = map.indexOf((String)mapSettingsWallComboBox.getSelectedItem());
            }
            if (mapSettingsFloorComboBox.getSelectedIndex() > 0) {
                floor = map.indexOf((String)mapSettingsFloorComboBox.getSelectedItem());
            }
            if (mapSettingsLiquidComboBox.getSelectedIndex() > 0) {
                liquid = map.indexOf((String)mapSettingsLiquidComboBox.getSelectedItem());
            }
            if (mapSettingsGreaterObstructComboBox.getSelectedIndex() > 0) {
                structure = map.indexOf((String) mapSettingsGreaterObstructComboBox.getSelectedItem());
            }
            configs.rows = tileMapRows;
            configs.columns = tileMapColumns;
            configs.wall = wall;
            configs.floor = floor;
            configs.liquid = liquid;
            configs.greaterStructure = structure;
            configs.zoom = zoom;
            configs.seed = random.nextLong();
            configs.algorithm = TileMapFactory.Algorithm.valueOf(toGenerate);

            tileMap = TileMapFactory.create(configs);

            setupGrid(tileMapRows, tileMapColumns, true);
            System.out.println("Created Tile Map");
        });
        panel.add(mapSettingsGeneratorButton, constraints);

        constraints.gridy = 10;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        label = new JButton("SAVE");
        label.addActionListener(e -> {
            if (tileMap == null) { return; }
            try {
//                UserSavedData.getInstance().saveObject(tileMap);
                tileMap.saveToFile();
                System.out.println("SaVED!");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        panel.add(label, constraints);

        panel.setBackground(ColorPalette.getRandomColor());

        return panel;
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
            checkToEnableGeneratorButton();
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

    private void checkToEnableGeneratorButton() {
        mapSettingsGeneratorButton.setEnabled(false);
        if (mapSettingsAlgorithmComboBox.getSelectedIndex() == 0) { return; }
        if (mapSettingsWallComboBox.getSelectedIndex() == 0) { return; }
        if (mapSettingsFloorComboBox.getSelectedIndex() == 0) { return; }
        mapSettingsGeneratorButton.setEnabled(true);
    }

    @Override
    public void jSceneUpdate(GameModel model) {
//        revalidate();
//        repaint();
    }
}
