package game;

import constants.Constants;
//import core.Camera;
//import core.Entity;
//import core.EntityStore;
//import core.components.Dimension;
//import core.components.Vector;
//import core.model.CameraHandler;
//import core.model.TilePathFinder;
//import core.model.mapbuilding.MapBuilder;
//import core.model.mapbuilding.TileMap;
//import core.queues.RPGQueue;
//import core.view.FloatingContext;
//import creature.Creature;
import engine.EngineController;
import game.camera.Camera;
import game.components.*;
import game.entity.Entity;
import game.map.generators.HauberkLikeDungeonRoomsMap;
import game.map.generators.OpenMap;
import game.map.generators.RandomSquareRoomsMap;
import game.map.TileMap;
import game.queue.RPGQueue;
import game.stores.factories.UnitFactory;
import game.systems.CameraSystem;
import game.systems.UpdateSystem;
import logging.Logger;
import logging.LoggerFactory;


public class GameModel {

    private TileMap tileMap;
    public final RPGQueue queue = new RPGQueue();
    private final Vector mousePosition = new Vector();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public GameModel() {
//        tileMap = new RandomSquareRoomsMap().build(15, 20);
        tileMap = new HauberkLikeDungeonRoomsMap().build(15, 20);
//        tileMap = new OpenMap().build(15, 20);

        queue.enqueue(new Entity[]{
//                EntityBuilder.get().unit("Water Nymph"),
//                EntityBuilder.get().unit("Dark Nymph"),
//                EntityBuilder.get().unit("Earth Nymph"),
//                EntityBuilder.get().unit("Fire Nymph", true),

//                EntityBuilder.get().unit("Orc"),
//                EntityBuilder.get().unit("Fire Nymph"),
//                EntityBuilder.get().unit("Light Nymph"),

//                EntityBuilder.get().unit("Elf"),
//                EntityBuilder.get().unit("Elf Fighter"),
//                EntityBuilder.get().unit("Elf Fighter"),
//                EntityBuilder.get().unit("Elf Warrior"),
//                EntityBuilder.get().unit("Elf Warrior"),
//                EntityBuilder.get().unit("Elf Warrior"),
//                EntityBuilder.get().unit("Elf Archer", true),
//                EntityBuilder.get().unit("Elf Mage"),


//                EntityBuilder.get().unit("Human"),
//                EntityBuilder.get().unit("Human Fighter"),
//                EntityBuilder.get().unit("Human Fighter"),
//                EntityBuilder.get().unit("Human Warrior"),
//                EntityBuilder.get().unit("Human Warrior"),
//                EntityBuilder.get().unit("Human Warrior"),
//                EntityBuilder.get().unit("Human Enchanter"),


//                EntityBuilder.get().unit("Tsukuyomi"),
//                EntityBuilder.get().unit("Amaterasu"),
                UnitFactory.create("Orc"),
                UnitFactory.create("Orc Fighter"),
                UnitFactory.create("Orc Fighter"),
                UnitFactory.create("Orc Warrior"),
                UnitFactory.create("Orc Warrior"),
                UnitFactory.create("Orc Warrior"),
                UnitFactory.create("Orc Wizard"),
//                EntityBuilder.instance().getUnit("Sphinx", false)

        });

        queue.enqueue(new Entity[] {
//                EntityBuilder.get().unit("Merfolk"),
//                EntityBuilder.get().unit("Merfolk"),
//                EntityBuilder.get().unit("Water Nymph"),
////                EntityBuilder.get().unit("Merfolk Fighter"),
//                EntityBuilder.get().unit("Merfolk Fighter"),
//                EntityBuilder.get().unit("Merfolk Fighter"),
//                EntityBuilder.get().unit("Merfolk Warrior"),
//                EntityBuilder.get().unit("Merfolk Warrior"),
//                EntityBuilder.get().unit("Merfolk Warrior"),

                UnitFactory.create("Light Nymph"),
                UnitFactory.create("Water Nymph"),
                UnitFactory.create("Dark Nymph"),
                UnitFactory.create("Fire Nymph"),
                UnitFactory.create("Earth Nymph", true),
        });

        tileMap.place(queue);
    }

    public void update(EngineController engine) {
        UpdateSystem.update(engine);
        Camera.get().update();

    }

    public void input(EngineController engine) {
        CameraSystem.handle(engine);

        mousePosition.copy(engine.model.input.mouse().position);

//        if (engine.model.input.keyboard().isPressed(KeyEvent.VK_PERIOD)) {
//            tileMap = MapBuilder.build(Constants.TEST_MAP);
//            tileMap.place(queue);
//            logger.log("Map changed");
//        }
    }

    public Entity tryFetchingMousedTile() {
        Vector pv = Camera.get().get(Vector.class);
        int column = (int) ((mousePosition.x + pv.x) / Constants.SPRITE_SIZE);
        int row = (int) ((mousePosition.y + pv.y) / Constants.SPRITE_SIZE);
        return tryFetchingTileAt(row, column);
    }

    public int getRows() { return tileMap.tiles.length; }
    public int getColumns() { return tileMap.tiles[0].length; }

    public double getVisibleStartOfRows() {
        Vector pv = Camera.get().get(Vector.class);
        return pv.y / (double) Constants.SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector pv = Camera.get().get(Vector.class);
        return pv.x / (double) Constants.SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector pv = Camera.get().get(Vector.class);
        Dimension d = Camera.get().get(Dimension.class);
        return (pv.x + d.width) / (double) Constants.SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector pv = Camera.get().get(Vector.class);
        Dimension d = Camera.get().get(Dimension.class);
        return (pv.y + d.height) / (double) Constants.SPRITE_SIZE;
    }

    public Entity tryFetchingTileAt(int row, int column) {
        if (row < 0 || column < 0 || row >= tileMap.tiles.length || column >= tileMap.tiles[row].length) {
            return null;
        } else {
            return tileMap.tiles[row][column];
        }
    }
}
