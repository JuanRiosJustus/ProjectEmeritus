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
import game.camera.Camera;
import game.components.*;
import game.entity.Entity;
import game.map.TileMapFactory;
import game.map.TileMap;
import game.queue.RPGQueue;
import game.stores.factories.UnitFactory;
import game.stores.pools.AssetPool;
import game.systems.CameraSystem;
import game.systems.UpdateSystem;
import logging.Logger;
import logging.LoggerFactory;
import ui.GameUiModel;
import utils.TileMapIO;

import java.util.SplittableRandom;


public class GameModel {

    private TileMap tileMap;
    public final RPGQueue queue = new RPGQueue();
    public final GameUiModel ui = new GameUiModel();
    private final GameController controller;
    private final Vector mousePosition = new Vector();
    private final SplittableRandom random = new SplittableRandom();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    public GameModel(GameController gameController) {
        controller = gameController;
        int terrain = random.nextInt(AssetPool.instance().getSpriteSheet(Constants.TERRAIN_SPRITESHEET_FILEPATH).rows() - 1);
        while (terrain % 2 != 0 || terrain == 0) {
            terrain = random.nextInt(AssetPool.instance().getSpriteSheet(Constants.TERRAIN_SPRITESHEET_FILEPATH).rows() - 1);
        }

        tileMap = TileMapFactory.create(4, 15, 20, terrain, terrain + 1);
//        TileMapIO.encode(tileMap);
//        tileMap = TileMapIO.decode("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2022-12-28-04-50.tilemap");

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

    public void update() {
        UpdateSystem.update(this, controller.input);
        Camera.get().update();

    }

    public void input() {
        controller.input.update();
        CameraSystem.handle(controller.input, this);
        mousePosition.copy(controller.input.mouse().position);
    }

    public Entity tryFetchingMousedTile() {
        Vector pv = Camera.get().get(Vector.class);
        int column = (int) ((mousePosition.x + pv.x) / Constants.CURRENT_SPRITE_SIZE);
        int row = (int) ((mousePosition.y + pv.y) / Constants.CURRENT_SPRITE_SIZE);
        return tryFetchingTileAt(row, column);
    }

    public int getRows() { return tileMap.raw.length; }
    public int getColumns() { return tileMap.raw[0].length; }

    public double getVisibleStartOfRows() {
        Vector pv = Camera.get().get(Vector.class);
        return pv.y / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector pv = Camera.get().get(Vector.class);
        return pv.x / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector pv = Camera.get().get(Vector.class);
        Dimension d = Camera.get().get(Dimension.class);
        return (pv.x + d.width) / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector pv = Camera.get().get(Vector.class);
        Dimension d = Camera.get().get(Dimension.class);
        return (pv.y + d.height) / (double) Constants.CURRENT_SPRITE_SIZE;
    }

    public Entity tryFetchingTileAt(int row, int column) {
        if (row < 0 || column < 0 || row >= tileMap.raw.length || column >= tileMap.raw[row].length) {
            return null;
        } else {
            return tileMap.raw[row][column];
        }
    }
}
