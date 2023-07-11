package engine;

import game.main.GameController;
import input.InputController;

public class EngineModel {

    public GameController game;
    public InputController input;

    public EngineModel() { init(); }

    private void init() {
        input = InputController.instance();
        game =  GameController.instance();
        game.setInput(input);
    }

    public void update() { game.update(); }

    public void input() {  game.input(); }
}
