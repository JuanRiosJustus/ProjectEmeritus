package input;

public class InputController {

    private final Keyboard keyboard = new Keyboard();
    private final Mouse mouse = new Mouse();
    public static InputController instance = new InputController();

    public static InputController instance() { return instance; }

    private InputController() { }
    public void update() { keyboard.update(); mouse.update(); }
    public Mouse mouse() { return mouse; }
    public Keyboard keyboard() { return keyboard; }
}
