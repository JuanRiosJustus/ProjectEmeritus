package main.engine;

import main.game.main.GameController;
import main.input.InputController;

public class EngineModel {

    public GameController game;
    public InputController input;

    public EngineModel() { init(); }

    private void init() {
        input = InputController.instance();
        game =  GameController.getInstance();
        game.setInput(input);
    }

    public void update() { game.update(); }

    public void input() {  game.input(); }
}
