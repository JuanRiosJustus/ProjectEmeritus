package graphics;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import utils.ComponentUtils;

public class JScene extends JPanel {

    protected final JButton enterButton;
    protected final JButton exitButton;

    public JScene(int width, int height, String name) {
        enterButton = new JButton("Enter");
        exitButton = new JButton("Exit");
        setName(name);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        setDoubleBuffered(true);
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
