package main.ui.presets.loadout;

import main.game.stores.factories.EntityFactory;
import main.graphics.GameUI;
import org.json.JSONObject;
import main.engine.Engine;
import main.engine.EngineScene;
import main.game.components.IdentityComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameView;
import main.game.state.UserSavedData;
import main.game.stores.pools.UnitDatabase;
import main.input.InputController;

import main.utils.RandomUtils;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class LoadOutScene extends EngineScene {

    private SummaryCardsPanel mSummaryCardsPanel = null;
    private CurrentlyDeployedScene mCurrentlyDeployedScene = null;
    private OtherOptionsScene mOtherOptionsScene = null;
    private GameView gv = null;
    private JPanel gp = null;
    private GameController gc = null;
    private Color primaryColor = Color.DARK_GRAY;
    private SplittableRandom mRandom = new SplittableRandom();
    private final String PLAYER_SPAWN = "PLAYER_SPAWN";
    private final String ENEMY_SPAWN = "ENEMY_SPAWN";


    public LoadOutScene(int width, int height) {
        super(width, height, LoadOutScene.class.getSimpleName());

//        setLayout(new GridBagLayout());
//        setSize(new Dimension(width, height));
//        setBackground(primaryColor);
        Color color = Color.DARK_GRAY;

        List<Entity> unitsFromSave = UserSavedData.getInstance().load(new String[]{ UserSavedData.UNITS })
                .toMap()
                .values()
                .stream()
                .map(e -> {
                    // Transform Objects into in game entities
                    JSONObject JSONObject = (JSONObject) e;
//                    String uuid = UnitPool.getInstance().create(JSONObject, true);
                    String uuid = "";
                    Entity entity = EntityFactory.getInstance().get(uuid);
                    return entity;
                })
                .toList();

        int summaryCardsPanelWidth = (int) (width * .25);
        int summaryCardsPanelHeight = (int) (height * .75);
        mSummaryCardsPanel = new SummaryCardsPanel();
        mSummaryCardsPanel.setup(unitsFromSave, summaryCardsPanelWidth, summaryCardsPanelHeight, color);
//        mUnitSelectionListScene.setC(primaryColor);

        int mapSceneWidth = width - summaryCardsPanelWidth;
        int mapSceneHeight = summaryCardsPanelHeight;
        int rows = 20, columns = 20;
        int spriteWidth = mapSceneWidth / rows;
        int spriteHeight = mapSceneHeight / columns;

        gc = GameController.getInstance().create(
                mapSceneWidth, mapSceneHeight,
                rows, columns,
                spriteWidth, spriteHeight
        );
//        gc = GameController.getInstance().create(mapSceneWidth, mapSceneHeight, rows, columns);
//        gc.setSettings(Settings.GAMEPLAY_MODE, Settings.GAMEPLAY_MODE_LOAD_OUT);
//        gc.getSettings().setGameMode(Settings.GAMEPLAY_MODE_UNIT_DEPLOYMENT);
//        gc.setSettings(Settings.GAMEPLAY_DEBUG_MODE, true);
//        gc.getSettings().setModeAsUnitDeploymentMode();

//        gc.setSpawnRegion(PLAYER_SPAWN, 0, 0, spawnWidth, gc.getRows());
//        gc.setSpawnRegion(ENEMY_SPAWN, 0, gc.getColumns() - spawnWidth, spawnWidth, gc.getRows());

        gp = gc.getGamePanel(mapSceneWidth, mapSceneHeight);

        // TODO should this be how we get user input?
        gp.addMouseMotionListener(InputController.getInstance().getMouse());
        gp.addMouseListener(InputController.getInstance().getMouse());
        gp.addMouseWheelListener(InputController.getInstance().getMouse());

        gp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Entity mousedTile = null; //gc.tryFetchingTileMousedAt();
                if (mousedTile == null) { return; }
                Tile tile = mousedTile.get(Tile.class);

                Entity selectedEntity = mSummaryCardsPanel.getSelectedEntity();
                if (selectedEntity == null) { return; }

                String tileData = "@" + tile.row + ", " + tile.column;
                boolean placed = gc.spawnUnit(
                        selectedEntity,
                        RandomUtils.createRandomName(3, 6),
                        tile.row,
                        tile.column
                );
                if (!placed) { return; }
                mCurrentlyDeployedScene.addUnitToDeploymentList(selectedEntity, mSummaryCardsPanel, tileData);
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
        int currentlyDeployedSceneWidth = summaryCardsPanelWidth;
        int currentlyDeployedSceneHeight = height - summaryCardsPanelHeight;
        mCurrentlyDeployedScene.setup(3, 4, currentlyDeployedSceneWidth, currentlyDeployedSceneHeight);
        mCurrentlyDeployedScene.setBackground(primaryColor);


        int optionsPanelWidth = width - currentlyDeployedSceneWidth;
        int optionsPanelHeight = currentlyDeployedSceneHeight;
        mOtherOptionsScene = new OtherOptionsScene();
        mOtherOptionsScene.setup(
                new String[]{ "Fight", "Retreat", "Units", "Rewards", "AI", "Clear"},
                 optionsPanelWidth, optionsPanelHeight,
                null
        );
        mOtherOptionsScene.setBackground(primaryColor);

        mOtherOptionsScene.getButton("Fight").addActionListener(e -> {

            GameController controller = GameController.getInstance().create(
                    mapSceneWidth, mapSceneHeight,
                    rows, columns,
                    spriteWidth, spriteHeight
            );
//            controller.setSettings(GameState.VIEW_SPRITE_WIDTH, 64);
//            controller.setSettings(GameState.VIEW_SPRITE_HEIGHT, 64);
//            controller.setSettings(GameState.GAMEPLAY_MODE, GameState.GAMEPLAY_MODE_REGULAR);

            // Copy whats on the screen, to the new game
//            JSONObject placementObject = gc.getUnitPlacementModel();
//            JSONObject tileMapJson = gc.getTileMapModel();
//            controller.setMap(tileMapJson, placementObject);

//            UserSavedData.getInstance().save("tileMaps", "tileMap_1", tileMapJson);
//            UserSavedData.getInstance().save("TestTileMapSave", tileMapJson, UserSavedData.TILEMAPS);

            controller.run();
            Engine.getInstance().getController().stage(controller);
        });

        mOtherOptionsScene.getButton("Retreat").addActionListener(e -> {
//            TileMap tileMap = TileMapFactory.create(rows, columns);
//            gc.setMap(tileMap.toJsonObject(), null);
//            gc.setSettings(Settings.GAMEPLAY_MODE, Settings.GAMEPLAY_MODE_LOAD_OUT);
//            gc.getSettings().setGameMode(Settings.GAMEPLAY_MODE_UNIT_DEPLOYMENT);
//            ....gc.getSettings().setModeAsUnitDeploymentMode();
//            ....gc.getSettings().setSpriteWidthAndHeight(mapSceneWidth / rows, mapSceneHeight / columns);
//            gc.setSettings(Settings.GAMEPLAY_SPRITE_WIDTH, mapSceneWidth / rows);
//            gc.setSettings(Settings.GAMEPLAY_SPRITE_HEIGHT, mapSceneHeight / columns);
//            gc.setSpawnRegion(PLAYER_SPAWN, 0, 0, spawnWidth, gc.getRows());
//            gc.setSpawnRegion(ENEMY_SPAWN, 0, gc.getColumns() - spawnWidth, spawnWidth, gc.getRows());
        });

        mOtherOptionsScene.getButton("AI").addActionListener(e -> {
            String randomUnit = "rrrr"; //UnitPool.getInstance().getRandomUnit();
            Entity entity = UnitDatabase.getInstance().get(randomUnit);
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);

//            List<Entity> nonPlayerSpawns = gc.get
            Random random = new Random();
            int randomRow =  random.nextInt(gc.getRows());
            int randomColumn =  random.nextInt(gc.getColumns());
            gc.spawnUnit(entity, "enemy", randomRow, randomColumn);

            JSONObject unitSave = UnitDatabase.getInstance().save(entity);
            UserSavedData.getInstance().save(identityComponent.getNickname(), unitSave, UserSavedData.UNITS);
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

//
//        setLayout(null);
//        setSize(new Dimension(width, height));
//        setBackground(primaryColor);
//
////        GridBagConstraints gbc = new GridBagConstraints();
////        gbc.gridx = 0;
////        gbc.gridy = 0;
////        gbc.anchor = GridBagConstraints.NORTHWEST;
//////        gbc.fill = GridBagConstraints.BOTH;
////        gbc.weighty = 1;
////        gbc.weightx = 1;
//        add(mSummaryCardsPanel);
//        mSummaryCardsPanel.setBounds(0, 0, summaryCardsPanelWidth, summaryCardsPanelHeight);

//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        add(gp, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        add(mCurrentlyDeployedScene, gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 1;
//        add(mOtherOptionsScene, gbc);






        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setSize(new Dimension(width, height));
        setBackground(primaryColor);


        JPanel topRow = new GameUI();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setPreferredSize(new Dimension(width, summaryCardsPanelHeight));
        topRow.setMinimumSize(topRow.getPreferredSize());
        topRow.setMaximumSize(topRow.getPreferredSize());
        topRow.add(mSummaryCardsPanel);
        topRow.add(gp);

        add(topRow);

        JPanel bottomRow = new GameUI();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        bottomRow.setPreferredSize(new Dimension(width, currentlyDeployedSceneHeight));
        bottomRow.setMinimumSize(bottomRow.getPreferredSize());
        bottomRow.setMaximumSize(bottomRow.getPreferredSize());
        bottomRow.add(mCurrentlyDeployedScene);
        bottomRow.add(mOtherOptionsScene);
        bottomRow.setOpaque(true);
        bottomRow.setBackground(Color.BLUE);
        add(bottomRow);

//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
////        gbc.anchor = GridBagConstraints.NORTHWEST;
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.weighty = 1;
//        gbc.weightx = 1;
//        add(mSummaryCardsPanel, gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        add(gp, gbc);

//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        add(mCurrentlyDeployedScene, gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 1;
//        add(mOtherOptionsScene, gbc);
    }

    @Override
    public void update() {
        mCurrentlyDeployedScene.update();
//        mMapScene.setSelected(mUnitSelectionListScene.getSelectedEntity());
//        mMapScene.setCurrentlyDeployedPane(mCurrentlyDeployedScene);
        mCurrentlyDeployedScene.setSelected(mSummaryCardsPanel.getSelectedEntity());
        gc.update();
    }

    @Override
    public void input() {}

    @Override
    public JPanel render() {
        return this;
    }

}
