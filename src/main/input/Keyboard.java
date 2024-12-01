package main.input;

import java.awt.KeyboardFocusManager;
import java.util.*;

public class Keyboard {
    private final Set<Integer> mPressedBuffer = new HashSet<>();
    private final Set<Integer> mPressed = new HashSet<>();

    public boolean getPressed() { return !mPressed.isEmpty(); }
    public boolean isPressed(int e) { return mPressed.contains(e); }
    public boolean isPressed() { return !mPressed.isEmpty(); }

    private static final int MAX_SUPPORTED_KEYCODES = 525;
    public Keyboard() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            System.out.println("Got key event " + e.getKeyChar());
            mPressedBuffer.add(e.getKeyCode());
            return false;
        });
    }

    public void update() {
        mPressed.clear();
        mPressed.addAll(mPressedBuffer);
        mPressedBuffer.clear();
    }
}
