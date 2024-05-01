package main.engine;

import main.game.main.GameController;
import main.input.InputController;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public class EngineModel {

    private EngineScene mEngineScene;
    public GameController mGameController;
    public InputController mInputController;

    public void init() {
        mInputController = InputController.getInstance();
        mGameController =  GameController.getInstance();
    }

    public void update() { if (mEngineScene != null) { mEngineScene.update(); } }

//    public void input() {  if (mEngineScene != null) { mEngineScene.input(); } }
    public void input() {
        if (mEngineScene != null) {
            mEngineScene.input();
//            if (mInputController.getKeyboard().isPressed(KeyEvent.VK_SPACE)) {
////                Engine.getInstance().getController().stage();
//            }
        }
    }

    public void stage(EngineScene engineScene) { mEngineScene = engineScene; }
}
