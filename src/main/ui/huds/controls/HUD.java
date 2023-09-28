package main.ui.huds.controls;

import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.ImagePanel;

import javax.swing.*;
import java.awt.Dimension;

public abstract class HUD extends JScene {

    protected Entity currentTile;
    protected Entity previousTile;
    protected Entity currentUnit;
    protected Entity previousUnit;

    protected GameModel model;
    public ImagePanel selection;

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(HUD.class);

    public HUD(int width, int height, String name) {
        super(width, height, name);
    }

    public void jSceneUpdate(GameModel gameModel, Entity entity) {
        model = gameModel;

        previousUnit = (previousTile == null ? null : previousTile.get(Tile.class).unit);
        previousTile = currentTile;
        currentUnit = (entity == null ? null : entity.get(Tile.class).unit);
        currentTile = entity;

        jSceneUpdate(model);
    }

    public static JScrollPane createScalingPane(int width, int height, JPanel panel) {

        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
        scrollPane.setPreferredSize(new Dimension(width, height));

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }
}
