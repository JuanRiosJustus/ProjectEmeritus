package ui.presets;

import engine.Engine;
import graphics.JScene;

public class ExitScene extends JScene {
    public ExitScene(int width, int height) {
        super(width, height, "");
    }

    public void exit() {
        Engine.get().stop();
    }
}
