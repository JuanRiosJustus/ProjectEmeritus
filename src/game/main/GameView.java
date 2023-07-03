package game.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import constants.ColorPalette;
import constants.Constants;
import constants.GameStateKey;
import game.camera.Camera;
import game.collectibles.Gem;
import game.components.ActionManager;
import game.components.OverlayAnimation;
import game.components.Dimension;
import game.components.Inventory;
import game.components.MovementManager;
import game.components.Animation;
import game.components.Tile;
import game.components.Vector;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Resource;
import game.entity.Entity;
import game.stores.pools.AssetPool;
import game.stores.pools.FontPool;
import logging.Logger;
import logging.LoggerFactory;
import ui.panels.ControlPanel;
import ui.panels.GamePanel;
import ui.panels.LogPanel;
import ui.panels.TurnOrderPanel;
import utils.MathUtils;


public class GameView extends JPanel {

    private final GameController controller;
    private final ControlPanel controlPanel;
    private final TurnOrderPanel turnOrderPanel;
    private final LogPanel loggerPanel;
    private final GamePanel gamePanel;

    private Logger logger = LoggerFactory.instance().logger(getClass());

    public GameView(GameController gc) {
        controller = gc;
        controller.getModel();
        controlPanel =  new ControlPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        turnOrderPanel = new TurnOrderPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        loggerPanel = new LogPanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        gamePanel = new GamePanel(gc, Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);

        // /**
        //  * Exception in thread "main" java.lang.NullPointerException: Cannot store to object array because "this.xChildren" is null
        // at java.desktop/javax.swing.OverlayLayout.checkRequests(OverlayLayout.java:273)
        // at java.desktop/javax.swing.OverlayLayout.layoutContainer(OverlayLayout.java:225)
        // at java.desktop/java.awt.Container.layout(Container.java:1541)
        // at java.desktop/java.awt.Container.doLayout(Container.java:1530)
        // at java.desktop/java.awt.Container.validateTree(Container.java:1725)
        // at java.desktop/java.awt.Container.validateTree(Container.java:1734)
        // at java.desktop/java.awt.Container.validateTree(Container.java:1734)
        //  */
        setLayout(new OverlayLayout(this));
        add(turnOrderPanel);
        add(controlPanel);
        add(loggerPanel);
        add(gamePanel);
        setBackground(ColorPalette.TRANSPARENT);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public void update() {
        controller.getModel();
        controlPanel.update(controller.getModel());
        turnOrderPanel.update(controller.getModel());
        loggerPanel.update(controller.getModel());
        gamePanel.update();
    }
}
