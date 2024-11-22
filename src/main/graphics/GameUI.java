package main.graphics;


import java.awt.Dimension;
import java.awt.FlowLayout;

import main.game.main.GameModel;

import javax.swing.JPanel;

public class GameUI extends JPanel {

    public GameUI() { setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); setOpaque(true); }
    public GameUI(int width, int height) { this(true, width, height); }
    public GameUI(boolean autoRefresh, int width, int height) {
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(width, height));
        setOpaque(autoRefresh);
    }

    public void gameUpdate(GameModel model) {
    }
}
