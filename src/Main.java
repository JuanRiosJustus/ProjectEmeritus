import constants.Constants;
import engine.Engine;
import game.main.GameController;
import game.stores.pools.AssetPool;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.unit.UnitPool;
import ui.presets.EditorScene;
import ui.presets.MenuScene;
import ui.presets.SceneManager;

public class Main {

    
    public static void main(String[] args) {

        // Loads the resources before game has started
        AssetPool.instance();
        AbilityPool.getInstance();
        UnitPool.instance();
        SceneManager.instance();
        GameController.instance();

            
        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
            
        SceneManager.instance().install(Constants.MAIN_MENU_SCENE, new MenuScene(width, height));
        
        SceneManager.instance().install(Constants.EDIT_SCENE, new EditorScene(width, height));
        
        SceneManager.instance().install(Constants.GAME_SCENE, GameController.instance().getView());


        SceneManager.instance().set(SceneManager.GAME_SCENE);                        
        // SceneManager.instance().set(SceneManager.MAIN_MENU_SCENE);
        Engine.instance().run();

    }
}
