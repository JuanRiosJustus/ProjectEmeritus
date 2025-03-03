package main.game.main;


import java.awt.Color;

public class GameViewV1 {
    private final GameModel mGameModel;
    private GamePanelV1 mGamePanelV1;

    public GameViewV1(GameModel gameModel) { mGameModel = gameModel; }

    public GamePanelV1 getViewPort(int width, int height) {
        mGamePanelV1 = new GamePanelV1(mGameModel, width, height);
        mGamePanelV1.setBackground(Color.BLACK);
        mGameModel.getGameState().setViewportWidth(width);
        mGameModel.getGameState().setViewportHeight(height);
        return mGamePanelV1;
    }

    public void update(GameModel model) {
        if (!model.isRunning()) { return; }
        mGamePanelV1.gameUpdate(model);
    }

    public boolean isGamePanelShowing() {
        return mGamePanelV1.isShowing();
    }
}
