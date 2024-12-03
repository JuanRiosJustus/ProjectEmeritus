package main.engine;

import main.game.main.GameController;
import main.input.InputController;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public class EngineModel {

    private EngineScene mEngineScene;
    public void update() { if (mEngineScene == null) { return; } mEngineScene.update(); }
    public void input() { if (mEngineScene == null) { return; } mEngineScene.input(); }

    public void stage(EngineScene engineScene) { mEngineScene = engineScene; }
}
