package main.game.stores.factories;

import main.constants.Settings;
import main.game.components.History;
import main.game.components.Overlay;
import main.game.components.Size;
import main.game.components.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

public class TileFactory {

    public static Entity create(int row, int column) {
        Entity tile = EntityFactory.create(row + "x" + column);
        int tileWidth = Settings.getInstance().getSpriteWidth();
        int tileHeight = Settings.getInstance().getSpriteHeight();
        tile.add(new Vector3f(column * tileWidth , row * tileHeight, -1));
//        tile.add(new Size(size, size));
        tile.add(new Tile(row, column, null, null, null, null));
        tile.add(new Overlay());
        tile.add(new History());

        return tile;
    }

    public static Entity create(int row, int column, String collider, String height, String terrain, String liquid) {
        Entity entity = EntityFactory.create(row + "x" + column);
        int tileWidth = Settings.getInstance().getSpriteWidth();
        int tileHeight = Settings.getInstance().getSpriteHeight();
        entity.add(new Vector3f(column * tileWidth , row * tileHeight, -1));
        entity.add(new Tile(row, column,  collider, height, terrain, liquid));
        entity.add(new Overlay());
        entity.add(new History());

        return entity;
    }
}
