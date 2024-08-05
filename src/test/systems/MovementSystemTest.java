package test.systems;

import main.constants.Direction;
import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.game.stores.factories.TileFactory;
import main.game.systems.MovementSystem;
import org.junit.Assert;
import org.junit.Test;

public class MovementSystemTest {
    private final MovementSystem movementSystem = new MovementSystem();

    @Test
    public void correctlyDeterminesDirectionBasedOnTileDestination() {
        Entity startTileEntity = TileFactory.create(4, 4);
        Entity endTileEntity = TileFactory.create(7, 4);

        Direction direction = movementSystem.getDirection(startTileEntity, endTileEntity);
        Assert.assertEquals(direction, Direction.South);
    }

    @Test
    public void correctlyDeterminesDirectionBasedOnTileDestination2() {
        Entity startTileEntity = TileFactory.create(4, 4);
        Entity endTileEntity = TileFactory.create(4, 7);

        Direction direction = movementSystem.getDirection(startTileEntity, endTileEntity);
        Assert.assertEquals(direction, Direction.East);
    }

    @Test
    public void correctlyDeterminesDirectionBasedOnTileDestination3() {
        Entity startTileEntity = TileFactory.create(6, 6);
        Entity endTileEntity = TileFactory.create(4, 4);

        Direction direction = movementSystem.getDirection(startTileEntity, endTileEntity);
        Assert.assertEquals(direction, Direction.North);
    }
}
