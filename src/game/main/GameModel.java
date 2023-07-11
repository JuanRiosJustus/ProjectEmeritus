package game.main;

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
import game.logging.ActivityLogger;
import game.map.TileMap;
import game.map.TileMapFactory;
import game.map.generators.validation.SchemaConfigs;
import game.queue.SpeedQueue;
import game.stores.factories.UnitFactory;
import game.stores.pools.AssetPool;
import game.systems.InputHandler;
import game.systems.UpdateSystem;
import input.Mouse;
import logging.ELogger;
import logging.ELoggerFactory;
import ui.GameState;


public class GameModel {

    private TileMap tileMap = null;
    public SpeedQueue speedQueue = null;
    public ActivityLogger logger = null;
    public GameState state = null;
    public Vector mousePosition = null;
    private SplittableRandom random = null;
    private GameController controller = null;
    public InputHandler input = null;
    public UpdateSystem system = null;
    private SchemaConfigs configs = null;

    public GameModel(GameController gc) { initialize(gc); }

    private void initialize(GameController gc) {
        controller = gc;

        system = new UpdateSystem();
        input = new InputHandler();
        random = new SplittableRandom();
        mousePosition = new Vector();
        state = new GameState();
        logger = new ActivityLogger();
        speedQueue = new SpeedQueue();

        configs = SchemaConfigs.newConfigs()
            .setSize(15, 20)
            .setType(2)
            // .setType(random.nextInt(0, 5))
            .setZoom(.6f)
            .setWalling(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.WALLS_SPRITESHEET_FILEPATH).getSize()))
            .setFlooring(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH).getSize()))
            .setStructure(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getSize()))
            .setLiquid(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getSize()));

        tileMap = TileMapFactory.create(configs);
    //    tileMap = TileMapFactory.load("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-01-15-02-59.json");
//        tileMap.toJson()
//        TileMapIO.encode(tileMap);
//        tileMap = TileMapIO.decode("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-01-12-04-42.tilemap");

        speedQueue.enqueue(new Entity[]{
                UnitFactory.create("Topaz Dragon"),
                UnitFactory.create("Sapphire Dragon"),
                UnitFactory.create("Ruby Dragon", true),
                UnitFactory.create("Emerald Dragon"),
        });

        speedQueue.enqueue(new Entity[] {
                UnitFactory.create("Diamond Dragon"),
                UnitFactory.create("Onyx Dragon"),
        });

        tileMap.place(speedQueue);
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
            initialize(controller);
// //            TileMapIO.encode(tileMap);
//             configs = SchemaConfigs.newConfigs()
//                 .setWalling(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.WALLS_SPRITESHEET_FILEPATH).getSize()))
//                 .setFlooring(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH).getSize()))
//                 .setSize(20, 25)
//                 .setType(4)
//                 .setZoom(.6f)
//                 .setStructure(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getSize()))
//                 .setLiquid(random.nextInt(0, AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getSize()));

                    
//             tileMap = TileMapFactory.create(configs);
//             tileMap.place(speedQueue);
        }

        if (controller.input.getKeyboard().isPressed(KeyEvent.VK_S)) {
            // logger.info("Saving map... " + UUID.randomUUID());
            // uiLogQueue.add("Added " + UUID.randomUUID());

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
