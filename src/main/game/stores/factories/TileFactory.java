package main.game.stores.factories;

import main.constants.Constants;
import main.game.components.OverlayAnimation;
import main.game.components.Statistics;
import main.game.components.Dimension;
import main.game.components.Vector;
import main.game.components.Tile;
import main.game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class TileFactory {

    public static Entity create(int row, int column) {
        Entity tile = new Entity();
        int size = Constants.CURRENT_SPRITE_SIZE;
        tile.add(new Vector(column * size , row * size, -1));
        tile.add(new Dimension(size, size));
        tile.add(new Tile(row, column));
        tile.add(new OverlayAnimation());
        // tile.add(new Statistics());

        return tile;
    }
}
