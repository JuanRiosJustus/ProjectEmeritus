package engine;

import game.main.GameController;
import input.InputController;

public class EngineModel {

    public final GameController game;
    public final InputController input;

    public EngineModel() {
        input = InputController.instance();
        game =  GameController.instance();
        game.setInput(input);
    }

    public void update() { GameController.instance().update(); }

    public void input() { GameController.instance().input(); }
}
