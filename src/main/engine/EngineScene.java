package main.engine;



import main.graphics.GameUI;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public abstract class EngineScene extends GameUI {

    public EngineScene(int width, int height, String name) {
        super(true, width, height);
    }

    public EngineScene() { }

    public abstract void update();
    public abstract void input();
    public abstract JPanel render();
}
