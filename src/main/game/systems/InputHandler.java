package main.game.systems;

import main.constants.GameState;
import main.constants.Settings;
import main.game.camera.Camera;
import main.game.components.SecondTimer;
import main.game.components.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.input.InputController;
import main.input.Keyboard;
import main.input.Mouse;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int edgeBuffer = 20;
    private final int speed = 128;
    private final Vector3f selected = null;
    private final SecondTimer selectionTimer = new SecondTimer();
    private boolean initialLockOn = false;
    private boolean starting = true;

    public void handle(InputController controls, GameModel model) {



//        System.out.println("CAMERA POSITION " + Camera.getInstance().getVector());

        // Glide to the selected entity
        if (model.mGameState.getBoolean(GameState.GLIDE_TO_SELECTED)) {
            Entity selected = (Entity) model.mGameState.getObject(GameState.CURRENTLY_SELECTED);
            if (selected != null) {
                Camera.getInstance().glide(selected.get(Vector3f.class));
                model.mGameState.put(GameState.GLIDE_TO_SELECTED, false);
            };
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

        Keyboard keyboard = controls.getKeyboard();
        if (keyboard.isPressed(KeyEvent.VK_C)) {
            model.getSettings().put(Settings.GAMEPLAY_SPRITE_WIDTH, (int) (model.getSettings().getSpriteWidth() * .8));
            model.getSettings().put(Settings.GAMEPLAY_SPRITE_HEIGHT, (int) (model.getSettings().getSpriteHeight() * .8));
        }


        Mouse mouse = controls.getMouse();
        Vector3f current = mouse.position;

        if (!initialLockOn) { tryLockingOn(model); }

        if (mouse.isHeld()) {
            Camera.getInstance().drag(current, controls.getMouse().isPressed());
//            selected.copy(current);

            Entity selected = model.tryFetchingTileMousedAt();
            if (selected == null) { return; }
            // Disable rapid clicks that some mouses have??
            if (selectionTimer.elapsed() >= .2) {
                // Store the previous state
                model.mGameState.put(GameState.PREVIOUSLY_SELECTED, model.mGameState.getObject(GameState.CURRENTLY_SELECTED));
                boolean isActionPanelShowing = model.mGameState.getBoolean(GameState.ACTION_HUD_IS_SHOWING);
                if (mouse.isLeftButtonPressed() && !isActionPanelShowing) {
                    if (selected == model.mGameState.getObject(GameState.CURRENTLY_SELECTED)) {
                        model.mGameState.put(GameState.CURRENTLY_SELECTED, null);
                    } else {
                        model.mGameState.put(GameState.CURRENTLY_SELECTED, selected);
                    }
                } else if (mouse.isRightButtonPressed()) {
                    model.mGameState.put(GameState.CURRENTLY_SELECTED, null);
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
                Camera.getInstance().glide(selected);
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


//        Entity middle = model.tryFetchingTileAt(model.getRows() / 2, model.getColumns() / 2);
//        Tile tile = middle.get(Tile.class);
//
//        Vector3f xAndY = model.getCamera().getGlobalCoordinates(
//                tile.getColumn() * model.getSettings().getSpriteWidth(),
//                tile.getRow() * model.getSettings().getSpriteHeight()
//        );


//        model.getCamera().set(xAndY);

        initialLockOn = true;
    }
}
