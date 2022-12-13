package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class Keyboard implements KeyListener {

    private final Set<Integer> pressedBuffer = new HashSet<>();
    private final Set<Integer> releasedBuffer = new HashSet<>();
    private final Set<Integer> typedBuffer = new HashSet<>();
    private final Set<Integer> pressed = new HashSet<>();
    private final Set<Integer> m_released = new HashSet<>();
    private final Set<Integer> m_typed = new HashSet<>();

    public boolean isPressed() { return pressed.size() > 0; }
    public boolean isPressed(int e) { return pressed.contains(e); }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println(e.getKeyCode() + " typed");
        typedBuffer.add(e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println(e.getKeyCode() + " pressed");
        pressedBuffer.add(e.getKeyCode()); }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println(e.getKeyCode() + " released");
        releasedBuffer.add(e.getKeyCode());
    }

    public void update() {
        m_typed.clear();
        pressed.clear();
        m_released.clear();

        m_typed.addAll(typedBuffer);
        pressed.addAll(pressedBuffer);
        m_released.addAll(releasedBuffer);

        typedBuffer.clear();
        pressedBuffer.clear();
        releasedBuffer.clear();
    }
}
