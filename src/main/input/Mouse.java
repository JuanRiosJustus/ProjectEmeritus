package main.input;


import main.constants.Vector3f;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

    public final Vector3f position;
    private boolean released;
    private boolean pressed;
    private boolean pressedBuffer;
    private boolean releasedBuffer;
    private boolean wheeledBuffer;
    private boolean wheeled;
    private boolean held;
    private boolean onScreen;
    private int wheelRotation = 0;
    private int buttonPressedBuffer = 0;
    private int wheelRotationBuffer = 0;

    public boolean isPressed() { return pressed; }
    public boolean isReleased() { return released; }
    public boolean isWheeled() { return wheeled; }
    public boolean isOnScreen() { return onScreen; }
    public boolean isLeftButtonPressed() { return position.z == 1; }
    public boolean isRightButtonPressed() { return position.z == 3; }
    public boolean isWheelPressed() { return position.z == 2; }
    public int getWheelRotation() { return wheelRotation; }

    public Vector3f getPosition() { return position; }
    public Mouse() { position = new Vector3f(0, 0, 0); }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println(e.getX() + " , " + e.getY());
//        pressedBuffer = true;
//        held = true;
//        position.copy(e.getX(), e.getY(), 0);
//        buttonPressedBuffer = e.getButton();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getX() + " , " + e.getY());
        pressedBuffer = true;
        held = true;
        position.copy(e.getX(), e.getY(), 0);
        buttonPressedBuffer = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println(e.getX() + " , " + e.getY());
        releasedBuffer = true;
        pressedBuffer = false;
        held = false;
        position.copy(e.getX(), e.getY(), 0);
    }
    public boolean isHeld() { return held; }
    @Override
    public void mouseEntered(MouseEvent e) { onScreen = true; }
    @Override
    public void mouseExited(MouseEvent e) {
        onScreen = false;
    }

    public void update() {
        position.z = 0;
        pressed = false;
        released = false;
        wheeled = false;
        wheelRotation = wheelRotationBuffer;

        if (pressedBuffer) {
            pressed = true;
            position.z = buttonPressedBuffer;
        }

        if (releasedBuffer) {
            released = true;
            position.z = buttonPressedBuffer;
        }

        if (wheeledBuffer) {
            wheeled = true;
            position.z = buttonPressedBuffer;
        }

        buttonPressedBuffer = 0;
        pressedBuffer = false;
        releasedBuffer = false;
        wheeledBuffer = false;
        wheelRotationBuffer = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        System.out.println(e.getX() + " , " + e.getY());
        position.copy(e.getX(), e.getY(), 0);
        held = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println(e.getX() + " , " + e.getY());
        position.copy(e.getX(), e.getY(), 0);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        wheelRotationBuffer = e.getWheelRotation();
        wheeledBuffer = true;
    }
}