package engine;

public class EngineController {

    public final EngineModel model;
    public final EngineView view;

    public EngineController() {
        model = new EngineModel();
        view = new EngineView(this);
    }

    public void input() { model.input(this); }

    public void update() { model.update(this); }

    public void render() { view.render(this); }
}
