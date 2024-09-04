package main.input;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Keyboard implements KeyListener {
    private final Set<Integer> pressedBuffer = new HashSet<>();
    private final Set<Integer> releasedBuffer = new HashSet<>();
    private final Set<Integer> typedBuffer = new HashSet<>();
    private final Set<Integer> pressed = ConcurrentHashMap.newKeySet();
    private final Set<Integer> released = new HashSet<>();
    private final Set<Integer> typed = new HashSet<>();

    public boolean isPressed() { return !pressed.isEmpty(); }
    public boolean isPressed(int e) { return pressed.contains(e); }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println(e.getKeyCode() + " typed");
        typedBuffer.add(e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println(e.getKeyCode() + " pressed");
        pressedBuffer.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println(e.getKeyCode() + " released");
        releasedBuffer.add(e.getKeyCode());
    }

    public void update() {
        typed.clear();
        pressed.clear();
        released.clear();

        typed.addAll(typedBuffer);
        pressed.addAll(pressedBuffer);
        released.addAll(releasedBuffer);

        typedBuffer.clear();
        pressedBuffer.clear();
        releasedBuffer.clear();
    }
}
