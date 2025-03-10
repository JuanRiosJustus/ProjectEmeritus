package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameGenerationConfigs;
import main.logging.EmeritusLogger;
import main.ui.game.SceneManager;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.asset.AssetPool;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Main extends Application {
    private SceneManager sceneManager;
    private long lastUpdateTime = 0;

    @Override
    public void start(Stage primaryStage) {
        GameController gameController = testMetaGame();
        Scene scene = gameController.getGameScene();


        primaryStage.setScene(scene);
        primaryStage.setTitle("Emeritus RPG");
        primaryStage.show();
//        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(t -> {
            EmeritusLogger logger = EmeritusLogger.create(Main.class);
            logger.info(gameController.getState());
            logger.flush();
            Platform.exit();
            System.exit(0);
        });

        gameController.runGameLoop();
    }

    public static void main(String[] args) {


        launch(args);
    }


    private static GameController testMetaGame() {
//        int screenWidth = 1600;
//        int screenHeight = 1000;
        int screenWidth = 1500;
        int screenHeight = 950;
//        int screenWidth = 1280;
//        int screenHeight = 720;
//        int rows = screenHeight / 64;
//        int columns = screenWidth / 64;
//        int rows = 10;
//        int columns = 10;

//        Engine.getInstance().getController().setSize(screenWidth, screenHeight);

        Map<String, String> bucket = AssetPool.getInstance().getBucketV2("floor_tiles");
        Map<String, String> bucket2 = AssetPool.getInstance().getBucketV2("structures");

        GameGenerationConfigs configs = GameGenerationConfigs.getDefaults()
                .setRows(10)
                .setColumns(10)
                .setStartingSpriteWidth(96)
                .setStartingSpriteHeight(96)
                .setStartingCameraWidth(screenWidth)
                .setStartingCameraHeight(screenHeight)
                .setTerrainAsset(new ArrayList<>(bucket.keySet()).get(new Random().nextInt(bucket.size())))
                .setStructureAssets(bucket2.keySet().stream().toList().stream().findFirst().stream().toList());
        GameController gameController = GameController.create(configs);

        // Setup enemies
        Entity unitEntity = null;
        Random random = new Random();
        int unitsPerTeam = 4;
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

        return gameController;
    }
}
