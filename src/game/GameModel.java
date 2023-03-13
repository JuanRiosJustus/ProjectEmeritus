package game;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SplittableRandom;
import java.util.UUID;

import constants.Constants;
import game.camera.Camera;
import game.components.Dimension;
import game.components.Vector;
import game.entity.Entity;
import game.map.TileMap;
import game.map.TileMapFactory;
import game.map.generators.validation.SchemaConfigs;
import game.queue.SpeedQueue;
import game.stores.factories.UnitFactory;
import game.stores.pools.AssetPool;
import game.systems.InputHandler;
import game.systems.UpdateSystem;
import input.Mouse;
import logging.Logger;
import logging.LoggerFactory;
import ui.GameState;


public class GameModel {

    private TileMap tileMap;
    public final SpeedQueue unitTurnQueue = new SpeedQueue();
    public final Queue<String> uiLogQueue = new LinkedList<>();
    public final GameState state = new GameState();
    public final Vector mousePosition = new Vector();
    private final SplittableRandom random = new SplittableRandom();
    private GameController controller;
    public final InputHandler input = new InputHandler();
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    public final UpdateSystem system = new UpdateSystem();

    private SchemaConfigs configs = null;

    public void initialize(GameController gc) {
        controller = gc;

        configs = SchemaConfigs.newConfigs()
            .setSize(20, 25)
            .setType(random.nextInt(0, 5))
            .setZoom(.6f)
            .setWalling(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.WALLS_SPRITESHEET_FILEPATH).getSize()))
            .setFlooring(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH).getSize()))
            .setStructure(random.nextInt(-1, AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getSize()))
            .setLiquid(random.nextInt(-1, AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getSize()));

        tileMap = TileMapFactory.create(configs);
    //    tileMap = TileMapFactory.load("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-01-15-02-59.json");
//        tileMap.toJson()
//        TileMapIO.encode(tileMap);
//        tileMap = TileMapIO.decode("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-01-12-04-42.tilemap");

        unitTurnQueue.enqueue(new Entity[]{
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


//                UnitFactory.create("Human"),
//                UnitFactory.create("Human"),
//                UnitFactory.create("Human"),
                UnitFactory.create("Human", false),
//                EntityBuilder.get().unit("Human Fighter"),
//                EntityBuilder.get().unit("Human Fighter"),
//                EntityBuilder.get().unit("Human Warrior"),
//                EntityBuilder.get().unit("Human Warrior"),
//                EntityBuilder.get().unit("Human Warrior"),
//                EntityBuilder.get().unit("Human Enchanter"),


//                EntityBuilder.get().unit("Tsukuyomi"),
//                EntityBuilder.get().unit("Amaterasu"),
//                UnitFactory.create("Orc"),
//                UnitFactory.create("Orc Fighter"),
//                UnitFactory.create("Orc Fighter"),
//                UnitFactory.create("Orc Warrior"),
//                UnitFactory.create("Orc Warrior"),
//                UnitFactory.create("Orc Warrior"),
//                UnitFactory.create("Orc Wizard"),
//                EntityBuilder.instance().getUnit("Sphinx", false)

        });

        unitTurnQueue.enqueue(new Entity[] {
//                EntityBuilder.get().unit("Merfolk"),
//                EntityBuilder.get().unit("Merfolk"),
//                EntityBuilder.get().unit("Water Nymph"),
//                EntityBuilder.get().unit("Merfolk Fighter"),
//                EntityBuilder.get().unit("Merfolk Fighter"),
//                EntityBuilder.get().unit("Merfolk Fighter"),
//                EntityBuilder.get().unit("Merfolk Warrior"),
//                EntityBuilder.get().unit("Merfolk Warrior"),
//                EntityBuilder.get().unit("Merfolk Warrior"),

                UnitFactory.create("Light Nymph", true),
                UnitFactory.create("Air Nymph"),
                UnitFactory.create("Water Nymph"),
                UnitFactory.create("Dark Nymph"),
                UnitFactory.create("Nature Nymph"),
                UnitFactory.create("Fire Nymph"),
        });

        tileMap.place(unitTurnQueue);
    }

    public void update() {
        system.update(this);
//        UpdateSystem.update(this, controller.input);
        Camera.instance().update();
    }

    public void input() {
        controller.input.update();
        input.handle(controller.input, this);
        mousePosition.copy(controller.input.getMouse().position);

        if (controller.input.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
//            TileMapIO.encode(tileMap);
            configs = SchemaConfigs.newConfigs()
                .setWalling(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.WALLS_SPRITESHEET_FILEPATH).getSize()))
                .setFlooring(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH).getSize()))
                .setSize(20, 25)
                .setType(4)
                .setZoom(.6f)
                .setStructure(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getSize()))
                .setLiquid(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getSize()));

                    
            tileMap = TileMapFactory.create(configs);
            tileMap.place(unitTurnQueue);
//            queue.enqueue(null);
        }

        if (controller.input.getKeyboard().isPressed(KeyEvent.VK_S)) {
            logger.log("Saving map... " + UUID.randomUUID());
            uiLogQueue.add("Added " + UUID.randomUUID());

//            TileMapFactory.save(tileMap);
//            tileMap.toJson(".");
//            TileMapIO.encode(tileMap);
        }
//        System.out.println(Camera.instance().get(Vector.class).toString());
//        System.out.println(controller.input.getMouse().position);
    }

    public Entity tryFetchingTileMousedAt() {
        Vector camera = Camera.instance().get(Vector.class);
        Mouse mouse = controller.input.getMouse();
        int column = (int) ((mouse.position.x + camera.x) / Constants.CURRENT_SPRITE_SIZE);
        int row = (int) ((mouse.position.y - Constants.MAC_WINDOW_HANDLE_HEIGHT + camera.y) / Constants.CURRENT_SPRITE_SIZE);
        return tryFetchingTileAt(row, column);
    }

    public int getRows() { return tileMap.getRows(); }
    public int getColumns() { return tileMap.getColumns(); }

    public double getVisibleStartOfRows() {
        Vector pv = Camera.instance().get(Vector.class);
        return pv.y / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector pv = Camera.instance().get(Vector.class);
        return pv.x / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector pv = Camera.instance().get(Vector.class);
        Dimension d = Camera.instance().get(Dimension.class);
        return (pv.x + d.width) / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector pv = Camera.instance().get(Vector.class);
        Dimension d = Camera.instance().get(Dimension.class);
        return (pv.y + d.height) / (double) Constants.CURRENT_SPRITE_SIZE;
    }

    public Entity tryFetchingTileAt(int row, int column) { return tileMap.tryFetchingTileAt(row, column); }
}
