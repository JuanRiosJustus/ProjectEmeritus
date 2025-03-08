package main.ui.custom;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.event.MouseAdapter;

public class MouseHoverEffect extends MouseAdapter {
    private final JComponent mComponent;
    private final Color mEntered;
    private final Color mExited;
    public MouseHoverEffect(JComponent component, Color entered, Color exited) {
        mEntered = entered;
        mExited = exited;
        mComponent = component;
    }
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        mComponent.setBackground(mEntered);
    }
    public void mouseExited(java.awt.event.MouseEvent evt) {
        mComponent.setBackground(mExited);
    }
}
