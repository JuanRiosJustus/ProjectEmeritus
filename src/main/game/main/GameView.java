package main.game.main;

import javax.swing.JLayeredPane;

import main.ui.huds.controls.JGamePanel;
import main.ui.panels.GamePanel;

import java.awt.Color;


public class GameView extends JGamePanel {

    private GamePanelHud mGamePanelHud;
    private GameController mGameController;
    private GamePanel mGamePanel;

    public GameView(GameController gc, int width, int height) {
        init(gc, width, height);
    }

    public void init(GameController gc, int width, int height) {
        removeAll();
        JLayeredPane container = new JLayeredPane();
        mGameController = gc;

        mGamePanelHud = new GamePanelHud(width, height, 0, 0);
        mGamePanelHud.setVisible(true);
        mGamePanelHud.setOpaque(false); // Or true, depending on your needs

        mGamePanel = new GamePanel(gc, width, height);

        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(mGamePanelHud, JLayeredPane.MODAL_LAYER);

        setLayout(null);
        container.setBounds(0, 0, width, height);

        add(container);
        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public GameView(GameController gameController, GameSettings gameSettings) {
        init(gameController, gameSettings);
    }

    public void init(GameController gameController, GameSettings gameSettings) {
        int width = gameSettings.getViewPortWidth();
        int height = gameSettings.getViewPortHeight();

        removeAll();
        setLayout(null);

        JLayeredPane container = new JLayeredPane();
        mGameController = gameController;

        // Initialize and configure mGamePanelHud
        mGamePanelHud = new GamePanelHud(width, height, 0, 0);
        mGamePanelHud.setVisible(true);
        mGamePanelHud.setOpaque(false); // Set as needed
        mGamePanelHud.setBounds(0, 0, width, height);
        mGamePanelHud.setBackground(Color.BLUE); // Debug color

        // Initialize and configure mGamePanel
        mGamePanel = new GamePanel(gameController, width, height);
        mGamePanel.setBounds(0, 0, width, height);
        mGamePanel.setBackground(Color.GREEN); // Debug color

        // Add to layers, with mGamePanelHud on a higher layer
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER);
        container.add(mGamePanelHud, JLayeredPane.POPUP_LAYER);

        // Set layout and bounds of container
        container.setBounds(0, 0, width, height);
        add(container);

        // Validate and repaint to force immediate render
        container.revalidate();
        container.repaint();

        setDoubleBuffered(true);
        setOpaque(true);
        setVisible(true);
    }

    public GamePanel getGamePanel(int width, int height) {
        mGamePanel = new GamePanel(mGameController, width, height);
        return mGamePanel;
    }

    public void update(GameModel model) {
        if (!model.isRunning()) { return; }
        mGamePanel.gameUpdate(model);

//        mGamePanelHud.gameUpdate(model);
        if (model.getSettings().isMapEditorMode()) {

            System.out.println("toto");
        } else {
            mGamePanelHud.gameUpdate(model);
        }
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
