package main.input;

import javafx.scene.input.KeyEvent;

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
    public void setOnKeyPressed(KeyEvent e) {
        mPressedBuffer.add(e.getCode().getCode());
//        pressedBuffer = true;
//        held = true;
//        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
//        buttonPressedBuffer = e.getButton().ordinal();
//        System.out.println("Mouse Pressed " + getMouseIdentity(e));
    }

    public void setOnKeyReleased() {
//        releasedBuffer = true;
//        pressedBuffer = false;
//        held = false;
//        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
//        System.out.println("Mouse Release " + getMouseIdentity(e));
    }

    public void update() {
        mPressed.clear();
        mPressed.addAll(mPressedBuffer);
        mPressedBuffer.clear();
    }
}
