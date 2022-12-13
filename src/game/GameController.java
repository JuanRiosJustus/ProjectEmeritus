package game;

import engine.EngineController;
import engine.EngineModel;

import java.awt.*;

public class GameController {

    public GameModel model = new GameModel();
    public GameView view = new GameView();
    private static final GameController instance = new GameController();
    public static GameController get() { return instance; }

    public void render(EngineController engine, Graphics g) { view.render(engine, g); }

    public void update(EngineController engine) { model.update(engine); }

    public void input(EngineController engine) { model.input(engine); }

//    public void render(EngineController engine, Graphics g) { view.render(model, g); }
//
//    public void update(EngineController engine) { model.update(model); }
//
//    public void input(EngineController engine) { model.input(model); }
}
