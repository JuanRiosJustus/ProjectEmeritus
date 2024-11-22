package main.engine;



import main.graphics.GameUI;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public abstract class EngineSceneV1 extends GameUI {

    public EngineSceneV1(int width, int height, String name) {
        setName(name.replaceAll("Panel", ""));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setLayout(new GridBagLayout());
        setDoubleBuffered(true);
    }

    public EngineSceneV1() { }

    public abstract void update();
    public abstract void input();
    public abstract JPanel render();
}
