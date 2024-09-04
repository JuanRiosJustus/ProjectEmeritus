package main.game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import main.engine.Engine;
import main.game.stores.pools.FontPool;
import main.graphics.ControllerUI;
import main.ui.components.OutlineButton;
import main.ui.custom.*;
import main.ui.huds.*;
import main.ui.huds.controls.BlankHoldingPane;
import main.ui.huds.controls.ControllerPanel;
import main.ui.huds.controls.JGamePanel;
import main.ui.huds.controls.OutlineMapPanel;
import main.ui.panels.GamePanel;


public class GameView extends JGamePanel {

    private GamePanelHud mGamePanelHud;
    private GameController mGameController;
    private GamePanel mGamePanel;
    private JLayeredPane container;
    private Color color = null;

    public GameView(GameController gc, int width, int height) {
        initialize(gc, width, height);
    }

    public void initialize(GameController gc, int width, int height) {
        removeAll();
        container = new JLayeredPane();
        mGameController = gc;

        color = Color.DARK_GRAY;

        mGamePanelHud = new GamePanelHud(width, height, 0, 0);
        mGamePanel = new GamePanel(gc, width, height, 0, 0);

        container.setPreferredSize(new Dimension(width, height));
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(mGamePanelHud, JLayeredPane.MODAL_LAYER);

        setLayout(null);
        container.setBounds(0, 0, width, height);
        add(container);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public GamePanel getNewGamePanel(int width, int height) {
        mGamePanel = new GamePanel(mGameController, width, height, 0, 0);
        return mGamePanel;
    }

    public void update(GameModel model) {
        if (!model.isRunning()) { return; }
        mGamePanel.gameUpdate(model);
        mGamePanelHud.gameUpdate(model);
    }

    public void hideAuxPanels() {
//        controllerHUD.setVisible(!controllerHUD.isVisible());
//        mControllerPanel.setVisible(!mControllerPanel.isVisible());
//        mTimeLinePanel.setVisible(!mTimeLinePanel.isVisible());
//        loggerHUD.setVisible(!loggerHUD.isVisible());
    }

    public boolean isGamePanelShowing() {
        return mGamePanel.isShowing();
    }
}
