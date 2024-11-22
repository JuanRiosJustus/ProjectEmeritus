package main.game.main;

import org.json.JSONObject;

import main.game.components.IdentityComponent;
import main.game.components.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Date;

public class JsonUtils {
    /**
     * Validates that all keys in the requiredKeys list are present in the JSONObject.
     *
     * @param json  The JSONObject to validate.
     * @param keys A list of keys that must be present in the JSONObject.
     * @return true if all required keys are present, false otherwise.
     */
    public static JSONObject validate(JSONObject json, String[] keys) {
        for (String key : keys) {
            if (json.has(key)) { continue;}
            return null;
        }
        return json;
    }

    public static void save(String name, String data) {
        try {
            String fileName = name + ".json";
            PrintWriter out = new PrintWriter(new FileWriter( fileName, false), true);
            String prettyJson = data;
            out.write(prettyJson);
            out.close();
        } catch (Exception ex) {
            System.err.println("SAVE FAILED - FLUSHING EXCEPTION");
        }
    }

    public static JSONObject getUnitPlacementModel(GameModel gameModel) {
        // The purpose of this method is to create a json object which represents
        // units and where they are placed
        JSONObject unitPlacementModel = new JSONObject();
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int column = 0; column < gameModel.getColumns(); column++) {
                // Get a tile when it has a unit on it
                Entity entity = gameModel.tryFetchingTileAt(row, column);
                Tile tile = entity.get(Tile.class);
                if (tile.getUnit() == null) { continue; }
                entity = tile.getUnit();
                // Get the data about the unit and place it within the model object
                StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
                IdentityComponent identityComponent = entity.get(IdentityComponent.class);
                JSONObject unitData = new JSONObject();
                unitData.put("name", identityComponent.getName());
                unitData.put("uuid", identityComponent.getUuid());
                unitData.put("row", row);
                unitData.put("column", column);
                // Check the spawn region. If it is a new spawn region, insert into teamMap
                JSONObject team = unitPlacementModel.optJSONObject(String.valueOf(tile.getSpawnRegion()), new JSONObject());
                team.put(identityComponent.getUuid(), unitData);
                unitPlacementModel.put(String.valueOf(tile.getSpawnRegion()), team);
            }
        }

        return unitPlacementModel;
    }

    public static JSONObject getTileMapModel(GameModel gameModel) {
//        return gameModel.getTileMap().toJsonObject();
        return null;
    }
}
