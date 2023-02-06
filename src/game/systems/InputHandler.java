package game.systems;

import constants.GameStateKey;
import game.GameModel;
import game.camera.Camera;
import game.components.SecondTimer;
import game.components.Animation;
import game.components.Vector;
import game.entity.Entity;
import input.InputController;
import input.Keyboard;
import input.Mouse;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int edgeBuffer = 20;
    private final int speed = 128;
    private final Vector selected = Camera.instance().get(Vector.class).copy();
    private final SecondTimer selectionTimer = new SecondTimer();
    private boolean initialLockOn = false;
    private boolean starting = true;

    public void handle(InputController controls, GameModel model) {

        // Glide to the selected entity
        if (model.state.getBoolean(GameStateKey.ZOOM_TOO_SELECTED)) {
            Entity selected = (Entity) model.state.getObject(GameStateKey.CURRENTLY_SELECTED);
            if (selected != null) {
                Camera.instance().glide(selected.get(Vector.class));
                model.state.set(GameStateKey.ZOOM_TOO_SELECTED, false);
            };
        }

        if (!starting && !controls.getMouse().isOnScreen()) { return; }
        if (starting) { starting = false; }
//        if (!controls.mouse().isOnScreen() && !started) { started = true; return; }

//        if (controls.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
////            Engine.get().controller().model.ui.setVisible(!Engine.get().controller().model.ui.isVisible());
//        }

        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();
        Vector current = mouse.position;

        if (!initialLockOn) { tryLockingOn(model); }

        if (mouse.isHeld()) {
            Camera.instance().drag(current, controls.getMouse().isPressed());
            selected.copy(current);

            Entity selected = model.tryFetchingTileMousedAt();
            if (selected == null) { return; }
            // Disable rapid clicks that some mouses have??
            if (selectionTimer.elapsed() >= .2) {
                // Store the previous state
                model.state.set(GameStateKey.PREVIOUSLY_SELECTED, model.state.getObject(GameStateKey.CURRENTLY_SELECTED));
                if (mouse.isLeftButtonPressed()) {
                    model.state.set(GameStateKey.CURRENTLY_SELECTED, selected);
                } else if (mouse.isRightButtonPressed()) {
                    model.state.set(GameStateKey.CURRENTLY_SELECTED, null);
                }
                selectionTimer.reset();
            }

        } else if (model.state.getBoolean(GameStateKey.ZOOM_TOO_SELECTED)) {

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
                Camera.instance().glide(selected);
            }
        }
    }

    private void tryLockingOn(GameModel model) {
        Entity first = model.unitTurnQueue.peek();
        if (first != null) {
            selected.copy((first.get(Animation.class).animatedX()), (first.get(Animation.class).animatedY()));
            Camera.instance().set(selected);
        }
        initialLockOn = true;
    }
}
