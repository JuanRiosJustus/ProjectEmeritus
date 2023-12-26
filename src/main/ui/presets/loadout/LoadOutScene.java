package main.ui.presets.loadout;

import main.constants.ColorPalette;
import main.engine.EngineScene;
import main.game.map.base.TileMap;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;

public class LoadOutScene extends EngineScene {

    private JPanel mCharacterSelectPanel = null;
    private MapScene mCharacterPlacementPanel = null;
    private JPanel mContextMenuPanel = null;

    public LoadOutScene(int width, int height) {
        super(width, height, LoadOutScene.class.getSimpleName());

        setLayout(null);
        setSize(new Dimension(width, height));
        setBackground(ColorPalette.getRandomColor());

        mCharacterSelectPanel = new JPanel();
        mCharacterSelectPanel.setBackground(ColorPalette.BLUE);
        int characterSelectPanelWidth = (int) (width * .25);
        int characterSelectPanelHeight = (int) (height * .8);
        mCharacterSelectPanel.setBounds(0, 0, characterSelectPanelWidth, characterSelectPanelHeight) ;

        mCharacterPlacementPanel = new MapScene();
        int characterPlacementPanelWidth = (int) (width * .75);
        int characterPlacementPanelHeight = (int) (height * .8);
        mCharacterPlacementPanel.setBounds(characterSelectPanelWidth, 0,
                characterPlacementPanelWidth, characterPlacementPanelHeight);
        // Maybe this should be set elsewhere
        mCharacterPlacementPanel.setTileMap(
                TileMap.createRandom(15, 20),
                characterPlacementPanelWidth, characterPlacementPanelHeight);

        mContextMenuPanel = new JPanel();
        mContextMenuPanel.setBackground(ColorPalette.GREEN);
        int contextPanelWidth = (width);
        int contextPanelHeight = (int) (height * .2);
        mContextMenuPanel.setBounds(0, characterPlacementPanelHeight, contextPanelWidth, contextPanelHeight);

        add(mCharacterSelectPanel);
        add(mCharacterPlacementPanel);
        add(mContextMenuPanel);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        mCharacterPlacementPanel.paintComponents(g);
//        if (!isShowing()) { return; }
//        if (mCharacterPlacementPanel.hasTileMap()) {
//            mCharacterPlacementPanel.update();
//        }
//        g.drawImage(image, 0, 0, null);
    }

    @Override
    public void update() {
        if (mCharacterPlacementPanel.hasTileMap()) {
            mCharacterPlacementPanel.update();
        }
    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return this;
    }

}
