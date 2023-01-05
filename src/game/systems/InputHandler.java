package game.systems;

import constants.Constants;
import game.GameModel;
import game.camera.Camera;
import game.components.SpriteAnimation;
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
    private boolean initialLockOn = false;
    private boolean starting = true;

    public void handle(InputController controls, GameModel model) {
//        InputController controls = Engine.instance().controller.model.input;
//        Keyboard keyboard = controls.getKeyboard();
//        Mouse mouse = controls.getMouse();
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
        } else if (mouse.isPressed()) {
            System.out.println("yooo");
//        } else if (mouse.isWheeled())  {
//            if (mouse.getWheelRotation() < 0) {
//                Constants.CURRENT_SPRITE_SIZE++;
//            } else if (mouse.getWheelRotation() > 0) {
//                Constants.CURRENT_SPRITE_SIZE--;
//            }
            System.out.println("Wheeled");
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
        Entity first = model.queue.peek();
        if (first != null) {
            selected.copy(
                    (first.get(SpriteAnimation.class).animatedX()),
                    (first.get(SpriteAnimation.class).animatedY())
            );
            Camera.instance().set(selected);
        }
        initialLockOn = true;
    }
}
