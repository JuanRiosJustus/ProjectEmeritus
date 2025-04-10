package main.engine;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.constants.Constants;
import main.input.InputController;

import java.util.HashMap;
import java.util.Map;

public class EngineView extends Stage {

    private final Map<String, Scene> mSceneMap = new HashMap<>();
    private Scene mCurrentScene = null;

    public EngineView() {
        setTitle(Constants.APPLICATION_NAME);
//        initModality(Modality.APPLICATION_MODAL);
//        initStyle(StageStyle.UTILITY);
//
        setResizable(false);
//        System.out.println(this.getWidth() + " " + this.getMinWidth() + " " + this.getMaxWidth());
//        System.out.println(this.getHeight() + " " + this.getMinHeight() + " " + this.getMaxHeight());
        show();
    }

//    public void stage(String name, EngineRunnable scene) {
//        Scene toRender = scene.render();
//
//        if (mCurrentScene != null) {
////            InputController.getInstance().clear(mCurrentScene);
//            mCurrentScene = toRender;
//        }
//
//
//
//        mSceneMap.put(name, toRender);
//
////        setupNewScene(name);
//        centerOnScreen();
//    }

//    public void setupNewScene(String name) {
//
//        Scene currentScene = getScene();
//        InputController.getInstance().clear(currentScene);
//
//        Scene nextScene = mSceneMap.get(name);
//        if (nextScene == null) { return; }
//
//        InputController.getInstance().setup(nextScene);
//        setScene(nextScene);
//    }
}