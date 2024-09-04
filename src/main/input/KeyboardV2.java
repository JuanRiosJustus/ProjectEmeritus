package main.input;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public class KeyboardV2 {
    private final Set<Integer> mPressedBuffer = new HashSet<>();
    private final Set<Integer> mPressed = new HashSet<>();

    public boolean getPressed() { return !mPressed.isEmpty(); }
    public boolean isPressed(int e) { return mPressed.contains(e); }
    private final Map<Integer, AbstractAction> mKeyBindings = new HashMap<>();

    private static final int MAX_SUPPORTED_KEYCODES = 525;
    public KeyboardV2() {
        // MOST RELEVANT KEY CODES UNDER 525
        // https://stackoverflow.com/questions/15313469/java-keyboard-keycodes-list
        for (int index = 0; index < MAX_SUPPORTED_KEYCODES; index++) {
//            System.out.println(KeyEvent.getKeyText(index) +  " ?");
            if (KeyEvent.getKeyText(index).contains("Unknown")) { continue; }
            int finalIndex = index;
            mKeyBindings.put(index, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mPressedBuffer.add(finalIndex);
                    System.out.println(finalIndex + " ? " + e.getActionCommand());
                }
            });
        }
    }

    public void link(JComponent component) {
        InputMap[] inputMaps = new InputMap[]{
                component.getInputMap(JComponent.WHEN_FOCUSED),
                component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
                component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        };

        int iteration = 0;
        for (InputMap inputMap : inputMaps) {
            ActionMap actionMap = component.getActionMap();
            for (Map.Entry<Integer, AbstractAction> entry : mKeyBindings.entrySet()) {
                String mapper = String.valueOf(entry.getKey() + iteration);
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(entry.getKey());
                inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), mapper);
                actionMap.put(mapper, entry.getValue());
                iteration++;
            }
        }
    }

    public void update() {
        mPressed.clear();
        mPressed.addAll(mPressedBuffer);
        mPressedBuffer.clear();
    }
}
