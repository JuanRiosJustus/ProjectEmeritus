package main.ui.huds.controls;

import javax.swing.JPanel;
import java.awt.Graphics;

public class JGamePanel extends JPanel {

    public JGamePanel() {
        setDoubleBuffered(true);
        setOpaque(false);
    }

    public JGamePanel(boolean isOpaque) {
        setDoubleBuffered(true);
        setOpaque(isOpaque);
    }

    @Override
    public void update(Graphics g) {
        paintComponent(g);
    }
}
