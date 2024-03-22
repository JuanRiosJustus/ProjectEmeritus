package main.ui.panels;

import main.game.stores.pools.ColorPalette;
import main.game.main.GameModel;
import main.graphics.JScene;

public class FullStatsPanel extends JScene {
    public FullStatsPanel(int width, int height) {
        super(width, height, FullStatsPanel.class.getSimpleName());
        setBackground(ColorPalette.GREEN);
    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
