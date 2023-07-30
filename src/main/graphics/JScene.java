package main.graphics;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import main.game.main.GameModel;

public abstract class JScene extends JPanel {

    protected final JButton enterButton;
    protected final JButton exitButton;

    public JScene(int width, int height, String name) {
        enterButton = new JButton("Enter");
        exitButton = new JButton("Exit");
        setName(name.replaceAll("Panel", ""));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        setDoubleBuffered(true);
    }

    public JButton getEnterButton() { return enterButton; }
    public JButton getExitButton() { return exitButton; }

    public void setName(String name) {
        super.setName(name);
        enterButton.setText(name);
        enterButton.setName(name);
        exitButton.setName(name);
    }

    public abstract void update(GameModel model);

    public int getWidth() { return (int)getPreferredSize().getWidth(); }
    public int getHeight() { return (int)getPreferredSize().getHeight(); }

    public String getSimplePanelName() { return getName().replaceAll("Panel", ""); }
}
