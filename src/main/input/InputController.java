package main.input;

public class InputController {
    private final Keyboard mKeyBoard = new Keyboard();
    private final Mouse mMouse = new Mouse();
    private final KeyboardV2 mKeyBoardV2 = new KeyboardV2();
    private static InputController instance = null;
    public static InputController getInstance() {
        if (instance == null) {
            instance = new InputController();
        }
        return instance;
    }
    private InputController() { }
    public void update() { mKeyBoard.update(); mMouse.update(); mKeyBoardV2.update(); }
    public Mouse getMouse() { return mMouse; }
    public Keyboard getKeyboard() { return mKeyBoard; }
    public KeyboardV2 getKeyboardV2() { return mKeyBoardV2; }
}
