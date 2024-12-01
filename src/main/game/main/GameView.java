package main.game.main;


import java.awt.Color;

public class GameView {
    private final GameModel mGameModel;
    private GamePanel mGamePanel;

    public GameView(GameModel gameModel) { mGameModel = gameModel; }

    public GamePanel getViewPort(int width, int height) {
        mGamePanel = new GamePanel(mGameModel, width, height);
        mGamePanel.setBackground(Color.BLACK);
        mGameModel.getGameState().setViewportWidth(width);
        mGameModel.getGameState().setViewportHeight(height);
        return mGamePanel;
    }

    public void update(GameModel model) {
        if (!model.isRunning()) { return; }
        mGamePanel.gameUpdate(model);
    }

    public boolean isGamePanelShowing() {
        return mGamePanel.isShowing();
    }
}
