package main.graphics;


import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import main.engine.EngineScene;
import main.game.main.GameModel;
import main.ui.huds.controls.UI;

public abstract class JScene extends JPanel {

    protected final JButton enterButton;
    protected final JButton exitButton;

    public JScene(int width, int height, String name) {
        enterButton = new JButton("Enter");
        exitButton = new JButton("Exit");
        setName(name.replaceAll("Panel", ""));
        setPreferredSize(new Dimension(width, height));
        setDoubleBuffered(true);
    }

    public JButton getEnterButton() { return enterButton; }
    public JButton getExitButton() { return exitButton; }

    public void setPreferredLocation(int x, int y) {
        setBounds(x, y, (int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
    }

    public void setName(String name) {
        super.setName(name);
        enterButton.setText(name);
        enterButton.setName(name);
        exitButton.setName(name);
    }

    public abstract void jSceneUpdate(GameModel model);

    public int getWidth() { return (int)getPreferredSize().getWidth(); }
    public int getHeight() { return (int)getPreferredSize().getHeight(); }
}
