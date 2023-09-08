package main.game.stores.factories;

import main.constants.Settings;
import main.game.components.Overlay;
import main.game.components.Size;
import main.game.components.Vector;
import main.game.components.Tile;
import main.game.entity.Entity;

public class TileFactory {

    public static Entity create(int row, int column) {
        Entity tile = EntityFactory.create(row + "x" + column);
        int size = Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
        tile.add(new Vector(column * size , row * size, -1));
        tile.add(new Size(size, size));
        tile.add(new Tile(row, column));
        tile.add(new Overlay());

        return tile;
    }
}
