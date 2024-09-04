package main.engine;

import main.input.InputController;
import main.ui.custom.SwingUiUtils;

import java.util.HashMap;
import java.util.Map;

public class EngineController {

    public final EngineModel mModel;
    public final EngineView mView;

    public EngineController() {
        mModel = new EngineModel();
        mView = new EngineView();
    }

    public void input() { mModel.input(); }
    public void update() { mModel.update(); }
    public void render() { /*mView.render();*/ }
//    public void render() {
//        mView.addMouseMotionListener(InputController.getInstance().getMouse());
//        mView.addMouseListener(InputController.getInstance().getMouse());
//        mView.addKeyListener(InputController.getInstance().getKeyboard());
//        mView.addMouseWheelListener(InputController.getInstance().getMouse());
//    }
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
