package main.ui.presets.loadout;

import main.engine.Engine;
import main.engine.EngineController;
import main.engine.EngineScene;
import main.game.main.GameController;
import main.game.map.base.TileMap;
import main.ouput.UserSave;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

public class LoadOutScene extends EngineScene {

    private UnitSelectionListScene mUnitSelectionListScene = null;
    private MapScene mMapScene = null;
    private CurrentlyDeployedScene mCurrentlyDeployedScene = null;
    private OtherOptionsScene mOtherOptionsScene = null;
    private Color primaryColor = Color.DARK_GRAY;

    public LoadOutScene(int width, int height) {
        super(width, height, LoadOutScene.class.getSimpleName());

        setLayout(null);
        setSize(new Dimension(width, height));
        setBackground(primaryColor);

        mUnitSelectionListScene = new UnitSelectionListScene();
        int characterSelectPanelWidth = (int) (width * .25);
        int characterSelectPanelHeight = (int) (height * .75);
        mUnitSelectionListScene.setup(
                UserSave.getInstance().loadUnitsFromCollection(),
                new Rectangle(0, 0, characterSelectPanelWidth, characterSelectPanelHeight)
        );
        mUnitSelectionListScene.setBackground(primaryColor);

        mMapScene = new MapScene();
        int mapSceneWidth = (int) (width * .75);
        int mapSceneHeight = (int) (height * .75);
        mMapScene.setup(
                TileMap.createRandom(15, 20),
                new Rectangle(characterSelectPanelWidth, 0, mapSceneWidth, mapSceneHeight),
                mUnitSelectionListScene
        );
        mMapScene.setBackground(primaryColor);

        mCurrentlyDeployedScene = new CurrentlyDeployedScene();
        int currentlyDeployedSceneWidth = (int) (width * .25);
        int currentlyDeployedSceneHeight = (int) (height * .25);
        mCurrentlyDeployedScene.setup(
                3,
                4,
                new Rectangle(0, mapSceneHeight, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight)
        );
        mCurrentlyDeployedScene.setBackground(primaryColor);



        int optionsPanelWidth = (int) (width * .75);
        int optionsPanelHeight = (int) (height * .25);
        mOtherOptionsScene = new OtherOptionsScene();
        mOtherOptionsScene.setBackground(Color.BLUE);
        mOtherOptionsScene.setup(
                new String[]{ "Fight", "Retreat", "Units", "Rewards", "AI", "Clear"},
                new Rectangle(currentlyDeployedSceneWidth, mapSceneHeight, optionsPanelWidth, optionsPanelHeight),
                mMapScene
        );

        mOtherOptionsScene.getButton("Fight").addActionListener(e -> {
            GameController.getInstance().run();
            GameController.getInstance().setMap(new TileMap(mMapScene.getTileMap().asJson()));
            Engine.getInstance().getController().stage(GameController.getInstance());
        });

        add(mUnitSelectionListScene);
        add(mMapScene);
        add(mCurrentlyDeployedScene);
        add(mOtherOptionsScene);
//        add(button);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        mMapScene.paintComponents(g);
//        if (!isShowing()) { return; }
//        if (mCharacterPlacementPanel.hasTileMap()) {
//            mCharacterPlacementPanel.update();
//        }
//        g.drawImage(image, 0, 0, null);
    }

    @Override
    public void update() {
        mCurrentlyDeployedScene.update();
        mMapScene.setSelected(mUnitSelectionListScene.getSelectedEntity());
        mMapScene.setCurrentlyDeployedPane(mCurrentlyDeployedScene);
        mCurrentlyDeployedScene.setSelected(mUnitSelectionListScene.getSelectedEntity());
    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return this;
    }

}
