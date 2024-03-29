package main.engine;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public abstract class EngineScene extends JPanel {

    public EngineScene(int width, int height, String name) {
        setName(name.replaceAll("Panel", ""));
        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());
        setDoubleBuffered(true);
    }

    public EngineScene() { }

    public abstract void update();
    public abstract void input();
    public abstract JPanel render();
}
