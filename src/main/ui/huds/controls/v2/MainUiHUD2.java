package main.ui.huds.controls.v2;

import main.constants.GameState;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;

import javax.swing.*;
import java.awt.Component;

public class MainUiHUD2 extends JScene {
    private final ButtonTabbedPane mButtonTabbedPane;

    public MainUiHUD2(int width, int height, int x, int y) {
        super(width, height, x, y, "mainUiHud");

        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);

        mButtonTabbedPane = new ButtonTabbedPane(width, height, false);
        add(mButtonTabbedPane);
    }

    public void addPanel(String componentName, JComponent component, JButton backButton) {
        mButtonTabbedPane.addPanel(componentName, component, backButton, false);
    }

    public void addPanelRaw(String componentName, JButton button) {
        mButtonTabbedPane.addPanel(componentName, button, button, true);
    }

    @Override
    public void jSceneUpdate(GameModel model) {

        // By default, we can show the movement pathing of the current unit
        boolean isVisible = mButtonTabbedPane.isShowingHomeScreen();
        model.setGameState(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING, isVisible);

        boolean shouldChangeBattleUiToHome = model.getGameStateBoolean(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN);
        if (shouldChangeBattleUiToHome) {
            mButtonTabbedPane.setScreenToHome();
            model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, false);
        }

        mButtonTabbedPane.jSceneUpdate(model);

//        System.out.println("IS SHOWING? " + isShowing());
    }

    public int getDisplayWidth() { return mButtonTabbedPane.getDisplayWidth(); }
    public int geDisplayHeight() { return mButtonTabbedPane.geDisplayHeight(); }
    public JButton getButton(String key) { return mButtonTabbedPane.getButton(key); }
}
