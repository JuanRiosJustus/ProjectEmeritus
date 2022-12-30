package game.systems;

import game.GameModel;
import game.components.Tile;
import game.entity.Entity;
import input.InputController;

public class BuffSpawnerSystem extends GameSystem {

    private static final int SPAWN_ATTEMPTS = 50;
    public void update(GameModel model, Entity unit) {


//        int ow = model.getRows();
//        int randomColumn =
        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            int row = model.getRows();
            int column = model.getColumns();

            Entity entity = model.tryFetchingTileAt(row, column);
            Tile tile = entity.get(Tile.class);

            if (tile.isWall() || tile.isOccupied()) { continue; }

//            tile.set
            return;
        }
    }
}
