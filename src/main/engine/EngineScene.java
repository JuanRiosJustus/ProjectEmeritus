package main.engine;



import main.graphics.GameUI;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public abstract class EngineScene extends GameUI {

    public EngineScene(int width, int height) {
        super(0, 0, width, height, true);
    }

    public EngineScene() { }

    public abstract void update();
    public abstract void input();
    public abstract JPanel render();
}
