package main.game.systems;

import main.constants.GameState;
import main.game.camera.Camera;
import main.game.components.SecondTimer;
import main.game.components.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.input.InputController;
import main.input.Keyboard;
import main.input.Mouse;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int edgeBuffer = 20;
    private final int speed = 128;
    private final Vector3f selected = Camera.getInstance().get(Vector3f.class).copy();
    private final SecondTimer selectionTimer = new SecondTimer();
    private boolean initialLockOn = false;
    private boolean starting = true;

    public void handle(InputController controls, GameModel model) {

//        System.out.println("CAMERA POSITION " + Camera.getInstance().getVector());

        // Glide to the selected entity
        if (model.gameState.getBoolean(GameState.GLIDE_TO_SELECTED)) {
            Entity selected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
            if (selected != null) {
                Camera.getInstance().glide(selected.get(Vector3f.class));
                model.gameState.set(GameState.GLIDE_TO_SELECTED, false);
            };
        }

        if (!starting && !controls.getMouse().isOnScreen()) { return; }
        if (starting) {
//            Camera.getInstance().set(new Vector3f(0, 0));
            starting = false;
            return;
        }
//        if (!controls.mouse().isOnScreen() && !started) { started = true; return; }

//        if (controls.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
////            Engine.get().controller().model.ui.setVisible(!Engine.get().controller().model.ui.isVisible());
//        }

        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();
        Vector3f current = mouse.position;

        if (!initialLockOn) { tryLockingOn(model); }

        if (mouse.isHeld()) {
            Camera.getInstance().drag(current, controls.getMouse().isPressed());
            selected.copy(current);

            Entity selected = model.tryFetchingTileMousedAt();
            if (selected == null) { return; }
            // Disable rapid clicks that some mouses have??
            if (selectionTimer.elapsed() >= .2) {
                // Store the previous state
                model.gameState.set(GameState.PREVIOUSLY_SELECTED, model.gameState.getObject(GameState.CURRENTLY_SELECTED));
                boolean isMovePanelShowing = model.gameState.getBoolean(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING);
                boolean isActionPanelShowing = model.gameState.getBoolean(GameState.ACTION_HUD_IS_SHOWING);
                boolean hasSelection = model.gameState.getObject(GameState.CURRENTLY_SELECTED) != null;
                if (mouse.isLeftButtonPressed() && !isActionPanelShowing) {
                    if (selected == model.gameState.getObject(GameState.CURRENTLY_SELECTED)) {
                        model.gameState.set(GameState.CURRENTLY_SELECTED, null);
                    } else {
                        model.gameState.set(GameState.CURRENTLY_SELECTED, selected);
                    }
                } else if (mouse.isRightButtonPressed()) {
                    model.gameState.set(GameState.CURRENTLY_SELECTED, null);
                }
                selectionTimer.reset();
            }

        } else if (model.gameState.getBoolean(GameState.GLIDE_TO_SELECTED)) {

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
        Entity first = model.speedQueue.peek();
        if (first != null) {

        }
//        if (first != null) {
//            selected.copy((first.get(Animation.class).animatedX()), (first.get(Animation.class).animatedY()));
//            Camera.instance().set(selected);
//        }


        Entity middle = model.tryFetchingTileAt(model.getRows() / 2, model.getColumns() / 2);
        Vector3f v = middle.get(Vector3f.class);
        Camera.getInstance().set(v);
//        Camera.getInstance().set(new Vector3f());

        initialLockOn = true;
    }
}
