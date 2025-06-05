package main.input;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class InputController {
    private final Mouse mMouse = new Mouse();
    private final Keyboard mKeyBoard = new Keyboard();
    private static InputController instance = null;
    public static InputController getInstance() {
        if (instance == null) {
            instance = new InputController();
        }
        return instance;
    }

    private InputController() { }
    public void update() { mKeyBoard.update(); mMouse.update(); }
    public Mouse getMouse() { return mMouse; }
    public Keyboard getKeyboard() { return mKeyBoard; }

    public void setup(Scene scene) {
        scene.setOnMousePressed(mMouse::setOnMousePressed);
        scene.setOnMouseReleased(mMouse::setOnMouseReleased);
        scene.setOnMouseMoved(mMouse::setOnMouseMoved);
        scene.setOnMouseDragged(mMouse::setOnMouseDragged);

        scene.setOnKeyPressed(mKeyBoard::setOnKeyPressed);
    }

    public void clear(Scene scene) {
        if (scene == null) { return; }

        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
        scene.setOnMouseClicked(null);
        scene.setOnMousePressed(null);
        scene.setOnMouseReleased(null);
        scene.setOnMouseMoved(null);
        scene.setOnMouseDragged(null);
    }
}
