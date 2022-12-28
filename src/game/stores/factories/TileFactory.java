package game.stores.factories;

import constants.Constants;
import game.components.Dimension;
import game.components.Vector;
import game.components.Tile;
import game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class TileFactory {

    public static final List<Entity> list = new ArrayList<>();

    public static Entity create(int row, int column) {
        Entity tile = new Entity();
        int size = Constants.CURRENT_SPRITE_SIZE;
        tile.add(new Vector(column * size , row * size, -1));
        tile.add(new Dimension(size, size));
        tile.add(new Tile(row, column));

        list.add(tile);

        return tile;
    }
}
