package main;

import javax.swing.UIManager;

import main.constants.Constants;
import main.engine.Engine;
import main.game.main.GameController;
import main.game.state.UserDataStore;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.ability.AbilityPool;
import main.game.stores.pools.unit.UnitPool;
import main.ui.presets.EditorScene;
import main.ui.presets.MenuScene;

public class Main {

        
    public static void main(String[] args) throws Exception {

        // UserData.getInstance().load(Constants.USER_SAVE_DIRECTORY + "ExampleSaveData.json");
        // UserDataStore.getInstance().createOrRead("TestFilePath.json");
        // UserDataStore.getInstance().update();

        // Loads the resources before game has started
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

        var r  = new MenuScene(width, height);
        // var r  = new EditorScene(width, height);
        r.setName("yooo");
        
        // Engine.getInstance().getController().getView().addScene(r);
        // Engine.getInstance().getController().getView().showScene(r);

        Engine.getInstance().getController().getView().addScene(GameController.getInstance().getView());
        Engine.getInstance().getController().getView().showScene(GameController.getInstance().getView());
        Engine.getInstance().run();
    }

    
    // public static void main(String[] args) throws Exception {

    //     // UserData.getInstance().load(Constants.USER_SAVE_DIRECTORY + "ExampleSaveData.json");
    //     // UserDataStore.getInstance().createOrRead("TestFilePath.json");
    //     // UserDataStore.getInstance().update();

    //     // Loads the resources before game has started
    //     AssetPool.getInstance();
    //     AbilityPool.getInstance();
    //     UnitPool.getInstance();
    //     SceneManager.getInstance();
    //     GameController.getInstance();

            
    //     int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
            
    //     SceneManager.getInstance().install(Constants.MAIN_MENU_SCENE, new MenuScene(width, height));
        
    //     SceneManager.getInstance().install(Constants.EDIT_SCENE, new EditorScene(width, height));
        
    //     SceneManager.getInstance().install(Constants.GAME_SCENE, GameController.getInstance().getView());
    //     // SceneManager.getInstance().install(Constants.GAME_SCENE, new GameController().getView());


    //     // SceneManager.instance().set(SceneManager.GAME_SCENE);                        
    //     SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
    //     // SceneManager.instance().set(SceneManager.EDITOR_SCENE);
    //     Engine.getInstance().run();
    // }
}
