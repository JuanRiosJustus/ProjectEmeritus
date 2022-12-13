package engine;

import game.GameController;
import input.InputController;
import ui.panels.Ui;

public class EngineModel {

    public final GameController game;
    public final InputController input;
    public final Ui ui;

    public EngineModel() {
        input = InputController.get();
        game =  GameController.get();
        ui = new Ui();
//        reset();
    }

    public void update(EngineController engine) {
        game.update(engine);
    }

    public void input(EngineController engine) { input.update(); game.input(engine); }
}
