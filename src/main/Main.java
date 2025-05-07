package main;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
import main.ui.scenes.mapeditor.MapEditorScene;
import main.ui.scenes.MenuScene;

import java.util.Collections;
import java.util.Iterator;
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

        GameController gameController = GameController.create(8, 12, 1500, 950);
        setup(gameController, 5);


        engineController.stage(Constants.MENU_SCENE, new MenuScene(1500, 950));
        engineController.stage(Constants.MAP_EDITOR_SCENE, new MapEditorScene(1500, 950));
        engineController.stage(Constants.GAME_SCENE, gameController);


        engineController.setOnCloseRequest(t -> {
            EmeritusLogger logger = EmeritusLogger.create(Main.class);
            logger.flush();
            Platform.exit();
            System.exit(0);
        });
    }

    private static GameController setup(GameController gameController, int unitsPerTeam) {

        // Get available spawns
//        JSONArray spawns = gameController.getSpawnRegions();
        JSONObject spawnData = gameController.getSpawnRegionsData();
        Iterator<String> spawnIterator = spawnData.keySet().iterator();


        // Setup enemies
        Entity unitEntity = null;
        Random random = new Random();
        String spawnRegion = spawnIterator.next();
        JSONArray tiles = spawnData.getJSONArray(spawnRegion);
        Collections.shuffle(tiles);
        for (int i = 0; i < unitsPerTeam; i++) {

            String randomUnitID = EntityStore.getInstance().getOrCreateUnit(false);
            JSONObject tile = tiles.getJSONObject(0);
            tiles.removeFirst();
            String tileID = tile.getString("tile_id");

            JSONObject spawnPlacementData = new JSONObject();
            spawnPlacementData.put("unit_id", randomUnitID);
            spawnPlacementData.put("tile_id", tileID);
            spawnPlacementData.put("team_id", "Enemy");
            gameController.setUnitSpawn(spawnPlacementData);
        }

        spawnRegion = spawnIterator.next();
        tiles = spawnData.getJSONArray(spawnRegion);
        Collections.shuffle(tiles);

        // Setup friendly
        for (int i = 0; i < unitsPerTeam; i++) {
            String randomUnitID = EntityStore.getInstance().getOrCreateUnit(true);
            JSONObject tile = tiles.getJSONObject(0);
            tiles.removeFirst();

            String tileID = tile.getString("tile_id");

            JSONObject spawnPlacementData = new JSONObject();
            spawnPlacementData.put("unit_id", randomUnitID);
            spawnPlacementData.put("tile_id", tileID);
            spawnPlacementData.put("team_id", "Ally");

            gameController.setUnitSpawn(spawnPlacementData);
        }

        return gameController;
    }


//    private static GameController setup(GameController gameController, int unitsPerTeam) {
//
//        // Get available spawns
////        JSONArray spawns = gameController.getSpawnRegions();
//        JSONObject spawnData = gameController.getSpawnRegionsData();
//        Iterator<String> spawnIterator = spawnData.keySet().iterator();
//
//
//        // Setup enemies
//        Entity unitEntity = null;
//        Random random = new Random();
//        String spawnRegion = spawnIterator.next();
//        JSONArray tiles = spawnData.getJSONArray(spawnRegion);
//        Collections.shuffle(tiles);
//        for (int i = 0; i < unitsPerTeam; i++) {
//            String randomUnitID = EntityStore.getInstance().getOrCreateUnit(false);
//            unitEntity = EntityStore.getInstance().get(randomUnitID);
//            JSONObject tile = tiles.getJSONObject(random.nextInt(tiles.size()));
//
//            String tileID = tile.getString("tile_id");
//            int randomRow =  tile.getIntValue("row");
//            int randomColumn =  tile.getIntValue("column");
//
//            gameController.setUnit(randomUnitID, tileID);
////            gameController.spawnUnit(unitEntity, "enemy", randomRow, randomColumn);
//        }
//
//        // Setup friendly
//        for (int i = 0; i < unitsPerTeam; i++) {
//            String randomUnit = EntityStore.getInstance().getOrCreateUnit(true);
//            unitEntity = EntityStore.getInstance().get(randomUnit);
//            int randomRow =  random.nextInt(gameController.getRows());
//            int randomColumn =  random.nextInt(gameController.getColumns());
//            gameController.spawnUnit(unitEntity, "user", randomRow, randomColumn);
//        }
//
//        return gameController;
//    }
}
