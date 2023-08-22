package main;

import main.constants.Constants;
import main.engine.Engine;
import main.game.main.GameController;
import main.game.state.UserSavedData;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.ability.AbilityPool;
import main.game.stores.pools.unit.UnitPool;
import main.ui.panels.GamePanel;
import main.ui.panels.PreGamePanel;
import main.ui.presets.MenuScene;

public class Main {

        
    public static void main(String[] args) throws Exception {

//         UserSavedData.getInstance().load(Constants.USER_SAVE_DIRECTORY + "ExampleSaveData.json");
//         UserSavedData.getInstance().createOrRead("TestFilePath.json");
//         UserSavedData.getInstance().update();

        // Loads the resources before game has started
        Engine.getInstance();
        AssetPool.getInstance();
        FontPool.getInstance();
        AbilityPool.getInstance();
        UnitPool.getInstance();
        GameController.getInstance();


        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;

        // // SceneManager.instance().set(SceneManager.GAME_SCENE);
        // SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
        // Engine.getInstance().controller.view.
        // SceneManager.instance().set(SceneManager.EDITOR_SCENE);

//        var r = new PreGamePanel(width, height);
//        var r  = new MenuScene(width, height);
//        var r  = new EditorScene(width, height);
        
//         Engine.getInstance().getController().getView().addScene(r);
//         Engine.getInstance().getController().getView().showScene(r);
//
        Engine.getInstance().getController().getView().addScene(GameController.getInstance().getView());
        Engine.getInstance().getController().getView().showScene(GameController.getInstance().getView());
        Engine.getInstance().run();
    }
}
