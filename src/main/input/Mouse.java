package main.input;

import javafx.scene.input.MouseEvent;
import main.constants.Vector3f;

public class Mouse {
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

    public void setOnMouseClicked(MouseEvent e) {
        pressedBuffer = true;
        held = true;
        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
        buttonPressedBuffer = e.getButton().ordinal();
//        System.out.println("Mouse Clicked " + getMouseIdentity(e));
    }

    public void setOnMousePressed(MouseEvent e) {
        pressedBuffer = true;
        held = true;
        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
        buttonPressedBuffer = e.getButton().ordinal();
//        System.out.println("Mouse Pressed " + getMouseIdentity(e));
    }

    public void setOnMouseReleased(MouseEvent e) {
        releasedBuffer = true;
        pressedBuffer = false;
        held = false;
        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
//        System.out.println("Mouse Release " + getMouseIdentity(e));
    }

    public void setOnMouseDragged(MouseEvent e) {
        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
        held = true;
//        System.out.println("Mouse Dragged " + getMouseIdentity(e));
    }

    public void setOnMouseMoved(MouseEvent e) {
        position.copy((float) e.getX(), (float) e.getY(), (float) e.getZ());
//        System.out.println("Mouse Moved " + getMouseIdentity(e));
    }

    private String getMouseIdentity(MouseEvent e) {
        String str = "(" + e.getButton() + ") [" + e.getX() + " , " + e.getY() + ", " + e.getZ();
        return str;
    }

    public boolean isButtonBeingHeldDown() { return held; }
    public void mouseEntered(MouseEvent e) { onScreen = true; }
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

//    @Override
//    public void mouseWheelMoved(MouseWheelEvent e) {
//        wheelRotationBuffer = e.getWheelRotation();
//        wheeledBuffer = true;
//    }
}
