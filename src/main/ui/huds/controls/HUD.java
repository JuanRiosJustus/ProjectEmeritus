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

    protected Entity mCurrentTile;
    protected Entity mPreviousTile;
    protected Entity mCurrentUnit;
    protected Entity mPreviousUnit;

    protected GameModel model;
    public ImagePanel mImagePanel;

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(HUD.class);

    public HUD(int width, int height, int x, int y) {
        super(width, height, HUD.class.getSimpleName());
    }
    public HUD(int width, int height, int x, int y, String name) {
        super(width, height, x, y, name);
    }

    public void jSceneUpdate(GameModel gameModel, Entity entity) {
        model = gameModel;

        mPreviousUnit = (mPreviousTile == null ? null : mPreviousTile.get(Tile.class).mUnit);
        mPreviousTile = mCurrentTile;
        mCurrentUnit = (entity == null ? null : entity.get(Tile.class).mUnit);
        mCurrentTile = entity;

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
