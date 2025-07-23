package main.engine;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.constants.Constants;
import main.constants.Pair;
import main.input.InputController;
import main.logging.EmeritusLogger;

import java.util.LinkedHashMap;
import java.util.Map;

public class EngineController {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(EngineController.class);
    private static EngineController mInstance = null;
    public static EngineController getInstance() {
        if (mInstance == null) {
            mInstance = new EngineController();
        }
        return mInstance;
    }
    private final EngineModel mModel;
    private final EngineView mView;
    private final Map<String, EngineRunnable> mRunnableMap = new LinkedHashMap<>();
    private Pair<EngineRunnable, Scene> mCurrent = null;
    private EngineController() {
        mModel = new EngineModel();
        mView = new EngineView();
        mCurrent = null;

        mView.addEventFilter(KeyEvent.KEY_PRESSED,  (event) -> {
            String sceneToRender = null;
            switch (event.getCode()) {
                case M -> sceneToRender = Constants.GAME_SCENE;
                case N -> sceneToRender = Constants.MENU_SCENE;
                case B -> sceneToRender = Constants.MAP_EDITOR_SCENE;
            }

            if (sceneToRender == null) { return; }
            setScene(sceneToRender);
        });
    }

    public void stage(String key, EngineRunnable scene) {
        mRunnableMap.put(key, scene);
        setScene(key);
    }

    private void setScene(String key) {
        EngineRunnable runnable = mRunnableMap.get(key);
        // If there is no scene with the key, ignore submission
        if (runnable == null) { return; }
        // Stop the current scene from running
        if (mCurrent != null) { mCurrent.getFirst().stop(); InputController.getInstance().clear(mCurrent.getSecond()); }
        mLogger.info("Started setting scene to {}", key);

        // Draw the scene
        Scene toRender = runnable.render();
        mView.setScene(toRender);

        // Start the scene
        mView.centerOnScreen();

        runnable.run();
        // Set the scene
        InputController.getInstance().setup(toRender);
        toRender.getRoot().requestFocus();


        mCurrent = new Pair<>(runnable, toRender);
        mLogger.info("Finished setting scene to {}", key);
    }

    public void setOnCloseRequest(EventHandler<WindowEvent> eventHandler) {
        mView.setOnCloseRequest(eventHandler);
    }
    public double getDeltaTime() { return mModel.getDeltaTime(); }
    public Stage getStage() { return mView; }
}
