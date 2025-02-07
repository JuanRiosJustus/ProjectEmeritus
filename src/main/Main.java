package main;

import main.engine.Engine;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameGenerationConfigs;
import main.game.state.UserSavedData;
import main.game.stores.JsonObjectDatabase;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.action.ActionDatabase;
import main.game.stores.pools.UnitDatabase;
//import main.logging.ELogger;
import main.ui.presets.editor.EditorScene;
import main.ui.presets.editor.GameScene;
import main.ui.presets.loadout.LoadOutScene;

import javax.swing.UIManager;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Main {


    public static void main(String[] args) throws Exception {

//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
//        for (UIManager.LookAndFeelInfo look : looks) {
//            System.out.println(look.getClassName());
//        }

//        UIManager.put("ProgressBar.repaintInterval", 100);
//        UIManager.put("ProgressBar.border", com.formdev.flatlaf.themes.FlatMacDarkLaf
//                BorderFactory.createLineBorder(Color.blue, 2));

//         UserSavedData.getInstance().load(Constants.USER_SAVE_DIRECTORY + "ExampleSaveData.json");
//         UserSavedData.getInstance().createOrRead("TestFilePath.json");
//         UserSavedData.getInstance().update();


        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        // Loads the resources before game has started
        AssetPool.getInstance();
        FontPool.getInstance();
        ActionDatabase.getInstance();
        UnitDatabase.getInstance();
        UserSavedData.getInstance();
        JsonObjectDatabase.getInstance();






        // // SceneManager.instance().set(SceneManager.GAME_SCENE);
        // SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
        // SceneManager.instance().set(SceneManager.EDITOR_SCENE);

//        testLoadOutPanel();


        testMetaGame();
//        testMetaGameFullScreen();
//        testEditorScene();
//        testEditorSceneFullScreen();
    }

    private static void testEditorScene(int width, int height) {
        Engine.getInstance().getController().stage(new EditorScene(width, height));
        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }

    private static void testEditorScene() {
        int width = Engine.getInstance().getViewWidth();
        int height = Engine.getInstance().getViewHeight();
        Engine.getInstance().getController().stage(new EditorScene(width, height));
        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }

    private static void testEditorSceneFullScreen() {
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        Engine.getInstance().getController().setSize(width, height);
        Engine.getInstance().getController().stage(new EditorScene(width, height));
        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }

    private static void testLoadOutPanel() {
        int width = Engine.getInstance().getViewWidth();
        int height = Engine.getInstance().getViewHeight();
        Engine.getInstance().getController().stage(new LoadOutScene(width, height));
        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }

    private static void testMetaGame() {
//        int screenWidth = 1600;
//        int screenHeight = 1000;
        int screenWidth = 1400;
        int screenHeight = 850;
//        int screenWidth = 1280;
//        int screenHeight = 720;
//        int rows = screenHeight / 64;
//        int columns = screenWidth / 64;
//        int rows = 10;
//        int columns = 10;

        Engine.getInstance().getController().setSize(screenWidth, screenHeight);

        Map<String, String> bucket = AssetPool.getInstance().getBucketV2("floor_tiles");
        Map<String, String> bucket2 = AssetPool.getInstance().getBucketV2("structures");

        GameGenerationConfigs configs = GameGenerationConfigs.getDefaults()
                .setRows(10)
                .setColumns(10)
                .setStartingViewportWidth(screenWidth)
                .setStartingViewportHeight(screenHeight)
                .setTerrainAsset(new ArrayList<>(bucket.keySet()).get(new Random().nextInt(bucket.size())))
                .setStructureAssets(bucket2.keySet().stream().toList().stream().findFirst().stream().toList());
        GameController gameController = GameController.create(configs);


//        gameController.setSettings(GameStateV2.GAMEPLAY_MODE, GameStateV2.GAMEPLAY_MODE_REGULAR);

        // Setup enemies
        Entity unitEntity = null;
        Random random = new Random();
        int unitsPerTeam = 5;
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = EntityStore.getInstance().getOrCreateUnit(false); //UnitPool.getInstance().getRandomUnit(false);
            unitEntity = EntityStore.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.spawnUnit(unitEntity, "enemy", randomRow, randomColumn);
        }

        // Setup friendly
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = EntityStore.getInstance().getOrCreateUnit(true); //UnitPool.getInstance().getRandomUnit(true);
            unitEntity = EntityStore.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        }

        String randomUnit = EntityStore.getInstance().getOrCreateUnit(true);
        unitEntity = EntityStore.getInstance().get(randomUnit);

        int randomRow =  random.nextInt(gameController.getRows());
        int randomColumn =  random.nextInt(gameController.getColumns());
        boolean wasPlaced = gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        while (!wasPlaced) {
            randomRow =  random.nextInt(gameController.getRows());
            randomColumn =  random.nextInt(gameController.getColumns());
            wasPlaced = gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        }

//        Engine.getInstance().getController().stage(controller);
//        controller.run();


        Engine.getInstance().getController().stage(new GameScene(gameController, screenWidth, screenHeight));

        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }

    private static void testMetaGameFullScreen() {
//        int screenWidth = 1600;
//        int screenHeight = 1000;
//        int screenWidth = 1400;
//        int screenHeight = 850;
//        int screenWidth = 1280;
//        int screenHeight = 720;
//        int rows = screenHeight / 64;
//        int columns = screenWidth / 64;
//        int rows = 10;
//        int columns = 10;

        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        Engine.getInstance().getController().setSize(width, height);

        Map<String, String> bucket = AssetPool.getInstance().getBucketV2("floor_tiles");
        Map<String, String> bucket2 = AssetPool.getInstance().getBucketV2("structures");

        GameGenerationConfigs configs = GameGenerationConfigs.getDefaults()
                .setRows(10)
                .setColumns(10)
                .setStartingViewportWidth(width)
                .setStartingViewportHeight(height)
                .setStartingSpriteWidth(96)
                .setStartingSpriteHeight(96)
                .setTerrainAsset(new ArrayList<>(bucket.keySet()).get(new Random().nextInt(bucket.size())))
                .setStructureAssets(bucket2.keySet().stream().toList().stream().findFirst().stream().toList());
        GameController gameController = GameController.create(configs);


//        gameController.setSettings(GameStateV2.GAMEPLAY_MODE, GameStateV2.GAMEPLAY_MODE_REGULAR);

        // Setup enemies
        Entity unitEntity = null;
        Random random = new Random();
        int unitsPerTeam = 5;
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = EntityStore.getInstance().getOrCreateUnit(false); //UnitPool.getInstance().getRandomUnit(false);
            unitEntity = EntityStore.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.spawnUnit(unitEntity, "enemy", randomRow, randomColumn);
        }

        // Setup friendly
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = EntityStore.getInstance().getOrCreateUnit(true); //UnitPool.getInstance().getRandomUnit(true);
            unitEntity = EntityStore.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        }

        String randomUnit = EntityStore.getInstance().getOrCreateUnit(true);
        unitEntity = EntityStore.getInstance().get(randomUnit);

        int randomRow =  random.nextInt(gameController.getRows());
        int randomColumn =  random.nextInt(gameController.getColumns());
        boolean wasPlaced = gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        while (!wasPlaced) {
            randomRow =  random.nextInt(gameController.getRows());
            randomColumn =  random.nextInt(gameController.getColumns());
            wasPlaced = gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        }

//        Engine.getInstance().getController().stage(controller);
//        controller.run();


        Engine.getInstance().getController().stage(new GameScene(gameController, width, height - Engine.getInstance().getHeaderSize()));

        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }
}
