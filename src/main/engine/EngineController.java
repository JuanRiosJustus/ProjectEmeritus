package main.engine;

public class EngineController {

    public final EngineModel model;
    public final EngineView view;

    public EngineController() {
        model = new EngineModel();
        view = new EngineView();
    }

    public void input() { model.input(); }

    public void update() { model.update(); }

    public void render() { view.render(); }

    public EngineView getView() { return view; }
    public EngineModel getModel() { return model; }
}
