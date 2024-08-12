package main.ui.presets.loadout;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Settings;
import main.engine.Engine;
import main.engine.EngineScene;
import main.game.components.Identity;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameView;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapFactory;
import main.game.state.UserSavedData;
import main.game.stores.pools.unit.UnitPool;
import main.input.InputController;
import main.ui.panels.GamePanel;
import main.utils.RandomUtils;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
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
    private final String PLAYER_SPAWN = "PLAYER_SPAWN";
    private final String ENEMY_SPAWN = "ENEMY_SPAWN";


    public LoadOutScene(int width, int height) {
        super(width, height, LoadOutScene.class.getSimpleName());

        setLayout(new GridBagLayout());
        setSize(new Dimension(width, height));
        setBackground(primaryColor);

        mUnitSelectionListScene = new UnitSelectionListScene();
        int characterSelectPanelWidth = (int) (width * .25);
        int characterSelectPanelHeight = (int) (height * .75);

        List<Entity> unitsFromSave = UserSavedData.getInstance().load(new String[]{ UserSavedData.UNITS })
                .values()
                .stream()
                .map(e -> {
                    // Transform Objects into in game entities
                    JsonObject jsonObject = (JsonObject) e;
                    String uuid = UnitPool.getInstance().create(jsonObject, true);
                    Entity entity = UnitPool.getInstance().get(uuid);
                    return entity;
                })
                .toList();

        mUnitSelectionListScene.setup(unitsFromSave, characterSelectPanelWidth, characterSelectPanelHeight);
        mUnitSelectionListScene.setBackground(primaryColor);

        int mapSceneWidth = width - characterSelectPanelWidth;
        int mapSceneHeight = characterSelectPanelHeight;

        int rows = 20, columns = 20;

        gc = GameController.getInstance().create();
//        gc = GameController.getInstance().create(mapSceneWidth, mapSceneHeight, rows, columns);
        gc.setSettings(Settings.GAMEPLAY_MODE, Settings.GAMEPLAY_MODE_LOAD_OUT);
//        gc.setSettings(Settings.GAMEPLAY_DEBUG_MODE, true);
        gc.setSettings(Settings.GAMEPLAY_SPRITE_WIDTH, mapSceneWidth / rows);
        gc.setSettings(Settings.GAMEPLAY_SPRITE_HEIGHT, mapSceneHeight / columns);

        int spawnWidth = Math.max(3, gc.getWidth());
//        gc.setSpawnRegion(PLAYER_SPAWN, 0, 0, spawnWidth, gc.getRows());
//        gc.setSpawnRegion(ENEMY_SPAWN, 0, gc.getColumns() - spawnWidth, spawnWidth, gc.getRows());

        gp = gc.getNewGamePanel(mapSceneWidth, mapSceneHeight);

        // TODO should this be how we get user input?
        gp.addMouseMotionListener(InputController.getInstance().getMouse());
        gp.addMouseListener(InputController.getInstance().getMouse());
        gp.addKeyListener(InputController.getInstance().getKeyboard());
        gp.addMouseWheelListener(InputController.getInstance().getMouse());

        gp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Entity mousedTile = gc.tryFetchingTileMousedAt();
                if (mousedTile == null) { return; }
                Tile tile = mousedTile.get(Tile.class);

                Entity selectedEntity = mUnitSelectionListScene.getSelectedEntity();
                if (selectedEntity == null) { return; }

                String tileData = "@" + tile.row + ", " + tile.column;
                boolean placed = gc.placeUnit(
                        selectedEntity,
                        RandomUtils.createRandomName(3, 6),
                        tile.row,
                        tile.column
                );
                if (!placed) { return; }
                mCurrentlyDeployedScene.addUnitToDeploymentList(selectedEntity, mUnitSelectionListScene, tileData);
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

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

            GameController controller = GameController.getInstance().create();
            controller.setSettings(Settings.GAMEPLAY_SPRITE_WIDTH, 64);
            controller.setSettings(Settings.GAMEPLAY_SPRITE_HEIGHT, 64);
            controller.setSettings(Settings.GAMEPLAY_MODE, Settings.GAMEPLAY_MODE_REGULAR);

            // Copy whats on the screen, to the new game
            JsonObject placementObject = gc.getUnitPlacementModel();
            JsonObject tileMapJson = gc.getTileMapModel();
            controller.setMap(tileMapJson, placementObject);

//            UserSavedData.getInstance().save("tileMaps", "tileMap_1", tileMapJson);
            UserSavedData.getInstance().save("TestTileMapSave", tileMapJson, UserSavedData.TILEMAPS);

            controller.run();
            Engine.getInstance().getController().stage(controller);
        });

        mOtherOptionsScene.getButton("Retreat").addActionListener(e -> {
            TileMap tileMap = TileMapFactory.create(rows, columns);
            gc.setMap(tileMap.toJsonObject(), null);
            gc.setSettings(Settings.GAMEPLAY_MODE, Settings.GAMEPLAY_MODE_LOAD_OUT);
            gc.setSettings(Settings.GAMEPLAY_SPRITE_WIDTH, mapSceneWidth / rows);
            gc.setSettings(Settings.GAMEPLAY_SPRITE_HEIGHT, mapSceneHeight / columns);
//            gc.setSpawnRegion(PLAYER_SPAWN, 0, 0, spawnWidth, gc.getRows());
//            gc.setSpawnRegion(ENEMY_SPAWN, 0, gc.getColumns() - spawnWidth, spawnWidth, gc.getRows());
        });

        mOtherOptionsScene.getButton("AI").addActionListener(e -> {
            String randomUnit = UnitPool.getInstance().getRandomUnit();
            Entity entity = UnitPool.getInstance().get(randomUnit);
            Identity identity = entity.get(Identity.class);

//            List<Entity> nonPlayerSpawns = gc.get
            Random random = new Random();
            int randomRow =  random.nextInt(gc.getRows());
            int randomColumn =  random.nextInt(gc.getColumns());
            gc.placeUnit(entity, "enemy", randomRow, randomColumn);

            JsonObject unitSave = UnitPool.getInstance().save(entity);
            UserSavedData.getInstance().save(identity.getName(), unitSave, UserSavedData.UNITS);
        });

//        JPanel row1 = new JPanel();
//        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
//
//        row1.add(mUnitSelectionListScene);
//        row1.add(gp);
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.NORTHWEST;
////        gbc.fill = GridBagConstraints.BOTH;
//        gbc.weighty = 1;
//        gbc.weightx = 1;
//        add(row1, gbc);




        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
//        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add(mUnitSelectionListScene, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(gp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(mCurrentlyDeployedScene, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(mOtherOptionsScene, gbc);
    }

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
