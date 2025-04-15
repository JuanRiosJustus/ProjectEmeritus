package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.constants.Constants;
import main.engine.EngineController;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.AbilityTable;
import main.game.stores.UnitTable;
import main.state.UserSaveStateManager;
import main.logging.EmeritusLogger;
import main.game.stores.EntityStore;
import main.ui.scenes.MapEditorScene;
import main.ui.scenes.MenuScene;

import java.util.Random;

public class Main extends Application {
    public static void main(String[] args) { launch(args); }



    @Override
    public void start(Stage ignored) {

        UnitTable.getInstance();
        AbilityTable.getInstance();
        UserSaveStateManager.getInstance();
//        String id = EntityStore.getInstance().getOrCreateUnit(null, "Light_Dragon", "Himothy", true);
//        JSONObject unitData = EntityStore.getInstance().getUnitSaveData(id);


        EngineController engineController = EngineController.getInstance();

        GameController gameController = GameController.create(8, 8, 1500, 950);
        setup(gameController, 5);


        engineController.stage(Constants.MENU_SCENE, new MenuScene(1500, 950));
//        engineController.stage(Constants.MAP_EDITOR_SCENE, new MapEditorScene(1500, 950));
        engineController.stage(Constants.GAME_SCENE, gameController);


//        engineController.getStage().set

        engineController.setOnCloseRequest(t -> {
            EmeritusLogger logger = EmeritusLogger.create(Main.class);
            logger.flush();
            Platform.exit();
            System.exit(0);
        });
    }


//    @Override
//    public void start(Stage primaryStage) {
//
//
//        GameController gameController = testMetaGame(1500, 950);
//        Scene scene = gameController.render();
//
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Emeritus RPG");
//        primaryStage.show();
//        primaryStage.setResizable(false);
//
//        primaryStage.setOnCloseRequest(t -> {
//            EmeritusLogger logger = EmeritusLogger.create(Main.class);
////            logger.info(gameController.getState());
//            logger.flush();
//            Platform.exit();
//            System.exit(0);
//        });
//        gameController.run();
//        gameController.update();
//    }

    private static GameController setup(GameController gameController, int unitsPerTeam) {

        // Setup enemies
        Entity unitEntity = null;
        Random random = new Random();
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnit = EntityStore.getInstance().getOrCreateUnit(false); //UnitPool.getInstance().getRandomUnit(false);
            unitEntity = EntityStore.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.spawnUnit(unitEntity, "enemy", randomRow, randomColumn);
        }

        // Setup friendly
        for (int i = 0; i < unitsPerTeam / 2; i++) {
            String randomUnit = EntityStore.getInstance().getOrCreateUnit(true); //UnitPool.getInstance().getRandomUnit(true);
            unitEntity = EntityStore.getInstance().get(randomUnit);
            int randomRow =  random.nextInt(gameController.getRows());
            int randomColumn =  random.nextInt(gameController.getColumns());
            gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
        }

        return gameController;
    }
}
