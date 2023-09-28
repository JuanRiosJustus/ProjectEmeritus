package main;

import main.constants.Settings;
import main.engine.Engine;
import main.game.main.GameController;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.action.ActionPool;
import main.game.stores.pools.unit.UnitPool;
import main.input.InputController;
import main.ui.presets.EditorScene;

public class Main {

        
    public static void main(String[] args) throws Exception {

//         UserSavedData.getInstance().load(Constants.USER_SAVE_DIRECTORY + "ExampleSaveData.json");
//         UserSavedData.getInstance().createOrRead("TestFilePath.json");
//         UserSavedData.getInstance().update();

        // Loads the resources before game has started
        Engine.getInstance();
        AssetPool.getInstance();
        FontPool.getInstance();
        ActionPool.getInstance();
        UnitPool.getInstance();
        GameController.getInstance();


        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

        // // SceneManager.instance().set(SceneManager.GAME_SCENE);
        // SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
        // Engine.getInstance().controller.view.
        // SceneManager.instance().set(SceneManager.EDITOR_SCENE);

//        var r = new PreGamePanel(width, height);
//        var r  = new MenuScene(width, height);

//
        var r  = new EditorScene(width, height);
        Engine.getInstance().getController().setScene(r);
        Engine.getInstance().run();

//
//        Engine.getInstance().getController().getView().addScene(GameController.getInstance().getView());
//        Engine.getInstance().getController().getView().showScene(GameController.getInstance().getView());
//        GameController.getInstance().setInput(InputController.getInstance());
//        Engine.getInstance().getController().getModel().set(GameController.getInstance());
//        Engine.getInstance().run();

//        Engine.getInstance().getController().setScene(GameController.getInstance());
//        Engine.getInstance().run();
    }
}
