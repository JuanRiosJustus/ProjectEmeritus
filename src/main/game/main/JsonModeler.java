package main.game.main;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

import java.util.Locale;

public class JsonModeler {
    public static JsonObject getUnitPlacementModel(GameModel gameModel) {
        // The purpose of this method is to create a json object which represents
        // units and where they are placed
        JsonObject unitPlacementModel = new JsonObject();
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int column = 0; column < gameModel.getColumns(); column++) {
                // Get a tile when it has a unit on it
                Entity entity = gameModel.tryFetchingTileAt(row, column);
                Tile tile = entity.get(Tile.class);
                if (tile.getUnit() == null) { continue; }
                entity = tile.getUnit();
                // Get the data about the unit and place it within the model object
                Statistics statistics = entity.get(Statistics.class);
                Identity identity = entity.get(Identity.class);
                JsonObject unitData = new JsonObject();
                unitData.put("name", identity.getName());
                unitData.put("uuid", identity.getUuid());
                unitData.put("row", row);
                unitData.put("column", column);
                // Check the spawn region. If it is a new spawn region, insert into teamMap
                JsonObject team = (JsonObject) unitPlacementModel.getOrDefault(String.valueOf(tile.getSpawnRegion()), new JsonObject());
                team.put(identity.getUuid(), unitData);
                unitPlacementModel.put(String.valueOf(tile.getSpawnRegion()), team);
            }
        }

        return unitPlacementModel;
    }

    public static JsonObject getTileMapModel(GameModel gameModel) {
        return gameModel.getTileMap().toJsonObject();
    }
}
