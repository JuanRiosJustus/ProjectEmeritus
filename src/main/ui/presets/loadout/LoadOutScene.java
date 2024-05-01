package main.ui.presets.loadout;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.GameState;
import main.engine.Engine;
import main.engine.EngineScene;
import main.game.camera.Camera;
import main.game.components.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameView;
import main.game.map.base.TileMap;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.unit.UnitPool;
import main.input.InputController;
import main.ouput.UserSave;
import main.ui.panels.GamePanel;
import main.utils.RandomUtils;
import main.utils.StringUtils;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import java.util.SplittableRandom;

public class LoadOutScene extends EngineScene {

    private UnitSelectionListScene mUnitSelectionListScene = null;
    private MapScene mMapScene = null;
    private CurrentlyDeployedScene mCurrentlyDeployedScene = null;
    private OtherOptionsScene mOtherOptionsScene = null;
    private GameView gv = null;
    private GamePanel gp = null;
    private GameController gc = null;
    private Color primaryColor = Color.DARK_GRAY;
    private SplittableRandom mRandom = new SplittableRandom();



    public LoadOutScene(int width, int height) {
        super(width, height, LoadOutScene.class.getSimpleName());

        setLayout(new GridBagLayout());
        setSize(new Dimension(width, height));
        setBackground(primaryColor);

        mUnitSelectionListScene = new UnitSelectionListScene();
        int characterSelectPanelWidth = (int) (width * .25);
        int characterSelectPanelHeight = (int) (height * .75);
        mUnitSelectionListScene.setup(UserSave.getInstance().loadUnitsFromCollection(),
                 characterSelectPanelWidth, characterSelectPanelHeight);
        mUnitSelectionListScene.setBackground(primaryColor);

//        mMapScene = new MapScene();
//        int mapSceneWidth = (int) (width * .75);
//        int mapSceneHeight = (int) (height * .75);
//        mMapScene.setup(
//                TileMap.createRandom(15, 20),
//                new Rectangle(characterSelectPanelWidth, 0, mapSceneWidth, mapSceneHeight),
//                mUnitSelectionListScene
//        );


        int mapSceneWidth = width - characterSelectPanelWidth;
        int mapSceneHeight = characterSelectPanelHeight;

        int rows = 40, columns = 40;
        AssetPool.getInstance().resize(gc, mapSceneWidth / rows, mapSceneHeight / columns);
        gc = GameController.getInstance().createNewGame(mapSceneWidth, mapSceneHeight, rows, columns);
        gc.setGameModelState(GameState.FIT_TO_SCREEN, true);

        gp = gc.getNewGamePanel(mapSceneWidth, mapSceneHeight);

        // TODO should this be how we get user input?
        gp.addMouseMotionListener(InputController.getInstance().getMouse());
        gp.addMouseListener(InputController.getInstance().getMouse());
        gp.addKeyListener(InputController.getInstance().getKeyboard());
        gp.addMouseWheelListener(InputController.getInstance().getMouse());

        gp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String[] units = new String[]{ "Light Dragon", "Water Dragon", "Dark Dragon", "Fire Dragon", "Earth Dragon" };
                String uuid = UnitPool.getInstance().create(
                        units[mRandom.nextInt(units.length)],
                        RandomUtils.createRandomName(3, 6),
                        null,
                        false
                );
                Entity entity = UnitPool.getInstance().get(uuid);
                gc.addUnit(entity, RandomUtils.createRandomName(3, 6),
                        mRandom.nextInt(gc.getRows()), mRandom.nextInt(gc.getColumns()));
                System.out.println("yoo");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

//        gp.addMouseMotionListener(new MouseMotionListener() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
////                Camera.getInstance().set(new Vector(-e.getX(), -e.getY()));
////                System.out.println("Setting to " + Camera.getInstance().toString());
////                Camera.getInstance().set(new Vector(1000, 1000));
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
////                System.out.println(gc.mInputController.getMouse().isPressed() + " ?");
//                Camera.getInstance().set(new Vector3f(e.getX(), e.getY()));
////                System.out.println("Setting to " + Camera.getInstance().toString());
//            }
//        });
        gc.run();


        mCurrentlyDeployedScene = new CurrentlyDeployedScene();
        int currentlyDeployedSceneWidth = characterSelectPanelWidth;
        int currentlyDeployedSceneHeight = height - characterSelectPanelHeight;
        mCurrentlyDeployedScene.setup(3, 4, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight);
        mCurrentlyDeployedScene.setBackground(primaryColor);


        int optionsPanelWidth = width - currentlyDeployedSceneWidth;
        int optionsPanelHeight = currentlyDeployedSceneHeight;
        mOtherOptionsScene = new OtherOptionsScene();
        mOtherOptionsScene.setup(
                new String[]{ "Fight", "Retreat", "Units", "Rewards", "AI", "Clear"},
                 optionsPanelWidth, optionsPanelHeight,
                mMapScene
        );
        mOtherOptionsScene.setBackground(primaryColor);

        mOtherOptionsScene.getButton("Fight").addActionListener(e -> {
            GameController.getInstance().run();
            JsonObject placementObject = mMapScene.getUnitsAndPlacements();
            JsonObject tileMapJson = mMapScene.getTileMap().toJsonObject();
            GameController.getInstance().setMap(tileMapJson, placementObject);
            Engine.getInstance().getController().stage(GameController.getInstance());
        });

        int finalMapSceneWidth = mapSceneWidth;
        mOtherOptionsScene.getButton("Retreat").addActionListener(e -> {
            mCurrentlyDeployedScene.setup(3, 4, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight);
            mMapScene.setup(
                    TileMap.createRandom(15, 20),
                    new Rectangle(characterSelectPanelWidth, 0, finalMapSceneWidth, mapSceneHeight),
                    mUnitSelectionListScene
            );
        });

        mOtherOptionsScene.getButton("AI").addActionListener(e -> {
            String randomUnit = UnitPool.getInstance().getRandomUnit();
            Entity entity = UnitPool.getInstance().get(randomUnit);

            Random random = new Random();
            int randomRow =  random.nextInt(mMapScene.getTileMap().getRows());
            int randomColumn =  random.nextInt(mMapScene.getTileMap().getColumns());
            mMapScene.getTileMap().place(entity, randomRow, randomColumn);
        });


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1;
        gbc.weightx = 1;
//        mUnitSelectionListScene.setOpaque(true);
//        mUnitSelectionListScene.setBackground(Color.BLUE);
        add(mUnitSelectionListScene, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
//        gp.setOpaque(true);
//        gp.setBackground(Color.RED);
        add(gp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(mCurrentlyDeployedScene, gbc);
//        mCurrentlyDeployedScene.setOpaque(true);
//        mCurrentlyDeployedScene.setBackground(ColorPalette.GREEN);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(mOtherOptionsScene, gbc);
//        mOtherOptionsScene.setOpaque(true);
//        mOtherOptionsScene.setBackground(ColorPalette.getRandomColor());
    }

//    public LoadOutScene(int width, int height) {
//        super(width, height, LoadOutScene.class.getSimpleName());
//
//        setLayout(null);
//        setSize(new Dimension(width, height));
//        setBackground(primaryColor);
//
//        mUnitSelectionListScene = new UnitSelectionListScene();
//        int characterSelectPanelWidth = (int) (width * .25);
//        int characterSelectPanelHeight = (int) (height * .75);
//        mUnitSelectionListScene.setup(
//                UserSave.getInstance().loadUnitsFromCollection(),
//                new Rectangle(0, 0, characterSelectPanelWidth, characterSelectPanelHeight)
//        );
//        mUnitSelectionListScene.setBackground(primaryColor);
//
//        mMapScene = new MapScene();
//        int mapSceneWidth = (int) (width * .75);
//        int mapSceneHeight = (int) (height * .75);
//        mMapScene.setup(
//                TileMap.createRandom(15, 20),
//                new Rectangle(characterSelectPanelWidth, 0, mapSceneWidth, mapSceneHeight),
//                mUnitSelectionListScene
//        );
//        mMapScene.setBackground(Color.RED);
//
////        container.add
////        Settings.getInstance().set(Settings.DISPLAY_WIDTH, mapSceneWidth);
////        Settings.getInstance().set(Settings.DISPLAY_HEIGHT, mapSceneHeight);
////        Settings.getInstance().set(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE, 32);
////        AssetPool.getInstance().resize(Math.min(mapSceneWidth / 20, mapSceneHeight / 20));
//
//        mapSceneWidth = (int) (mapSceneWidth * .9);
//        gc = GameController.getInstance().getNewGameController(mapSceneWidth, mapSceneHeight);
//        gc.setMap(TileMap.createRandom(20, 20).toJsonObject(), null);
//        gp = gc.getView().getGamePanel(mapSceneWidth, mapSceneHeight);
//
//
//        gc.setGameModelState(GameState.FIT_TO_SCREEN, true);
//        AssetPool.getInstance().resize(mapSceneWidth / gc.getModel().getColumns(), mapSceneHeight / gc.getModel().getRows());
//
//        gp.setBounds(characterSelectPanelWidth + 600, 0, mapSceneWidth, mapSceneHeight);
////        gp.setOpaque(true);
////        gp.setBackground(Color.RED);
//
//        gp.addMouseMotionListener(new MouseMotionListener() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
////                Camera.getInstance().set(new Vector(-e.getX(), -e.getY()));
////                System.out.println("Setting to " + Camera.getInstance().toString());
////                Camera.getInstance().set(new Vector(1000, 1000));
////                gp.revalidate();
////                gp.repaint();
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                Camera.getInstance().set(new Vector(e.getX(), e.getY()));
////                System.out.println("Setting to " + Camera.getInstance().toString());
////                gp.revalidate();
////                gp.repaint();
//            }
//        });
//
//        int x = gp.getX();
//        int y = gp.getY();
//        System.out.println(x + " , " + y);
//
////        gc.update();
//        gc.run();
//
//
//        mCurrentlyDeployedScene = new CurrentlyDeployedScene();
//        int currentlyDeployedSceneWidth = (int) (width * .25);
//        int currentlyDeployedSceneHeight = (int) (height * .25);
//        mCurrentlyDeployedScene.setup(
//                3,
//                4,
//                new Rectangle(0, mapSceneHeight, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight)
//        );
//        mCurrentlyDeployedScene.setBackground(primaryColor);
//
//
//
//        int optionsPanelWidth = (int) (width * .75);
//        int optionsPanelHeight = (int) (height * .25);
//        mOtherOptionsScene = new OtherOptionsScene();
//        mOtherOptionsScene.setup(
//                new String[]{ "Fight", "Retreat", "Units", "Rewards", "AI", "Clear"},
//                new Rectangle(currentlyDeployedSceneWidth, mapSceneHeight, optionsPanelWidth, optionsPanelHeight),
//                mMapScene
//        );
//        mOtherOptionsScene.setBackground(primaryColor);
//
//        mOtherOptionsScene.getButton("Fight").addActionListener(e -> {
//            GameController.getInstance().run();
//            JsonObject placementObject = mMapScene.getUnitsAndPlacements();
//            JsonObject tileMapJson = mMapScene.getTileMap().toJsonObject();
//            GameController.getInstance().setMap(tileMapJson, placementObject);
//            Engine.getInstance().getController().stage(GameController.getInstance());
//        });
//
//        int finalMapSceneWidth = mapSceneWidth;
//        mOtherOptionsScene.getButton("Retreat").addActionListener(e -> {
//            mCurrentlyDeployedScene.setup(
//                    3,
//                    4,
//                    new Rectangle(0, mapSceneHeight, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight)
//            );
//            mMapScene.setup(
//                    TileMap.createRandom(15, 20),
//                    new Rectangle(characterSelectPanelWidth, 0, finalMapSceneWidth, mapSceneHeight),
//                    mUnitSelectionListScene
//            );
//        });
//
//        mOtherOptionsScene.getButton("AI").addActionListener(e -> {
//            String randomUnit = UnitPool.getInstance().getRandomUnit();
//            Entity entity = UnitPool.getInstance().get(randomUnit);
//
//            Random random = new Random();
//            int randomRow =  random.nextInt(mMapScene.getTileMap().getRows());
//            int randomColumn =  random.nextInt(mMapScene.getTileMap().getColumns());
//            mMapScene.getTileMap().place(entity, randomRow, randomColumn);
//        });
//
//
////        add(mUnitSelectionListScene);
//        add(gp);
////        add(mCurrentlyDeployedScene);
////        add(mOtherOptionsScene);
//    }

//    public LoadOutScene(int width, int height) {
//        super(width, height, LoadOutScene.class.getSimpleName());
//
//        setLayout(null);
//        setSize(new Dimension(width, height));
//        setBackground(primaryColor);
//
//        mUnitSelectionListScene = new UnitSelectionListScene();
//        int characterSelectPanelWidth = (int) (width * .25);
//        int characterSelectPanelHeight = (int) (height * .75);
//        mUnitSelectionListScene.setup(
//                UserSave.getInstance().loadUnitsFromCollection(),
//                new Rectangle(0, 0, characterSelectPanelWidth, characterSelectPanelHeight)
//        );
//        mUnitSelectionListScene.setBackground(primaryColor);
//
//        mMapScene = new MapScene();
//        int mapSceneWidth = (int) (width * .75);
//        int mapSceneHeight = (int) (height * .75);
//        mMapScene.setup(
//                TileMap.createRandom(15, 20),
//                new Rectangle(characterSelectPanelWidth, 0, mapSceneWidth, mapSceneHeight),
//                mUnitSelectionListScene
//        );
//        mMapScene.setBackground(Color.RED);
//
//        JPanel container = new JPanel();
//
//        gc = GameController.getInstance().getNewGameController(mapSceneWidth, mapSceneHeight);
//        gc.setMap(null, null);
//        gp = gc.getView().getGamePanel();
//        container.add(gp);
//        container.setBackground(Color.BLUE);
//
//        container.setBounds(0, 0, 400, 400);
//        Camera.getInstance().set(new Vector(100, 100));
//        gc.update();
//        gc.run();
//
//
//        mCurrentlyDeployedScene = new CurrentlyDeployedScene();
//        int currentlyDeployedSceneWidth = (int) (width * .25);
//        int currentlyDeployedSceneHeight = (int) (height * .25);
//        mCurrentlyDeployedScene.setup(
//                3,
//                4,
//                new Rectangle(0, mapSceneHeight, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight)
//        );
//        mCurrentlyDeployedScene.setBackground(primaryColor);
//
//
//
//        int optionsPanelWidth = (int) (width * .75);
//        int optionsPanelHeight = (int) (height * .25);
//        mOtherOptionsScene = new OtherOptionsScene();
//        mOtherOptionsScene.setBackground(Color.BLUE);
//        mOtherOptionsScene.setup(
//                new String[]{ "Fight", "Retreat", "Units", "Rewards", "AI", "Clear"},
//                new Rectangle(currentlyDeployedSceneWidth, mapSceneHeight, optionsPanelWidth, optionsPanelHeight),
//                mMapScene
//        );
//
//        mOtherOptionsScene.getButton("Fight").addActionListener(e -> {
//            GameController.getInstance().run();
//            JsonObject placementObject = mMapScene.getUnitsAndPlacements();
//            JsonObject tileMapJson = mMapScene.getTileMap().toJsonObject();
//            GameController.getInstance().setMap(tileMapJson, placementObject);
//            Engine.getInstance().getController().stage(GameController.getInstance());
//        });
//
//        mOtherOptionsScene.getButton("Retreat").addActionListener(e -> {
//            mCurrentlyDeployedScene.setup(
//                    3,
//                    4,
//                    new Rectangle(0, mapSceneHeight, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight)
//            );
//            mMapScene.setup(
//                    TileMap.createRandom(15, 20),
//                    new Rectangle(characterSelectPanelWidth, 0, mapSceneWidth, mapSceneHeight),
//                    mUnitSelectionListScene
//            );
//        });
//
//        mOtherOptionsScene.getButton("AI").addActionListener(e -> {
//            String randomUnit = UnitPool.getInstance().getRandomUnit();
//            Entity entity = UnitPool.getInstance().get(randomUnit);
//
//            Random random = new Random();
//            int randomRow =  random.nextInt(mMapScene.getTileMap().getRows());
//            int randomColumn =  random.nextInt(mMapScene.getTileMap().getColumns());
//            mMapScene.getTileMap().place(entity, randomRow, randomColumn);
//        });
//
//        add(mUnitSelectionListScene);
////        add(container);
////        add(gp);
//        add(mMapScene);
//        add(mCurrentlyDeployedScene);
//        add(mOtherOptionsScene);
////        setBackground(Color.WHITE);
////        add(button);
//    }


//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
////        gp.paintComponent(g);
//    }

//    @Override
//    public void paintComponent(Graphics g) {
////        gp.paintComponent(g);
//
////        g.dispose();
//        super.paintComponent(g);
//        gp.paintComponent(g);
////        gp.paintComponent(g);
////        mMapScene.paintComponents(g);
//
////        if (!isShowing()) { return; }
////        if (mCharacterPlacementPanel.hasTileMap()) {
////            mCharacterPlacementPanel.update();
////        }
////        g.drawImage(image, 0, 0, null);
////        gp.paintComponent(g);
////        gp.update(g);
////        gp.paintComponent(g);
////        revalidate();
////        repaint();
//    }

    @Override
    public void update() {
        mCurrentlyDeployedScene.update();
//        mMapScene.setSelected(mUnitSelectionListScene.getSelectedEntity());
//        mMapScene.setCurrentlyDeployedPane(mCurrentlyDeployedScene);
        mCurrentlyDeployedScene.setSelected(mUnitSelectionListScene.getSelectedEntity());
        gc.update();
    }

    @Override
    public void input() {}

    @Override
    public JPanel render() {
        return this;
    }

}
