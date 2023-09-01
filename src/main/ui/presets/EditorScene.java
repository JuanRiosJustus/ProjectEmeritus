package main.ui.presets;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.constants.Settings;
import main.engine.Engine;
import main.game.main.GameModel;
import main.game.stores.pools.AssetPool;
import main.graphics.JScene;
import main.graphics.SpriteSheetArray;
import main.input.InputController;
import main.ui.screen.editor.GameEditorMainPanel;
import main.ui.screen.editor.GameEditorSidePanel;
import main.utils.ImageUtils;
import main.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditorScene extends JScene {
    public GameEditorMainPanel gameBoard;
    public GameEditorSidePanel editorPane;
    private final JPanel gridPanel = new JPanel();
    private final JButton selectedTileButton = new JButton();
    private BufferedImage selectedTileImage;
    private final JComboBox<String> brushModeComboBox = new JComboBox<>();
    private int tileMapRows = 22, tileMapColumns = 33;
    private final Map<Integer, ImageIcon> tileImageIconCacheMap = new HashMap<>();

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

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;

        setupGrid(11, 20);
        return gridPanel;
    }

    private void setupGrid(int rows, int columns) {
        if (rows == 0 || columns == 0) { return; }

        GridBagConstraints constraints = new GridBagConstraints();
        gridPanel.removeAll();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;

        final boolean[] isBeingPressed = {false};
        int width = (int) gridPanel.getPreferredSize().getWidth();
        int height = (int) gridPanel.getPreferredSize().getHeight();
        ArrayList<EditorTile> tiles = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                EditorTile et = new EditorTile(row, column);
                et.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) { }
                    @Override
                    public void mousePressed(MouseEvent e) { isBeingPressed[0] = true; setIconAndRemoveText(); }
                    @Override
                    public void mouseReleased(MouseEvent e) { isBeingPressed[0] = false; }
                    @Override
                    public void mouseEntered(MouseEvent e) { setIconAndRemoveText(); }
                    @Override
                    public void mouseExited(MouseEvent e) {}
                    public void setIconAndRemoveText() {
                        if (selectedTileImage == null) { return; }
                        if (!isBeingPressed[0] || selectedTileButton.getIcon() == null) { return; }
                        int hash = Objects.hash(selectedTileImage, et.getWidth(), et.getHeight());
                        ImageIcon icon = tileImageIconCacheMap.get(hash);
                        if (icon == null) {
                            BufferedImage img = ImageUtils.getResizedImage(selectedTileImage, et.getWidth(), et.getHeight());
                            icon = new ImageIcon(img);
                            tileImageIconCacheMap.put(hash, icon);
                        }

//                        if (brushModeComboBox.getSelectedItem())
                        String selectedMode = (String) brushModeComboBox.getSelectedItem();
                        if (selectedMode == null) { return; }

                        if (selectedMode.equalsIgnoreCase("Add")) {
                            et.setIcon(icon);
                        } else if (selectedMode.equalsIgnoreCase("Remove")) {
                            et.setIcon(null);
                        } else if (selectedMode.equalsIgnoreCase("Fill")) {
                            for (EditorTile tile : tiles) { tile.setIcon(icon); }
                        }
                        et.setText("");
                    }
                });
                et.setOpaque(false);
//                et.setOpaque(true);
//                et.setBackground(ColorPalette.getRandomColor());
                et.setPreferredSize(new Dimension(width / columns, height / rows));
//                et.setFocusPainted(false);
//                et.setBorderPainted(false);
                constraints.gridy = row;
                constraints.gridx = column;
                tiles.add(et);

                gridPanel.add(et, constraints);
            }
        }
    }

    private static EditorTile getEditorTile(int row, int column, boolean[] isBeingPressed) {
        EditorTile et = new EditorTile(row, column);
        et.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) { isBeingPressed[0] = true; }
            @Override
            public void mouseReleased(MouseEvent e) { isBeingPressed[0] = false; }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isBeingPressed[0]) {
//                    et.setIcon;
                    System.out.println(et.row + ", " + et.column);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return et;
    }

    private JPanel setupControlPanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(width, height));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1;

        panel.add(setupMapSettings(), constraints);

        constraints.gridy = 1;
        panel.add(setupBrushSettings(), constraints);


        constraints.gridy = 2;
        panel.add(setupTileTraits(), constraints);

        return panel;
    }



    private JPanel setupTileTraits() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // ROW 1
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 2;
        JButton label = new JButton("Tile Details");
        label.setBorderPainted(false);
        label.setFocusPainted(false);
        panel.add(label, constraints);

        // Row 2
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        JComboBox<String> tiles = new JComboBox<>();
        SpriteSheetArray map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITESHEET_FILEPATH);
        for (String sprite : map.getKeys()) { tiles.addItem(sprite); }
        panel.add(tiles, constraints);

        constraints.gridy = 2;
        JCheckBox collisionCheckbox = new JCheckBox();
        collisionCheckbox.setText("is Walkable?");
        panel.add(collisionCheckbox, constraints);

        selectedTileButton.setIcon(new ImageIcon(new BufferedImage(
                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
                Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE),
                BufferedImage.TYPE_INT_ARGB)));
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.gridheight = 2;
        panel.add(selectedTileButton, constraints);
        tiles.addActionListener(e -> {
            selectedTileImage = map.get(tiles.getItemAt(tiles.getSelectedIndex())).getSprite(0, 0);
            selectedTileButton.setIcon(new ImageIcon(selectedTileImage));
        });

        panel.setBackground(ColorPalette.getRandomColor());

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
        JTextField textField = new JTextField("Enter Brush Size");
        textField.addFocusListener(createTextfieldTemplate(textField, textField.getText()));
        textField.setText("1");
        panel.add(textField, constraints);


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
        brushModeComboBox.addItem("Add");
        brushModeComboBox.addItem("Remove");
        brushModeComboBox.addItem("Fill");
        panel.add(brushModeComboBox, constraints);

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
        JButton nameLabel = new JButton("Name");
        panel.add(nameLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        JTextField textField = new JTextField("Enter Map Name");
        textField.addFocusListener(createTextfieldTemplate(textField, textField.getText()));
        panel.add(textField, constraints);

        // ROW 3
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        nameLabel = new JButton("Size");
        panel.add(nameLabel, constraints);

        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        textField = new JTextField("Enter two integers");
        textField.addFocusListener(createTextfieldTemplate(textField, textField.getText()));
        textField.addActionListener(e -> {
            String[] data = e.getActionCommand().split(" ");
            if (data.length != 2) { return; }
            if (!StringUtils.containsOnlyDigits(data[0]) || !StringUtils.containsOnlyDigits(data[1])) { return; }

            tileMapRows = Integer.parseInt(data[0]);
            tileMapColumns = Integer.parseInt(data[1]);

            setupGrid(tileMapRows, tileMapColumns);
        });
        panel.add(textField, constraints);


        // ROW 4
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.WEST;
        nameLabel = new JButton("SAVE");
        panel.add(nameLabel, constraints);

        panel.setBackground(ColorPalette.getRandomColor());

        return panel;
    }



    public void init(InputController controls){
        removeAll();
        setLayout(new BorderLayout());

        gameBoard = new GameEditorMainPanel(getWidth() - Constants.SIDE_BAR_WIDTH, getHeight());
        add(gameBoard, BorderLayout.CENTER);

        editorPane = new GameEditorSidePanel(Constants.SIDE_BAR_WIDTH, getHeight());
//        JLayeredPane jlp = new JLayeredPane();
        add(editorPane, BorderLayout.EAST);


//        JButton jb = new JButton("Test");
//        jb.setOpaque(true);
//        jb.setBounds(50, 50, 100, 100);
//
//
//        JLayeredPane jlp = new JLayeredPane();
//        jlp.add(m_board, JLayeredPane.DEFAULT_LAYER);
//        jlp.add(jb, JLayeredPane.MODAL_LAYER);
////
//        add(jlp, BorderLayout.CENTER);

        gameBoard.linkToScreen(controls, editorPane);
        revalidate();
        repaint();
    }

    private FocusListener createTextfieldTemplate(JTextField field, String template) {
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

    @Override
    public void jSceneUpdate(GameModel model) {
//        revalidate();
//        repaint();
    }
}
