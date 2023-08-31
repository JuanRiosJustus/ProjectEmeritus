package main.ui.presets;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.main.GameModel;
import main.game.stores.pools.AssetPool;
import main.graphics.JScene;
import main.graphics.SpriteMap;
import main.graphics.SpriteSheet;
import main.input.InputController;
import main.ui.screen.editor.GameEditorMainPanel;
import main.ui.screen.editor.GameEditorSidePanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class EditorScene extends JScene {
    public GameEditorMainPanel gameBoard;
    public GameEditorSidePanel editorPane;

    private JPanel rightHandPanel = new JPanel();

    public EditorScene(int width, int height) {
        super(width, height, EditorScene.class.getSimpleName());
        setLayout(null);

        int controlPaneWidth = width / 5, controlPaneHeight = height;
        JPanel controlPane = setupControlPanel(controlPaneWidth, controlPaneHeight);
        controlPane.setBounds(width - controlPaneWidth, 0, controlPaneWidth, controlPaneHeight);
        controlPane.setOpaque(true);
//        controlPane.setBackground(ColorPalette.getRandomColor());


        add(controlPane);
        setBackground(ColorPalette.getRandomColor());

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

    private JPanel setupControlPanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(width, height));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1;

        panel.add(setupGeneralMapSettings(), constraints);


        constraints.gridy = 1;
        panel.add(setupTileTraits(), constraints);

        return panel;
    }

    private JPanel setupTileTraits() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        JComboBox<String> tiles = new JComboBox<>();

//        SpriteSheet sheet = AssetPool.getInstance().getSheet(Constants.FLOORS_SPRITESHEET_FILEPATH);
        SpriteMap map = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITESHEET_FILEPATH);
        for (String sprite : map.getSheetNameKeys()) {
            tiles.addItem(sprite);
        }
        panel.add(tiles, constraints);

        panel.setBackground(ColorPalette.getRandomColor());

        return panel;
    }

    private JPanel setupGeneralMapSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());


        // ROW 1
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        JButton nameLabel = new JButton("Map Name");
        panel.add(nameLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        JTextField textField = new JTextField("Enter map name here");
        panel.add(textField, constraints);

        // ROW 2
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        nameLabel = new JButton("Map Size");
        panel.add(nameLabel, constraints);

        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        textField = new JTextField("[ROWS, COLUMNS]");
        panel.add(textField, constraints);


        // ROW 3 - SAVE
        constraints.gridy = 2;
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

    @Override
    public void jSceneUpdate(GameModel model) {
//        revalidate();
//        repaint();
    }
}
