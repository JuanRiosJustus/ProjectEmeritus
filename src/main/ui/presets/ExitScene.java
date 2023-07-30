package main.ui.presets;

import main.engine.Engine;
import main.game.main.GameModel;
import main.graphics.JScene;

public class ExitScene extends JScene {
    public ExitScene(int width, int height) {
        super(width, height, "");
    }

    public void exit() {
        Engine.getInstance().stop();
    }

    @Override
    public void update(GameModel model) {

    }
}
