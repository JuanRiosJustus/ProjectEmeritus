package main.ui.panels;

import main.game.components.Tile;
import main.game.main.GameModel;
import main.graphics.JScene;
import javax.swing.*;

import main.game.entity.Entity;

import java.awt.*;

public abstract class ControlPanelPane extends JScene {

    public ImagePanel topLeft;
    protected Entity currentTile;
    protected Entity currentUnit;
    protected Entity previousTile;
    protected Entity previousUnit;
    protected GameModel model;

    public final JPanel topRight = new JPanel();
    public final JPanel top = new JPanel();
    public final JPanel middle = new JPanel();
    public final JPanel bottom = new JPanel();

    public final JButton button1 = getExitButton();
    public final JButton button2 = new JButton("2");
    public final JButton button3 = new JButton("3");
    public final JButton button4 = new JButton("4");

    public ControlPanelPane(int width, int height, String name) {
        super(width, height, name);

        int topHeight = (int) (height * .45);
        int inv = (int) (height * .025);
        int bottomHeight = (int) (height * .4);
        int navHeight = (int) (height * .05);

        top.setPreferredSize(new Dimension(width, topHeight));
        top.add(createTopHalf(width, topHeight));
        add(top);

        // add(Box.createVerticalStrut(inv));

        middle.setPreferredSize(new Dimension(width, bottomHeight));
        add(middle);

        bottom.setPreferredSize(new Dimension(width, navHeight));
        bottom.setLayout(new GridLayout(1, 4));
        bottom.add(button1);
        bottom.add(button2);
        bottom.add(button3);
        bottom.add(button4);
        add(bottom);
    }

    private JPanel createTopHalf(int width, int height) {

        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;

        topLeft = new ImagePanel((int) (width * .25), height);
        result.add(topLeft, gbc);

        gbc.gridx = 1;
        topRight.setPreferredSize(new Dimension((int) (width * .7), height));

        result.add(topRight, gbc);
        result.setPreferredSize(new Dimension(width, height));

        return result;
    }

    protected abstract JScrollPane createTopRightPanel(JComponent reference);
    protected abstract JScrollPane createMiddlePanel(JComponent reference);
    public abstract void jSceneUpdate(GameModel gameModel);
    public void contentPaneUpdate(GameModel gameModel, Entity target) {
        if (gameModel == null) { return; }
        model = gameModel;
        if (target == null) { return; }
        if (previousTile != currentTile) { previousTile = currentTile; }
        currentTile = target;
        if (target.get(Tile.class) == null) { return; }
        if (previousUnit != currentUnit) { previousUnit = currentUnit; }
        currentUnit = target.get(Tile.class).unit;
        jSceneUpdate(model);
    }

    public Entity getUnitSelected() {
        return currentUnit;
    }
}
