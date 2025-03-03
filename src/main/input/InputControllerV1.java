package main.input;

import javax.swing.JComponent;

public class InputControllerV1 {
    private final MouseV1 mMouseV1 = new MouseV1();
    private final Keyboard mKeyBoard = new Keyboard();
    private static InputControllerV1 instance = null;
    public static InputControllerV1 getInstance() {
        if (instance == null) {
            instance = new InputControllerV1();
        }
        return instance;
    }

    private InputControllerV1() { }
    public void update() { mKeyBoard.update(); mMouseV1.update(); }
    public MouseV1 getMouse() { return mMouseV1; }
    public Keyboard getKeyboard() { return mKeyBoard; }

    public void setup(JComponent component) {
        component.addMouseListener(mMouseV1);
        component.addMouseMotionListener(mMouseV1);
        component.addMouseWheelListener(mMouseV1);
    }
}
