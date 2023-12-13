package main;

import main.constants.Settings;
import main.engine.Engine;
import main.game.main.GameController;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.action.AbilityPool;
import main.game.stores.pools.unit.UnitPool;
import main.ui.presets.EditorScene;
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


//        UIManager.put("ComboBox.background", new ColorUIResource(ColorPalette.getRandomColor()));


        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);

        // // SceneManager.instance().set(SceneManager.GAME_SCENE);
        // SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
        // Engine.getInstance().controller.view.
        // SceneManager.instance().set(SceneManager.EDITOR_SCENE);

//        var r = new PreGamePanel(width, height);
//        var r  = new MenuScene(width, height);

//        var r = new EditorScene(width, height);
//        Engine.getInstance().getController().stage(r);

//        Engine.getInstance().getController().stage(new MenuScene(width, height));
        Engine.getInstance().getController().stage(new EditorScene(width, height));
//        Engine.getInstance().getController().stage(GameController.getInstance());

        GameController.getInstance().getModel().run();
        Engine.getInstance().run();
    }
}
