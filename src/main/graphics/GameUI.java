package main.graphics;


import java.awt.Dimension;
import java.awt.FlowLayout;

import main.game.main.GameController;
import main.game.main.GameModel;

import javax.swing.JPanel;

public class GameUI extends JPanel {
    protected int mWidth = 0;
    protected int mHeight = 0;
    public GameUI() { this(0, 0, 0, 0, true); }
    public GameUI(int width, int height) { this(0, 0, width, height, true); }
    public GameUI(int x, int y, int width, int height) { this(x, y, width, height, true); }
    public GameUI(int x, int y, int width, int height, boolean autoRefresh) {
        mWidth = width;
        mHeight = height;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(width, height));
        setOpaque(autoRefresh);
        setBounds(x, y, width, height);
    }

    public void gameUpdate(GameModel model) { }
    public void gameUpdate(GameController gameController) { }
}
