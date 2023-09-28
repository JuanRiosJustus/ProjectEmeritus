package main.engine;

import main.game.main.GameController;
import main.input.InputController;

public class EngineModel {

    private EngineScene mEngineScene;
    public GameController game;
    public InputController input;

//    public EngineModel() { init(); }

    public void init() {
        input = InputController.getInstance();
        game =  GameController.getInstance();
        game.setInput(input);
    }

//    public void update() { game.update(); }
//
//    public void input() {  game.input(); }

    public void update() { if (mEngineScene != null) { mEngineScene.update(); } }

    public void input() {  if (mEngineScene != null) { mEngineScene.input(); } }

    public void append(EngineScene engineScene) { mEngineScene = engineScene; }
}
