package main.input;

public class InputController {
    private final Keyboard keyboard = new Keyboard();
    private final Mouse mouse = new Mouse();
    private static InputController instance = null;
    public static InputController getInstance() {
        if (instance == null) {
            instance = new InputController();
        }
        return instance;
    }
    private InputController() { }
    public void update() { keyboard.update(); mouse.update(); }
    public Mouse getMouse() { return mouse; }
    public Keyboard getKeyboard() { return keyboard; }
}
