package graphics;


import utils.ComponentUtils;

import javax.swing.*;
import java.awt.*;

public class JScene extends JPanel {

    protected final JButton enterButton;
    protected final JButton exitButton;

    public boolean update = false;

    public JScene(int width, int height, String name) {
        enterButton = new JButton(name);
        exitButton = new JButton("Exit");
        setName(name);
        mold(width, height);
        setDoubleBuffered(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void mold(int width, int height) {
        ComponentUtils.setSize(this, width, height);
    }

    public void setLayout(Component component) { add(component); }
    public JButton getEnterButton() { return enterButton; }
    public JButton getExitButton() { return exitButton; }

    public void setName(String name) {
        super.setName(name);
        enterButton.setText(name);
        enterButton.setName(name);
        exitButton.setName(name);
    }
}
