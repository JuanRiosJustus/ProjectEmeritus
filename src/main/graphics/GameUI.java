package main.graphics;


import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import main.game.main.GameModel;
import main.ui.components.OutlineButton;
import main.ui.huds.controls.JGamePanel;

public abstract class GameUI extends JGamePanel {
    protected final int mWidth;
    protected final int mHeight;

    public GameUI(int width, int height, String name) {
        this(width, height, 0, 0, name);
    }
    public GameUI(int width, int height, int x, int y, String name) {
        this(
                width,
                height,
                x,
                y,
                new OutlineButton("Enter", SwingConstants.CENTER),
                new OutlineButton("Exit", SwingConstants.CENTER),
                name
        );
    }

    public GameUI(int width, int height, int x, int y, JButton enter, JButton exit, String name) {
        mWidth = width;
        mHeight = height;
        setPreferredSize(new Dimension(width, height));
        setPreferredLocation(x, y);
    }

    public void setPreferredLocation(int x, int y) {
        setBounds(x, y, (int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
    }

    public abstract void gameUpdate(GameModel model);
}
