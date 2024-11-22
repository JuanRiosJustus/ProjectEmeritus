package main.game.stores.factories;

import org.json.JSONObject;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

public class TileFactory {

    public static Entity create(int row, int column, Object collider, Object height, Object terrain, Object liquid) {
        JSONObject JSONObject = new JSONObject();
        JSONObject.put(Tile.ROW, row);
        JSONObject.put(Tile.COLUMN, column);
        JSONObject.put(Tile.COLLIDER, collider);
        JSONObject.put(Tile.HEIGHT, height);
        JSONObject.put(Tile.TERRAIN, terrain);
        JSONObject.put(Tile.LIQUID, liquid);
//        JSONObject.put(Tile.OBSTRUCTION, null);
//        JSONObject.put(Tile.SPAWNERS, new );
        return create(row, column, JSONObject);
    }

    public static Entity create(int row, int column, JSONObject JSONObject) {

        Entity entity = EntityFactory.create(row + "x" + column);
        entity.add(new AssetComponent());
        entity.add(new Tile(JSONObject));
        entity.add(new Overlay());
        entity.add(new History());

        return entity;
    }
}
