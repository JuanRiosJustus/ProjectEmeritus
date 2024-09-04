package main.ui.huds.controls;

import main.game.main.GameState;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.GameUI;

import javax.swing.*;

public class ControllerHUD extends GameUI {
    private final ButtonTabbedPane mButtonTabbedPane;

    public ControllerHUD(int width, int height, int x, int y) {
        super(width, height, x, y, ControllerHUD.class.getSimpleName());

//        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(true);

        mButtonTabbedPane = new ButtonTabbedPane(width, height, false);
        setBackground(ColorPalette.getRandomColor());
        add(mButtonTabbedPane);
    }

    public void addPanel(String componentName, JComponent component, JButton enterButton, JButton backButton) {
        mButtonTabbedPane.addPanel(componentName, component, enterButton, backButton, false);
    }
    public void addPanelButton(String componentName, JComponent component, JButton enterButton, JButton backButton) {
        mButtonTabbedPane.addPanel(componentName, component, enterButton, backButton, true);
    }

    @Override
    public void gameUpdate(GameModel model) {

        // By default, we can show the movement pathing of the current unit
        boolean isVisible = mButtonTabbedPane.isShowingHomeScreen();
        model.setGameState(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING, isVisible);

        boolean shouldChangeBattleUiToHome = model.getGameStateBoolean(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN);
        if (shouldChangeBattleUiToHome) {
            mButtonTabbedPane.setScreenToHome();
            model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, false);
        }

        mButtonTabbedPane.gameUpdate(model);

//        System.out.println("IS SHOWING? " + isShowing());
    }
}
