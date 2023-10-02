package main.game.main;

import java.awt.event.KeyEvent;
import java.util.SplittableRandom;

import main.constants.Settings;
import main.engine.Engine;
import main.game.camera.Camera;
import main.game.components.Size;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.logging.ActivityLogger;
import main.game.map.TileMap;
import main.game.map.builders.TileMapBuilder;
import main.game.queue.SpeedQueue;
import main.game.stores.factories.UnitFactory;
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
    private GameController mGameController = null;
    public InputHandler input = null;
    public UpdateSystem system = null;
    private boolean running = false;
    public GameModel(GameController gc) { mGameController = gc; }

    public void initialize(GameController gc) {
        mGameController = gc;

        system = new UpdateSystem();
        input = new InputHandler();
        random = new SplittableRandom();
        mousePosition = new Vector();
        gameState = new GameState();
        logger = new ActivityLogger();
        speedQueue = new SpeedQueue();

        setup();
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

        tileMap.placeRandomly(speedQueue);
//        tileMap.placeByTeam(speedQueue, 2, 2);
    }

    public void update() {
        if (!running) { return; }
        system.update(this);
//        UpdateSystem.update(this, controller.input);
        Camera.getInstance().update();
    }

    public void input() {
        if (!running) { return; }
        mGameController.mInputController.update();
        input.handle(mGameController.mInputController, this);
        mousePosition.copy(mGameController.mInputController.getMouse().position);

        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
            setup();
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
//            UserSavedData.getInstance().save(speedQueue.peek());
//            UserSavedData.getInstance().createOrRead("test.json");
//            UserSavedData.getInstance().createOrRead("tests.json");
            logger.log("SAVING DATA");
        }

        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_P)) {
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

        if (mGameController.mInputController.getKeyboard().isPressed(KeyEvent.VK_COMMA)) {
            // logger.info("Saving map... " + UUID.randomUUID());
            // uiLogQueue.add("Added " + UUID.randomUUID());

//            TileMapFactory.save(tileMap);
//            tileMap.toJson(".");
//            TileMapIO.encode(tileMap);
            mGameController.getView().hideAuxPanels();
        }
//        System.out.println(Camera.instance().get(Vector.class).toString());
//        System.out.println(controller.input.getMouse().position);
    }

    public Entity tryFetchingTileMousedAt() {
        Vector camera = Camera.getInstance().get(Vector.class);
        Mouse mouse = mGameController.mInputController.getMouse();
        int titleBarHeight = Engine.getInstance().getHeaderSize();
        int spriteSize = Settings.getInstance().getSpriteSize();
        int column = (int) ((mouse.position.x + camera.x) / spriteSize);
        int row = (int) ((mouse.position.y - titleBarHeight + camera.y) / spriteSize);
        return tryFetchingTileAt(row, column);
    }

    public boolean isRunning() { return running; }
    public void run() { running = true; initialize(mGameController); }
    public void stop() { running = false; }

    public int getRows() { return tileMap.getRows(); }
    public int getColumns() { return tileMap.getColumns(); }

    public double getVisibleStartOfRows() {
        Vector pv = Camera.getInstance().get(Vector.class);
        return pv.y / (double) Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
    }
    // How much our camera has moved in terms of tiles on the y axis
    public double getVisibleStartOfColumns() {
        Vector pv = Camera.getInstance().get(Vector.class);
        return pv.x / (double) Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
    }
    // How much our camera has moved in terms of tiles on the x axis on the other end of the screen (width)
    public double getVisibleEndOfColumns() {
        Vector pv = Camera.getInstance().get(Vector.class);
        Size d = Camera.getInstance().get(Size.class);
        return (pv.x + d.width) / (double) Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
    }
    // How much our camera has moved in terms of tiles on the y axis on the other end of the screen (height)
    public double getVisibleEndOfRows() {
        Vector pv = Camera.getInstance().get(Vector.class);
        Size d = Camera.getInstance().get(Size.class);
        return (pv.y + d.height) / (double) Settings.getInstance().getInteger(Settings.GAMEPLAY_CURRENT_SPRITE_SIZE);
    }

    public Entity tryFetchingTileAt(int row, int column) { return tileMap.tryFetchingTileAt(row, column); }

    private void setup() {

//        tileMap = LargeContinousRoom.newBuilder()
//        tileMap = HauberkDungeonMap.newBuilder()
//        tileMap = BorderedMapWithBorderedRooms.newBuilder()
//        tileMap = LargeBorderedRoom.newBuilder()
//        tileMap = NoBorderWithSmallRooms.newBuilder()

        try {
//            tileMap = (TileMap) UserSavedData.getInstance().loadObject("test.tilemap");
//            tileMap.reload();
//            tileMap = TileMapFactory.load("/Users/justusbrown/Desktop/ProjectEmeritus/ProjectEmeritus/2023-09-04-23-20.json");
//            throw new Exception("test");
            System.out.println("Success!");
        } catch (Exception ex) {
//            tileMap = BasicOpenMap.newBuilder()
//                .setRowAndColumn(11, 20)
//                .setSeed(random.nextLong())
//                .setExiting(2)
//                .setZoom(.9f)
//                .setWalling(wall)
//                .setFlooring(floor)
//                .setStructure(structure)
//                .setLiquid(liquid)
//                .build();
        }
//        tileMap = TileMapFactory.random(11, 20);
        tileMap = TileMapBuilder.createRandom(15, 20);
    }
}
