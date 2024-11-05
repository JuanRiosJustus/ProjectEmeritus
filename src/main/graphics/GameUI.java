package main.graphics;


import java.awt.Dimension;
import java.awt.FlowLayout;

import main.game.main.GameModel;
import main.ui.huds.controls.JGamePanel;

public class GameUI extends JGamePanel {

    public GameUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }
    public GameUI(int width, int height) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(width, height));
    }

    public void gameUpdate(GameModel model) {

    }
}
