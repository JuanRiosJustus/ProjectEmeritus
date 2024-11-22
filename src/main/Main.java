package main;

import main.game.main.GameConfigurations;
import main.engine.Engine;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.state.UserSavedData;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.action.ActionPool;
import main.game.stores.pools.unit.UnitPool;
//import main.logging.ELogger;
import main.ui.presets.editor.EditorScene;
import main.ui.presets.editor.GameScene;
import main.ui.presets.loadout.LoadOutScene;

import javax.swing.UIManager;
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
        ActionPool.getInstance();
        UnitPool.getInstance();
        UserSavedData.getInstance();

        // // SceneManager.instance().set(SceneManager.GAME_SCENE);
        // SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
        // SceneManager.instance().set(SceneManager.EDITOR_SCENE);

//        testLoadOutPanel();
        testMetaGame();
//        testEditorScene();
    }

    private static void testEditorScene() {
        int width = Engine.getInstance().getViewWidth();
        int height = Engine.getInstance().getViewHeight();
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


        GameConfigurations gameConfigurations = GameConfigurations.getDefaults()
                .setViewportWidth(screenWidth)
                .setViewportHeight(screenHeight);
        GameController gameController = GameController.create(gameConfigurations);


        gameController.setSettings(GameConfigurations.GAMEPLAY_MODE, GameConfigurations.GAMEPLAY_MODE_REGULAR);

        // Setup enemies
        Entity unitEntity = null;
        Random random = new Random();
        int unitsPerTeam = 5;
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = UnitPool.getInstance().getRandomUnit(false);
            unitEntity = UnitPool.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.placeUnit(unitEntity, "enemy", randomRow, randomColumn);
        }

        // Setup friendly
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = UnitPool.getInstance().getRandomUnit(false);
            unitEntity = UnitPool.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.placeUnit(unitEntity, "user", randomRow, randomColumn);
        }

        String randomUnit = UnitPool.getInstance().getRandomUnit(true);
        unitEntity = UnitPool.getInstance().get(randomUnit);

        int randomRow =  random.nextInt(gameController.getRows());
        int randomColumn =  random.nextInt(gameController.getColumns());
        boolean wasPlaced = gameController.placeUnit(unitEntity, "user", randomRow, randomColumn);
        while (!wasPlaced) {
            randomRow =  random.nextInt(gameController.getRows());
            randomColumn =  random.nextInt(gameController.getColumns());
            wasPlaced = gameController.placeUnit(unitEntity, "user", randomRow, randomColumn);
        }

//        Engine.getInstance().getController().stage(controller);
//        controller.run();


        Engine.getInstance().getController().stage(new GameScene(gameController, screenWidth, screenHeight));

        Engine.getInstance().getController().getView().setVisible(true);
        Engine.getInstance().run();
    }
}
