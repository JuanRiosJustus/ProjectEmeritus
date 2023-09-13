package main.game.components.tile;

import main.constants.Direction;

public class TileUtils {

    public static Direction getDirectionFrom(Tile from, Tile to) {
        int vertical = (int) Math.signum(from.row - to.row);
        int horizontal = (int) Math.signum(from.column - to.column);
        Direction result = null;
        for (Direction direction : Direction.values()) {
            if (direction.x != horizontal || direction.y != vertical) { continue; }
            result = direction;
        }

        return result;
    }
}
