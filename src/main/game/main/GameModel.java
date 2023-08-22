package main.game.main;

import java.awt.event.KeyEvent;
import java.util.SplittableRandom;

import main.constants.Constants;
import main.engine.Engine;
import main.game.camera.Camera;
import main.game.components.Dimension;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.TileMap;
import main.game.map.builders.BorderedMapWithBorderedRooms;
import main.game.queue.SpeedQueue;
import main.game.state.UserSavedData;
import main.game.stores.factories.UnitFactory;
import main.game.stores.pools.AssetPool;
import main.game.systems.InputHandler;
import main.game.systems.UpdateSystem;
import main.input.Mouse;
import main.constants.GameState;


public class GameModel {

    private TileMap tileMap = null;
    public SpeedQueue speedQueue = null;
    public ActivityLogger logger = null;
    public GameState gameState = null;
    public Vector mousePosition = null;
    private SplittableRandom random = null;
    private GameController controller = null;
    public InputHandler input = null;
    public UpdateSystem system = null;

    public GameModel(GameController gc) { initialize(gc); }

    private void initialize(GameController gc) {
        controller = gc;

        system = new UpdateSystem();
        input = new InputHandler();
        random = new SplittableRandom();
        mousePosition = new Vector();
        gameState = new GameState();//GameState.getInstance();
        logger = new ActivityLogger();
        speedQueue = new SpeedQueue();

        // tileMap = LargeContinousRoom.newBuilder()
//         tileMap = HauberkDungeonMap.newBuilder()
        tileMap = BorderedMapWithBorderedRooms.newBuilder()
//         tileMap = LargeBorderedRoom.newBuilder()
//         tileMap = NoBorderWithSmallRooms.newBuilder()
//         tileMap = BasicOpenMap.newBuilder()
            .setRowAndColumn(15, 22)
            .setSeed(random.nextLong())
            .setExiting(2)
            .setZoom(.9f)
            .setWalling(random.nextInt(1, AssetPool.getInstance().getSheet(Constants.WALLS_SPRITESHEET_FILEPATH).getRows()))
            .setFlooring(random.nextInt(1, AssetPool.getInstance().getSheet(Constants.FLOORS_SPRITESHEET_FILEPATH).getRows()))
            .setStructure(random.nextInt(1, AssetPool.getInstance().getSheet(Constants.STRUCTURES_SPRITESHEET_FILEPATH).getRows()))
            .setLiquid(random.nextInt(1, AssetPool.getInstance().getSheet(Constants.LIQUIDS_SPRITESHEET_FILEPATH).getRows()))
            .build();

    //    tileMap = TileMapFactory.load("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-01-15-02-59.json");
//        tileMap.toJson()
//        TileMapIO.encode(tileMap);
//        tileMap = TileMapIO.decode("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-01-12-04-42.tilemap");

        speedQueue.enqueue(new Entity[]{
                UnitFactory.create("Topaz Dragon" ),
                UnitFactory.create("Sapphire Dragon"),
                UnitFactory.create("Ruby Dragon", true),
                UnitFactory.create("Emerald Dragon"),
        });

        speedQueue.enqueue(new Entity[] {
                UnitFactory.create("Diamond Dragon"),
                UnitFactory.create("Onyx Dragon"),
        });

        // tileMap.placeRandomly(speedQueue);
        tileMap.placeByTeam(speedQueue, 3, 4);
    }

    public void update() {
        system.update(this);
//        UpdateSystem.update(this, controller.input);
        Camera.getInstance().update();
    }

    public void input() {
        controller.input.update();
        input.handle(controller.input, this);
        mousePosition.copy(controller.input.getMouse().position);

        if (controller.input.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
//            System.out.println("t-t-t-t-t-t-t--t-t-t-t-tt-t-t-t");
//            initialize(controller);
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
            UserSavedData.getInstance().save(speedQueue.peek());
//            UserSavedData.getInstance().createOrRead("test.json");
//            UserSavedData.getInstance().createOrRead("tests.json");
            logger.log("SAVING DATA");
        }

        if (controller.input.getKeyboard().isPressed(KeyEvent.VK_P)) {
            // logger.info("Saving map... " + UUID.randomUUID());
            // uiLogQueue.add("Added " + UUID.randomUUID());

//            TileMapFactory.save(tileMap);
//            tileMap.toJson(".");
//            TileMapIO.encode(tileMap);
//            controller.getView().hideAuxPanels();
//            UserSavedData.getInstance().saveCharacter(speedQueue.peek());
//            UserSavedData.getInstance().createOrRead("tests.json");
//            logger.log("SAVING DATA");
        }

        if (controller.input.getKeyboard().isPressed(KeyEvent.VK_COMMA)) {
            // logger.info("Saving map... " + UUID.randomUUID());
            // uiLogQueue.add("Added " + UUID.randomUUID());

//            TileMapFactory.save(tileMap);
//            tileMap.toJson(".");
//            TileMapIO.encode(tileMap);
            controller.getView().hideAuxPanels();
        }
//        System.out.println(Camera.instance().get(Vector.class).toString());
//        System.out.println(controller.input.getMouse().position);
    }

    public Entity tryFetchingTileMousedAt() {
        Vector camera = Camera.getInstance().get(Vector.class);
        Mouse mouse = controller.input.getMouse();
        int titleBarHeight = Engine.getInstance().getController().view.getInsets().top;
        int column = (int) ((mouse.position.x + camera.x) / Constants.CURRENT_SPRITE_SIZE);
        int row = (int) ((mouse.position.y - titleBarHeight + camera.y) / Constants.CURRENT_SPRITE_SIZE);
        return tryFetchingTileAt(row, column);
    }

    public int getRows() { return tileMap.getRows(); }
    public int getColumns() { return tileMap.getColumns(); }

    public double getVisibleStartOfRows() {
        Vector pv = Camera.getInstance().get(Vector.class);
        return pv.y / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector pv = Camera.getInstance().get(Vector.class);
        return pv.x / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector pv = Camera.getInstance().get(Vector.class);
        Dimension d = Camera.getInstance().get(Dimension.class);
        return (pv.x + d.width) / (double) Constants.CURRENT_SPRITE_SIZE;
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector pv = Camera.getInstance().get(Vector.class);
        Dimension d = Camera.getInstance().get(Dimension.class);
        return (pv.y + d.height) / (double) Constants.CURRENT_SPRITE_SIZE;
    }

    public Entity tryFetchingTileAt(int row, int column) { return tileMap.tryFetchingTileAt(row, column); }
}
