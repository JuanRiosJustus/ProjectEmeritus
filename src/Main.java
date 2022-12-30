import constants.Constants;
import engine.Engine;
import game.GameController;
import game.components.SecondTimer;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.AssetPool;
import game.stores.pools.UnitPool;
import ui.presets.SceneManager;

public class Main {

    public static void main(String[] args) {

        SecondTimer st = new SecondTimer();
        AbilityPool.instance();
        AssetPool.instance();
        UnitPool.instance();
        AbilityPool.instance();
        GameController.instance();
        Engine.instance();
//        Engine.instance.controller.view.setScene(SceneManager.instance.getScene(Constants.GAME_SCENE));
//        Engine.instance.controller.view.setScene(SceneManager.instance.getScene(Constants.MAIN_MENU_SCENE));

//        SceneManager.instance.setScene(Constants.GAME_SCENE);
        SceneManager.instance().setScene(Constants.MAIN_MENU_SCENE);


        System.out.println(st.elapsed() + " ?");
        Engine.instance().run();


//        Application application = new Application();
//        SecondTimer st = new SecondTimer();
//        Thread.sleep(2519);
//        System.out.println(st.elapsed());
    }
}
