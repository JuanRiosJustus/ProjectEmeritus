package main.game.systems;

import main.ui.GameState;
import main.game.camera.Camera;
import main.game.components.SecondTimer;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.input.InputController;
import main.input.Keyboard;
import main.input.Mouse;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int edgeBuffer = 20;
    private final int speed = 128;
    private final Vector selected = Camera.getInstance().get(Vector.class).copy();
    private final SecondTimer selectionTimer = new SecondTimer();
    private boolean initialLockOn = false;
    private boolean starting = true;

    public void handle(InputController controls, GameModel model) {

        // Glide to the selected entity
        if (model.gameState.getBoolean(GameState.GLIDE_TO_SELECTED)) {
            Entity selected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
            if (selected != null) {
                Camera.getInstance().glide(selected.get(Vector.class));
                model.gameState.set(GameState.GLIDE_TO_SELECTED, false);
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
            Camera.getInstance().drag(current, controls.getMouse().isPressed());
            selected.copy(current);

            Entity selected = model.tryFetchingTileMousedAt();
            if (selected == null) { return; }
            // Disable rapid clicks that some mouses have??
            if (selectionTimer.elapsed() >= .2) {
                // Store the previous state
                model.gameState.set(GameState.PREVIOUSLY_SELECTED, model.gameState.getObject(GameState.CURRENTLY_SELECTED));
                boolean isMovePanelShowing = model.gameState.getBoolean(GameState.UI_MOVEMENT_PANEL_SHOWING);
                boolean isActionPanelShowing = model.gameState.getBoolean(GameState.UI_ACTION_PANEL_SHOWING);
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

    private void tryLockingOn(GameModel model) {
//        Entity first = model.speedQueue.peek();
//        if (first != null) {
//            selected.copy((first.get(Animation.class).animatedX()), (first.get(Animation.class).animatedY()));
//            Camera.instance().set(selected);
//        }
        Entity middle = model.tryFetchingTileAt(model.getRows() / 2, model.getColumns() / 2);
        Vector v = middle.get(Vector.class);
        Camera.getInstance().set(v);

        initialLockOn = true;
    }
}
