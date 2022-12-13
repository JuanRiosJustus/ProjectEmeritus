package input;

public class InputController {

    private final Keyboard keyboard = new Keyboard();
    private final Mouse mouse = new Mouse();
    private static InputController instance = null;
    private InputController() { }

    public static InputController get() {
        if (instance == null) {
            instance = new InputController();
        }
        return instance;
    }

    public void update() { keyboard.update(); mouse.update(); }
    public Mouse mouse() { return mouse; }
    public Keyboard keyboard() { return keyboard; }
}
