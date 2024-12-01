package main.input;

import javax.swing.JComponent;

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

    public void setup(JComponent component) {
        component.addMouseListener(mMouse);
        component.addMouseMotionListener(mMouse);
        component.addMouseWheelListener(mMouse);
    }
}
