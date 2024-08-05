package main.engine;

import java.util.HashMap;
import java.util.Map;

public class EngineController {

    public final EngineModel mModel;
    public final EngineView mView;
    private final Map<String, EngineScene> mScenes = new HashMap<>();

    public EngineController() {
        mModel = new EngineModel();
        mView = new EngineView();
    }

    public void input() { mModel.input(); }
    public void update() { mModel.update(); }
    public void render() { mView.render(); }
    public EngineView getView() { return mView; }
    public EngineModel getModel() { return mModel; }
    public void setSize(int width, int height) {
        mView.setEngineWidthAndHeight(width, height);
    }
    public void stage(EngineScene scene) {
        mModel.stage(scene);
        mView.stage(scene.render());
    }
}
