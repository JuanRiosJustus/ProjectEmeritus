package game.systems;

import engine.Engine;
import game.GameModel;
import game.camera.Camera;
import game.components.SpriteAnimation;
import game.components.Vector;
import game.entity.Entity;
import input.InputController;
import input.Keyboard;
import input.Mouse;

import java.awt.event.KeyEvent;

public class CameraSystem {

    private static final int edgeBuffer = 20;
    private static final int speed = 128;
    private static final Vector selected = Camera.get().get(Vector.class).copy();
    private static boolean initialLockOn = false;
    private static boolean starting = true;

    public static void handle(InputController control, GameModel model) {
        InputController controls = Engine.instance.controller.model.input;
        if (!starting && !controls.mouse().isOnScreen()) { return; }
        if (starting) { starting = false; }
//        if (!controls.mouse().isOnScreen() && !started) { started = true; return; }

        if (controls.keyboard().isPressed(KeyEvent.VK_SPACE)) {
//            Engine.get().controller().model.ui.setVisible(!Engine.get().controller().model.ui.isVisible());
        }

        Keyboard keyboard = controls.keyboard();
        Mouse mouse = controls.mouse();
        Vector current = mouse.position;

        if (!initialLockOn) {
            tryLockingOn(model);
        }

        if (mouse.isHeld()) {
            Camera.get().drag(current, controls.mouse().isPressed());
            selected.copy(current);
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
                Camera.get().glide(selected);
            }
        }
    }

    private static void tryLockingOn(GameModel model) {
        Entity first = model.queue.peek();
        if (first != null) {
            selected.copy(
                    (first.get(SpriteAnimation.class).animatedX()),
                    (first.get(SpriteAnimation.class).animatedY())
            );
            Camera.get().set(selected);
        }
        initialLockOn = true;
    }
}
