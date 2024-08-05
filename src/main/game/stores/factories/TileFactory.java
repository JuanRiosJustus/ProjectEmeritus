package main.game.stores.factories;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Settings;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

import java.util.TreeMap;

public class TileFactory {

    public static Entity create(int row, int column) {
        return create(row, column, null, null, null, null);
    }

    public static Entity create(int row, int column, Object collider, Object height, Object terrain, Object liquid) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Tile.ROW, row);
        jsonObject.put(Tile.COLUMN, column);
        jsonObject.put(Tile.COLLIDER, collider);
        jsonObject.put(Tile.HEIGHT, height);
        jsonObject.put(Tile.TERRAIN, terrain);
        jsonObject.put(Tile.LIQUID, liquid);
        jsonObject.put(Tile.OBSTRUCTION, null);
        jsonObject.put(Tile.SPAWN_REGION, null);
        return create(row, column, jsonObject);
    }

    public static Entity create(int row, int column, JsonObject jsonObject) {

        Entity entity = EntityFactory.create(row + "x" + column);
        int tileWidth = Settings.getInstance().getSpriteWidth();
        int tileHeight = Settings.getInstance().getSpriteHeight();
        entity.add(new Vector3f(column * tileWidth , row * tileHeight, -1));
        entity.add(new Assets());
        entity.add(new Tile(jsonObject));
        entity.add(new Overlay());
        entity.add(new History());

        return entity;
    }
}
