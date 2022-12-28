package engine;

import game.GameController;
import input.InputController;

public class EngineModel {

    public final GameController game;
    public final InputController input;

    public EngineModel() {
        input = InputController.instance();
        game =  GameController.instance();
    }

    public void update() { game.update(); }

    public void input() { game.input(); }
}
