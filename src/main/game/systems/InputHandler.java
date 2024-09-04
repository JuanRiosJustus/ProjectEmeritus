package main.game.systems;

import main.game.components.tile.Tile;
import main.game.main.GameState;
import main.game.main.Settings;
import main.game.components.SecondTimer;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.input.InputController;
import main.input.KeyboardV2;
import main.input.Mouse;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int edgeBuffer = 20;
    private final int speed = 128;
    private final Vector3f selected = new Vector3f();
    private final SecondTimer selectionTimer = new SecondTimer();
    private boolean initialLockOn = false;

    public void handle(InputController controls, GameModel model) {
//        System.out.println("CAMERA POSITION " + Camera.getInstance().getVector());

        // Glide to the selected entity
        Entity tileToGlideTo = model.getGameState().getTileToGlideTo();
        if (tileToGlideTo != null) {
            model.getCamera().glide(model, tileToGlideTo);
            model.getGameState().setTileToGlideTo(null);
        }

//        if (!starting && !controls.getMouse().isOnScreen()) { return; }
//        if (starting) {
////            model.getCamera().set(new Vector3f(0, 0));
////            Camera.getInstance().set(new Vector3f(0, 0)); .
//            starting = false;
//            return;
//        }
//        if (!controls.mouse().isOnScreen() && !started) { started = true; return; }

//        if (controls.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
////            Engine.get().controller().model.ui.setVisible(!Engine.get().controller().model.ui.isVisible());
//        }



        KeyboardV2 keyboard = controls.getKeyboardV2();

        if (keyboard.isPressed(KeyEvent.VK_9)) {
            model.getSettings().put(Settings.GAMEPLAY_SPRITE_WIDTH, (int) (model.getSettings().getSpriteWidth() * .8));
            model.getSettings().put(Settings.GAMEPLAY_SPRITE_HEIGHT, (int) (model.getSettings().getSpriteHeight() * .8));
            System.out.println("GETTING SMALLER");
        }

        if (keyboard.isPressed(KeyEvent.VK_0)) {
            model.getSettings().put(Settings.GAMEPLAY_SPRITE_WIDTH, (int) (model.getSettings().getSpriteWidth() * 1.2));
            model.getSettings().put(Settings.GAMEPLAY_SPRITE_HEIGHT, (int) (model.getSettings().getSpriteHeight() * 1.2));
            System.out.println("GETTING BIGGER");
        }


        Mouse mouse = controls.getMouse();
        Vector3f current = mouse.position;

        if (!initialLockOn) { tryLockingOn(model); }

        if (mouse.isHeld()) {
            model.getCamera().drag(current, controls.getMouse().isPressed());
//            selected.copy(current);

            Entity selected = model.tryFetchingTileMousedAt();
            if (selected == null) { return; }
            // Disable rapid clicks that some mouses have??
            if (selectionTimer.elapsed() >= .2) {
                // Store the previous state
                Entity currentlySelected = model.getGameState().getCurrentlySelectedTileEntity();
                model.mGameState.put(GameState.PREVIOUSLY_SELECTED, currentlySelected);
                boolean isActionPanelOpen = model.getGameState().isActionPanelOpen();
                if (mouse.isLeftButtonPressed() && !isActionPanelOpen) {
                    model.getGameState().setupEntitySelections(selected);
                } else if (mouse.isRightButtonPressed()) {
                    model.getGameState().setupEntitySelections(null);
                }
                selectionTimer.reset();
            }

        } else if (model.mGameState.getBoolean(GameState.GLIDE_TO_SELECTED)) {

//            Entity selected = (Entity) model.state.getObject(GameStateKey.CURRENTLY_SELECTED);
//            if (selected != null) {
//                Camera.instance().glide(selected.get(Vector.class));
//                model.state.set(GameStateKey.ZOOM_TOO_SELECTED, false);
//                System.out.println("chamgd");
//            }
        } else {
            boolean cornering = false;

            if (keyboard.isPressed(KeyEvent.VK_A)) {
                selected.x -= speed;
                cornering = true;
            }
            if (keyboard.isPressed(KeyEvent.VK_D)) {
                selected.x += speed;
                cornering = true;
            }
            if (keyboard.isPressed(KeyEvent.VK_W)) {
                selected.y -= speed;
                cornering = true;
            }
            if (keyboard.isPressed(KeyEvent.VK_S)) {
                selected.y += speed;
                cornering = true;
            }
            if (cornering) {
                model.getCamera().glide(selected);
            }
        }
    }

    public void set(GameModel model, Vector3f current) {
//        Camera.getInstance().set(current);
//        selected.copy(current);
    }
    private void tryLockingOn(GameModel model) {
        Entity first = model.mSpeedQueue.peek();
//        return;

//        if (first != null) {
//            selected.copy((first.get(Animation.class).animatedX()), (first.get(Animation.class).animatedY()));
//            Camera.instance().set(selected);
//        }


        Entity middle = model.tryFetchingTileAt(model.getRows() / 2, model.getColumns() / 2);
        Tile tile = middle.get(Tile.class);

        int x = tile.getColumn() * model.getSettings().getSpriteWidth();
        int y = tile.getRow() * model.getSettings().getSpriteHeight();
        Vector3f xAndY = model.getCamera().getGlobalCoordinates(
                (int) (x - (x * .25)),
                (int) (y - (y * .25))
        );


//        model.getCamera().set(xAndY);

        initialLockOn = true;
    }
}
